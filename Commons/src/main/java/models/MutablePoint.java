package models;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;

import static java.lang.Math.sqrt;

/**
 * Created by mgo65 on 6/03/17.
 * Represents a moving point on a cartesian plane
 */
public class MutablePoint implements Vector{

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

    public DoubleProperty getX() {
        return this.x;
    }
    public void setX(Double x) {
        this.x.setValue(x);
    }
    public double getXValue() {
        return this.x.getValue();
    }
    public DoubleProperty getY() {
        return this.y;
    }
    public void setY(Double y) {
        this.y.setValue(y);
    }
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
     * @param paddingX double the x padding
     * @param paddingY double the y padding
     */
    public void factor(double xFactor, double yFactor, double minX, double minY, double paddingX, double paddingY) {
        setX(((getXValue() - minX) * xFactor + paddingX));
        setY(((getYValue() - minY) * yFactor + paddingY));
    }

    /**
     * Compares mutable point objects's coordinates to see if they are equal.
     * @param o Object
     * @return boolean true if the objects are equal
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MutablePoint point = (MutablePoint) o;
        return x.getValue().equals(point.x.getValue()) && y.getValue().equals(point.y.getValue());
    }


    /**
     * Generates a hashcode with a built in algorithm from Intellij
     * @return int a hashcode
     */
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
     * @param o the other mutable point
     * @return true if this point is close enough to o
     */
    public boolean isWithin(MutablePoint o) {
        double EPSILON = 0.0002;
        return Math.abs(getXValue() - o.getXValue()) < EPSILON && Math.abs(getYValue() - o.getYValue()) < EPSILON * 2;
    }

    public double getMagnitude(){
        return sqrt(getXValue()*getXValue()+getYValue()*getYValue());
    }
    /**
     * shifts the point by x,y
     * @param x the x value to be shifted by
     * @param y the y value to be shifted by
     * @return MutablePoint the shifted point
     */
    public MutablePoint shift(double x, double y){
        return new MutablePoint(getXValue()+x,getYValue()+y);
    }
}
