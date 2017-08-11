package parsers.boatLocation;

import models.MutablePoint;

/**
 * Created by psu43 on 25/04/17.
 * Boat data
 */
public class BoatData {
    private int sourceID;
    private int deviceType;
    private double latitude;
    private double longitude;
    private double heading;
    private double speed;
    private MutablePoint mercatorPoint;

    /**
     * Constructs a boat data read from a data source
     * @param sourceID int sourceID of the boat
     * @param deviceType int the device type
     * @param latitude double latitude
     * @param longitude double longitude
     * @param heading double heading
     * @param speed double speed
     * @param mercatorPoint MutablePoint pixel point
     */
    BoatData(int sourceID, int deviceType, double latitude, double longitude, double heading, double speed, MutablePoint mercatorPoint) {
        this.sourceID = sourceID;
        this.deviceType = deviceType;
        this.latitude = latitude;
        this.longitude = longitude;
        this.heading = heading;
        this.speed = speed;
        this.mercatorPoint = mercatorPoint;
    }


    public int getDeviceType() {
        return deviceType;
    }

    public int getSourceID() {
        return sourceID;
    }

    public void setSourceID(int sourceID) {
        this.sourceID = sourceID;
    }

    public double getHeading() {
        return heading;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    @Override
    public String toString() {
        return "BoatData{" +
                "sourceID=" + sourceID +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", heading=" + heading +
                ", speed=" + speed +
                '}';
    }

    public MutablePoint getMercatorPoint() {
        return mercatorPoint;
    }
}
