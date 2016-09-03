package com.datadrivendota.parser;

import java.math.BigInteger;

/**
 * Dopey helper class to send messages back to RabbitMQ.
 *
 * The API on those messages will need to change when the postprocessing moves over here.
 *
 * Created by ben on 7/22/16.
 */
public class MatchResponse {
    public String msg;
    public BigInteger match_id;

    public MatchResponse(String filename, BigInteger match_id){
        this.msg = filename;
        this.match_id = match_id;
    }
}
