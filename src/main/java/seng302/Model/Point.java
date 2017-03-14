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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Point point = (Point) o;

        if (!x.equals(point.x)) return false;
        return y.equals(point.y);
    }

    @Override
    public int hashCode() {
        int result = x.hashCode();
        result = 31 * result + y.hashCode();
        return result;
    }
}
