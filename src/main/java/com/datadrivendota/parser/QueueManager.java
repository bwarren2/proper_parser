package com.datadrivendota.parser;

import com.google.gson.Gson;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.QueueingConsumer;
import com.rabbitmq.client.impl.AMQImpl;

import java.io.IOException;
import java.math.BigInteger;

import static com.rabbitmq.client.QueueingConsumer.*;

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

    private Channel channel;
    private QueueingConsumer consumer;
    private String queueName = null;
    private Delivery delivery = null;

    public QueueManager() {

        try {
            Connection connection = this.getConnection();
            this.channel = connection.createChannel();
            this.channel.exchangeDeclare(LISTEN_EXCHANGE, "direct");
            this.queueName = this.channel.queueDeclare(
                    LISTEN_QUEUE,  DURABLE, PASSIVE, EXCLUSIVE, null
            ).getQueue();
            this.channel.queueBind(this.queueName, LISTEN_EXCHANGE, LISTEN_EXCHANGE);
            this.channel.basicQos(3);
            this.consumer = new QueueingConsumer(channel);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Stringify the AMQP message into a struct with filename and match id.
     *
     * @return POJO for com.datadrivendota.parser.Worker.
     */
    public MatchRequest getResp() throws Exception{

        this.channel.basicConsume(this.queueName, false, this.consumer);

        this.delivery = this.consumer.nextDelivery();
        String message = new String(delivery.getBody(),"UTF-8");
        Gson gson = new Gson();
        MatchRequest req = gson.fromJson(message, MatchRequest.class);
        System.out.println(" [x] Received '" + req.getUrl()+ "' (M#"+req.getMatch_id().toString() + ")");
        return req;
    }

    public MatchRequest fakeGetResp() throws Exception{

        MatchRequest req = new MatchRequest("http://replay113.valve.net/570/2549583869_1329696221.dem.bz2", new BigInteger("2549583869"));
        System.out.println(" [x] Received '" + req.getUrl()+ "' (M#"+req.getMatch_id().toString() + ")");
        return req;
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

        Connection connection = this.getConnection();
        Channel channel = connection.createChannel();

        channel.exchangeDeclare(SEND_EXCHANGE, "direct", true);
        String queueName = channel.queueDeclare(
                SEND_QUEUE, DURABLE, PASSIVE, EXCLUSIVE, null
        ).getQueue();
        channel.queueBind(queueName, SEND_EXCHANGE, SEND_EXCHANGE);

        MatchResponse msgObj = new MatchResponse(
                filename,
                new BigInteger(String.valueOf(match_id))
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

    public void ackDelivery() {
        try {
            this.channel.basicAck(this.delivery.getEnvelope().getDeliveryTag(), false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Connection getConnection() throws Exception{
        ConnectionFactory factory = new ConnectionFactory();
        factory.setUri(System.getenv("CLOUDAMQP_URL"));
        factory.setAutomaticRecoveryEnabled(true);
        System.out.println(System.getenv("CLOUDAMQP_URL"));
        Connection connection = factory.newConnection();
        return connection;

    }
}