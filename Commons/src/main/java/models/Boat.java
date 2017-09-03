package models;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.paint.Color;
import parsers.BoatStatusEnum;
import javafx.scene.shape.Line;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by mgo65 on 3/03/17.
 * Boat object
 */
public class Boat implements Competitor {


    final int earthRadius = 6371;

    private String teamName;
    private double velocity;
    private MutablePoint position;
    private MutablePoint position17;
    private Color color;
    private String abbreName;
    private DoubleProperty currentHeading = new SimpleDoubleProperty();
    private int sourceID;
    private BoatStatusEnum status;
    private String lastMarkPassed;
    private int legIndex;
    private long timeToNextMark;
    private long timeAtLastMark;
    private double latitude;
    private double longitude;
    private boolean isRounding = false;

    private Line roundingLine1;
    private Line roundingLine2;
    //how much the boat if affected by wind, can be parsed in as constructor
    private double blownFactor = 0.01;
    private int healthLevel = 100;
    private int maxHealth = 100;
    private Force boatSpeed;


    //collision size
    private double collisionRadius=20;

    //external forces
    private List<Force> externalForces=new ArrayList<>();



    public MutablePoint getPosition17() {
        return position17;
    }

    public void setPosition17(MutablePoint position17) {
        this.position17 = position17;
    }

    private boolean sailsOut = true;
    private double sailValue = 4;

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
        this.boatSpeed=new Force(velocity,0,false);
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
     * @param status        BoatStatusEnum status of the boat
     */
    public Boat(String teamName, int velocity, MutablePoint startPosition, String abbreName, int sourceID, BoatStatusEnum status) {
        this.boatSpeed=new Force(velocity,0,false);
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
        this.boatSpeed=new Force(0,0,false);
    }


    public Line getRoundingLine1() {
        return roundingLine1;
    }

    public void setRoundingLine1(Line roundingLine1) {
        this.roundingLine1 = roundingLine1;
    }

    public Line getRoundingLine2() {
        return roundingLine2;
    }

    public void setRoundingLine2(Line roundingLine2) {
        this.roundingLine2 = roundingLine2;
    }

    public void setMaxHealth(int health){
        this.maxHealth = health;
    }

    public void setHealthLevel(int health){
        this.healthLevel = health;
    }

    public int getMaxHealth() {
        return maxHealth;
    }

    public int getHealthLevel() {
        return healthLevel;
    }

    /**
     * updates the boat health when they collide or round
     * @param delta int the amount the boat health changes by
     */
    public void updateHealth(int delta) {
        int resultHealth = healthLevel + delta;

        if(resultHealth > maxHealth) {
           this.healthLevel = maxHealth;
        } else this.healthLevel = resultHealth;
    }

    /**
     * Switches the sail state of the boat between sails in and sails out
     */
    public void switchSails() {
        sailsOut = !sailsOut;
    }

    /**
     * Switches the sail state of the boat to be further in
     */
    public void sailsIn(){
        double minSailValue = 0;
        if(sailValue != minSailValue){
            sailValue -= 0.8;
        }
    }

    /**
     * Switches the sails state of the boat to be further out
     */
    public void sailsOut(){
        double maxSailValue = 4;
        if(sailValue != maxSailValue){
            sailValue += 0.8;
        }
    }

    /**
     * Returns the sail state of the boat
     * @return double - the value of the sail slider
     */
    public double getSailValue() { return sailValue; }

    public double getCollisionRadius() {
        return collisionRadius;
    }

    public void setBoatSpeed(Force boatSpeed) {
        this.boatSpeed = boatSpeed;
    }

    @Override
    public Force getBoatSpeed() {
        return boatSpeed;
    }

    @Override
    public BoatStatusEnum getStatus() {
        return status;
    }

    @Override
    public void setStatus(BoatStatusEnum status) {
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
        return boatSpeed.getMagnitude();
    }

    public void setVelocity(double velocity) {
        boatSpeed.setMagnitude(velocity);
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
        if (currentHeading < 0) {
            this.currentHeading.set(currentHeading+360);
//            boatSpeed.setDirection(currentHeading + 360);
        }
        else{
            this.currentHeading.set(currentHeading%360);
//            boatSpeed.setDirection(currentHeading%360);
        }
    }



    public void startRounding() {
        this.isRounding = true;
    }

    public void finishedRounding() {
        this.isRounding = false;
        this.legIndex += 1;
    }

    public boolean isRounding() {
        return this.isRounding;
    }


    /**
     * Updates the boats position given the time changed
     *
     * @param elapsedTime the time elapsed in seconds
     */
    public void updatePosition(double elapsedTime) {
        //moves the boat by its speed
        MutablePoint p=moveBoat(boatSpeed,getPosition(),elapsedTime);

        //calculate all external forces on it
        for(Force force:new ArrayList<>(externalForces)){
            p=moveBoat(force,p,elapsedTime);
            //reduce the external force
            force.reduce(0.95);
            if(force.getMagnitude()<0.1){
                externalForces.remove(force);
            }
        }
        setPosition(p);
//        System.out.println(p);
    }

    /**
     * moves the boat from the current position by the force
     * @param force the force which the boat is affected by
     * @param point the point at the start
     * @param elapsedTime the time period of this movement
     */
    private MutablePoint moveBoat(Force force,MutablePoint point, double elapsedTime){
        double distance = force.getMagnitude() * elapsedTime / 1000; // in km
        double lat1 = point.getXValue() * Math.PI / 180; // in radians
        double lng1 = point.getYValue() * Math.PI / 180;
        double bearing = force.getDirection() * Math.PI / 180;

        //calculate new positions
        double lat2 = Math.asin(Math.sin(lat1) * Math.cos(distance / earthRadius) +
                Math.cos(lat1) * Math.sin(distance / earthRadius) * Math.cos(bearing));
        double lng2 = lng1 + Math.atan2(Math.sin(bearing) * Math.sin(distance / earthRadius) * Math.cos(lat1),
                Math.cos(distance / earthRadius) - Math.sin(lat1) * Math.sin(lat2));

        //turn the new lat and lng back to degrees
        return new MutablePoint(lat2 * 180 / Math.PI, lng2 * 180 / Math.PI);
    }

    public void addForce(Force externalForce){
        externalForces.add(externalForce);
    }
    public void removeForce(Force externalForce){
        externalForces.remove(externalForce);
    }

    public List<Force> getExternalForces() {
        return externalForces;
    }

    /**
     * Returns the downwind given wind angle
     * @param windAngle double wind angle
     * @return double downWind
     */
    public double getDownWind(double windAngle) {
       double downWind = windAngle + 180;
       if(downWind > 360) {
           downWind = downWind - 360;
       }
       return downWind;
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
        double currentHeading=getCurrentHeading();
        if(currentHeading>= windAngle && currentHeading <= downWind) {
            if(upwind) {
                setCurrentHeading(currentHeading - turnAngle);
            }
            else {
                setCurrentHeading(currentHeading + turnAngle);
            }
        }
        else {
            if(upwind) {
                setCurrentHeading(currentHeading + turnAngle);
            }
            else {
                setCurrentHeading(currentHeading - turnAngle);
            }
        }
//        setCurrentHeading(currentHeading % 360);
//        System.out.println(currentHeading);
//        System.out.println(boatSpeed.getDirection());
//        currentHeading.setValue(currentHeading.getValue() % 360);
    }

    @Override
    public String toString() {
        return "Boat{" +
                "teamName='" + teamName + '\'' +
                ", position=" + position +
                ", position17=" + position17 +
                ", color=" + color +
                ", abbreName='" + abbreName + '\'' +
                ", sourceID=" + sourceID +
                ", status=" + status +
                ", lastMarkPassed='" + lastMarkPassed + '\'' +
                ", legIndex=" + legIndex +
                ", timeToNextMark=" + timeToNextMark +
                ", timeAtLastMark=" + timeAtLastMark +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", blownFactor=" + blownFactor +
                ", boatSpeed=" + boatSpeed +
                ", sailsOut=" + sailsOut +
                '}';
    }
}