package com.datadrivendota.parser;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by ben on 8/4/16.
 */
public class PlayerFile {

    private HashMap<Integer, StateEntry> stateMap = new HashMap<>();

    public PlayerFile() {
    }

    public void addState(StateEntry entry) {
        this.stateMap.put(entry.getTick_time(), entry);
    }

    public HashMap<Integer, StateEntry> getStateMap() {
        return stateMap;
    }
    public void setOffset(Integer offset) {
        for (Integer key: this.stateMap.keySet()) this.stateMap.get(key).setOffset_time(offset);
    }

    public void swapItemNames(HashMap<Integer, String> stringTable) {
        for (Integer key: this.stateMap.keySet()) this.stateMap.get(key).swapItemNames(stringTable);
    }
}
