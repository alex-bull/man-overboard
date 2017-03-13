package seng302.Model;

/**
 * Created by mgo65 on 6/03/17.
 * Represents a point on a cartesian plane
 */
public class Point {

    private final Double x;
    private final Double y;

    /**
     * Creates a point
     * @param x Double the x coordinate
     * @param y Double the y coordinate
     */
    public Point (Double x, Double y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Getter for the x value
     * @return Double x
     */
    public Double getX () {
        return this.x;
    }

    /**
     * Getter for the y value
     * @return Double y
     */
    public Double getY () {
        return this.y;
    }
}
