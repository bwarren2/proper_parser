/**
 * The interface with the parsing library.
 *
 * Instead of the current terrible DS implementation, use the FileBox abstraction.
 *
 * Created by ben on 7/22/16.
 */
public class Parser {
    private Replay replay;

    public Parser(Replay replay) {
        this.replay = replay;
    }

    /**
     *
     * @param filebox the place to stuff data that will get handled & uploaded
     * @return
     */
    public FileBox run(FileBox filebox){
        // Setup business logic from original Parser.run here.
        // Mostly calling `CDemoFileInfo info = Clarity.infoForFile(replay.getFilename());`
        return filebox;
    }

    // The various helper fns and @OnEntities etc logic here
    // Instead of the stateadd etc logic in the original, user handlers on Filebox to add info
}
