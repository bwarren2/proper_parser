package com.datadrivendota.parser;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.EnvironmentVariableCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import org.apache.commons.io.output.ByteArrayOutputStream;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
    private HashMap<Integer, PlayerFile> files = new HashMap<>();

    // Combatlog events happen at ~random times, and can have many at a single tick.
    // We process them later.
    private List<CombatEntry> combat= new ArrayList<>();

    // These are the strings mapped to ids given by protobuf.
    public HashMap<Integer, String> stringTable = new HashMap<>();
    private String winner;

    public FileBox() {
    }

    public void addState(StateEntry entry){
        Integer slot = entry.getPlayer_slot();
        PlayerFile file = files.get(slot);
        if (file == null) {
            files.put(slot, new PlayerFile());
        }
        files.get(slot).addState(entry);

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
    public void printStrings(){
        for (Integer key: this.stringTable.keySet()) {
            System.out.println(key+" "+this.stringTable.get(key));
        }
    }

    public PlayerFile getPlayerFile(Integer slot){
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

    public void tranforms() {
        this.transformAndRollup();
        this.generateJsonFiles();
        this.uploadFiles();
        this.purgeFiles();
    }

    private void transformAndRollup(){
        this.offsetTimes();
        this.convertItems();
        this.saveItemBuys();
        // this.stateMath();
        // this.combatMath();
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
            this.files.get(key).setOffset(offset);
        }
    }

    private void convertItems() {
        for (Integer key: this.files.keySet()){
            this.files.get(key).swapItemNames(stringTable);
        }
    }

    /**
     *  JSONify the postprocessed POJOs.
     */
    private void generateJsonFiles(){

    }

    /**
     *  Write the JSON to S3.
     */
    private void uploadFiles(){

    }

    /**
     *  Delete the JSON files after upload.
     */
    private void purgeFiles(){

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

}
