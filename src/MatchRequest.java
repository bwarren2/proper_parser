import java.math.BigInteger;

/**
 * Created by ben on 7/22/16.
 */

public class MatchRequest {
    private String url;
    private BigInteger match_id;

    public String getUrl() {
        return url;
    }

    public BigInteger getMatch_id() {
        return match_id;
    }

    public MatchRequest(String url, BigInteger match_id){
        this.url = url;
        this.match_id = match_id;
    }
}