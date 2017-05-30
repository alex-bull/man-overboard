package parsers.raceStatus;

/**
 * Created by Pang on 3/05/17.
 * A competitor from the boat status message
 */
public class BoatStatus {
    private int sourceID;
    private int legNumber;
    private long estimatedTimeAtNextMark;


    /**
     * Boat status
     * @param sourceID int source id
     * @param legNumber int leg number
     * @param estimatedTimeAtNextMark long estimated time at next mark
     */
    BoatStatus(int sourceID, int legNumber, long estimatedTimeAtNextMark) {
        this.sourceID = sourceID;
        this.legNumber = legNumber;
        this.estimatedTimeAtNextMark = estimatedTimeAtNextMark;
    }


    public int getSourceID() {
        return sourceID;
    }

    public int getLegNumber() {
        return legNumber;
    }

    public long getEstimatedTimeAtNextMark() {
        return estimatedTimeAtNextMark;
    }


}
