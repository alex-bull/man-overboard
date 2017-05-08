package parsers;

/**
 * Created by Pang on 3/05/17.
 * A competitor from the boat status message
 */
public class BoatStatus {
    private int sourceID;
    private int boatStatus;
    private int legNumber;
    private int penaltiesAwarded;
    private int penaltiesServed;
    private int estimatedTimeAtNextMark;
    private int estimatedTimeAtFinish;

    public BoatStatus(int sourceID, int boatStatus, int legNumber, int penaltiesAwarded, int penaltiesServed, int estimatedTimeAtNextMark, int estimatedTimeAtFinish) {
        this.sourceID = sourceID;
        this.boatStatus = boatStatus;
        this.legNumber = legNumber;
        this.penaltiesAwarded = penaltiesAwarded;
        this.penaltiesServed = penaltiesServed;
        this.estimatedTimeAtNextMark = estimatedTimeAtNextMark;
        this.estimatedTimeAtFinish = estimatedTimeAtFinish;
    }


    public int getSourceID() {
        return sourceID;
    }

    public int getBoatStatus() {
        return boatStatus;
    }

    public int getLegNumber() {
        return legNumber;
    }

    public int getPenaltiesAwarded() {
        return penaltiesAwarded;
    }

    public int getPenaltiesServed() {
        return penaltiesServed;
    }

    public int getEstimatedTimeAtNextMark() {
        return estimatedTimeAtNextMark;
    }

    public int getEstimatedTimeAtFinish() {
        return estimatedTimeAtFinish;
    }

}
