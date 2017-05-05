package seng302.Model;

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
    private MutablePoint GPSPoint2;

    private double exitHeading;
    private boolean isFinish;
    private boolean isLine;
    private int index;
    private int rounding;
    private int sourceID;
    private int zoneSize;

    /**
     * Creates a course gate
     *
     * @param name        String the name of the gate
     * @param GPSPoint1   MutablePoint the GPS location of one end
     * @param GPSPoint2   MutablePoint the GPS location of the other end
     * @param pixelPoint1 MutablePoint the scaled pixel coordinates of one end.
     * @param pixelPoint2 MutablePoint the scaled pixel coordinates of the other end.
     * @param isFinish    boolean true if the gate is a finishing gate
     * @param isLine      boolean true if the gate needs a line
     */
    public Gate(String name, MutablePoint GPSPoint1, MutablePoint GPSPoint2, MutablePoint pixelPoint1, MutablePoint pixelPoint2, boolean isFinish, boolean isLine, int index) {

        this.name = name;
        this.GPSPoint1 = GPSPoint1;
        this.GPSPoint2 = GPSPoint2;
        this.pixelPoint1 = pixelPoint1;
        this.pixelPoint2 = pixelPoint2;
        this.isFinish = isFinish;
        this.isLine = isLine;
        this.index = index;
    }

    /**
     * Getter for isLine flag
     *
     * @return boolean isLine if gate needs line
     */
    public boolean isLine() {
        return isLine;
    }

    /**
     * Getter for rounding
     *
     * @return String rounding. Represents how the course feature is passed.
     */
    @Override
    public int getRounding() {
        return this.rounding;
    }

    /**
     * Setter for rounding
     *
     * @param rounding the rounding of the course feature
     */
    @Override
    public void setRounding(int rounding) {
        this.rounding = rounding;
    }

    /**
     * Getter for zoneSize
     *
     * @return int zoneSize
     */
    @Override
    public int getZoneSize() {
        return this.zoneSize;
    }

    /**
     * Setter for zoneSize
     *
     * @param zoneSize the zoneSize of the course feature
     */
    @Override
    public void setZoneSize(String zoneSize) {
        this.zoneSize = Integer.valueOf(zoneSize);
    }

    /**
     * Getter for isFinish flag
     *
     * @return boolean isFinish
     */
    public boolean isFinish() {
        return this.isFinish;
    }

    /**
     * Getter for the gate name
     *
     * @return String the name
     */
    public String getName() {
        return this.name;
    }

    /**
     * Getter for index of the gate
     *
     * @return int the index
     */
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

        return new MutablePoint((this.GPSPoint1.getXValue() + this.GPSPoint2.getXValue()) / 2,
                (this.GPSPoint1.getYValue() + this.GPSPoint2.getYValue()) / 2);
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
    public void factor(double xFactor, double yFactor, double minX, double minY, double xBuffer, double yBuffer) {
        pixelPoint1.factor(xFactor, yFactor, minX, minY, xBuffer, yBuffer);
        pixelPoint2.factor(xFactor, yFactor, minX, minY, xBuffer, yBuffer);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Gate gate = (Gate) o;

        if (isFinish != gate.isFinish) return false;
        if (name != null ? !name.equals(gate.name) : gate.name != null) return false;
        return (pixelPoint1 != null ? pixelPoint1.equals(gate.pixelPoint1) : gate.pixelPoint1 == null) && (pixelPoint2 != null ? pixelPoint2.equals(gate.pixelPoint2) : gate.pixelPoint2 == null);
    }

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
    public MutablePoint getGPSPoint() {
        return GPSPoint1;
    }
}