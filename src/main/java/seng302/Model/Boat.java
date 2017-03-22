package seng302.Model;

import javafx.scene.paint.Color;

/**
 * Created by mgo65 on 3/03/17.
 * Boat object
 */
public class Boat implements Competitor {
    private String teamName;
    private Integer velocity;
    private MutablePoint position;
    private Color color;
    private String abbreName;

    /**
     * gets the abbreviated team name
     * @return the abbreviated team name
     */
    @Override
    public String getAbbreName() {
        return abbreName;
    }

    /**
     * Getter for the team color
     * @return Color the team color
     */
    public Color getColor() {
        return color;
    }

    /**

     * Creates a boat
     *
     * @param teamName String the boats team name
     * @param velocity Integer the boats velocity in m/s
     */
    public Boat(String teamName, Integer velocity, MutablePoint startPosition, Color color, String abbreName) {
        this.velocity = velocity;
        this.teamName = teamName;
        this.position = startPosition;
        this.color = color;
        this.abbreName=abbreName;
    }

    /**
     * Getter for team name
     *
     * @return String The name of the boat team
     */
    public String getTeamName() {
        return this.teamName;
    }

    /**
     * Getter for the boats velocity
     *
     * @return Integer the velocity in m/s
     */
    public Integer getVelocity() {
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


}