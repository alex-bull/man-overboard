package parsers.courseWind;

import java.util.Arrays;
import java.util.HashMap;

import static parsers.Converter.*;

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
            int loopCount = hexByteArrayToInt(Arrays.copyOfRange(body, 2, 3));
            HashMap<Integer, WindStatus> windStatuses = new HashMap<>();

            int currentByte = 3;
            for(int i = 0; i < loopCount; i++) {
                int windID = hexByteArrayToInt(Arrays.copyOfRange(body, currentByte, currentByte + 1));
                double windDirection = parseHeading(Arrays.copyOfRange(body, currentByte + 11, currentByte + 13));
                int windSpeed = hexByteArrayToInt(Arrays.copyOfRange(body, currentByte + 13, currentByte + 15));
                WindStatus windStatus = new WindStatus(windDirection, windSpeed);
                windStatuses.put(windID, windStatus);
                currentByte += 20;
            }
            return new CourseWindData(windStatuses);
        }
        catch(Exception e) {
            return null;
        }

    }
}
