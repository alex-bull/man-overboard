package seng302.Model;

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
     * @param x Double the x coordinate
     * @param y Double the y coordinate
     */
    public MutablePoint(Double x, Double y) {
        this.x.setValue(x);
        this.y.setValue(y);
    }

    /**
     * Sets the value of the x property
     * @param x Double the new value
     */
    public void setX(Double x) {
        this.x.setValue(x);
    }

    /**
     * Sets the value of the y property
     * @param y Double the new value
     */
    public void setY(Double y) {
        this.y.setValue(y);
    }

    /**
     * Getter for the x property
     * @return DoubleProperty x
     */
    public DoubleProperty getX () {
        return this.x;
    }

    /**
     * Getter for the x value
     * @return Double x value
     */
    public Double getXValue () {
        return this.x.getValue();
    }

    /**
     * Getter for the y property
     * @return DoubleProperty y
     */
    public DoubleProperty getY () {
        return this.y;
    }

    /**
     * Getter for the y value
     * @return Double y value
     */
    public Double getYValue () {
        return this.y.getValue();
    }


    /**
     * Factors MutablePoint object
     * @param xFactor
     * @param yFactor
     */
    public void factor(double xFactor,double yFactor,double minX,double minY){
        setX((getXValue()-minX)*xFactor);
        setY((getYValue()-minY)*yFactor);
//        System.out.println(getXValue());
//        System.out.println(getYValue());
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MutablePoint point = (MutablePoint) o;

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
