package seng302.Parsers;

import java.util.Arrays;
import java.util.List;

import static seng302.Parsers.Converter.hexByteArrayToInt;

/**
 * Created by psu43 on 13/04/17.
 * Parser for boat data.
 */
public class BoatDataParser {

    private long sourceID;
    private double latitude;
    private double longitude;
    private double heading;
    private long speed;

    /**
     * Process the given list of data and parse source id, latitude, longitude, heading, speed
     * @param body List a list of hexadecimal bytes
     */
    public void processMessage(byte[] body) {

        this.sourceID = hexByteArrayToInt(Arrays.copyOfRange(body, 7,11));
        this.latitude = parseCoordinate(Arrays.copyOfRange(body, 16,20));
        this.longitude = parseCoordinate(Arrays.copyOfRange(body, 20,24));
        this.heading = hexByteArrayToInt(Arrays.copyOfRange(body, 28,30));
        this.speed = hexByteArrayToInt(Arrays.copyOfRange(body, 34,36));


        /////// comment this out to disable printing values ////////
//        System.out.println("sourceID " + sourceIDHexValues);
//        System.out.println("lat " + latitudeHexValues);
//        System.out.println("long " + longitudeHexValues);
//        System.out.println("head " + headingHexValues);
//        System.out.println("Speed " + speedHexValues);

//        System.out.println("parsed source ID: " + sourceID);
//        System.out.println("parsed lat: " + latitude);
//        System.out.println("parsed long: " + longitude);
//        System.out.println("parsed heading : " + heading);
//        System.out.println("parsed speed: " + speed);
    }

    /**
     * Convert a list of little endian hex values into a decimal heading
     * @param hexValues List a list of (2) hexadecimal bytes in little endian format
     * @return Double the value of the heading
     */
    private Double parseHeading(byte[] hexValues) {
        return (double) hexByteArrayToInt(hexValues) * 360.0 / 65536.0;
    }

    /**
     * Convert a list of little endian hex values into a decimal latitude or longitude
     * @param hexValues List a list of (4) hexadecimal bytes in little endian format
     * @return Double the value of the coordinate value
     */
    private Double parseCoordinate(byte[] hexValues) {
        return (double) hexByteArrayToInt(hexValues) * 180.0 /  2147483648.0;
    }

    public long getSourceID() {
        return sourceID;
    }

    public void setSourceID(long sourceID) {
        this.sourceID = sourceID;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getHeading() {
        return heading;
    }

    public void setHeading(double heading) {
        this.heading = heading;
    }

    public long getSpeed() {
        return speed;
    }

    public void setSpeed(long speed) {
        this.speed = speed;
    }

}
