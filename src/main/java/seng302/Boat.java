package seng302;

/**
 * Created by mgo65 on 3/03/17.
 * Boat object
 */
public class Boat implements Competitor{
    private String teamName;

    /**
     * Creates a boat with boat team name
     * @param teamName String name of team
     */
    public Boat (String teamName) {
        this.teamName = teamName;
    }

    /**
     * Getter for team name
     * @return String The name of the boat team
     */
    public String getTeamName () {
        return this.teamName;
    }
}
