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
    private int currentLegIndex;



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
        currentLegIndex=0;
    }

    /**
     * Sets the current Leg Index
     * @param currentLegIndex the current leg index
     */
    public void setCurrentLegIndex(int currentLegIndex) {
        this.currentLegIndex = currentLegIndex;
    }


    /**
     * Getter for Current Leg Index
     * @return the current leg index
     */
    public int getCurrentLegIndex() {

        return currentLegIndex;
    }


    /**
     * Getter for the boat's team name
     *
     * @return String The name of the boat team
     */
    public String getTeamName() {
        return this.teamName;
    }

    /**
     * Setter for the boat's velocity
     *
     * @param velocity boat's velocity m/s
     */
    public void setVelocity(int velocity) {
        this.velocity = velocity;
    }


    /**
     * Getter for the boats velocity
     *
     * @return int the velocity in m/s
     */
    public double getVelocity() {
        return this.velocity;
    }

    /**
     * Getter for the boat's position
     *
     * @return MutablePoint the coordinate of the boat
     */
    public MutablePoint getPosition() {
        return this.position;
    }

    /**
     * Sets the new position of the boat
     *
     * @param newPos the new position of the boat
     */
    private void setPosition(MutablePoint newPos) {
        this.position = newPos;
    }

    /**
     * Getter for the abbreviated team name
     *
     * @return String the abbreviated team name
     */
    @Override
    public String getAbbreName() {
        return abbreName;
    }

    /**
     * Getter for the team color
     *
     * @return Color the team color
     */
    @Override
    public Color getColor() {
        return color;
    }

    /**
     * Setter for the current heading
     *
     * @param currentHeading double the angle of the heading
     */
    public void setCurrentHeading(double currentHeading) {
        this.currentHeading.setValue(currentHeading);
    }

    /**
     * Getter for the current heading
     *
     * @return double the current heading
     */
    public double getCurrentHeading() {
        return currentHeading.getValue();
    }

    /**
     * Getter for the double property of the heading
     *
     * @return DoubleProperty the heading property
     */
    public DoubleProperty getHeadingProperty() {
        return this.currentHeading;
    }

    /**
     * updates the boats position given the time changed
     *
     * @param dt the time elapsed in seconds
     */
    public void updatePosition(double dt) {
        //radius of earth in km
        int R = 6371;
        //find distance travelled in kilometers
        double distance = velocity * dt / 1000;
        //turn everything to radians
        double lat1 = position.getXValue()* Math.PI / 180;
        double lng1 = position.getYValue()* Math.PI / 180;
        //turn bearing into radians
        double bearing = currentHeading.getValue() * Math.PI / 180;

        //calculate new positions
        double lat2 = Math.asin(Math.sin(lat1) * Math.cos(distance / R) +
                Math.cos(lat1) * Math.sin(distance / R) * Math.cos(bearing));

        double lng2 = lng1 + Math.atan2(Math.sin(bearing) * Math.sin(distance / R) * Math.cos(lat1), Math.cos(distance / R) - Math.sin(lat1) * Math.sin(lat2));

        //turn the new lat and lng back to degress
        setPosition(new MutablePoint(lat2*180/Math.PI, lng2*180/Math.PI));

    }
}