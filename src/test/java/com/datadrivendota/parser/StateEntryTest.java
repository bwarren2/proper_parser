package com.datadrivendota.parser;

import org.apache.commons.compress.archivers.Lister;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by ben on 8/12/16.
 */
public class StateEntryTest {
    @Test
    public void equals() throws Exception {
        StateEntry se1 = new StateEntry();
        StateEntry se2 = new StateEntry();
        assertEquals(se1, se2);

        se1.health = 1;
        se2.health = 2;
        assertNotEquals(se1, se2);
    }

    @Test
    public void equalsList() throws Exception {
        StateEntry se1 = new StateEntry();
        StateEntry se2 = new StateEntry();
        se1.health = 1;
        se2.health = 2;
        List<StateEntry> foo = new ArrayList<>();
        foo.add(se1);
        foo.add(se2);

        StateEntry se3 = new StateEntry();
        StateEntry se4 = new StateEntry();
        se3.health = 1;
        se4.health = 2;
        List<StateEntry> bar = new ArrayList<>();
        bar.add(se3);
        bar.add(se4);
        assertEquals(foo, bar);

    }

}