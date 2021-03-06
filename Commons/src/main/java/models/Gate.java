package models;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by msl47 on 15/03/17.
 * Represents a gate course feature.
 */

public class Gate implements CourseFeature {

    private String name;
    private MutablePoint pixelPoint1;
    private MutablePoint pixelPoint2;
    private MutablePoint GPSPoint1;

    private double exitHeading;
    private boolean isFinish;
    private boolean isLine;
    private int index;
    private int rounding;

    /**
     * Creates a course gate
     *
     * @param name        String the name of the gate
     * @param GPSPoint1   MutablePoint the GPS location of one end
     * @param pixelPoint1 MutablePoint the scaled pixel coordinates of one end.
     * @param pixelPoint2 MutablePoint the scaled pixel coordinates of the other end.
     * @param isFinish    boolean true if the gate is a finishing gate
     * @param isLine      boolean true if the gate needs a line
     * @param index       int the index of the gate
     */
    public Gate(String name, MutablePoint GPSPoint1, MutablePoint pixelPoint1, MutablePoint pixelPoint2, boolean isFinish, boolean isLine, int index) {

        this.name = name;
        this.GPSPoint1 = GPSPoint1;
        this.pixelPoint1 = pixelPoint1;
        this.pixelPoint2 = pixelPoint2;
        this.isFinish = isFinish;
        this.isLine = isLine;
        this.index = index;
    }

    public boolean isLine() {
        return isLine;
    }

    @Override
    public int getRounding() {
        return this.rounding;
    }

    @Override
    public void setRounding(int rounding) {
        this.rounding = rounding;
    }

    public String getName() {
        return this.name;
    }

    public int getIndex() {
        return this.index;
    }


    /**
     * Gets a list of scaled pixel points representing the screen location of the gate
     *
     * @return List the pixel points
     */
    public List<MutablePoint> getPixelLocations() {

        List<MutablePoint> points = new ArrayList<>();
        points.add(this.pixelPoint1);
        points.add(this.pixelPoint2);
        return points;
    }

    /**
     * Getter for the centre GPS location of the marker
     *
     * @return MutablePoint the GPS location
     */
    public MutablePoint getGPSCentre() {

        return this.GPSPoint1;
    }

    /**
     * Get the centre point in the view frame
     *
     * @return MutablePoint
     */
    public MutablePoint getPixelCentre() {
        return new MutablePoint((this.pixelPoint1.getXValue() + this.pixelPoint2.getXValue()) / 2,
                (this.pixelPoint1.getYValue() + this.pixelPoint2.getYValue()) / 2);
    }

    /**
     * Scales the points to fit the screen
     *
     * @param xFactor double the factor to scale by in the x axis
     * @param yFactor double the factor to scale by in the y axis
     * @param minX    double the min x value
     * @param minY    double the min y value
     */
    @Override
    public MutablePoint factor(double xFactor, double yFactor, double minX, double minY, double xBuffer, double yBuffer) {
        pixelPoint1.factor(xFactor, yFactor, minX, minY, xBuffer, yBuffer);

        return pixelPoint2.factor(xFactor, yFactor, minX, minY, xBuffer, yBuffer);
    }

    @Override
    public void setPixelLocation(MutablePoint newLocation) {

    }

    @Override
    public void setPixelLocation(double x, double y) {

    }

    /**
     * Compares gate objects's name, isFinish and loation to see if they are equal.
     *
     * @param o Object
     * @return boolean true if the objects are equal
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Gate gate = (Gate) o;

        return isFinish == gate.isFinish && (name != null ? name.equals(gate.name) : gate.name == null) &&
                (pixelPoint1 != null ? pixelPoint1.equals(gate.pixelPoint1) : gate.pixelPoint1 == null) &&
                (pixelPoint2 != null ? pixelPoint2.equals(gate.pixelPoint2) : gate.pixelPoint2 == null);
    }

    /**
     * Generates a hashcode with a built in algorithm from Intellij
     *
     * @return int a hashcode
     */
    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (pixelPoint1 != null ? pixelPoint1.hashCode() : 0);
        result = 31 * result + (pixelPoint2 != null ? pixelPoint2.hashCode() : 0);
        result = 31 * result + (isFinish ? 1 : 0);
        return result;
    }

    /**
     * Getter for the exitHeading property
     *
     * @return Double the heading
     */
    public double getExitHeading() {
        return this.exitHeading;
    }

    /**
     * Sets the exitHeading property
     *
     * @param exitHeading Double the direction in which competitors exit the gate.
     */
    public void setExitHeading(Double exitHeading) {
        this.exitHeading = exitHeading;
    }

    @Override
    public String toString() {
        return "Gate{" +
                "name='" + name + '\'' +
                ", pixelPoint1=" + pixelPoint1 +
                ", pixelPoint2=" + pixelPoint2 +
                ", index=" + index +
                '}';
    }

    @Override
    public MutablePoint getGPSPoint() {
        return GPSPoint1;
    }

    @Override
    public CourseFeature shift(double x, double y) {
        return new Gate(name, GPSPoint1, pixelPoint1.shift(x, y), pixelPoint2.shift(x, y), isFinish, isLine, index);
    }

    @Override
    public void shiftInPlace(double x, double y) {
        pixelPoint1.shiftInPlace(x, y);
        pixelPoint2.shiftInPlace(x, y);
    }
}