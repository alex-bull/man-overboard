package seng302.Model;

/**
 * Created by mgo65 on 3/03/17.
 * Boat object
 */
public class Boat implements Competitor{
    private String teamName;
    private Integer velocity;
    private Point position;

    /**
     * Creates a boat
     * @param teamName String the boats team name
     * @param velocity Integer the boats velocity in m/s
     */
    public Boat (String teamName, Integer velocity, Point startPosition) {
        this.velocity = velocity;
        this.teamName = teamName;
        this.position = startPosition;
    }

    /**
     * Getter for team name
     * @return String The name of the boat team
     */
    public String getTeamName () {
        return this.teamName;
    }

    /**
     * Getter for the boats velocity
     * @return Integer the velocity in m/s
     */
    public Integer getVelocity () {
        return this.velocity;
    }

    /**
     * Getter for the boat's position
     * @return Point the coordinate of the boat
     */
    public Point getPosition() {
        return this.position;
    }
}
