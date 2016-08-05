package com.datadrivendota.parser;

import org.junit.Test;

import java.io.File;

import static org.junit.Assert.*;

/**
 * Created by ben on 8/5/16.
 */
public class ParserTest {
    @Test
    public void run() throws Exception {
        Replay replay = new Replay("2549583869.dem");
        replay.setFilename("2549583869.dem");
        FileBox filebox = new FileBox();
        Parser parser = new Parser(replay);
        parser.run(filebox);
        assertEquals(filebox.getStringTableEntries().size(), 806);
    }

}