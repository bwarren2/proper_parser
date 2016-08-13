package com.datadrivendota.parser;

import com.amazonaws.services.dynamodbv2.xspec.NULL;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.json.PackageVersion;
import skadistats.clarity.model.Entity;
import skadistats.clarity.processor.runner.Context;

import java.util.HashMap;

/**
 * Created by ben on 8/4/16.
 */
public class StateEntry implements Cloneable{
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
    public Integer damage_taken;
    public Integer health;
    public Integer max_health;
    public Float mana;
    public Float max_mana;
    public Float armor;
    private Float magic_resist_pct;
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

    public StateEntry() {
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
        this.setMagic_resist_pct((Float) Util.getEntityProperty(hero, "m_flMagicalResistanceValue", null));
        this.recent_damage = Util.getEntityProperty(hero, "m_iRecentDamage", null);
        this.armor = Util.getEntityProperty(hero, "m_flPhysicalArmorValue", null);
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
                ", total_earned_gold=" + total_earned_gold +
                ", reliable_gold=" + reliable_gold +
                ", unreliable_gold=" + unreliable_gold +
                ", shared_gold=" + shared_gold +
                ", hero_kill_gold=" + hero_kill_gold +
                ", creep_kill_gold=" + creep_kill_gold +
                ", income_gold=" + income_gold +
                ", xp=" + xp +
                ", healing=" + healing +
                ", last_hits=" + last_hits +
                ", nearby_creep_deaths=" + nearby_creep_deaths +
                ", tower_kills=" + tower_kills +
                ", roshan_kills=" + roshan_kills +
                ", denies=" + denies +
                ", misses=" + misses +
                ", stuns=" + stuns +
                ", kills=" + kills +
                ", assists=" + assists +
                ", deaths=" + deaths +
                ", hero_id=" + hero_id +
                ", x=" + x +
                ", y=" + y +
                ", damage_taken=" + damage_taken +
                ", health=" + health +
                ", max_health=" + max_health +
                ", mana=" + mana +
                ", max_mana=" + max_mana +
                ", armor=" + armor +
                ", magic_resist_pct=" + getMagic_resist_pct() +
                ", damage_min=" + damage_min +
                ", damage_max=" + damage_max +
                ", damage_bonus=" + damage_bonus +
                ", recent_damage=" + recent_damage +
                ", strength=" + strength +
                ", agility=" + agility +
                ", intelligence=" + intelligence +
                ", strength_total=" + strength_total +
                ", agility_total=" + agility_total +
                ", intelligence_total=" + intelligence_total +
                ", respawn_time=" + respawn_time +
                ", lifestate=" + lifestate +
                ", item_0_id=" + item_0_id +
                ", item_1_id=" + item_1_id +
                ", item_2_id=" + item_2_id +
                ", item_3_id=" + item_3_id +
                ", item_4_id=" + item_4_id +
                ", item_5_id=" + item_5_id +
                ", item_0='" + item_0 + '\'' +
                ", item_1='" + item_1 + '\'' +
                ", item_2='" + item_2 + '\'' +
                ", item_3='" + item_3 + '\'' +
                ", item_4='" + item_4 + '\'' +
                ", item_5='" + item_5 + '\'' +
                '}';
    }

    public void changeOffset_time(Integer offset) {
        this.offset_time = this.tick_time - offset;
    }
    public Integer getPlayer_slot() {
        return player_slot;
    }
    public Integer getTick_time() {return tick_time;}
    public Double doubleMana() {return Double.valueOf(mana);}

    public void setPct_health() {}
    public void setPct_mana() {}
    public Integer getPct_health(){
        try {
            if (this.max_health<=0){
                return 0;
            }
            else {
                return Math.round(this.health/this.max_health*10000)/100;
            }
        } catch (NullPointerException e){
            return 0;
        }
    }
    public Integer getPct_mana(){
        try {
            if (this.max_mana<=0){
                return 0;
            }
            else {
                return Math.round(this.mana/this.max_mana*10000)/100;
            }
        } catch (NullPointerException e){
            return 0;
        }
    }

    public void setArmor(){}
    public Float getArmor(){
        try {
            return this.armor + Math.round(this.agility/7*100)/100;
        } catch (NullPointerException e){
            return 0f;
        }
    }


    public void setBase_damage(){}
    public Integer getBase_damage(){
        try {
            return Math.round((this.damage_max + this.damage_min)/2);
        } catch (NullPointerException e){
            return 0;
        }
    }
    public Integer getBonus_damage(){
        return this.damage_bonus;
    }

    public void setTotal_damage(){}
    public Integer getTotal_damage(){
        try {
            return Math.round((this.damage_max + this.damage_min)/2)+this.damage_bonus;
        } catch (NullPointerException e){
            return 0;
        }
    }

    public void swapItemNames(HashMap<Integer, String> stringTableEntries) {
        // I should learn how to use reflection.  Later.
        if (this.item_0_id!=null && stringTableEntries.get(this.item_0_id)!=null){
            this.item_0 = stringTableEntries.get(this.item_0_id);
            this.item_0_id = null;
        }
        if (this.item_1_id!=null && stringTableEntries.get(this.item_1_id)!=null){
            this.item_1 = stringTableEntries.get(this.item_1_id);
            this.item_1_id = null;
        }
        if (this.item_2_id!=null && stringTableEntries.get(this.item_2_id)!=null){
            this.item_2 = stringTableEntries.get(this.item_2_id);
            this.item_2_id = null;
        }
        if (this.item_3_id!=null && stringTableEntries.get(this.item_3_id)!=null){
            this.item_3 = stringTableEntries.get(this.item_3_id);
            this.item_3_id = null;
        }
        if (this.item_4_id!=null && stringTableEntries.get(this.item_4_id)!=null){
            this.item_4 = stringTableEntries.get(this.item_4_id);
            this.item_4_id = null;
        }
        if (this.item_5_id!=null && stringTableEntries.get(this.item_5_id)!=null){
            this.item_5 = stringTableEntries.get(this.item_5_id);
            this.item_5_id = null;
        }
    }

    public Float getMagic_resist_pct() {
        return magic_resist_pct;
    }

    public void setMagic_resist_pct(Float magic_resist_pct) {
        this.magic_resist_pct = magic_resist_pct;
    }

    @Override
    public boolean equals(Object otherObj) {
        if((otherObj== null) || (getClass() != otherObj.getClass())){
            return false;
        } else {
            StateEntry obj = (StateEntry) otherObj;
            // We don't test armor because the json repr is different than stored repr (derived).
            float epsilon = (float) 0.00001;
            if (
                    (this.player_slot == obj.player_slot) &&
                    (this.offset_time == obj.offset_time) &&
                    (this.tick_time == obj.tick_time) &&
                    (this.assists == obj.assists) &&
                    (this.creep_kill_gold == obj.creep_kill_gold) &&
                    (this.damage_bonus == obj.damage_bonus) &&
                    (this.damage_max == obj.damage_max) &&
                    (this.damage_min == obj.damage_min) &&
                    (this.deaths == obj.deaths) &&
                    (this.denies == obj.denies) &&
                    (this.health == obj.health) &&
                    (this.hero_id == obj.hero_id) &&
                    (this.hero_kill_gold == obj.hero_kill_gold) &&
                    (this.income_gold == obj.income_gold) &&
                    (this.item_0 == obj.item_0) &&
                    (this.item_1 == obj.item_1) &&
                    (this.item_2 == obj.item_2) &&
                    (this.item_3 == obj.item_3) &&
                    (this.item_4 == obj.item_4) &&
                    (this.item_5 == obj.item_5) &&
                    (this.kills == obj.kills) &&
                    (this.last_hits == obj.last_hits) &&
                    (this.lifestate == obj.lifestate) &&
                    (this.max_health == obj.max_health) &&
                    (this.misses == obj.misses) &&
                    (this.nearby_creep_deaths == obj.nearby_creep_deaths) &&
                    (this.recent_damage == obj.recent_damage) &&
                    (this.reliable_gold == obj.reliable_gold) &&
                    (this.roshan_kills == obj.roshan_kills) &&
                    (this.shared_gold == obj.shared_gold) &&
                    (this.total_earned_gold == obj.total_earned_gold) &&
                    (this.tower_kills == obj.tower_kills) &&
                    (this.unreliable_gold == obj.unreliable_gold) &&
                    (this.x == obj.x) &&
                    (this.xp == obj.xp) &&
                    (this.y == obj.y)
                ) {
                    // TECHDEBT: Figure out how to write these so nulls don't break everything.
//                    Math.round(this.getMagic_resist_pct()) == Math.round(obj.getMagic_resist_pct()) &&
//                    Math.abs(this.agility - obj.agility) < epsilon &&
//                    Math.abs(this.agility_total - obj.agility_total) < epsilon &&
//                    Math.abs(this.healing - obj.healing) < epsilon &&
//                    Math.abs(this.intelligence - obj.intelligence) < epsilon &&
//                    Math.abs(this.intelligence_total - obj.intelligence_total) < epsilon &&
//                    Math.abs(this.mana - obj.mana) < epsilon &&
//                    Math.abs(this.max_mana - obj.max_mana) < epsilon &&
//                    Math.abs(this.respawn_time - obj.respawn_time) < epsilon &&
//                    Math.abs(this.strength - obj.strength) < epsilon &&
//                    Math.abs(this.strength_total - obj.strength_total) < epsilon &&
                return true;
            } else {
                return false;
            }
        }
    }

    public void add(StateEntry obj){

        if(this.agility!=null && obj.agility!= null) this.agility += obj.agility;
        if(this.agility_total!=null && obj.agility_total!= null) this.agility_total += obj.agility_total;
        if(this.armor!=null && obj.armor!= null) this.armor += obj.armor;
        if(this.assists!=null && obj.assists!= null) this.assists += obj.assists;
        if(this.creep_kill_gold!=null && obj.creep_kill_gold!= null) this.creep_kill_gold += obj.creep_kill_gold;
        if(this.damage_bonus!=null && obj.damage_bonus!= null) this.damage_bonus += obj.damage_bonus;
        if(this.damage_max!=null && obj.damage_max!= null) this.damage_max += obj.damage_max;
        if(this.damage_min!=null && obj.damage_min!= null) this.damage_min += obj.damage_min;
        if(this.damage_taken!=null && obj.damage_taken!= null) this.damage_taken += obj.damage_taken;
        if(this.deaths!=null && obj.deaths!= null) this.deaths += obj.deaths;
        if(this.denies!=null && obj.denies!= null) this.denies += obj.denies;
        if(this.healing!=null && obj.healing!= null) this.healing += obj.healing;
        if(this.health!=null && obj.health!= null) this.health += obj.health;
        if(this.hero_id!=null && obj.hero_id!= null) this.hero_id += obj.hero_id;
        if(this.hero_kill_gold!=null && obj.hero_kill_gold!= null) this.hero_kill_gold += obj.hero_kill_gold;
        if(this.income_gold!=null && obj.income_gold!= null) this.income_gold += obj.income_gold;
        if(this.intelligence!=null && obj.intelligence!= null) this.intelligence += obj.intelligence;
        if(this.intelligence_total!=null && obj.intelligence_total!= null) this.intelligence_total += obj.intelligence_total;
        if(this.kills!=null && obj.kills!= null) this.kills += obj.kills;
        if(this.last_hits!=null && obj.last_hits!= null) this.last_hits += obj.last_hits;
        if(this.lifestate!=null && obj.lifestate!= null) this.lifestate += obj.lifestate;
        if(this.magic_resist_pct!=null && obj.magic_resist_pct!= null) this.magic_resist_pct += obj.magic_resist_pct;
        if(this.mana!=null && obj.mana!= null) this.mana += obj.mana;
        if(this.max_health!=null && obj.max_health!= null) this.max_health += obj.max_health;
        if(this.max_mana!=null && obj.max_mana!= null) this.max_mana += obj.max_mana;
        if(this.misses!=null && obj.misses!= null) this.misses += obj.misses;
        if(this.nearby_creep_deaths!=null && obj.nearby_creep_deaths!= null) this.nearby_creep_deaths += obj.nearby_creep_deaths;
        if(this.offset_time!=null && obj.offset_time!= null) this.offset_time += obj.offset_time;
        if(this.player_slot!=null && obj.player_slot!= null) this.player_slot += obj.player_slot;
        if(this.recent_damage!=null && obj.recent_damage!= null) this.recent_damage += obj.recent_damage;
        if(this.reliable_gold!=null && obj.reliable_gold!= null) this.reliable_gold += obj.reliable_gold;
        if(this.respawn_time!=null && obj.respawn_time!= null) this.respawn_time += obj.respawn_time;
        if(this.roshan_kills!=null && obj.roshan_kills!= null) this.roshan_kills += obj.roshan_kills;
        if(this.shared_gold!=null && obj.shared_gold!= null) this.shared_gold += obj.shared_gold;
        if(this.strength!=null && obj.strength!= null) this.strength += obj.strength;
        if(this.strength_total!=null && obj.strength_total!= null) this.strength_total += obj.strength_total;
        if(this.stuns!=null && obj.stuns!= null) this.stuns += obj.stuns;
        if(this.tick_time!=null && obj.tick_time!= null) this.tick_time += obj.tick_time;
        if(this.total_earned_gold!=null && obj.total_earned_gold!= null) this.total_earned_gold += obj.total_earned_gold;
        if(this.tower_kills!=null && obj.tower_kills!= null) this.tower_kills += obj.tower_kills;
        if(this.unreliable_gold!=null && obj.unreliable_gold!= null) this.unreliable_gold += obj.unreliable_gold;
        if(this.x!=null && obj.x!= null) this.x += obj.x;
        if(this.xp!=null && obj.xp!= null) this.xp += obj.xp;
        if(this.y!=null && obj.y!= null) this.y += obj.y;
    }

    public void subtract(StateEntry obj){
        if(this.agility!=null && obj.agility!= null) this.agility -= obj.agility;
        if(this.agility_total!=null && obj.agility_total!= null) this.agility_total -= obj.agility_total;
        if(this.armor!=null && obj.armor!= null) this.armor -= obj.armor;
        if(this.assists!=null && obj.assists!= null) this.assists -= obj.assists;
        if(this.creep_kill_gold!=null && obj.creep_kill_gold!= null) this.creep_kill_gold -= obj.creep_kill_gold;
        if(this.damage_bonus!=null && obj.damage_bonus!= null) this.damage_bonus -= obj.damage_bonus;
        if(this.damage_max!=null && obj.damage_max!= null) this.damage_max -= obj.damage_max;
        if(this.damage_min!=null && obj.damage_min!= null) this.damage_min -= obj.damage_min;
        if(this.damage_taken!=null && obj.damage_taken!= null) this.damage_taken -= obj.damage_taken;
        if(this.deaths!=null && obj.deaths!= null) this.deaths -= obj.deaths;
        if(this.denies!=null && obj.denies!= null) this.denies -= obj.denies;
        if(this.healing!=null && obj.healing!= null) this.healing -= obj.healing;
        if(this.health!=null && obj.health!= null) this.health -= obj.health;
        if(this.hero_id!=null && obj.hero_id!= null) this.hero_id -= obj.hero_id;
        if(this.hero_kill_gold!=null && obj.hero_kill_gold!= null) this.hero_kill_gold -= obj.hero_kill_gold;
        if(this.income_gold!=null && obj.income_gold!= null) this.income_gold -= obj.income_gold;
        if(this.intelligence!=null && obj.intelligence!= null) this.intelligence -= obj.intelligence;
        if(this.intelligence_total!=null && obj.intelligence_total!= null) this.intelligence_total -= obj.intelligence_total;
        if(this.kills!=null && obj.kills!= null) this.kills -= obj.kills;
        if(this.last_hits!=null && obj.last_hits!= null) this.last_hits -= obj.last_hits;
        if(this.lifestate!=null && obj.lifestate!= null) this.lifestate -= obj.lifestate;
        if(this.magic_resist_pct!=null && obj.magic_resist_pct!= null) this.magic_resist_pct -= obj.magic_resist_pct;
        if(this.mana!=null && obj.mana!= null) this.mana -= obj.mana;
        if(this.max_health!=null && obj.max_health!= null) this.max_health -= obj.max_health;
        if(this.max_mana!=null && obj.max_mana!= null) this.max_mana -= obj.max_mana;
        if(this.misses!=null && obj.misses!= null) this.misses -= obj.misses;
        if(this.nearby_creep_deaths!=null && obj.nearby_creep_deaths!= null) this.nearby_creep_deaths -= obj.nearby_creep_deaths;
        if(this.offset_time!=null && obj.offset_time!= null) this.offset_time -= obj.offset_time;
        if(this.player_slot!=null && obj.player_slot!= null) this.player_slot -= obj.player_slot;
        if(this.recent_damage!=null && obj.recent_damage!= null) this.recent_damage -= obj.recent_damage;
        if(this.reliable_gold!=null && obj.reliable_gold!= null) this.reliable_gold -= obj.reliable_gold;
        if(this.respawn_time!=null && obj.respawn_time!= null) this.respawn_time -= obj.respawn_time;
        if(this.roshan_kills!=null && obj.roshan_kills!= null) this.roshan_kills -= obj.roshan_kills;
        if(this.shared_gold!=null && obj.shared_gold!= null) this.shared_gold -= obj.shared_gold;
        if(this.strength!=null && obj.strength!= null) this.strength -= obj.strength;
        if(this.strength_total!=null && obj.strength_total!= null) this.strength_total -= obj.strength_total;
        if(this.stuns!=null && obj.stuns!= null) this.stuns -= obj.stuns;
        if(this.tick_time!=null && obj.tick_time!= null) this.tick_time -= obj.tick_time;
        if(this.total_earned_gold!=null && obj.total_earned_gold!= null) this.total_earned_gold -= obj.total_earned_gold;
        if(this.tower_kills!=null && obj.tower_kills!= null) this.tower_kills -= obj.tower_kills;
        if(this.unreliable_gold!=null && obj.unreliable_gold!= null) this.unreliable_gold -= obj.unreliable_gold;
        if(this.x!=null && obj.x!= null) this.x -= obj.x;
        if(this.xp!=null && obj.xp!= null) this.xp -= obj.xp;
        if(this.y!=null && obj.y!= null) this.y -= obj.y;
    }
    public Object clone() {
        try {
            return super.clone();
        }
        catch (CloneNotSupportedException e) {
            // This should never happen
            throw new InternalError(e.toString());
        }
    }

}
