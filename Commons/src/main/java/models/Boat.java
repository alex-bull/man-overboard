package models;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;

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
    //how much the boat if affected by wind, can be parsed in as constructor
    private double blownFactor=0.01;
//    external forces on the boat
    private List<RepelForce> forces;

    private boolean sailsOut = true;
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
        forces =new ArrayList<>();
        this.color = color;
        this.abbreName = abbreName;
        legIndex = 0;
        timeToNextMark = 0;
        timeAtLastMark = 0;
    }

    public List<RepelForce> getForces() {
        return forces;
    }

    public void addForce(RepelForce force){
        this.forces.add(force);
    }

    /**
     * Creates a boat, for mock class only
     *
     * @param teamName      String the team name of the boat
     * @param velocity      int the velocity of the boat in m/s
     * @param startPosition MutablePoint the boat's start position coordinate
     * @param sourceID      sourceID of the boat
     * @param abbreName     String the abbreviated name of the boat
     * @param status        int status status of the boat
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

    /**
     * Switches the sail state of the boat between sails in and sails out
     */
    public void switchSails() {
        sailsOut = !sailsOut;
    }

    /**
     * Returns the sail state of the boat
     * @return sailsOut sail state of the boat
     */
    public boolean hasSailsOut() {
        return sailsOut;
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
        // convert negative current heading to positive?
        if (currentHeading.getValue() < 0) {
            this.currentHeading.setValue(currentHeading.getValue() + 360);
        }

        return currentHeading.getValue();
    }

    public void setCurrentHeading(double currentHeading) {
        // convert negative current heading to positive?
        if (currentHeading < 0) {
            this.currentHeading.setValue(currentHeading + 360);
        }
        else{
            this.currentHeading.setValue(currentHeading%360);
        }
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

    /**
     * Returns the downwind given wind angle
     * @param windAngle double wind angle
     * @return double downWind
     */
    private double getDownWind(double windAngle) {
       double downWind = windAngle + 180;
       if(downWind > 360) {
           downWind = downWind - 360;
       }
       return downWind;
    }

    public void blownByWind(double windAngle){
//        dont do anything if boat is not really moving
        if(getVelocity()<0.2){
            return;
        }

        double downWind=getDownWind(windAngle);

        double turnAngle=(getCurrentHeading()-windAngle)*blownFactor;

        if(currentHeading.getValue() >= windAngle && currentHeading.getValue() < downWind) {

            currentHeading.setValue(currentHeading.getValue() + turnAngle);
        }
        else{
            currentHeading.setValue(currentHeading.getValue() - turnAngle);
        }
        System.out.println(getCurrentHeading());
        setCurrentHeading(currentHeading.getValue() % 360);
    }

    /**
     * function to change boats heading
     * @param upwind true=upwind
     *                  false=downwind
     * @param windAngle the current wind angle
     */
    public void changeHeading(boolean upwind, double windAngle){
        int turnAngle = 3;


        double downWind = getDownWind(windAngle);

        if(currentHeading.getValue() >= windAngle && currentHeading.getValue() <= downWind) {
            if(upwind) {
                currentHeading.setValue(currentHeading.getValue() - turnAngle);
            }
            else {
                currentHeading.setValue(currentHeading.getValue() + turnAngle);
            }
        }
        else {

            if(upwind) {
                currentHeading.setValue(currentHeading.getValue() + turnAngle);
            }
            else {
                currentHeading.setValue(currentHeading.getValue() - turnAngle);
            }
        }
        setCurrentHeading(currentHeading.getValue() % 360);
//        currentHeading.setValue(currentHeading.getValue() % 360);
    }

    @Override
    public String toString() {
        return "Boat{" +
                "teamName='" + teamName + '\'' +
                ", velocity=" + velocity +
                ", position=" + position +
                ", color=" + color +
                ", abbreName='" + abbreName + '\'' +
                ", currentHeading=" + currentHeading +
                ", sourceID=" + sourceID +
                '}';
    }
}