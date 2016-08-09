package com.datadrivendota.parser;

import com.fasterxml.jackson.annotation.JsonIgnore;
import skadistats.clarity.model.Entity;
import skadistats.clarity.processor.runner.Context;

import java.util.ArrayList;
import java.util.HashMap;

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

    @JsonIgnore
    public Integer stuns;

    public Integer kills;
    public Integer assists;
    public Integer deaths;

    public Integer hero_id;

    public Integer x;
    public Integer y;

    @JsonIgnore
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

    @JsonIgnore
    public Integer item_0_id;
    @JsonIgnore
    public Integer item_1_id;
    @JsonIgnore
    public Integer item_2_id;
    @JsonIgnore
    public Integer item_3_id;
    @JsonIgnore
    public Integer item_4_id;
    @JsonIgnore
    public Integer item_5_id;

    public String item_0;
    public String item_1;
    public String item_2;
    public String item_3;
    public String item_4;
    public String item_5;


    public StateEntry(Integer player_slot, Integer tick_time){
        this.player_slot = player_slot;
        this.tick_time= tick_time;
    }

    public void addPlayerState(Entity playerresource, Integer i){
        this.hero_id = Util.getEntityProperty(playerresource, "m_vecPlayerTeamData.%i.m_nSelectedHeroID", i);
        this.kills = Util.getEntityProperty(playerresource, "m_vecPlayerTeamData.%i.m_iKills", i);
        this.assists = Util.getEntityProperty(playerresource, "m_vecPlayerTeamData.%i.m_iAssists", i);
        this.deaths = Util.getEntityProperty(playerresource,"m_vecPlayerTeamData.%i.m_iDeaths", i);
    }

    public void addSideState(Entity side_resource, Integer lookup_i){
        this.total_earned_gold = Util.getEntityProperty(side_resource, "m_vecDataTeam.%i.m_iTotalEarnedGold", lookup_i);
        this.reliable_gold = Util.getEntityProperty(side_resource, "m_vecDataTeam.%i.m_iReliableGold", lookup_i);
        this.unreliable_gold = Util.getEntityProperty(side_resource, "m_vecDataTeam.%i.m_iUnreliableGold", lookup_i);
        this.xp = Util.getEntityProperty(side_resource, "m_vecDataTeam.%i.m_iTotalEarnedXP", lookup_i);
        this.shared_gold = Util.getEntityProperty(side_resource, "m_vecDataTeam.%i.m_iSharedGold", lookup_i);
        this.hero_kill_gold = Util.getEntityProperty(side_resource, "m_vecDataTeam.%i.m_iHeroKillGold", lookup_i);
        this.creep_kill_gold = Util.getEntityProperty(side_resource, "m_vecDataTeam.%i.m_iCreepKillGold", lookup_i);
        this.income_gold = Util.getEntityProperty(side_resource, "m_vecDataTeam.%i.m_iIncomeGold", lookup_i);
        this.denies = Util.getEntityProperty(side_resource, "m_vecDataTeam.%i.m_iDenyCount", lookup_i);
        this.last_hits = Util.getEntityProperty(side_resource, "m_vecDataTeam.%i.m_iLastHitCount", lookup_i);
        this.nearby_creep_deaths = Util.getEntityProperty(
                side_resource,
                "m_vecDataTeam.%i.m_iNearbyCreepDeathCount",
                lookup_i
        );
        this.misses = Util.getEntityProperty(side_resource, "m_vecDataTeam.%i.m_iMissCount", lookup_i);
        this.healing = Util.getEntityProperty(side_resource, "m_vecDataTeam.%i.m_fHealing", lookup_i);
        this.tower_kills = Util.getEntityProperty(side_resource, "m_vecDataTeam.%i.m_iTowerKills", lookup_i);
        this.roshan_kills = Util.getEntityProperty(side_resource, "m_vecDataTeam.%i.m_iRoshanKills", lookup_i);
    }

    public void addHeroState(Entity hero, Context ctx){
        this.x = Util.getEntityProperty(hero, "CBodyComponent.m_cellX", null);
        this.y = Util.getEntityProperty(hero, "CBodyComponent.m_cellY", null);

        this.health = Util.getEntityProperty(hero, "m_iHealth", null);
        this.max_health = Util.getEntityProperty(hero, "m_iMaxHealth", null);
        this.mana = Util.getEntityProperty(hero, "m_flMana", null);
        this.max_mana = Util.getEntityProperty(hero, "m_flMaxMana", null);
        this.max_mana = Util.getEntityProperty(hero, "m_flMaxMana", null);

        this.damage_max = Util.getEntityProperty(hero, "m_iDamageMax", null);
        this.damage_min = Util.getEntityProperty(hero, "m_iDamageMin", null);

        this.damage_bonus = Util.getEntityProperty(hero, "m_iDamageBonus", null);
        this.strength = Util.getEntityProperty(hero, "m_flStrength", null);
        this.agility = Util.getEntityProperty(hero, "m_flAgility", null);
        this.intelligence = Util.getEntityProperty(hero, "m_flIntellect", null);
        this.strength_total = Util.getEntityProperty(hero, "m_flStrengthTotal", null);
        this.agility_total = Util.getEntityProperty(hero, "m_flAgilityTotal", null);
        this.intelligence_total = Util.getEntityProperty(hero, "m_flIntellectTotal", null);
        this.respawn_time = Util.getEntityProperty(hero, "m_flRespawnTime", null);
        this.recent_damage = Util.getEntityProperty(hero, "m_iRecentDamage", null);
        this.armor = Util.getEntityProperty(hero, "m_flPhysicalArmorValue", null);
        this.magic_resist_pct = Util.getEntityProperty(hero, "m_flMagicalResistanceValue", null);
        this.lifestate = Util.getEntityProperty(hero, "m_lifeState", null);

        this.item_0_id = Util.getItem(ctx, hero, 0);
        this.item_1_id = Util.getItem(ctx, hero, 1);
        this.item_2_id = Util.getItem(ctx, hero, 2);
        this.item_3_id = Util.getItem(ctx, hero, 3);
        this.item_4_id = Util.getItem(ctx, hero, 4);
        this.item_5_id = Util.getItem(ctx, hero, 5);

    }

    @Override
    public String toString() {
        return "StateEntry{" +
                "tick_time=" + tick_time +
                ", offset_time=" + offset_time +
                ", player_slot=" + player_slot +
                ", health=" + health +
                ", mana=" + mana +
                ", item_0_id='" + item_0_id + '\'' +
                ", item_0='" + item_0 + '\'' +
                '}';
    }

    public void setOffset_time(Integer offset) {
        this.offset_time = this.tick_time - offset;
    }
    public Integer getPlayer_slot() {
        return player_slot;
    }
    public Integer getTick_time() {return tick_time;}
    public Double doubleMana() {return Double.valueOf(mana);}

    public Integer getPct_health(){
        if (this.max_health<=0){
            return 0;
        }
        else {
            return Math.round(this.health/this.max_health*10000)/100;
        }
    }
    public Integer getPct_mana(){
        if (this.max_mana<=0){
            return 0;
        }
        else {
            return Math.round(this.mana/this.max_mana*10000)/100;
        }
    }

    public Float getArmor(){
        return this.armor + Math.round(this.agility/7*100)/100;
    }

    public Integer getBase_damage(){
        return Math.round((this.damage_max + this.damage_min)/2);
    }
    public Integer getBonus_damage(){
        return this.damage_bonus;
    }
    public Integer getTotal_damage(){
        return Math.round((this.damage_max + this.damage_min)/2)+this.damage_bonus;
    }

    public void swapItemNames(HashMap<Integer, String> stringTableEntries) {
        // I should learn how to use reflection.  Later.
        if (this.item_0_id!=null && stringTableEntries.get(this.item_0_id)!=null){
            this.item_0 = stringTableEntries.get(this.item_0_id);
        }
        if (this.item_1_id!=null && stringTableEntries.get(this.item_1_id)!=null){
            this.item_1 = stringTableEntries.get(this.item_1_id);
        }
        if (this.item_2_id!=null && stringTableEntries.get(this.item_2_id)!=null){
            this.item_2 = stringTableEntries.get(this.item_2_id);
        }
        if (this.item_3_id!=null && stringTableEntries.get(this.item_3_id)!=null){
            this.item_3 = stringTableEntries.get(this.item_3_id);
        }
        if (this.item_4_id!=null && stringTableEntries.get(this.item_4_id)!=null){
            this.item_4 = stringTableEntries.get(this.item_4_id);
        }
        if (this.item_5_id!=null && stringTableEntries.get(this.item_5_id)!=null){
            this.item_5 = stringTableEntries.get(this.item_5_id);
        }
    }
}
