package com.datadrivendota.parser;

import java.util.ArrayList;
import java.util.HashMap;

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

    private HashMap<Integer, PlayerFile> files = new HashMap<>();

    public ArrayList<StringTableEntry> stringTableEntries = new ArrayList<>();

    public ArrayList<StringTableEntry> getStringTableEntries() {
        return stringTableEntries;
    }

    public FileBox() {

    }

    public void addStringTableEntry(StringTableEntry ste){
        this.stringTableEntries.add(ste);
    }

    public void printStrings(){
        System.out.print(stringTableEntries);
    }

    public PlayerFile getPlayerFile(Integer slot){
        return this.files.get(slot);
    }

    /**
     *  After the box has been filled with the raw data in parser, transform it into the shape of the final files, put
     *  them on S3, and purge everything out.
     */
    public String handle(){
        this.transformAndRollup();
        this.generateJsonFiles();
        this.uploadFiles();
        this.purgeFiles();
        return "";
    }

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
    private void transformAndRollup(){

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

}
