package com.datadrivendota.parser;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.*;

/**
 * Created by ben on 8/5/16.
 */
public class ParserTest {
    @Test
    public void run() throws Exception {
        Replay replay = new Replay("test_2549583869.dem");
        replay.setFilename("test_2549583869.dem");
        FileBox filebox = new FileBox();
        Parser parser = new Parser(replay);
        parser.run(filebox);
        assertEquals(filebox.stringTable.size(), 533);

        filebox.tranforms();

        assertEquals(Math.round(filebox.getPlayerFile(0).getStateMap().get(1000).doubleMana()), 224);
        assertEquals(Math.round(filebox.getPlayerFile(1).getStateMap().get(1000).doubleMana()), 67);
        assertEquals(Math.round(filebox.getPlayerFile(2).getStateMap().get(1000).doubleMana()), 280);
        assertEquals(Math.round(filebox.getPlayerFile(3).getStateMap().get(1000).doubleMana()), 341);
        assertEquals(Math.round(filebox.getPlayerFile(4).getStateMap().get(1000).doubleMana()), 314);
        assertEquals(Math.round(filebox.getPlayerFile(5).getStateMap().get(1000).doubleMana()), 53);
        assertEquals(Math.round(filebox.getPlayerFile(6).getStateMap().get(1000).doubleMana()), 325);
        assertEquals(Math.round(filebox.getPlayerFile(7).getStateMap().get(1000).doubleMana()), 181);
        assertEquals(Math.round(filebox.getPlayerFile(8).getStateMap().get(1000).doubleMana()), 266);
        assertEquals(Math.round(filebox.getPlayerFile(9).getStateMap().get(1000).doubleMana()), 201);

        assertEquals(Math.round(filebox.getPlayerFile(9).getStateMap().get(1000).tick_time), 1000);
        assertEquals(Math.round(filebox.getPlayerFile(9).getStateMap().get(1000).offset_time), 291);

        assertEquals(filebox.getPlayerFile(0).getStateMap().get(3587).item_0, "item_phase_boots");
        assertEquals(filebox.getPlayerFile(1).getStateMap().get(3587).item_0, "item_sphere");
        assertEquals(filebox.getPlayerFile(2).getStateMap().get(3587).item_0, "item_force_staff");
        assertEquals(filebox.getPlayerFile(3).getStateMap().get(3587).item_0, "item_smoke_of_deceit");
        assertEquals(filebox.getPlayerFile(4).getStateMap().get(3587).item_0, "item_silver_edge");
        assertEquals(filebox.getPlayerFile(5).getStateMap().get(3587).item_0, "item_octarine_core");
        assertEquals(filebox.getPlayerFile(6).getStateMap().get(3587).item_0, null);
        assertEquals(filebox.getPlayerFile(7).getStateMap().get(3587).item_0, "item_abyssal_blade");
        assertEquals(filebox.getPlayerFile(8).getStateMap().get(3587).item_0, "item_black_king_bar");
        assertEquals(filebox.getPlayerFile(9).getStateMap().get(3587).item_0, "item_magic_wand");

        ObjectMapper om = new ObjectMapper();
        om.writeValue(new File("result.json"), filebox.getPlayerFile(7).getStateMap().get(3587));
    }
}