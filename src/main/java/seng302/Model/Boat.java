package seng302.Model;

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
    private String type;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    /**
     * Creates a boat
     * @param teamName String the team name of the boat
     * @param velocity int the velocity of the boat in m/s
     * @param startPosition MutablePoint the boat's start position coordinate
     * @param color Color the boat colour
     * @param abbreName String the abbreviated name of the boat
     */
    public Boat(String teamName, int velocity, MutablePoint startPosition, Color color, String abbreName) {
        this.velocity = velocity;
        this.teamName = teamName;
        this.position = startPosition;
        this.color = color;
        this.abbreName=abbreName;
    }

    /**
     * Empty Constructor
     */
    public Boat(){

    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public void setAbbreName(String abbreName) {
        this.abbreName = abbreName;
    }

    public int getSourceID() {
        return sourceID;
    }

    public void setSourceID(int sourceID) {
        this.sourceID = sourceID;
    }

    /**
     * Parse the Source ID as a string
     * @param sourceID the Source ID as string
     */
    public void setSourceID(String sourceID) {
        this.sourceID = Integer.parseInt(sourceID);
    }

    /**
     * Getter for the boat's team name
     * @return String The name of the boat team
     */
    public String getTeamName() {
        return this.teamName;
    }

    /**
     * Setter for the boat's velocity
     * @param velocity boat's velocity m/s
     */
    public void setVelocity(double velocity) {
        this.velocity = velocity;
    }


    /**
     * Getter for the boats velocity
     * @return int the velocity in m/s
     */
    public double getVelocity() {
        return this.velocity;
    }

    /**
     * Getter for the boat's position
     * @return MutablePoint the coordinate of the boat
     */
    public MutablePoint getPosition() {
        return this.position;
    }


    public void setPosition(MutablePoint position) {
        this.position = position;
    }

    /**
     * Getter for the abbreviated team name
     * @return String the abbreviated team name
     */
    @Override
    public String getAbbreName() {
        return abbreName;
    }

    /**
     * Getter for the team color
     * @return Color the team color
     */
    @Override
    public Color getColor() {
        return color;
    }

    /**
     * Setter for the current heading
     * @param currentHeading double the angle of the heading
     */
    public void setCurrentHeading(double currentHeading) {
        this.currentHeading.setValue(currentHeading);
    }

    /**
     * Getter for the current heading
     * @return double the current heading
     */
    public double getCurrentHeading() {
        return currentHeading.getValue();
    }

    /**
     * Getter for the double property of the heading
     * @return DoubleProperty the heading property
     */
    public DoubleProperty getHeadingProperty() {
        return this.currentHeading;
    }

    public void setProperties(double velocity, double heading, double latitude, double longitude){
        this.velocity=velocity;
        this.currentHeading.setValue(heading);
        this.position=new MutablePoint(latitude,longitude);

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