package com.datadrivendota.parser;

import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;
import java.io.*;
import java.net.SocketTimeoutException;
import java.net.URL;

import static org.apache.commons.io.FileUtils.copyURLToFile;

/**
 *
 *
 * Created by ben on 7/22/16.
 */
public class Replay {

    private String url;

    public String getUrl() {
        return url;
    }

    private String filename;
    private String zip_filename;

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getFilename() {
        return filename;
    }

    /**
     * On getting a url, download and unzip the file.
     *
     * @param url the Valve CDN replay file url
     */
    public Replay(String url) throws IOException {
        this.url = url;
        this.getFile(this.url);
    }

    public Replay() {}

    /**
     * Make the file at the given URL local and unzipped.  Probably will either get rolled into the constructor
     * or broken into more parts for testing.
     */
    private void getFile(String url) throws IOException, SocketTimeoutException, FileNotFoundException{

        URL query_url = new URL(url);

        String[] separated = url.split("/");
        String replay_name = separated[4];
        String[] match_num = replay_name.split("\\_");
        String[] split_ext = replay_name.split("\\.");
        String file_extension = split_ext[1]+'.'+split_ext[2];
        String filename = match_num[0] +'.'+ file_extension;

        System.out.println("Getting file data: " + filename);

        File destination = new File(filename);
        copyURLToFile(query_url, destination, 2000, 2000);

        System.out.println("Got File");

        String[] pieces = filename.split("\\.");
        String output_name = pieces[0] +'.'+ pieces[1];
        System.out.println("Unzipping to: "+output_name);

        this.zip_filename = filename;
        FileInputStream fin = new FileInputStream(filename);
        BufferedInputStream in = new BufferedInputStream(fin);
        FileOutputStream out = new FileOutputStream(output_name);
        BZip2CompressorInputStream bzIn = new BZip2CompressorInputStream(in);
        final byte[] buffer = new byte[256];

        int bytes_read;
        while ((bytes_read = bzIn.read(buffer)) > 0) {
            out.write(buffer, 0, bytes_read);
        }

        out.close();
        bzIn.close();

        System.out.println("Unzipped");
        deleteFile(filename);

        System.out.println("Unzipped");
        this.setFilename(output_name);

    }

    /**
     * Remove the local files.
     * To recover from potential file management errors, we flexibly delete and call this in multiple places.
     * (Why not just call at the end?  Trying to minimize memory overhead.
     */
    public void purgeFiles(){
        if (this.filename!=null) deleteFile(this.filename);
        if (this.zip_filename!=null) deleteFile(this.zip_filename);
    }
    private void deleteFile(String filename){
        File file = new File(filename);

        boolean status = file.delete();
        if (status)
            System.out.println("Deleted "+filename);
        else
            System.out.println(filename+" does not exists to delete");

    }

}
