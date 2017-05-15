package parsers.raceStatus;

import parsers.BoatStatus;
import parsers.Converter;
import parsers.RaceStatusEnum;

import java.util.Arrays;
import java.util.HashMap;

import static parsers.Converter.hexByteArrayToInt;

/**
 * Created by Pang on 3/05/17.
 * Parse for race status message
 */
public class RaceStatusParser {

    /**
     * Parse the race status message
     * @param body byte[] a byte array of the message that needs parsing
     * @return RaceStatusData the data from the race status message
     */
    public RaceStatusData processMessage(byte[] body) {
        try {
            long currentTime = Converter.hexByteArrayToLong(Arrays.copyOfRange(body, 1, 7));
            Integer raceID = hexByteArrayToInt(Arrays.copyOfRange(body, 7, 11));
            Integer raceStatus = hexByteArrayToInt(Arrays.copyOfRange(body, 11, 12));
            long expectedStartTime = Converter.hexByteArrayToLong(Arrays.copyOfRange(body, 12, 18));
            Integer windDirection = hexByteArrayToInt(Arrays.copyOfRange(body, 18, 20));
            Integer windSpeed = hexByteArrayToInt(Arrays.copyOfRange(body, 20, 22));
            Integer numBoatsInRace = hexByteArrayToInt(Arrays.copyOfRange(body, 22, 23));
            Integer raceType = hexByteArrayToInt(Arrays.copyOfRange(body, 23, 24));
            Double doubleWindDirection = windDirection * 360.0 / 65536.0;
            HashMap<Integer, BoatStatus> boatStatuses = new HashMap<>();
            int currentByte = 24;

            for (int i = 0; i < numBoatsInRace; i++) {

                Integer sourceID = hexByteArrayToInt(Arrays.copyOfRange(body, currentByte, currentByte + 4));
                Integer boatStatus = hexByteArrayToInt(Arrays.copyOfRange(body, currentByte + 4, currentByte + 5));
                Integer legNumber = hexByteArrayToInt(Arrays.copyOfRange(body, currentByte + 5, currentByte + 6));
                Integer numPenaltiesAwarded = hexByteArrayToInt(Arrays.copyOfRange(body, currentByte + 6, currentByte + 7));
                Integer numPenaltiesServed = hexByteArrayToInt(Arrays.copyOfRange(body, currentByte + 7, currentByte + 8));
                long estTimeToNextMark = hexByteArrayToInt(Arrays.copyOfRange(body, currentByte + 8, currentByte + 14));
                estTimeToNextMark = convertToRelativeTime(estTimeToNextMark, currentTime);
                long estTimeAtFinish = hexByteArrayToInt(Arrays.copyOfRange(body, currentByte + 14, currentByte + 20));

                boatStatuses.put(sourceID, new BoatStatus(sourceID, boatStatus, legNumber,
                        numPenaltiesAwarded, numPenaltiesServed, estTimeToNextMark, estTimeAtFinish));
                currentByte += 20;
            }

            return new RaceStatusData(currentTime, raceID, raceStatusToEnum(raceStatus), expectedStartTime, doubleWindDirection,
                    windSpeed, numBoatsInRace, raceType, boatStatuses);

        }
        catch (Exception e) {
            return null;
        }

    }

    /**
     * Converts race status to enum.
     * @param status Integer the race status
     * @return enum version of race status
     */
    private RaceStatusEnum raceStatusToEnum(Integer status) {
        return RaceStatusEnum.values()[status];
    }


}
