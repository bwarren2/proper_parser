package com.datadrivendota.parser;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.*;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigInteger;
import java.net.SocketTimeoutException;

/**
 * The major business.  Listens to the rabbit queue for work tickets and delegates to other classes for data transforms.
 *
 * Created by ben on 7/22/16.
 */
public class Worker {

    private final static String LISTEN_QUEUE = "java_parse";
    private final static String LISTEN_EXCHANGE = "java_parse";
    private final static String SEND_QUEUE = "python_parse";
    private final static String SEND_EXCHANGE = "python_parse";
    private final static boolean PASSIVE = false;
    private final static boolean DURABLE = true;
    private final static boolean EXCLUSIVE = false;

    public static void main(String[] args) throws Exception {

        try {
            ConnectionFactory factory = new ConnectionFactory();
            factory.setUri(System.getenv("CLOUDAMQP_URL"));
            factory.setAutomaticRecoveryEnabled(true);
            System.out.println("Registering with "+System.getenv("CLOUDAMQP_URL"));
            Connection connection = factory.newConnection();

            final Channel channel = connection.createChannel();
            channel.exchangeDeclare(LISTEN_EXCHANGE, "direct");
            String queueName = channel.queueDeclare(
                    LISTEN_QUEUE, DURABLE, PASSIVE, EXCLUSIVE, null
            ).getQueue();
            channel.queueBind(queueName, LISTEN_EXCHANGE, LISTEN_EXCHANGE);
            channel.basicQos(3);
            Consumer consumer = new DefaultConsumer(channel) {
                @Override
                public void handleDelivery(
                        String consumerTag,
                        Envelope envelope,
                        AMQP.BasicProperties properties,
                        byte[] body
                )
                throws IOException {
                    String message = new String(body, "UTF-8");
                    String output_msg = new String();
                    System.out.println(" [x] Received '" + message + "'");

                    ObjectMapper om = new ObjectMapper();
                    MatchRequest request = om.readValue(message, MatchRequest.class);
                    System.out.println(" [x] Received '" + request.getUrl()+ "' (M#"+request.getMatch_id().toString() + ")");
                    long deliveryTag = envelope.getDeliveryTag();
                    if(request!=null) try {
                        FileBox filebox = new FileBox();
                        Replay replay = new Replay(request.getUrl());
                        Parser parser = new Parser(replay);

                        filebox = parser.run(filebox);  // Rip apart the replay and populate the filebox.
                        replay.purgeFile();  // Remove the local replay file.
                        filebox.setMatch_id(request.getMatch_id());
                        filebox.handle();
                        filebox.uploadFiles();

                        channel.basicAck(deliveryTag, false);
                        output_msg = "Done";
                    } catch (FileNotFoundException | SocketTimeoutException e) {
                        System.out.println("Exception!");
                        e.printStackTrace();
                        System.out.println("/Exception!");
                        output_msg = "notfound";
                    } catch (IndexOutOfBoundsException e) {
                        System.out.println("Exception!");
                        e.printStackTrace();
                        System.out.println("/Exception!");
                        output_msg = "Oddball error";
                    }
                    try {
                        sendResp(output_msg, request.getMatch_id(), channel);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    channel.basicAck(deliveryTag, false);

                }
            };
            channel.basicConsume(LISTEN_QUEUE, true, consumer);
            System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void sendResp(String msg, BigInteger match_id, Channel channel) throws Exception {

        System.out.println("Sending response back to queue");
        MatchResponse msgObj = new MatchResponse(msg, new BigInteger(String.valueOf(match_id)));
        ObjectMapper om = new ObjectMapper();
        String json = om.writeValueAsString(msgObj);
        channel.basicPublish(SEND_EXCHANGE, SEND_QUEUE, null, json.getBytes("UTF-8"));
        System.out.println(" [x] Sent '" + json + "'");

    }


}
