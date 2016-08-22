package com.datadrivendota.parser;
import java.lang.IllegalArgumentException;
import java.util.List;
import java.util.Objects;

/**
 * Created by ben on 8/21/16.
 */
public class CombatSeries {

    public Integer time;
    public Integer offset_time;
    public Integer all_income;
    public Integer item_buys;
    public Integer healing;
    public Integer other_dmg_dealt;
    public Integer key_bldg_kills;
    public Integer creep_kill_income;
    public Integer other_dmg_taken;
    public Integer buyback_expense;
    public Integer death_expense;
    public Integer roshan_kill_income;
    public Integer xp;
    public Integer building_income;
    public Integer hero_dmg_taken;
    public Integer kills;
    public Integer hero_dmg_dealt;
    public Integer deaths;
    public Integer hero_kill_income;
    public Integer hero_xp;
    public Integer roshan_xp;
    public Integer creep_xp;
    public Integer last_hits;
    public Integer courier_kill_income;
    public Integer earned_income;
    public Integer key_bldg_dmg_dealt;

    public CombatSeries(Integer time, Integer offset_time) {
        this.time = time;
        this.offset_time = offset_time;
        this.all_income = 0;
        this.item_buys = 0;
        this.healing = 0;
        this.other_dmg_dealt = 0;
        this.key_bldg_kills = 0;
        this.creep_kill_income = 0;
        this.other_dmg_taken = 0;
        this.buyback_expense = 0;
        this.death_expense = 0;
        this.roshan_kill_income = 0;
        this.xp = 0;
        this.building_income = 0;
        this.hero_dmg_taken = 0;
        this.kills = 0;
        this.hero_dmg_dealt = 0;
        this.deaths = 0;
        this.hero_kill_income = 0;
        this.hero_xp = 0;
        this.roshan_xp = 0;
        this.creep_xp = 0;
        this.last_hits = 0;
        this.courier_kill_income = 0;
        this.earned_income = 0;
        this.key_bldg_dmg_dealt = 0;
    }

    public CombatSeries update(CombatEntry ce, String hero, List enemies, List allies){

        if (
            Objects.equals(ce.type, "kills") &&
            Objects.equals(ce.attacker_source, hero) &&
            ce.targetsHero() &&
            enemies.contains(ce.target)
        ){
            this.kills += 1;
        }

        if (
                Objects.equals(ce.type, "kills") &&
                Objects.equals(ce.target, hero) &&
                ce.targetsHero() &&
                enemies.contains(ce.attacker_source)
        ){
            this.deaths += 1;
        }

        if (
                Objects.equals(ce.type, "kills") &&
                Objects.equals(ce.attacker_source, hero) &&
                !ce.targetsHero() &&
                !ce.targetsBuilding() &&
                !ce.targetsIllusion()
        ){
            this.last_hits += 1;
        }

        if (
                Objects.equals(ce.type, "xp_reasons") &&
                Objects.equals(ce.target, hero)
        ){
            this.xp += ce.value;
        }

        if (
                Objects.equals(ce.type, "healing") &&
                Objects.equals(ce.attacker_source, hero) &&
                allies.contains(ce.target)
        ){
            this.healing += ce.value;
        }

        if (
                Objects.equals(ce.type, "damage") &&
                Objects.equals(ce.target, hero) &&
                enemies.contains(ce.attacker_name) &&
                !ce.targetsIllusion()
        ){
            this.hero_dmg_taken += ce.value;
        }

        if (
                Objects.equals(ce.type, "damage") &&
                Objects.equals(ce.attacker_name, hero) &&
                enemies.contains(ce.target) &&
                !ce.targetsIllusion()
        ){
            this.hero_dmg_dealt += ce.value;
        }

        if (
                Objects.equals(ce.type, "damage") &&
                Objects.equals(ce.target, hero) &&
                !enemies.contains(ce.attacker_source) &&
                !ce.targetsIllusion()
        ){
            this.other_dmg_taken += ce.value;
        }

        if (
                Objects.equals(ce.type, "damage") &&
                Objects.equals(ce.attacker_name, hero) &&
                !enemies.contains(ce.target) &&
                !ce.targetsIllusion()
        ){
            this.other_dmg_dealt += ce.value;
        }

        if (
                Objects.equals(ce.type, "gold_reasons") &&
                Objects.equals(ce.target, hero)
        ){
            this.all_income += ce.value;
        }

        if (
                Objects.equals(ce.type, "gold_reasons") &&
                Objects.equals(ce.target, hero) &&
                !Objects.equals(ce.key, "6")
        ){
            this.earned_income += ce.value;
        }

        if (
                Objects.equals(ce.type, "gold_reasons") &&
                Objects.equals(ce.target, hero) &&
                Objects.equals(ce.key, "11")
        ){
            this.building_income += ce.value;
        }

        if (
                Objects.equals(ce.type, "gold_reasons") &&
                Objects.equals(ce.target, hero) &&
                Objects.equals(ce.key, "15")
        ){
            this.courier_kill_income += ce.value;
        }

        if (
                Objects.equals(ce.type, "gold_reasons") &&
                Objects.equals(ce.target, hero) &&
                Objects.equals(ce.key, "13")
        ){
            this.creep_kill_income += ce.value;
        }

        if (
                Objects.equals(ce.type, "gold_reasons") &&
                Objects.equals(ce.target, hero) &&
                Objects.equals(ce.key, "12")
        ){
            this.hero_kill_income += ce.value;
        }

        if (
                Objects.equals(ce.type, "gold_reasons") &&
                Objects.equals(ce.target, hero) &&
                Objects.equals(ce.key, "14")
        ){
            this.roshan_kill_income+= ce.value;
        }

        if (
                Objects.equals(ce.type, "gold_reasons") &&
                Objects.equals(ce.target, hero) &&
                Objects.equals(ce.key, "2")
        ){
            this.buyback_expense += ce.value;
        }

        if (
                Objects.equals(ce.type, "gold_reasons") &&
                Objects.equals(ce.target, hero) &&
                Objects.equals(ce.key, "1")
        ){
            this.death_expense += ce.value;
        }

        if (
                Objects.equals(ce.type, "xp_reasons") &&
                Objects.equals(ce.target, hero) &&
                Objects.equals(ce.key, "1")
        ){
            this.hero_xp+= ce.value;
        }

        if (
                Objects.equals(ce.type, "xp_reasons") &&
                Objects.equals(ce.target, hero) &&
                Objects.equals(ce.key, "2")
        ){
            this.creep_xp+= ce.value;
        }

        if (
                Objects.equals(ce.type, "xp_reasons") &&
                Objects.equals(ce.target, hero) &&
                Objects.equals(ce.key, "3")
        ){
            this.roshan_xp += ce.value;
        }

        if (
                Objects.equals(ce.type, "damage") &&
                Objects.equals(ce.attacker_source, hero) &&
                ce.targetsBuilding()
        ){
            this.key_bldg_dmg_dealt += ce.value;
        }

        if (
                Objects.equals(ce.type, "kills") &&
                Objects.equals(ce.attacker_source, hero) &&
                ce.targetsBuilding()
        ){
            this.key_bldg_kills += 1;
        }

        if (
                Objects.equals(ce.type, "purchase") &&
                Objects.equals(ce.target, hero)
        ){
            this.item_buys += 1;
        }
        return this;
    }

    public CombatSeries add(CombatSeries b){
        if (Math.abs(this.offset_time - b.offset_time) > 1 || Math.abs(this.time - b.time) > 1) throw new IllegalArgumentException();
        CombatSeries c = new CombatSeries(this.time, this.offset_time);
        c.all_income = this.all_income + b.all_income;
        c.item_buys = this.item_buys + b.item_buys;
        c.healing = this.healing + b.healing;
        c.other_dmg_dealt = this.other_dmg_dealt + b.other_dmg_dealt;
        c.key_bldg_kills = this.key_bldg_kills + b.key_bldg_kills;
        c.creep_kill_income = this.creep_kill_income + b.creep_kill_income;
        c.other_dmg_taken = this.other_dmg_taken + b.other_dmg_taken;
        c.buyback_expense = this.buyback_expense + b.buyback_expense;
        c.death_expense = this.death_expense + b.death_expense;
        c.roshan_kill_income = this.roshan_kill_income + b.roshan_kill_income;
        c.xp = this.xp + b.xp;
        c.building_income = this.building_income + b.building_income;
        c.hero_dmg_taken = this.hero_dmg_taken + b.hero_dmg_taken;
        c.kills = this.kills + b.kills;
        c.hero_dmg_dealt = this.hero_dmg_dealt + b.hero_dmg_dealt;
        c.deaths = this.deaths + b.deaths;
        c.hero_kill_income = this.hero_kill_income + b.hero_kill_income;
        c.hero_xp = this.hero_xp + b.hero_xp;
        c.roshan_xp = this.roshan_xp + b.roshan_xp;
        c.creep_xp = this.creep_xp + b.creep_xp;
        c.last_hits = this.last_hits + b.last_hits;
        c.courier_kill_income = this.courier_kill_income + b.courier_kill_income;
        c.earned_income = this.earned_income + b.earned_income;
        c.key_bldg_dmg_dealt = this.key_bldg_dmg_dealt + b.key_bldg_dmg_dealt;
        return c;
    }
    public CombatSeries subtract(CombatSeries b){
        if (this.offset_time != b.offset_time || this.time != b.time) throw new IllegalArgumentException();
        CombatSeries c = new CombatSeries(this.time, this.offset_time);
        c.all_income = this.all_income - b.all_income;
        c.item_buys = this.item_buys - b.item_buys;
        c.healing = this.healing - b.healing;
        c.other_dmg_dealt = this.other_dmg_dealt - b.other_dmg_dealt;
        c.key_bldg_kills = this.key_bldg_kills - b.key_bldg_kills;
        c.creep_kill_income = this.creep_kill_income - b.creep_kill_income;
        c.other_dmg_taken = this.other_dmg_taken - b.other_dmg_taken;
        c.buyback_expense = this.buyback_expense - b.buyback_expense;
        c.death_expense = this.death_expense - b.death_expense;
        c.roshan_kill_income = this.roshan_kill_income - b.roshan_kill_income;
        c.xp = this.xp - b.xp;
        c.building_income = this.building_income - b.building_income;
        c.hero_dmg_taken = this.hero_dmg_taken - b.hero_dmg_taken;
        c.kills = this.kills - b.kills;
        c.hero_dmg_dealt = this.hero_dmg_dealt - b.hero_dmg_dealt;
        c.deaths = this.deaths - b.deaths;
        c.hero_kill_income = this.hero_kill_income - b.hero_kill_income;
        c.hero_xp = this.hero_xp - b.hero_xp;
        c.roshan_xp = this.roshan_xp - b.roshan_xp;
        c.creep_xp = this.creep_xp - b.creep_xp;
        c.last_hits = this.last_hits - b.last_hits;
        c.courier_kill_income = this.courier_kill_income - b.courier_kill_income;
        c.earned_income = this.earned_income - b.earned_income;
        c.key_bldg_dmg_dealt = this.key_bldg_dmg_dealt - b.key_bldg_dmg_dealt;
        return c;
    }

    public boolean equals(CombatSeries b){
        return Objects.equals(this.all_income, b.all_income) &&
        Objects.equals(this.item_buys, b.item_buys) &&
        Objects.equals(this.healing, b.healing) &&
        Objects.equals(this.other_dmg_dealt, b.other_dmg_dealt) &&
        Objects.equals(this.key_bldg_kills, b.key_bldg_kills) &&
        Objects.equals(this.creep_kill_income, b.creep_kill_income) &&
        Objects.equals(this.other_dmg_taken, b.other_dmg_taken) &&
        Objects.equals(this.buyback_expense, b.buyback_expense) &&
        Objects.equals(this.death_expense, b.death_expense) &&
        Objects.equals(this.roshan_kill_income, b.roshan_kill_income) &&
        Objects.equals(this.xp, b.xp) &&
        Objects.equals(this.building_income, b.building_income) &&
        Objects.equals(this.hero_dmg_taken, b.hero_dmg_taken) &&
        Objects.equals(this.kills, b.kills) &&
        Objects.equals(this.hero_dmg_dealt, b.hero_dmg_dealt) &&
        Objects.equals(this.deaths, b.deaths) &&
        Objects.equals(this.hero_kill_income, b.hero_kill_income) &&
        Objects.equals(this.hero_xp, b.hero_xp) &&
        Objects.equals(this.roshan_xp, b.roshan_xp) &&
        Objects.equals(this.creep_xp, b.creep_xp) &&
        Objects.equals(this.last_hits, b.last_hits) &&
        Objects.equals(this.courier_kill_income, b.courier_kill_income) &&
        Objects.equals(this.earned_income, b.earned_income) &&
        Objects.equals(this.key_bldg_dmg_dealt, b.key_bldg_dmg_dealt);
    }

    @Override
    public String toString() {
        return "CombatSeries{" +
                "all_income=" + all_income +
                ", item_buys=" + item_buys +
                ", healing=" + healing +
                ", other_dmg_dealt=" + other_dmg_dealt +
                ", key_bldg_kills=" + key_bldg_kills +
                ", creep_kill_income=" + creep_kill_income +
                ", other_dmg_taken=" + other_dmg_taken +
                ", time=" + time +
                ", buyback_expense=" + buyback_expense +
                ", death_expense=" + death_expense +
                ", roshan_kill_income=" + roshan_kill_income +
                ", xp=" + xp +
                ", building_income=" + building_income +
                ", hero_dmg_taken=" + hero_dmg_taken +
                ", kills=" + kills +
                ", hero_dmg_dealt=" + hero_dmg_dealt +
                ", deaths=" + deaths +
                ", hero_kill_income=" + hero_kill_income +
                ", hero_xp=" + hero_xp +
                ", roshan_xp=" + roshan_xp +
                ", creep_xp=" + creep_xp +
                ", last_hits=" + last_hits +
                ", courier_kill_income=" + courier_kill_income +
                ", earned_income=" + earned_income +
                ", key_bldg_dmg_dealt=" + key_bldg_dmg_dealt +
                ", offset_time=" + offset_time +
                '}';
    }
}
