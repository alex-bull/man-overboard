package seng302.Parsers;

import java.util.List;

import static seng302.Parsers.Converter.hexListToDecimal;

/**
 * Created by psu43 on 13/04/17.
 * Converter for boat data.
 */
public class BoatDataParser {

    long sourceID;
    double latitude;
    double longitude;
    double heading;
    long speed;

    /**
     * Process the given list of data and parse source id, latitude, longitude, heading, speed
     * @param body List a list of hexadecimal bytes
     */
    public void processMessage(List body) {

        // source id
        List sourceIDHexValues = body.subList(7, 11);
        System.out.println("sourceID " + sourceIDHexValues);
        this.sourceID = hexListToDecimal(sourceIDHexValues);
        System.out.println("parsed source ID: " + sourceID);

        List latitudeHexValues = body.subList(16, 20);
        List longitudeHexValues = body.subList(20, 24);
        // latitude calculations
        //System.out.println("lat " + latitudeHexValues);
        this.latitude = parseCoordinate(latitudeHexValues);
       // System.out.println("parsed lat: " + latitude);

        // longitude calculations
        //System.out.println("long " + longitudeHexValues);
        this.longitude = parseCoordinate(longitudeHexValues);
       // System.out.println("parsed long: " + longitude);

        // heading
        List headingHexValues = body.subList(28, 30);
        //System.out.println("head " + headingHexValues);
        this.heading = parseHeading(headingHexValues);
      //  System.out.println("parsed heading : " + heading);

        // speed
        List speedHexValues = body.subList(34, 36);
        System.out.println("Speed " + speedHexValues);
        this.speed = hexListToDecimal(speedHexValues);
        System.out.println("parsed speed: " + speed);
    }

    /**
     * Convert a list of little endian hex values into a decimal heading
     * @param hexValues List a list of (2) hexadecimal bytes in little endian format
     * @return Double the value of the heading
     */
    private Double parseHeading(List hexValues) {
        return (double) hexListToDecimal(hexValues) * 360.0 / 65536.0;
    }

    /**
     * Convert a list of little endian hex values into a decimal latitude or longitude
     * @param hexValues List a list of (4) hexadecimal bytes in little endian format
     * @return Double the value of the coordinate value
     */
    private Double parseCoordinate(List hexValues) {
        return (double) hexListToDecimal(hexValues) * 180.0 /  2147483648.0;
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
