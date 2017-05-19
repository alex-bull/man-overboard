package parsers.courseWind;

import parsers.boatLocation.BoatData;

import java.util.Arrays;

import static parsers.Converter.hexByteArrayToInt;
import static parsers.Converter.hexByteArrayToLong;
import static parsers.Converter.parseHeading;

/**
 * Created by psu43 on 19/05/17.
 * Parser for course wind data from the AC35 streaming data
 */
public class CourseWindParser {


    public CourseWindData processMessage(byte[] body) {
        try {
            int messageVersionNumber = hexByteArrayToInt(Arrays.copyOfRange(body, 1, 2));
            int selectedWindID = hexByteArrayToInt(Arrays.copyOfRange(body, 2, 3));
            int loopCount = hexByteArrayToInt(Arrays.copyOfRange(body, 3, 4));

            for(int i = 0; i < loopCount; i++) {
                int num = 20 * i;
                int windID = hexByteArrayToInt(Arrays.copyOfRange(body, 4 + num, 5 + num));
                long time = hexByteArrayToLong(Arrays.copyOfRange(body, 5 + num, 11 + num));
                int raceID = hexByteArrayToInt(Arrays.copyOfRange(body, 11 + num, 13 + num));
                double windDirection = parseHeading(Arrays.copyOfRange(body, 13 + num, 15 + num));
                int windSpeed = hexByteArrayToInt(Arrays.copyOfRange(body, 15 + num, 17 + num));
                double bestUpwindAngle = parseHeading(Arrays.copyOfRange(body, 17 + num, 19 + num));
                double bestDownwindAngle = parseHeading(Arrays.copyOfRange(body, 19 + num, 21 + num));
                int flags = hexByteArrayToInt(Arrays.copyOfRange(body, 21 + num, 22 + num));

            }



        }
        catch(Exception e) {
            return null;
        }
        return  null;

    }
}
