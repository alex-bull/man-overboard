package parsers.raceStatus;

import parsers.BoatStatusEnum;

/**
 * Created by Pang on 3/05/17.
 * A competitor from the boat status message
 */
public class BoatStatus {
    private int sourceID;
    private BoatStatusEnum boatStatus;
    private int legNumber;
    private long estimatedTimeAtNextMark;

    /**
     * Boat status
     *
     * @param sourceID                int source id
     * @param boatStatus              enum boat status
     * @param legNumber               int leg number
     * @param estimatedTimeAtNextMark long estimated time at next mark
     */
    BoatStatus(int sourceID, BoatStatusEnum boatStatus, int legNumber, long estimatedTimeAtNextMark) {
        this.sourceID = sourceID;
        this.boatStatus = boatStatus;
        this.legNumber = legNumber;
        this.estimatedTimeAtNextMark = estimatedTimeAtNextMark;
    }

    public void setBoatStatus(BoatStatusEnum boatStatus) {
        this.boatStatus = boatStatus;
    }

    public void setLegNumber(int legNumber) {
        this.legNumber = legNumber;
    }

    public void setEstimatedTimeAtNextMark(long estimatedTimeAtNextMark) {
        this.estimatedTimeAtNextMark = estimatedTimeAtNextMark;
    }

    public int getSourceID() {
        return sourceID;
    }

    public BoatStatusEnum getBoatStatus() {
        return boatStatus;
    }

    public int getLegNumber() {
        return legNumber;
    }

    public long getEstimatedTimeAtNextMark() {
        return estimatedTimeAtNextMark;

    }


}
