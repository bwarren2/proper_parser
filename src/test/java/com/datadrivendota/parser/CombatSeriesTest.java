package com.datadrivendota.parser;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.Assert.*;

/**
 * Created by ben on 8/22/16.
 */
public class CombatSeriesTest {
    String hero = "npc_dota_hero_wukong";
    String neutral = "wildkin";
    String enemy = "npc_dota_hero_axe";
    private List<String> enemies = asList(
        "npc_dota_hero_axe",
        "npc_dota_hero_abaddon",
        "npc_dota_hero_antimage",
        "npc_dota_hero_alchemist",
        "npc_dota_hero_ancient_apparition"
    );
    private List<String> allies = asList(
        "npc_dota_hero_wukong",
        "npc_dota_hero_weaver",
        "npc_dota_hero_warlock",
        "npc_dota_hero_wraith_king",
        "npc_dota_hero_winter_wyvern"
    );


    @Test
    public void testEquals() throws Exception {
        CombatSeries a = new CombatSeries(10, 0);
        CombatSeries b = new CombatSeries(10, 0);
        assertEquals(true, a.equals(b));
    }

    @Test
    public void testAdd() throws Exception {
        CombatSeries a = new CombatSeries(10, 0);
        a.all_income = 10;
        CombatSeries b = new CombatSeries(10, 0);
        b.all_income = 20;
        CombatSeries c = new CombatSeries(10, 0);
        c.all_income = 30;
        assertEquals(c.all_income, a.add(b).all_income);
    }

    @Test
    public void testSubtract() throws Exception {
        CombatSeries a = new CombatSeries(10, 0);
        a.all_income = 10;
        CombatSeries b = new CombatSeries(10, 0);
        b.all_income = 20;
        CombatSeries c = new CombatSeries(10, 0);
        c.all_income = 30;
        assertEquals(a.all_income, c.subtract(b).all_income);
    }

    @Test
    public void testKillsUpdate() throws Exception {
        CombatSeries a = new CombatSeries(10, 0);
        CombatEntry ce = new CombatEntry(10);
        ce.type = "kills";
        ce.target_illusion = false;
        ce.target_hero = true;

        ce.attacker_source = this.hero;
        ce.target = this.enemy;
        a.update(ce, this.hero, this.enemies, this.allies);
        assertEquals(1, (int) a.kills);
    }

    @Test
    public void testDeathsUpdate() throws Exception {
        CombatSeries a = new CombatSeries(10, 0);
        CombatEntry ce = new CombatEntry(10);
        ce.type = "kills";
        ce.target_illusion = false;
        ce.target_hero = true;


        ce.attacker_source = this.enemy;
        ce.target = this.hero;
        a.update(ce, this.hero, this.enemies, this.allies);
        assertEquals(1, (int) a.deaths);
    }

    @Test
    public void testLastHits() throws Exception {
        CombatSeries a = new CombatSeries(10, 0);
        CombatEntry ce = new CombatEntry(10);
        ce.type = "kills";
        ce.target_illusion = false;
        ce.target_hero = false;

        ce.attacker_source = this.hero;
        ce.target = this.neutral;
        a.update(ce, this.hero, this.enemies, this.allies);
        assertEquals(1, (int) a.last_hits);
    }

    @Test
    public void testXp() throws Exception {
        CombatSeries a = new CombatSeries(10, 0);
        CombatEntry ce = new CombatEntry(10);
        ce.type = "xp_reasons";
        ce.target_illusion = false;
        ce.target_hero = true;
        ce.value = 15;


        ce.target = this.hero;
        a.update(ce, this.hero, this.enemies, this.allies);
        assertEquals(15, (int) a.xp);
    }

    @Test
    public void testHealing() throws Exception {
        CombatSeries a = new CombatSeries(10, 0);
        CombatEntry ce = new CombatEntry(10);
        ce.type = "healing";
        ce.target_illusion = false;
        ce.target_hero = true;
        ce.value = 15;


        ce.attacker_source = this.hero;
        ce.target = this.allies.get(0);
        a.update(ce, this.hero, this.enemies, this.allies);
        assertEquals(15, (int) a.healing);
    }

    @Test
    public void testHeroDmgTaken() throws Exception {
        CombatSeries a = new CombatSeries(10, 0);
        CombatEntry ce = new CombatEntry(10);
        ce.type = "damage";
        ce.target_illusion = false;
        ce.target_hero = true;
        ce.value = 10;

        ce.attacker_name = this.enemy;
        ce.target = this.hero;
        a.update(ce, this.hero, this.enemies, this.allies);
        assertEquals(10, (int) a.hero_dmg_taken);
    }

    @Test
    public void testHeroDmgDealt() throws Exception {
        CombatSeries a = new CombatSeries(10, 0);
        CombatEntry ce = new CombatEntry(10);
        ce.type = "damage";
        ce.target_illusion = false;
        ce.target_hero = true;
        ce.value = 7;

        ce.attacker_name = this.hero;
        ce.target = this.enemy;
        a.update(ce, this.hero, this.enemies, this.allies);
        assertEquals(7, (int) a.hero_dmg_dealt);
    }

    @Test
    public void testOtherDmgTaken() throws Exception {
        CombatSeries a = new CombatSeries(10, 0);
        CombatEntry ce = new CombatEntry(10);
        ce.type = "damage";
        ce.target_illusion = false;
        ce.target_hero = true;
        ce.value = 6;

        ce.attacker_name = this.neutral;
        ce.target = this.hero;
        a.update(ce, this.hero, this.enemies, this.allies);
        assertEquals(6, (int) a.other_dmg_taken);
    }

    @Test
    public void testOtherDmgDealt() throws Exception {
        CombatSeries a = new CombatSeries(10, 0);
        CombatEntry ce = new CombatEntry(10);
        ce.type = "damage";
        ce.target_illusion = false;
        ce.target_hero = true;
        ce.value = 7;

        ce.attacker_name = this.hero;
        ce.target = this.neutral;
        a.update(ce, this.hero, this.enemies, this.allies);
        assertEquals(7, (int) a.other_dmg_dealt);
    }

    @Test
    public void testIncome() throws Exception {
        CombatSeries a = new CombatSeries(10, 0);
        CombatEntry ce = new CombatEntry(10);
        ce.type = "gold_reasons";
        ce.value = 2;

        ce.target = this.hero;
        a.update(ce, this.hero, this.enemies, this.allies);
        assertEquals(2, (int) a.all_income);
    }

    @Test
    public void testEarnedIncome() throws Exception {
        CombatSeries a = new CombatSeries(10, 0);
        CombatEntry ce = new CombatEntry(10);
        ce.type = "gold_reasons";
        ce.value = 3;
        ce.key= "11";

        ce.target = this.hero;
        a.update(ce, this.hero, this.enemies, this.allies);
        assertEquals(3, (int) a.earned_income);
    }

    @Test
    public void testBuildingIncome() throws Exception {
        CombatSeries a = new CombatSeries(10, 0);
        CombatEntry ce = new CombatEntry(10);
        ce.type = "gold_reasons";
        ce.value = 12;
        ce.key= "11";

        ce.target = this.hero;
        a.update(ce, this.hero, this.enemies, this.allies);
        assertEquals(12, (int) a.building_income);
    }
    @Test
    public void testCourierKillIncome() throws Exception {
        CombatSeries a = new CombatSeries(10, 0);
        CombatEntry ce = new CombatEntry(10);
        ce.type = "gold_reasons";
        ce.value = 150;
        ce.key= "15";

        ce.target = this.hero;
        a.update(ce, this.hero, this.enemies, this.allies);
        assertEquals(150, (int) a.courier_kill_income);
    }

    @Test
    public void testCreepKillIncome() throws Exception {
        CombatSeries a = new CombatSeries(10, 0);
        CombatEntry ce = new CombatEntry(10);
        ce.type = "gold_reasons";
        ce.value = 23;
        ce.key= "13";

        ce.target = this.hero;
        a.update(ce, this.hero, this.enemies, this.allies);
        assertEquals(23, (int) a.creep_kill_income);
    }

    @Test
    public void testHeroKillIncome() throws Exception {
        CombatSeries a = new CombatSeries(10, 0);
        CombatEntry ce = new CombatEntry(10);
        ce.type = "gold_reasons";
        ce.value = 120;
        ce.key= "12";

        ce.target = this.hero;
        a.update(ce, this.hero, this.enemies, this.allies);
        assertEquals(120, (int) a.hero_kill_income);
    }

    @Test
    public void testRoshanKillIncome() throws Exception {
        CombatSeries a = new CombatSeries(10, 0);
        CombatEntry ce = new CombatEntry(10);
        ce.type = "gold_reasons";
        ce.value = 200;
        ce.key= "14";

        ce.target = this.hero;
        a.update(ce, this.hero, this.enemies, this.allies);
        assertEquals(200, (int) a.roshan_kill_income);
    }

    @Test
    public void testBuybackExpense() throws Exception {
        CombatSeries a = new CombatSeries(10, 0);
        CombatEntry ce = new CombatEntry(10);
        ce.type = "gold_reasons";
        ce.value = -100;
        ce.key= "2";

        ce.target = this.hero;
        a.update(ce, this.hero, this.enemies, this.allies);
        assertEquals(-100, (int) a.buyback_expense);
    }

    @Test
    public void testDeathExpense() throws Exception {
        CombatSeries a = new CombatSeries(10, 0);
        CombatEntry ce = new CombatEntry(10);
        ce.type = "gold_reasons";
        ce.value = -80;
        ce.key= "1";

        ce.target = this.hero;
        a.update(ce, this.hero, this.enemies, this.allies);
        assertEquals(-80, (int) a.death_expense);
    }

    @Test
    public void testHeroXp() throws Exception {
        CombatSeries a = new CombatSeries(10, 0);
        CombatEntry ce = new CombatEntry(10);
        ce.type = "xp_reasons";
        ce.value = 1;
        ce.key= "1";

        ce.target = this.hero;
        a.update(ce, this.hero, this.enemies, this.allies);
        assertEquals(1, (int) a.hero_xp);
    }

    @Test
    public void testCreepXp() throws Exception {
        CombatSeries a = new CombatSeries(10, 0);
        CombatEntry ce = new CombatEntry(10);
        ce.type = "xp_reasons";
        ce.value = 2;
        ce.key= "2";

        ce.target = this.hero;
        a.update(ce, this.hero, this.enemies, this.allies);
        assertEquals(2, (int) a.creep_xp);
    }

    @Test
    public void testRoshanXp() throws Exception {
        CombatSeries a = new CombatSeries(10, 0);
        CombatEntry ce = new CombatEntry(10);
        ce.type = "xp_reasons";
        ce.value = 3;
        ce.key= "3";

        ce.target = this.hero;
        a.update(ce, this.hero, this.enemies, this.allies);
        assertEquals(3, (int) a.roshan_xp);
    }

    @Test
    public void testKeyBldgDmgDealt() throws Exception {
        CombatSeries a = new CombatSeries(10, 0);
        CombatEntry ce = new CombatEntry(10);
        ce.type = "damage";
        ce.value = 3;

        ce.attacker_source = this.hero;
        ce.target = ce.radiant_key_bldgs.get(0);
        a.update(ce, this.hero, this.enemies, this.allies);
        assertEquals(3, (int) a.key_bldg_dmg_dealt);
    }

    @Test
    public void testKeyBldgKills() throws Exception {
        CombatSeries a = new CombatSeries(10, 0);
        CombatEntry ce = new CombatEntry(10);
        ce.type = "kills";
        ce.target_hero = false;

        ce.target = ce.radiant_key_bldgs.get(0);
        ce.attacker_source = this.hero;
        a.update(ce, this.hero, this.enemies, this.allies);
        assertEquals(1, (int) a.key_bldg_kills);
    }

    @Test
    public void testItemBuys() throws Exception {
        CombatSeries a = new CombatSeries(10, 0);
        CombatEntry ce = new CombatEntry(10);
        ce.type = "purchase";

        ce.target = this.hero;
        a.update(ce, this.hero, this.enemies, this.allies);
        assertEquals(1, (int) a.item_buys);
    }
}