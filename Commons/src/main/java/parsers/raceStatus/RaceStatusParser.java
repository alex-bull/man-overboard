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

    /**
     * Parse the race status message
     *
     * @param body byte[] a byte array of the message that needs parsing
     * @return RaceStatusData the data from the race status message
     */
    public static RaceStatusData processMessage(byte[] body) {
        try {
            long currentTime = Converter.hexByteArrayToLong(Arrays.copyOfRange(body, 1, 7));
            Integer raceId = hexByteArrayToInt(Arrays.copyOfRange(body, 7, 11));
            Integer raceStatus = hexByteArrayToInt(Arrays.copyOfRange(body, 11, 12));
            long expectedStartTime = Converter.hexByteArrayToLong(Arrays.copyOfRange(body, 12, 18));
            Integer numBoatsInRace = hexByteArrayToInt(Arrays.copyOfRange(body, 22, 23));
            HashMap<Integer, BoatStatus> boatStatuses = new HashMap<>();
            Integer windDirection = hexByteArrayToInt(Arrays.copyOfRange(body, 18, 20));
            Integer windSpeed = hexByteArrayToInt(Arrays.copyOfRange(body, 20, 22));
            Double doubleWindDirection = windDirection * 360.0 / 65536.0;
            int currentByte = 24;

            for (int i = 0; i < numBoatsInRace; i++) {
                Integer sourceID = hexByteArrayToInt(Arrays.copyOfRange(body, currentByte, currentByte + 4));
                Integer boatStatus = hexByteArrayToInt(Arrays.copyOfRange(body, currentByte + 4, currentByte + 5));
                Integer legNumber = hexByteArrayToInt(Arrays.copyOfRange(body, currentByte + 5, currentByte + 6));
                long timeAtNextMark = hexByteArrayToLong(Arrays.copyOfRange(body, currentByte + 8, currentByte + 14));
                long estTimeToNextMark = convertToRelativeTime(timeAtNextMark, currentTime) * -1; // returned time is negative because time at next mark is after current time
                boatStatuses.put(sourceID, new BoatStatus(sourceID, boatStatusToEnum(boatStatus), legNumber, estTimeToNextMark));
                currentByte += 20;
            }

            return new RaceStatusData(currentTime, raceStatusToEnum(raceStatus), expectedStartTime, doubleWindDirection,
                    windSpeed, numBoatsInRace, boatStatuses);

        } catch (Exception e) {
            return null;
        }

    }

    /**
     * Converts race status to enum.
     *
     * @param status Integer the race status
     * @return enum version of race status
     */
    private static RaceStatusEnum raceStatusToEnum(Integer status) {
        return RaceStatusEnum.values()[status];
    }

    /**
     * Converts boat status to enum.
     *
     * @param status Integer the boat status
     * @return enum version of boat status
     */
    private static BoatStatusEnum boatStatusToEnum(Integer status) {
        return BoatStatusEnum.values()[status];
    }


}
