package parsers.xml.race;

/**
 * Created by psu43 on 26/04/17.
 * Yacht is given from race.XML as a participant
 */
public class YachtData {
    private int sourceID;

    /**
     * Yacht data from the race.XML packet (currently only reading sourceID)
     *
     * @param sourceID int Source ID of the yacht
     */
    YachtData(int sourceID) {
        this.sourceID = sourceID;
    }

    public int getSourceID() {
        return sourceID;
    }

    public void setSourceID(int sourceID) {
        this.sourceID = sourceID;
    }


}
