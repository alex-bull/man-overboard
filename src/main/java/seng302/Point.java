package seng302;

/**
 * Created by mgo65 on 6/03/17.
 * Represents a point on a cartesian plane
 */
public class Point {

    private final Double x;
    private final Double y;

    public Point (Double x, Double y) {
        this.x = x;
        this.y = y;
    }

    public Double getX () {
        return this.x;
    }

    public Double getY () {
        return this.y;
    }
}
