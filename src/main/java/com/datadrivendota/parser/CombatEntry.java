package com.datadrivendota.parser;

/**
 * Created by ben on 8/4/16.
 */
public class CombatEntry {
    public Integer time;
    public String type;
    public String subtype;
    public Integer team;
    public String key;
    public Integer value;
    public Integer slot;

    public String attacker_name;
    public Boolean attacker_illusion;
    public Boolean attacker_hero;
    public String attacker_source;

    public String target;
    public String target_source;
    public Boolean target_hero;
    public Boolean target_illusion;

    public String inflictor;
    public Integer gold_reason;
    public Integer xp_reason;
    public Integer gold;
    public Integer lh;
    public Integer xp;
    public Float x;
    public Float y;

    public CombatEntry(Integer time){
        this.time = time;
    }
}
