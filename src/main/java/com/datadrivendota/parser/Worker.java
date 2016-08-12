package com.datadrivendota.parser;

/**
 * The major business.  Listens to the rabbit queue for work tickets and delegates to other classes for data transforms.
 *
 * Created by ben on 7/22/16.
 */
public class Worker {

    public static void main(String[] args) throws Exception {
        QueueManager manager = new QueueManager();
        System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

        Integer x = 0;
        while (x<1){
            x = x + 1;
//            MatchRequest request = manager.getResp();
            MatchRequest request = manager.fakeGetResp();
            if(request!=null){
                FileBox filebox = new FileBox();
                Replay replay = new Replay("");
                replay.setFilename("test_2549583869.dem");
                Parser parser = new Parser(replay);

                filebox = parser.run(filebox);  // Rip apart the replay and populate the filebox.
//                replay.purgeFile();  // Remove the local replay file.
                filebox.setMatch_id(request.getMatch_id());
                filebox.handle();
//                filebox.upload();
//
//                manager.sendResp(filename, request.getMatch_id());
//                manager.ackDelivery();
            }
        }
    }
}
