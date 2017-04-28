package seng302.Parsers;

/**
 * Created by psu43 on 25/04/17.
 * Boat data
 */
public class BoatData {
    private int sourceID;
    private double latitude;
    private double longitude;
    private double heading;
    private double speed;

    public BoatData(int sourceID, double latitude, double longitude, double heading, double speed) {
        this.sourceID = sourceID;
        this.latitude = latitude;
        this.longitude = longitude;
        this.heading = heading;
        this.speed = speed;
    }

//    public BoatData(){}

    public int getSourceID() {
        return sourceID;
    }

    public void setSourceID(int sourceID) {
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
}
