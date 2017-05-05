package seng302.Parsers;

import java.util.HashMap;

/**
 * Created by Pang on 3/05/17.
 * Race status data
 */
public class RaceStatusData {

    private long currentTime;
    private Integer raceID;
    private String raceStatus;
    private long expectedStartTime;
    private Double windDirection;
    private Integer windSpeed;
    private Integer numBoatsInRace;
    private Integer raceType;
    private HashMap<Integer, BoatStatus> boatStatuses = new HashMap<>();


    public RaceStatusData(long currentTime, Integer raceID, String raceStatus, long expectedStartTime, Double windDirection, Integer windSpeed, Integer numBoatsInRace, Integer raceType, HashMap<Integer, BoatStatus> boatStatuses) {
        this.currentTime = currentTime;
        this.raceID = raceID;
        this.raceStatus = raceStatus;
        this.expectedStartTime = expectedStartTime;
        this.windDirection = windDirection;
        this.windSpeed = windSpeed;
        this.numBoatsInRace = numBoatsInRace;
        this.raceType = raceType;
        this.boatStatuses = boatStatuses;
    }


    public long getCurrentTime() {
        return currentTime;
    }

    public Integer getRaceID() {
        return raceID;
    }

    public String getRaceStatus() {
        return raceStatus;
    }

    public long getExpectedStartTime() {
        return expectedStartTime;
    }

    public Double getWindDirection() {
        return windDirection;
    }

    public Integer getWindSpeed() {
        return windSpeed;
    }

    public Integer getNumBoatsInRace() {
        return numBoatsInRace;
    }

    public Integer getRaceType() {
        return raceType;
    }

    public HashMap<Integer, BoatStatus> getBoatStatuses() {
        return boatStatuses;
    }


}
