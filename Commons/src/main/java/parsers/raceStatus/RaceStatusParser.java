package parsers.raceStatus;

import parsers.BoatStatusEnum;
import parsers.Converter;
import parsers.RaceStatusEnum;

import java.util.Arrays;
import java.util.HashMap;

import static parsers.Converter.*;

/**
 * Created by Pang on 3/05/17.
 * Parse for race status message
 */
public class RaceStatusParser {

    private long currentTime;
    private Integer raceId;
    private Integer raceStatus;
    private long expectedStartTime ;
    private Integer numBoatsInRace ;
    private HashMap<Integer, BoatStatus> boatStatuses ;
    private Integer windSpeed ;
    private Double doubleWindDirection;

    public long getCurrentTime() {
        return currentTime;
    }

    public Integer getRaceId() {
        return raceId;
    }

    public RaceStatusEnum getRaceStatus() {
        return raceStatusToEnum(raceStatus);
    }

    public long getExpectedStartTime() {
        return expectedStartTime;
    }

    public Integer getNumBoatsInRace() {
        return numBoatsInRace;
    }

    public HashMap<Integer, BoatStatus> getBoatStatuses() {
        return boatStatuses;
    }


    public Integer getWindSpeed() {
        return windSpeed;
    }

    public Double getWindDirection() {
        return doubleWindDirection;
    }

    /**
     * Parse the race status message
     *
     * @param body byte[] a byte array of the message that needs parsing
     * @return RaceStatusData the data from the race status message
     */
    public void update(byte[] body) {
        try {
            currentTime = Converter.hexByteArrayToLong(Arrays.copyOfRange(body, 1, 7));
            raceId = hexByteArrayToInt(Arrays.copyOfRange(body, 7, 11));
            raceStatus = hexByteArrayToInt(Arrays.copyOfRange(body, 11, 12));
            expectedStartTime = Converter.hexByteArrayToLong(Arrays.copyOfRange(body, 12, 18));
            numBoatsInRace = hexByteArrayToInt(Arrays.copyOfRange(body, 22, 23));
            boatStatuses = new HashMap<>();
            int windDirection = hexByteArrayToInt(Arrays.copyOfRange(body, 18, 20));
            windSpeed = hexByteArrayToInt(Arrays.copyOfRange(body, 20, 22));
            doubleWindDirection = windDirection * 360.0 / 65536.0;
            int currentByte = 24;

            for (int i = 0; i < numBoatsInRace; i++) {
                Integer sourceID = hexByteArrayToInt(Arrays.copyOfRange(body, currentByte, currentByte + 4));
                Integer boatStatus = hexByteArrayToInt(Arrays.copyOfRange(body, currentByte + 4, currentByte + 5));
                Integer legNumber = hexByteArrayToInt(Arrays.copyOfRange(body, currentByte + 5, currentByte + 6));
                long timeAtNextMark = hexByteArrayToLong(Arrays.copyOfRange(body, currentByte + 8, currentByte + 14));
                long estTimeToNextMark = convertToRelativeTime(timeAtNextMark, currentTime) * -1; // returned time is negative because time at next mark is after current time
                if(!boatStatuses.containsKey(sourceID)){
                boatStatuses.put(sourceID, new BoatStatus(sourceID, boatStatusToEnum(boatStatus), legNumber, estTimeToNextMark));
                }
                else{
                    boatStatuses.get(sourceID).setBoatStatus(boatStatusToEnum(boatStatus));
                    boatStatuses.get(sourceID).setLegNumber(legNumber);
                    boatStatuses.get(sourceID).setEstimatedTimeAtNextMark(estTimeToNextMark);
                }
                currentByte += 20;
            }


        } catch (Exception e) {
           // e.printStackTrace();
        }

    }

    /**
     * Converts race status to enum.
     *
     * @param status Integer the race status
     * @return enum version of race status
     */
    private RaceStatusEnum raceStatusToEnum(Integer status) {
        return RaceStatusEnum.values()[status];
    }

    /**
     * Converts boat status to enum.
     *
     * @param status Integer the boat status
     * @return enum version of boat status
     */
    private BoatStatusEnum boatStatusToEnum(Integer status) {
        return BoatStatusEnum.values()[status];
    }


}
