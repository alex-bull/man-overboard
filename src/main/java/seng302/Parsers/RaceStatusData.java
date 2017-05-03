package seng302.Parsers;

import java.util.Arrays;
import java.util.HashMap;

import static seng302.Parsers.Converter.hexByteArrayToInt;

/**
 * Created by Pang on 3/05/17.
 * Race status data
 */
public class RaceStatusData {

    private Integer currentTime;
    private Integer raceID;
    private String raceStatus;
    private Integer expectedStartTime;
    private Integer windDirection;
    private Integer windSpeed;
    private Integer numBoatsInRace;
    private Integer raceType;
    private HashMap<Integer, BoatStatus> boatStatuses = new HashMap<>();


    public RaceStatusData(Integer currentTime, Integer raceID, String raceStatus, Integer expectedStartTime, Integer windDirection, Integer windSpeed, Integer numBoatsInRace, Integer raceType, HashMap<Integer, BoatStatus> boatStatuses) {
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


    public Integer getCurrentTime() {
        return currentTime;
    }

    public Integer getRaceID() {
        return raceID;
    }

    public String getRaceStatus() {
        return raceStatus;
    }

    public Integer getExpectedStartTime() {
        return expectedStartTime;
    }

    public Integer getWindDirection() {
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
