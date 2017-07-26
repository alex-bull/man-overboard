package parsers.boatAction;

import models.Mark;
import models.MutablePoint;
import parsers.boatLocation.BoatData;

import java.util.Arrays;
import java.util.List;

import static parsers.Converter.hexByteArrayToInt;
import static parsers.Converter.parseHeading;
import static utility.Projection.mercatorProjection;

/**
 * Created by abu59 on 17/07/17.
 */
public class BoatActionParser {

    /**
     * Process the given data and parse source id, latitude, longitude, heading, speed
     * @param body byte[] a byte array of the boat data message
     * @return BoatData boat data object
     */
    public BoatAction processMessage(byte[] body) {
        try {
            int actionNum = hexByteArrayToInt(Arrays.copyOfRange(body, 0, 1));
            return BoatAction.values()[actionNum - 1];
        }
        catch (Exception e) {
            return null;
        }
    }

    /**
     * Convert a byte array of little endian hex values into a decimal heading
     *
     * @param hexValues byte[] a byte array of (2) hexadecimal bytes in little endian format
     * @return Double the value of the heading
     */
    private Double parseHeading(byte[] hexValues) {
        return (double) hexByteArrayToInt(hexValues) * 360.0 / 65536.0;
    }

    /**
     * Convert a byte array of little endian hex values into a decimal latitude or longitude
     *
     * @param hexValues byte[] a byte array of (4) hexadecimal bytes in little endian format
     * @return Double the value of the coordinate value
     */
    private Double parseCoordinate(byte[] hexValues) {
        return (double) hexByteArrayToInt(hexValues) * 180.0 / 2147483648.0;
    }

}
