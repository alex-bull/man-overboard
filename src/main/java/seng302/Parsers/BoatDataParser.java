package seng302.Parsers;

import seng302.Model.*;

import java.util.ArrayList;
import java.util.Arrays;

import static seng302.Parsers.Converter.hexByteArrayToInt;

/**
 * Created by psu43 on 13/04/17.
 * Parser for boat location data.
 */
public class BoatDataParser {
    private double latitude;
    private double longitude;
    private Integer sourceID;
    private double width;
    private double height;
    private CourseFeature courseFeature;
    private Competitor competitor;
    private MutablePoint pixelPoint;
    private BoatData boatData;


    public BoatDataParser(byte[] message, double width, double height) {
        this.width = width;
        this.height = height;
        this.boatData = processMessage(message);
    }

    /**
     * Process the given data and parse source id, latitude, longitude, heading, speed
     * @param body byte[] a byte array of the boat data message
     * @return BoatData boat data object
     */
    public BoatData processMessage(byte[] body) {

        this.sourceID = hexByteArrayToInt(Arrays.copyOfRange(body, 7,11));
        int deviceType = hexByteArrayToInt(Arrays.copyOfRange(body,15, 16));
        this.latitude = parseCoordinate(Arrays.copyOfRange(body, 16,20));
        this.longitude = parseCoordinate(Arrays.copyOfRange(body, 20,24));
        double heading = parseHeading(Arrays.copyOfRange(body, 28,30));
        //speed in mm/sec
        int speed = hexByteArrayToInt(Arrays.copyOfRange(body, 34,36));
        //speed in m/sec
        double convertedSpeed=speed/1000.0;

        ArrayList<Double> point = mercatorProjection(latitude, longitude, width, height);
        double pointX = point.get(0);
        double pointY = point.get(1);
        //System.out.println("lat lon and Y" + point1X + " +" + point1Y);
        this.pixelPoint = new MutablePoint(pointX, pointY);
        if(deviceType == 3){
            MutablePoint GPS = new MutablePoint(latitude, longitude);
            this.courseFeature = new Mark(sourceID.toString(), this.pixelPoint, GPS, 0);
        }

        return new BoatData(sourceID, deviceType, latitude, longitude, heading, convertedSpeed);
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


    public MutablePoint getPixelPoint() {
        return pixelPoint;
    }

    public void setPixelPoint(MutablePoint pixelPoint) {
        this.pixelPoint = pixelPoint;
    }


    /**
     * Function to map latitude and longitude to screen coordinates
     * @param lat latitude
     * @param lon longitude
     * @param width width of the screen
     * @param height height of the screen
     * @return ArrayList the coordinates in metres
     */
    private ArrayList<Double> mercatorProjection(double lat, double lon, double width, double height){
        ArrayList<Double> ret=new ArrayList<>();
        double x = (lon+180)*(width/360);
        double latRad = lat*Math.PI/180;
        double merc = Math.log(Math.tan((Math.PI/4)+(latRad/2)));
        double y = (height/2)-(width*merc/(2*Math.PI));
        ret.add(x);
        ret.add(y);
        return ret;

    }



    public Competitor getCompetitor() {
        return competitor;
    }


    public BoatData getBoatData() {
        return boatData;
    }

    public void setBoatData(BoatData boatData) {
        this.boatData = boatData;

    }

    public CourseFeature getCourseFeature() {
        return courseFeature;
    }

    public void setCourseFeature(CourseFeature courseFeature) {
        this.courseFeature = courseFeature;
    }
}
