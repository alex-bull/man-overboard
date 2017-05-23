package parsers.courseWind;

import parsers.boatLocation.BoatData;

import java.util.Arrays;
import java.util.HashMap;

import static parsers.Converter.hexByteArrayToInt;
import static parsers.Converter.hexByteArrayToLong;
import static parsers.Converter.parseHeading;

/**
 * Created by psu43 on 19/05/17.
 * Parser for course wind data from the AC35 streaming data
 */
public class CourseWindParser {


    /**
     * Parse the course wind message
     * @param body byte[] a byte array of the message that needs parsing
     * @return CourseWindData the data from the course wind message
     */
    public CourseWindData processMessage(byte[] body) {
        try {
            int messageVersionNumber = hexByteArrayToInt(Arrays.copyOfRange(body, 0, 1));
            int selectedWindID = hexByteArrayToInt(Arrays.copyOfRange(body, 1, 2));
            int loopCount = hexByteArrayToInt(Arrays.copyOfRange(body, 2, 3));
            HashMap<Integer, WindStatus> windStatuses = new HashMap<>();

            int currentByte = 3;
            for(int i = 0; i < loopCount; i++) {
                int windID = hexByteArrayToInt(Arrays.copyOfRange(body, currentByte, currentByte + 1));
                long time = hexByteArrayToLong(Arrays.copyOfRange(body, currentByte + 1, currentByte + 7));
                int raceID = hexByteArrayToInt(Arrays.copyOfRange(body, currentByte + 7, currentByte + 11));
                double windDirection = parseHeading(Arrays.copyOfRange(body, currentByte + 11, currentByte + 13));
                int windSpeed = hexByteArrayToInt(Arrays.copyOfRange(body, currentByte + 13, currentByte + 15));
                double bestUpwindAngle = parseHeading(Arrays.copyOfRange(body, currentByte + 15, currentByte + 17));
                double bestDownwindAngle = parseHeading(Arrays.copyOfRange(body, currentByte + 17, currentByte + 19));
                int flags = hexByteArrayToInt(Arrays.copyOfRange(body, currentByte + 19, currentByte + 20));
                WindStatus windStatus = new WindStatus(windID, time, raceID, windDirection, windSpeed, bestUpwindAngle, bestDownwindAngle, flags);
                windStatuses.put(windID, windStatus);
                currentByte += 20;
            }
            return new CourseWindData(messageVersionNumber, selectedWindID, windStatuses);
        }
        catch(Exception e) {
            return null;
        }

    }
}
