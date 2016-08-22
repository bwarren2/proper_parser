package com.datadrivendota.parser;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by ben on 8/22/16.
 */
public class CombatSeriesTest {

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
    }}