package com.datadrivendota.parser;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.EnvironmentVariableCredentialsProvider;
import com.amazonaws.services.ec2.model.State;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.output.ByteArrayOutputStream;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.util.*;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * Responsible for absorbing, transforming, and persisting the replay data info.
 *
 * Replaces all the bad old data structures like DataStreams.
 *
 * Created by ben on 7/22/16.
 */
public class FileBox {

    // ArrayLists for the various things in Datastream.java in the old version here.

    // POJOs for post-processed data structures.

    // Player slot, then time:state
    private HashMap<Integer, HashMap<Integer, StateEntry>> files = new HashMap<Integer, HashMap<Integer, StateEntry>>();

    // Combatlog events happen at ~random times, and can have many at a single tick.
    // We process them later.
    private List<CombatEntry> combat= new ArrayList<>();

    // These are the strings mapped to ids given by protobuf.
    public HashMap<Integer, String> stringTable = new HashMap<>();
    private String winner;
    private BigInteger match_id;

    private HashMap<String, byte[]> output_files = new HashMap<>();

    public FileBox() {
    }

    public void addState(StateEntry entry){
        Integer slot = entry.getPlayer_slot();
        HashMap<Integer, StateEntry> file = files.get(slot);
        if (file == null) {
            file = new HashMap<Integer, StateEntry>();
            files.put(slot, file);
        }
        files.get(slot).put(entry.getTick_time(), entry);

    }
    public void addCombat(CombatEntry entry){
        combat.add(entry);
    }

    public void addWinner(String winner){
        this.winner = winner;
    }

    public void addStringTableEntry(int rowIndex, String key) {
        this.stringTable.put(rowIndex, key);
    }

    public HashMap<Integer, StateEntry> getPlayerFile(Integer slot){
        return this.files.get(slot);
    }

    /**
     *  After the box has been filled with the raw data in parser, transform it into the shape of the final files, put
     *  them on S3, and purge everything out.
     */

    /**
     *  Transform the dumb arraylists into the final data structures.
     *  Basically:
     *    1. there are offsets that need to be applied on all the times;
     *    2. the data must be truncated to times when all players are loaded
     *      (there is ~blank data at the end of game before the server dies.)
     *    3. rollups need to be made.
     *      (there are two teams, radiant and dire, with 5 players each.  We measure:
     *        a) Unit state every second for each hero (1/player = 10)
     *        b) Summed stats for each player on each team (2 teams)
     *        c) Difference of sums of stats across the two teams (Radiant-Dire = 1)
     *       We currently do this in Python, but it is much faster in Java and avoids a second replay transfer))
     *
     *  This is easy for the state-based data, which is indexed on player-second anyway, but the combatlog has entries
     *  at random times.  That rollup is interpolation and summing.
     *
     *  None of this part is written yet, and is one of the major goals of the rewrite.
     */

    public void handle() {
        this.mungeTimes();
        this.saveItemBuys();
        this.setBasicState();
        this.setMergedState(0, 4, "radiant");
        this.setMergedState(5, 9, "dire");
        this.setDiffState("radiant-dire", "diff");
        this.setDiffState("dire-radiant", "negdiff");
    }

    private void setDiffState(String direction, String side) {
        HashMap<Integer, Integer> counts = new HashMap<>();
        ArrayList<StateEntry> radiant_ary = this.getOutputFileStateArray(this.makeFilename("radiant", "statelog", "allstate"));
        HashMap<Integer, StateEntry> radiant = new HashMap<>();
        for (StateEntry entry : radiant_ary){
            radiant.put(entry.tick_time, entry);
        }
        ArrayList<StateEntry> dire_ary = this.getOutputFileStateArray(this.makeFilename("dire", "statelog", "allstate"));
        HashMap<Integer, StateEntry> dire= new HashMap<>();
        for (StateEntry entry : dire_ary){
            dire.put(entry.tick_time, entry);
        }
        for (Integer time : radiant.keySet()) {
            if (radiant .get(time).health != null) { // If the player has a hero
                Integer existing;
                try {
                    existing = counts.get(time);
                } catch (NullPointerException e) {
                    existing = 0;
                }
                if (existing == null) existing = 0;  // Dunno why I need this, but I do.
                counts.put(time, existing + 1);
            }
        }

        for (Integer time : dire.keySet()) {
            if (radiant.get(time).health != null) { // If the player has a hero
                Integer existing;
                try {
                    existing = counts.get(time);
                } catch (NullPointerException e) {
                    existing = 0;
                }
                if (existing == null) existing = 0;  // Dunno why I need this, but I do.
                counts.put(time, existing + 1);
            }
        }

        List<Integer> validTimesList = new ArrayList<>();
        for (Integer time : counts.keySet()){
            if (counts.get(time)==2){
                validTimesList.add(time);
            }
        }
        Collections.sort(validTimesList);
        ArrayList<StateEntry> side_states = new ArrayList<>();
        for (Integer time : validTimesList){
            StateEntry base = null;
            if (direction=="radiant-dire") {
                base = (StateEntry) radiant.get(time).clone(); // Adding only well defined on populated things.
                base.subtract(dire.get(time));
            } else {
                base = (StateEntry) dire.get(time).clone(); // Adding only well defined on populated things.
                base.subtract(radiant.get(time));
            }
            side_states.add(base);
        }

        Comparator<StateEntry> comparator = new Comparator<StateEntry >() {
            public int compare(StateEntry se1, StateEntry se2) {
                return se1.offset_time - se2.offset_time;
            }
        };
        Collections.sort(side_states, comparator);
        ObjectMapper om = new ObjectMapper();
        om.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        try {
            String value = om.writeValueAsString(side_states);
            byte[] data = gzipString(value);
            String filename = makeFilename(side, "statelog", "allstate");
            this.output_files.put(filename, data);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void setBasicState() {
        for (Integer slot : this.files.keySet()){
            HashMap<Integer, StateEntry> statemap = this.files.get(slot);
            List<StateEntry> stateEntryList = new ArrayList<StateEntry>();
            for (StateEntry se : statemap.values()){
                if (se.health != null){
                    stateEntryList.add(se);
                }
            }
            Comparator<StateEntry > comparator = new Comparator<StateEntry >() {
                public int compare(StateEntry se1, StateEntry se2) {
                    return se1.offset_time - se2.offset_time;
                }
            };
            Collections.sort(stateEntryList, comparator);

            ObjectMapper om = new ObjectMapper();
            om.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            String filename = makeFilename(slot, "statelog", "allstate");
            try {
                String value = om.writeValueAsString(stateEntryList);
                byte[] data = gzipString(value);
                this.output_files.put(filename, data);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void setMergedState(Integer startIndex, Integer endIndex, String side) {
        ArrayList<StateEntry> side_states = new ArrayList<>();

        HashMap<Integer, Integer> counts = new HashMap<>();
        for (int slot = startIndex; slot <= endIndex; slot++) {
            for (Integer time : this.files.get(slot).keySet()){
                if (this.files.get(slot).get(time).health != null){ // If the player has a hero
                    Integer existing;
                    try {
                        existing = counts.get(time);
                    } catch (NullPointerException e){
                        existing = 0;
                    }
                    if (existing == null) existing = 0;  // Dunno why I need this, but I do.
                    counts.put(time, existing+1);
                }
            }
        }
        List<Integer> validTimesList = new ArrayList<>();
        for (Integer time : counts.keySet()){
            if (counts.get(time)==5){
                validTimesList.add(time);
            }
        }
        Collections.sort(validTimesList);
        for (Integer time : validTimesList){
            StateEntry base = null;
            for (int slot = startIndex; slot <= endIndex; slot++) {
                if (slot==startIndex) base = (StateEntry) this.files.get(slot).get(time).clone(); // Adding only well defined on populated things.
                if (slot!=startIndex) base.add(this.files.get(slot).get(time));
                base.offset_time = this.files.get(slot).get(time).offset_time;
                base.tick_time= this.files.get(slot).get(time).tick_time;
            }
            side_states.add(base);
        }

        Comparator<StateEntry> comparator = new Comparator<StateEntry >() {
            public int compare(StateEntry se1, StateEntry se2) {
                return se1.offset_time - se2.offset_time;
            }
        };
        Collections.sort(side_states, comparator);
        ObjectMapper om = new ObjectMapper();
        om.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        try {
            String value = om.writeValueAsString(side_states);
            byte[] data = gzipString(value);
            String filename = makeFilename(side, "statelog", "allstate");
            this.output_files.put(filename, data);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public String makeFilename(Integer slot, String log_type, String facet){
        Integer player_slot;
//        5-9 -> 128 132 for dire;
        if (slot>4) player_slot = slot + 123;
        else player_slot = slot;
        // Explicit argument indices may be used to re-order output.
        return String.format("%s_%s_%s_%s_v%s.json.gz",
                this.match_id,
                player_slot,
                log_type,
                facet,
                System.getenv("PARSER_VERSION")
        );

    }

    public String makeFilename(String dataslice, String log_type, String facet){

        return String.format("%s_%s_%s_%s_v%s.json.gz",
                this.match_id,
                dataslice,
                log_type,
                facet,
                System.getenv("PARSER_VERSION")
        );

    }

    private void mungeTimes(){
        this.offsetTimes();
        this.convertItems();
    }

    private void saveItemBuys() {
    // Fill it
    }

    private void offsetTimes(){
        Integer offset=-10;
        for (CombatEntry combatEntry : this.combat) {
            if (combatEntry.isPlaying()){
                offset = combatEntry.tick_time;
            }
        }
        if (offset==-10){
            System.exit(0);
        }
        for (CombatEntry combatEntry : combat) {
            combatEntry.setOffset_time(offset);
        }
        for (Integer key: this.files.keySet()){
            HashMap<Integer, StateEntry> file = this.files.get(key);
            for (Integer key2: file.keySet()) file.get(key2).changeOffset_time(offset);
        }
    }

    private void convertItems() {
        for (Integer key: this.files.keySet()){
            HashMap<Integer, StateEntry> file = this.files.get(key);
            for (Integer key2: file.keySet()) file.get(key2).swapItemNames(stringTable);

        }
    }

    /**
     *  Write the JSON to S3.
     */
    public void uploadFiles(){
        for (String filename : this.output_files.keySet()){
            System.out.println("Uploading "+filename);
            writeS3(filename, this.output_files.get(filename));
        }

    }

    public byte[] gzipString(String str) throws IOException {
        if (str == null || str.length() == 0) {
            return new byte[10];
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream(str.length());
        GZIPOutputStream gzip = new GZIPOutputStream(out);
        gzip.write(str.getBytes());
        gzip.close();

        byte[] compressedBytes = out.toByteArray();
        return compressedBytes;
    }

    public ArrayList<StateEntry> getOutputFileStateArray(String filename) {
        ObjectMapper om = new ObjectMapper();
        byte[] compressed = this.output_files.get(filename);
        ArrayList<StateEntry> stateEntryList = new ArrayList<>();
        ;
        try {
            stateEntryList = om.readValue(
                    this.ungzipString(compressed),
                    new TypeReference<ArrayList<StateEntry>>() {}
            );
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stateEntryList;
    }


    public String ungzipString(byte[] compressed) throws IOException {
        final int BUFFER_SIZE = 32;
        ByteArrayInputStream is = new ByteArrayInputStream(compressed);
        GZIPInputStream gis = new GZIPInputStream(is, BUFFER_SIZE);
        StringBuilder string = new StringBuilder();
        byte[] data = new byte[BUFFER_SIZE];
        int bytesRead;
        while ((bytesRead = gis.read(data)) != -1) {
            string.append(new String(data, 0, bytesRead));
        }
        gis.close();
        is.close();
        return string.toString();
    }

    public void writeS3(String filename, byte[] data){
        System.out.println("Writing to s3");
        AmazonS3 s3client = new AmazonS3Client(new
                EnvironmentVariableCredentialsProvider()
        );

        System.out.println("Got creds");
        String bucketName     = "datadrivendota";
        String keyName        = "raw_replay_parse/"+filename;

        try {
            ObjectMetadata md = new ObjectMetadata();
            // md.setContentLength(match_parse.length());
            md.setContentType("json");
            md.addUserMetadata("Content-Encoding", "gzip");

            // Convert the String into InputStream
            InputStream is = new ByteArrayInputStream(data);

            System.out.println("Uploading a new object to S3\n");
            s3client.putObject(
                    new PutObjectRequest(bucketName, keyName, is, md)
            );
        } catch (AmazonServiceException ase) {
            System.out.println("Caught an AmazonServiceException, which " +
                    "means your request made it " +
                    "to Amazon S3, but was rejected with an error response" +
                    " for some reason.");
            System.out.println("Error Message:    " + ase.getMessage());
            System.out.println("HTTP Status Code: " + ase.getStatusCode());
            System.out.println("AWS Error Code:   " + ase.getErrorCode());
            System.out.println("Error Type:       " + ase.getErrorType());
            System.out.println("Request ID:       " + ase.getRequestId());
        } catch (AmazonClientException ace) {
            System.out.println("Caught an AmazonClientException, which " +
                    "means the client encountered " +
                    "an internal error while trying to " +
                    "communicate with S3, " +
                    "such as not being able to access the network.");
            System.out.println("Error Message: " + ace.getMessage());
        } catch (Exception e) {
            System.out.print("Error!");
            e.printStackTrace();
        }
        System.out.println("Wrote to s3");

    }

    public void setMatch_id(BigInteger match_id) {
        this.match_id = match_id;
    }


}
