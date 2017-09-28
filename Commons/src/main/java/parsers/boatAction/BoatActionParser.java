package parsers.boatAction;

import java.util.Arrays;

import static parsers.Converter.hexByteArrayToInt;
import static parsers.boatAction.BoatAction.getBoatAction;

/**
 * Created by abu59 on 17/07/17.
 */
public class BoatActionParser {
    private BoatAction actionNum;
    private double heading;
    private double lat;
    private double lng;

    public BoatAction getActionNum() {
        return actionNum;
    }

    public double getHeading() {
        return heading;
    }

    public double getLat() {
        return lat;
    }

    public double getLng() {
        return lng;
    }

    /**
     * Process the given data and parse source id, latitude, longitude, heading, speed
     *
     * @param body byte[] a byte array of the boat data message
     * @return BoatData boat data object
     */
    public void update(byte[] body) {
        try {
            int num = hexByteArrayToInt(Arrays.copyOfRange(body, 0, 1));
            actionNum= getBoatAction(num);
        } catch (Exception e) {
            e.printStackTrace();
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
