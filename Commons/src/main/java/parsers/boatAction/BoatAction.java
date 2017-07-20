package parsers.boatAction;

/**
 * Created by abu59 on 17/07/17.
 */
public class BoatAction {
    private int sourceID;
    private String action;


    /**
     * Constructs a boat data read from a data source
     * @param action String
     */
    BoatAction(String action) {
        //this.sourceID = sourceID;
        this.action = action;
    }

    public String getAction() { return action; }




}
