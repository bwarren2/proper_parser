/**
 *
 *
 * Created by ben on 7/22/16.
 */
public class Replay {

    private String url;
    private String filename;

    public String getFilename() {
        return filename;
    }

    /**
     * On getting a url, download and unzip the file.
     *
     * @param url the Valve CDN replay file url
     */
    public Replay(String url) {
        this.url= url;
        this.getFile();
    }

    /**
     * Make the file at the given URL local
     */
    private void getFile(){
        // Get the file,
        // unzip it,
        // delete the zipped file for space
        // save the unzipped filename to to this.filename
    }

    /**
     * Remove the local file.
     */
    public void purgeFile(){
        // Delete the replay file.
    }

}
