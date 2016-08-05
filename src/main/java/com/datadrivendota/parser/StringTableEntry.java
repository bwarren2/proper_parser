package com.datadrivendota.parser;

/**
 * Created by ben on 8/4/16.
 */
public class StringTableEntry {
    public int idx;
    public String value;
    public StringTableEntry(){
    }

    @Override
    public String toString() {
        return "StringTableEntry{" +
                "idx=" + idx +
                ", value='" + value + '\'' +
                '}';
    }
}
