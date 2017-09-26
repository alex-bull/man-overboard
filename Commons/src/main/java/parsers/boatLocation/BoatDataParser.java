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

    /**
     * Process the given data and parse source id, latitude, longitude, heading, speed
     *
     * @param body byte[] a byte array of the boat data message
     * @return BoatData boat data object
     */
    public BoatData processMessage(byte[] body) {
        try {
            Integer sourceID = hexByteArrayToInt(Arrays.copyOfRange(body, 7, 11));
            int deviceType = hexByteArrayToInt(Arrays.copyOfRange(body, 15, 16));
            double latitude = parseCoordinate(Arrays.copyOfRange(body, 16, 20));
            double longitude = parseCoordinate(Arrays.copyOfRange(body, 20, 24));
            double heading = parseHeading(Arrays.copyOfRange(body, 28, 30));

            //speed in mm/sec
            int speed = hexByteArrayToInt(Arrays.copyOfRange(body, 38, 40));
            //speed in m/sec
            double convertedSpeed = speed / 1000.0;
            MutablePoint mercatorPoint = mercatorProjection(latitude, longitude);
            if (deviceType == 3) {
                MutablePoint GPS = new MutablePoint(latitude, longitude);
                this.courseFeature = new Mark(sourceID.toString(), mercatorPoint, GPS, 0);
            }
            return new BoatData(sourceID, deviceType, latitude, longitude, heading, convertedSpeed, mercatorPoint);
        } catch (Exception e) {
            return null;
        }

    }

    /**
     * gets the latency of the packet
     * @param body the body of the packet
     * @return the current latency
     */
    public long getLatency(byte[] body){
        return System.currentTimeMillis()-hexByteArrayToLong(Arrays.copyOfRange(body, 1, 7));

    }

    public CourseFeature getCourseFeature() {
        return courseFeature;
    }

}
