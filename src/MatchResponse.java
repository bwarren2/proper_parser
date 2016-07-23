import java.math.BigInteger;

/**
 * Created by ben on 7/22/16.
 */
public class MatchResponse {
    private String filename;
    private BigInteger match_id;

    public MatchResponse(String filename, BigInteger match_id){
        this.filename = filename;
        this.match_id = match_id;
    }
}
