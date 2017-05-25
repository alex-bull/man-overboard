package models;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.paint.Color;

/**
 * Created by mgo65 on 3/03/17.
 * Boat object
 */
public class Boat implements Competitor {
    private String teamName;
    private double velocity;
    private MutablePoint position;
    private Color color;
    private String abbreName;
    private DoubleProperty currentHeading = new SimpleDoubleProperty();
    private int sourceID;
    private int status;
    private String lastMarkPassed;
    private int legIndex;
    private long timeToNextMark;
    private long timeAtLastMark;
    private double latitude;
    private double longitude;
    /**
     * Creates a boat
     *
     * @param teamName      String the team name of the boat
     * @param velocity      int the velocity of the boat in m/s
     * @param startPosition MutablePoint the boat's start position coordinate
     * @param color         Color the boat colour
     * @param abbreName     String the abbreviated name of the boat
     */
    public Boat(String teamName, int velocity, MutablePoint startPosition, Color color, String abbreName) {
        this.velocity = velocity;
        this.teamName = teamName;
        this.position = startPosition;
        this.color = color;
        this.abbreName = abbreName;
        legIndex = 0;
        timeToNextMark = 0;
        timeAtLastMark = 0;
    }

    /**
     * Creates a boat, for mock class only
     *
     * @param teamName      String the team name of the boat
     * @param velocity      int the velocity of the boat in m/s
     * @param startPosition MutablePoint the boat's start position coordinate
     * @param sourceID      sourceID of the boat
     * @param abbreName     String the abbreviated name of the boat
     */
    public Boat(String teamName, int velocity, MutablePoint startPosition, String abbreName, int sourceID, int status) {
        this.velocity = velocity;
        this.teamName = teamName;
        this.position = startPosition;
        this.abbreName = abbreName;
        legIndex = 0;
        timeToNextMark = 0;
        timeAtLastMark = 0;
        this.sourceID = sourceID;
        this.status = status;
    }

    public Boat() {

    }

    @Override
    public int getStatus() {
        return status;
    }

    @Override
    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    public int getCurrentLegIndex() {
        return legIndex;
    }

    @Override
    public void setCurrentLegIndex(int legIndex) {
        this.legIndex = legIndex;
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
    public long getTimeToNextMark() {
        return timeToNextMark;
    }

    public void setTimeToNextMark(long timeToNextMark) {
        this.timeToNextMark =  timeToNextMark;
    }

    public long getTimeAtLastMark() {
        return timeAtLastMark;
    }

    public void setTimeAtLastMark(long timeAtLastMark) {
        this.timeAtLastMark = timeAtLastMark;
    }

    public int getLegIndex() {
        return legIndex;
    }

    public void setLegIndex(int legIndex) {
        this.legIndex = legIndex;
    }

    public String getLastMarkPassed() {
        return lastMarkPassed;
    }

    public void setLastMarkPassed(String lastMarkPassed) {
        this.lastMarkPassed = lastMarkPassed;
    }

    public int getSourceID() {
        return sourceID;
    }

    /**
     * Parse the Source ID as a string
     *
     * @param sourceID the Source ID as string
     */
    public void setSourceID(String sourceID) {
        this.sourceID = Integer.parseInt(sourceID);
    }

    public void setSourceID(int sourceID) {
        this.sourceID = sourceID;
    }
    public String getTeamName() {
        return this.teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public double getVelocity() {
        return this.velocity;
    }

    public void setVelocity(double velocity) {
        this.velocity = velocity;
    }

    public MutablePoint getPosition() {
        return this.position;
    }

    public void setPosition(MutablePoint newPos) {
        this.position = newPos;
    }

    @Override
    public String getAbbreName() {
        return abbreName;
    }
    public void setAbbreName(String abbreName) {
        this.abbreName = abbreName;
    }

    @Override
    public Color getColor() {
        return color;
    }

    @Override
    public void setColor(Color color) {
        this.color = color;
    }

    public double getCurrentHeading() {
        return currentHeading.getValue();
    }

    public void setCurrentHeading(double currentHeading) {
        this.currentHeading.setValue(currentHeading);
    }

    public DoubleProperty getHeadingProperty() {
        return this.currentHeading;
    }

    /**
     * Updates the boats position given the time changed
     *
     * @param elapsedTime the time elapsed in seconds
     */
    public void updatePosition(double elapsedTime) {
        int earthRadius = 6371;
        double distance = velocity * elapsedTime / 1000; // in km
        double lat1 = position.getXValue() * Math.PI / 180; // in radians
        double lng1 = position.getYValue() * Math.PI / 180;
        double bearing = currentHeading.getValue() * Math.PI / 180;

        //calculate new positions
        double lat2 = Math.asin(Math.sin(lat1) * Math.cos(distance / earthRadius) +
                Math.cos(lat1) * Math.sin(distance / earthRadius) * Math.cos(bearing));
        double lng2 = lng1 + Math.atan2(Math.sin(bearing) * Math.sin(distance / earthRadius) * Math.cos(lat1),
                Math.cos(distance / earthRadius) - Math.sin(lat1) * Math.sin(lat2));

        //turn the new lat and lng back to degrees
        setPosition(new MutablePoint(lat2 * 180 / Math.PI, lng2 * 180 / Math.PI));
    }

    public void setProperties(double velocity, double heading, double latitude, double longitude) {
        this.velocity = velocity;
        this.currentHeading.setValue(heading);
        this.position = new MutablePoint(latitude, longitude);

    }

    @Override
    public String toString() {
        return "Boat{" +
                "teamName='" + teamName + '\'' +
                ", abbreName='" + abbreName + '\'' +
                ", sourceID=" + sourceID +
                '}';
    }
}