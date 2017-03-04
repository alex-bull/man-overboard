package seng302;

/**
 * Created by mgo65 on 3/03/17.
 * Boat object
 */
public class Boat implements Competitor{
    private String teamName;
    private Integer velocity;
    private Integer scaleFactor = 3;

    /**
     * Creates a boat with boat team name
     * @param teamName String name of team
     */
    public Boat (String teamName, Integer velocity) {
        this.velocity = velocity * scaleFactor;
        this.teamName = teamName;
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
}
