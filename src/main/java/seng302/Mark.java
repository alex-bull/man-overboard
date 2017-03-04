package seng302;

import javafx.util.Pair;

/**
 * Created by mgo65 on 4/03/17.
 * Represents a course marker
 */
public class Mark implements CoursePoint{

    private String name;
    private Pair<Double, Double> location;

    /**
     * Creates a course mark
     * @param name String the name of the mark
     * @param location Pair the coordinates of the mark
     */
    public Mark (String name, Pair<Double, Double> location) {
        this.name = name;
        this.location = location;
    }

    /**
     * Getter for the mark name
     * @return String the name
     */
    public String getName () {
        return this.name;
    }

    /**
     * Getter for the course location
     * @return Pair, the coordinates of the mark
     */
    public Pair<Double, Double> getLocation () {
        return this.location;
    }

}