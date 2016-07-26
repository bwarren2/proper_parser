package com.datadrivendota.parser;

/**
 * The major business.  Listens to the rabbit queue for work tickets and delegates to other classes for data transforms.
 *
 * Created by ben on 7/22/16.
 */
public class Worker {

    public static void main(String[] args) {
        QueueManager manager = new QueueManager();
        while (true){
            MatchRequest request = manager.getResp();
            if(request!=null){
                FileBox filebox = new FileBox();
                Replay replay = new Replay(request.getUrl());
                Parser parser = new Parser(replay);

                parser.run(filebox);  // Rip apart the replay and populate the filebox.
                replay.purgeFile();  // Remove the local replay file.

                // Renames required.  Old system passed one file, we'll only pass success/fail codes
                String filename = filebox.handle();

                manager.sendResp(filename, request.getMatch_id());

            }
        }
    }
}
