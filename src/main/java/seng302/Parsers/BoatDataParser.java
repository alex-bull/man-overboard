package seng302.Parsers;

import java.util.Arrays;
import java.util.List;

import static seng302.Parsers.Converter.hexByteArrayToInt;

/**
 * Created by psu43 on 13/04/17.
 * Parser for boat data.
 */
public class BoatDataParser {


    /**
     * Process the given data and parse source id, latitude, longitude, heading, speed
     * @param body byte[] a byte array of the boat data message
     * @return BoatData boat data object
     */
    public BoatData processMessage(byte[] body) {

        int sourceID = hexByteArrayToInt(Arrays.copyOfRange(body, 7,11));
        double latitude = parseCoordinate(Arrays.copyOfRange(body, 16,20));
        double longitude = parseCoordinate(Arrays.copyOfRange(body, 20,24));
        double heading = hexByteArrayToInt(Arrays.copyOfRange(body, 28,30));
        int speed = hexByteArrayToInt(Arrays.copyOfRange(body, 34,36));

        return new BoatData(sourceID, latitude, longitude, heading, speed);
    }

    /**
     * Convert a byte array of little endian hex values into a decimal heading
     * @param hexValues byte[] a byte array of (2) hexadecimal bytes in little endian format
     * @return Double the value of the heading
     */
    private Double parseHeading(byte[] hexValues) {
        return (double) hexByteArrayToInt(hexValues) * 360.0 / 65536.0;
    }

    /**
     * Convert a byte array of little endian hex values into a decimal latitude or longitude
     * @param hexValues byte[] a byte array of (4) hexadecimal bytes in little endian format
     * @return Double the value of the coordinate value
     */
    private Double parseCoordinate(byte[] hexValues) {
        return (double) hexByteArrayToInt(hexValues) * 180.0 /  2147483648.0;
    }

}
