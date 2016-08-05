package com.datadrivendota.parser;

import com.google.gson.Gson;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.QueueingConsumer;

import java.io.IOException;
import java.math.BigInteger;

/**
 * An interfacing class to RabbitMQ for the worker.
 *
 * Basically knows how to get/send responses.  com.datadrivendota.parser.Worker is responsible for constantly checking for new responses and
 * dealing with them.
 *
 * Created by ben on 7/22/16.
 */
public class QueueManager {

    private final static String LISTEN_QUEUE = "java_parse";
    private final static String LISTEN_EXCHANGE = "java_parse";
    private final static String SEND_QUEUE = "python_parse";
    private final static String SEND_EXCHANGE = "python_parse";
    private final static boolean PASSIVE = false;
    private final static boolean DURABLE = true;
    private final static boolean EXCLUSIVE = false;

    private Channel listen_channel;

    public QueueManager() {

    }

    /**
     * Stringify the AMQP message into a struct with filename and match id.
     *
     * @return POJO for com.datadrivendota.parser.Worker.
     */
    public MatchRequest getResp() throws Exception{


        ConnectionFactory factory = new ConnectionFactory();
        factory.setUri(System.getenv("CLOUDAMQP_URL"));
        factory.setAutomaticRecoveryEnabled(true);
        System.out.println(System.getenv("CLOUDAMQP_URL"));
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.exchangeDeclare(LISTEN_EXCHANGE, "direct");

        String queueName = channel.queueDeclare(
                LISTEN_QUEUE,  DURABLE, PASSIVE, EXCLUSIVE, null
        ).getQueue();


        channel.queueBind(queueName, LISTEN_EXCHANGE, LISTEN_EXCHANGE);
        channel.basicQos(3);
        System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

        QueueingConsumer consumer = new QueueingConsumer(channel);
        boolean autoAck = false;
        channel.basicConsume(queueName, autoAck, consumer);

        while (true) {
            QueueingConsumer.Delivery delivery = consumer.nextDelivery();
            String message = new String(delivery.getBody(),"UTF-8");
            Gson gson = new Gson();
            MatchRequest req = gson.fromJson(message, MatchRequest.class);
            System.out.println(" [x] Received '" + req.url + "' (M#"+req.match_id.toString() + ")");
            new Main().parseID(req.url, req.match_id.toString());
            channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
        }

    }

    /**
     * Send back to RabbitMQ whatever the parser said about its job.  Usually a match ID and filename on s3,
     * sometimes error data.
     *
     * @param filename filename on s3 or magic error codes
     * @param match_id match ID the file corresponds to.
     */
    public void sendResp(String filename, BigInteger match_id) throws Exception {

        System.out.println("Sending response back to queue");

        ConnectionFactory factory = new ConnectionFactory();
        factory.setUri(System.getenv("CLOUDAMQP_URL"));
        factory.setAutomaticRecoveryEnabled(true);
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.exchangeDeclare(SEND_EXCHANGE, "direct", true);
        String queueName = channel.queueDeclare(
                SEND_QUEUE, DURABLE, PASSIVE, EXCLUSIVE, null
        ).getQueue();
        channel.queueBind(queueName, SEND_EXCHANGE, SEND_EXCHANGE);

        MatchResponse msgObj = new MatchResponse(
                filename,
                new BigInteger(match_id)
        );
        Gson gson = new Gson();
        String json = gson.toJson(msgObj);
        channel.basicPublish(
                SEND_EXCHANGE, SEND_QUEUE, null, json.getBytes("UTF-8")
        );
        channel.close();
        connection.close();

        System.out.println(" [x] Sent '" + json + "'");


    }
}