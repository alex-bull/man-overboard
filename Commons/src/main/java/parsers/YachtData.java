package parsers;

/**
 * Created by psu43 on 26/04/17.
 * Yacht is given from race.XML as a participant
 */
public class YachtData {
    private int sourceID;
    private String entry;

    public YachtData(int sourceID, String entry) {
        this.sourceID = sourceID;
        this.entry = entry;
    }

    public int getSourceID() {
        return sourceID;
    }

    public void setSourceID(int sourceID) {
        this.sourceID = sourceID;
    }

    public String getEntry() {
        return entry;
    }

    public void setEntry(String entry) {
        this.entry = entry;
    }


}
