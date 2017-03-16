package seng302.Model;

/**
 * Created by mgo65 on 3/03/17.
 * An interface for a competitor in a Race
 */
public interface Competitor {
    String getTeamName();
    Integer getVelocity();
    MutablePoint getPosition();
}
