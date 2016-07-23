/**
 * An interfacing class to AMQP for the worker.
 *
 * Basically knows how to get/send responses.  Worker is responsible for constantly checking for new responses and
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


    public QueueManager() {
        // Initialize based on CLOUDAMQP_URL etc.
    }

    /**
     * Stringify the AMQP message into a struct with filename and match id.
     *
     * @return POJO for Worker.
     */
    public MatchRequest getResp(){

    }

    /**
     * Send back to RabbitMQ whatever the parser said about its job.  Usually a match ID and filename on s3,
     * sometimes error data.
     *
     * @param filename filename on s3 or magic error codes
     * @param match_id match ID the file corresponds to.
     */
    public void sendResp(String filename, String match_id){
    }
}
