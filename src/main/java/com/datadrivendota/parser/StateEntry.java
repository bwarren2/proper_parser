package com.datadrivendota.parser;

/**
 * Created by ben on 8/4/16.
 */
public class StateEntry{
    public Integer tick_time;
    public Integer offset_time;
    public Integer player_slot;

    public Integer total_earned_gold;
    public Integer reliable_gold;
    public Integer unreliable_gold;
    public Integer shared_gold;
    public Integer hero_kill_gold;
    public Integer creep_kill_gold;
    public Integer income_gold;

    public Integer xp;
    public Float healing;
    public Integer last_hits;
    public Integer nearby_creep_deaths;
    public Integer tower_kills;
    public Integer roshan_kills;
    public Integer denies;
    public Integer misses;
    public Integer stuns;

    public Integer kills;
    public Integer assists;
    public Integer deaths;


    public Integer hero_id;

    public Integer x;
    public Integer y;

    public long damage_taken;
    public Integer health;
    public Integer max_health;
    public Float mana;
    public Float max_mana;
    public Float armor;
    public Float magic_resist_pct;
    public Integer damage_min;
    public Integer damage_max;
    public Integer damage_bonus;
    public Integer recent_damage;

    public Float strength;
    public Float agility;
    public Float intelligence;
    public Float strength_total;
    public Float agility_total;
    public Float intelligence_total;
    public Float respawn_time;

    public Integer lifestate;

    public Integer item_0;
    public Integer item_1;
    public Integer item_2;
    public Integer item_3;
    public Integer item_4;
    public Integer item_5;


    public StateEntry(){

    }
}
