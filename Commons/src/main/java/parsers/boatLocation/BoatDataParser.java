package parsers.boatLocation;

import models.CourseFeature;
import models.Mark;
import models.MutablePoint;

import java.util.Arrays;

import static parsers.Converter.*;
import static utility.Projection.mercatorProjection;

/**
 * Created by psu43 on 13/04/17.
 * parsers for boat location data.
 */
public class BoatDataParser {

    private CourseFeature courseFeature;
    private Integer sourceID ;
    private int deviceType;
    private double latitude;
    private double longitude ;
    private double heading;

    private double convertedSpeed;

    private MutablePoint mercatorPoint;


    public Integer getSourceID() {
        return sourceID;
    }

    public int getDeviceType() {
        return deviceType;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getHeading() {
        return heading;
    }

    public double getSpeed() {
        return convertedSpeed;
    }

    public MutablePoint getMercatorPoint() {
        return mercatorPoint;
    }

    /**
     * Process the given data and parse source id, latitude, longitude, heading, speed
     *
     * @param body byte[] a byte array of the boat data message
     * @return BoatData boat data object
     */
    public void update(byte[] body) {
        try {
            sourceID = hexByteArrayToInt(Arrays.copyOfRange(body, 7, 11));
            deviceType = hexByteArrayToInt(Arrays.copyOfRange(body, 15, 16));
            latitude = parseCoordinate(Arrays.copyOfRange(body, 16, 20));
            longitude = parseCoordinate(Arrays.copyOfRange(body, 20, 24));
            heading = parseHeading(Arrays.copyOfRange(body, 28, 30));

            //speed in mm/sec
            int speed = hexByteArrayToInt(Arrays.copyOfRange(body, 38, 40));
            //speed in m/sec
            convertedSpeed = speed / 1000.0;
            mercatorPoint = mercatorProjection(latitude, longitude);
            if (deviceType == 3) {
                MutablePoint GPS = new MutablePoint(latitude, longitude);
                courseFeature=new Mark(sourceID.toString(), mercatorPoint, GPS, 0);
            }

        } catch (Exception e) {
            //e.printStackTrace();
        }

    }

    public CourseFeature getCourseFeature() {
        return courseFeature;
    }

    /**
     * gets the latency of the packet
     * @param body the body of the packet
     * @return the current latency
     */
    public long getLatency(byte[] body){
        return System.currentTimeMillis()-hexByteArrayToLong(Arrays.copyOfRange(body, 1, 7));

    }


}
