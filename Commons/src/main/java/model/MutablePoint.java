package model;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;

/**
 * Created by mgo65 on 6/03/17.
 * Represents a moving point on a cartesian plane
 */
public class MutablePoint {

    private DoubleProperty x = new SimpleDoubleProperty();
    private DoubleProperty y = new SimpleDoubleProperty();


    /**
     * Creates a point
     *
     * @param x Double the x coordinate
     * @param y Double the y coordinate
     */
    public MutablePoint(Double x, Double y) {
        this.x.setValue(x);
        this.y.setValue(y);
    }

    /**
     * Getter for the x property
     *
     * @return DoubleProperty x
     */
    public DoubleProperty getX() {
        return this.x;
    }

    /**
     * Sets the value of the x property
     *
     * @param x Double the new value
     */
    public void setX(Double x) {
        this.x.setValue(x);
    }

    /**
     * Getter for the x value
     *
     * @return Double x value
     */
    public double getXValue() {
        return this.x.getValue();
    }

    /**
     * Getter for the y property
     *
     * @return DoubleProperty y
     */
    public DoubleProperty getY() {
        return this.y;
    }

    /**
     * Sets the value of the y property
     *
     * @param y Double the new value
     */
    public void setY(Double y) {
        this.y.setValue(y);
    }

    /**
     * Getter for the y value
     *
     * @return Double y value
     */
    public double getYValue() {
        return this.y.getValue();
    }


    /**
     * Scales coordinates by a factor
     *
     * @param xFactor double the x factor
     * @param yFactor double the y factor
     * @param minX    double the min x value
     * @param minY    double the min y value
     */
    public void factor(double xFactor, double yFactor, double minX, double minY, double xBuffer, double yBuffer) {
        setX(((getXValue() - minX) * xFactor + xBuffer));
        setY(((getYValue() - minY) * yFactor + yBuffer));
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MutablePoint point = (MutablePoint) o;

        if (!(x.getValue().equals(point.x.getValue()))) return false;
        return y.getValue().equals(point.y.getValue());
    }

    @Override
    public int hashCode() {
        int result = x.hashCode();
        result = 31 * result + y.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "MutablePoint{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }

    /**
     * Checks in the current point is close enough to the other point, current EPSILON is 0.0002, longitude needs a higher
     * EPSILON
     *
     * @param o the other mutable point
     * @return true if this point is close enough to o
     */
    public boolean isWithin(MutablePoint o) {
        double EPSILON = 0.0002;
        if (Math.abs(getXValue() - o.getXValue()) < EPSILON && Math.abs(getYValue() - o.getYValue()) < EPSILON * 2) {
            return true;
        } else {
            return false;
        }
    }

}
