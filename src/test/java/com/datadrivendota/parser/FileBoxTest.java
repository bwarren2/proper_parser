package com.datadrivendota.parser;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.net.SyslogAppender;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStreamReader;
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
    public void uploadS3() throws Exception {
        String str = "Hello my name is your brother.";
        FileBox box = new FileBox();
        byte[] compressedBytes = box.gzipString(str);
        // box.writeS3("foo.gz", compressedBytes);
        // Expect something at https://s3.amazonaws.com/datadrivendota/raw_replay_parse/foo.gz
    }



    // Note: can use getters/setters as well; here we just use public fields directly:
}