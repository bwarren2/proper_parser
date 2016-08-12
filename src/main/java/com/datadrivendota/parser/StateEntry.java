package com.datadrivendota.parser;

import com.fasterxml.jackson.annotation.JsonIgnore;
import skadistats.clarity.model.Entity;
import skadistats.clarity.processor.runner.Context;

import java.util.HashMap;

/**
 * Created by ben on 8/4/16.
 */
public class StateEntry{
    public Integer tick_time;
    public Integer offset_time;
    public Integer player_slot;

    @Override
    public boolean equals(Object otherObj) {
        if((otherObj== null) || (getClass() != otherObj.getClass())){
            System.out.print("Error!");
            return false;
        } else {
            StateEntry obj = (StateEntry) otherObj;
            // We don't test armor because the json repr is different than stored repr (derived).
            float epsilon = (float) 0.00001;
            if (
                    Math.abs(this.agility - obj.agility) < epsilon &&
                    Math.abs(this.agility_total - obj.agility_total) < epsilon &&
                    (this.assists == obj.assists) &&
                    (this.creep_kill_gold == obj.creep_kill_gold) &&
                    (this.damage_bonus == obj.damage_bonus) &&
                    (this.damage_max == obj.damage_max) &&
                    (this.damage_min == obj.damage_min) &&
                    (this.deaths == obj.deaths) &&
                    (this.denies == obj.denies) &&
                    Math.abs(this.healing - obj.healing) < epsilon &&
                    (this.health == obj.health) &&
                    (this.hero_id == obj.hero_id) &&
                    (this.hero_kill_gold == obj.hero_kill_gold) &&
                    (this.income_gold == obj.income_gold) &&
                    Math.abs(this.intelligence - obj.intelligence) < epsilon &&
                    Math.abs(this.intelligence_total - obj.intelligence_total) < epsilon &&
                    (this.item_0 == obj.item_0) &&
                    (this.item_1 == obj.item_1) &&
                    (this.item_2 == obj.item_2) &&
                    (this.item_3 == obj.item_3) &&
                    (this.item_4 == obj.item_4) &&
                    (this.item_5 == obj.item_5) &&
                    (this.kills == obj.kills) &&
                    (this.last_hits == obj.last_hits) &&
                    (this.lifestate == obj.lifestate) &&
                    Math.abs(this.getMagic_resist_pct() - obj.getMagic_resist_pct()) < epsilon &&
                    Math.abs(this.mana - obj.mana) < epsilon &&
                    (this.max_health == obj.max_health) &&
                    Math.abs(this.max_mana - obj.max_mana) < epsilon &&
                    (this.misses == obj.misses) &&
                    (this.nearby_creep_deaths == obj.nearby_creep_deaths) &&
                    (this.recent_damage == obj.recent_damage) &&
                    (this.reliable_gold == obj.reliable_gold) &&
                    Math.abs(this.respawn_time - obj.respawn_time) < epsilon &&
                    (this.roshan_kills == obj.roshan_kills) &&
                    (this.shared_gold == obj.shared_gold) &&
                    Math.abs(this.strength - obj.strength) < epsilon &&
                    Math.abs(this.strength_total - obj.strength_total) < epsilon &&
                    (this.total_earned_gold == obj.total_earned_gold) &&
                    (this.tower_kills == obj.tower_kills) &&
                    (this.unreliable_gold == obj.unreliable_gold) &&
                    (this.x == obj.x) &&
                    (this.xp == obj.xp) &&
                    (this.y == obj.y)
                    ) {
                System.out.print("True!");
                return true;
            } else {
                System.out.print("False!");
                return false;
            }
        }
    }

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

    public void setArmor(){}
    public Float getArmor(){
        try {
            return this.armor + Math.round(this.agility/7*100)/100;
        } catch (NullPointerException e){
            e.printStackTrace();
            return 0f;
        }
    }


    public void setBase_damage(){}
    public Integer getBase_damage(){
        return Math.round((this.damage_max + this.damage_min)/2);
    }
    public Integer getBonus_damage(){
        return this.damage_bonus;
    }

    public void setTotal_damage(){}
    public Integer getTotal_damage(){
        return Math.round((this.damage_max + this.damage_min)/2)+this.damage_bonus;
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
}
