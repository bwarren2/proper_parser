package com.datadrivendota.parser;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.FileUtils;
import ch.qos.logback.classic.net.SyslogAppender;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.util.zip.GZIPInputStream;

import static org.junit.Assert.*;

/**
 * Created by ben on 8/6/16.
 */
public class FileBoxTest {
    @Test
    public void gzipString() throws Exception {
        String str = "Hello my name is your brother.";
        FileBox box = new FileBox();
        byte[] compressedBytes = box.gzipString(str);

        // FileUtils.writeByteArrayToFile(new File("foo.gz"), compressedBytes );

        GZIPInputStream gis = new GZIPInputStream(new ByteArrayInputStream(compressedBytes ));
        BufferedReader bf = new BufferedReader(new InputStreamReader(gis, "UTF-8"));
        String outStr = "";
        String line;
        while ((line=bf.readLine())!=null) {
            outStr += line;
        }
        assertEquals(str, outStr);
    }

    @Test
    public void makeFilename() throws Exception {

        String expected = "1_all_statelog_128_v2.json.gz";
        FileBox f = new FileBox();
        f.setMatch_id(BigInteger.valueOf(1));
        String actual = f.makeFilename("all", "statelog", "128");
        assertEquals(expected, actual);

        String expected_2 = "2564868930_radiant_statelog_allstate_v2.json.gz";
        FileBox f2 = new FileBox();
        f2.setMatch_id(new BigInteger("2564868930"));
        String actual_2 = f2.makeFilename("radiant", "statelog", "allstate");
        assertEquals(expected_2, actual_2);

    }

    @Test
    public void uploadS3() throws Exception {
        String str = "Hello my name is your brother.";
        FileBox box = new FileBox();
        byte[] compressedBytes = box.gzipString(str);
        // box.writeS3("foo.gz", compressedBytes);
        // Expect something at https://s3.amazonaws.com/datadrivendota/raw_replay_parse/foo.gz
    }

   @Test
    public void testCombat() throws Exception {
       Replay replay = new Replay("test_2549583869.dem");
       replay.setFilename("/home/ben/IdeaProjects/proper_parser/test_2549583869.dem");
       FileBox filebox = new FileBox();
       Parser parser = new Parser(replay);
       parser.run(filebox);
       assertEquals(filebox.stringTable.size(), 533);

       filebox.setMatch_id(new BigInteger("2549583869"));
       filebox.mungeTimes();
       filebox.saveItemBuys();
    }



    // Note: can use getters/setters as well; here we just use public fields directly:
}