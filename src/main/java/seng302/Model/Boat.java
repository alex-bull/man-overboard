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
    public Boat(String teamName, Integer velocity, MutablePoint startPosition, Color color) {
        this.velocity = velocity;
        this.teamName = teamName;
        this.position = startPosition;
        this.color = color;
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