package models;


import java.util.ArrayList;
import java.util.List;

/**
 * Created by mgo65 on 4/03/17.
 * Represents a mark course feature
 */
public class Mark implements CourseFeature {

    private String name;
    private MutablePoint pixelLocation;
    private MutablePoint GPSLocation;
    private double exitHeading;
    private boolean isFinish = false;
    private int index;
    private int rounding;

    /**
     * Creates a course mark
     *
     * @param name          String the name of the mark
     * @param pixelLocation MutablePoint the pixel coordinates of the mark
     * @param GPSLocation   MutablePoint the GPS coordinates of the mark
     * @param index         int the index of the mark
     */
    public Mark(String name, MutablePoint pixelLocation, MutablePoint GPSLocation, int index) {
        this.name = name;
        this.pixelLocation = pixelLocation;
        this.GPSLocation = GPSLocation;
        this.index = index;
    }


    public boolean isLine() {
        return false;
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


    /**
     * Getter for the a list of pixel locations
     *
     * @return MutablePoint, the coordinates of the mark
     */
    public List<MutablePoint> getPixelLocations() {

        List<MutablePoint> points = new ArrayList<>();
        points.add(this.pixelLocation);
        return points;
    }

    /**
     * Get the centre point in the view frame
     * @return MutablePoint
     */
    public MutablePoint getPixelCentre() {
        return this.pixelLocation;
    }

    /**
     * Getter for the centre GPS Location of the marker
     *
     * @return MutablePoint the GPS Location
     */
    public MutablePoint getGPSCentre() {
        return this.GPSLocation;
    }

    /**
     * Setter for the index of the mark
     *
     * @return int index of the mark
     */

    public int getIndex() {
        return this.index;
    }


    /**
     * Factors point to fit the screen
     * @param xFactor double the factor to scale by in the x axis
     * @param yFactor double the factor to scale by in the y axis
     * @param minX    double the min x value
     * @param minY    double the min y value
     */
    @Override
    public void factor(double xFactor, double yFactor, double minX, double minY, double xBuffer, double yBuffer) {
        pixelLocation.factor(xFactor, yFactor, minX, minY, xBuffer, yBuffer);
    }

    /**
     * Compares mark objects's name, isFinish and location to see if they are equal.
     * @param o Object
     * @return boolean true if the objects are equal
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Mark mark = (Mark) o;
        return isFinish == mark.isFinish && (name != null ? name.equals(mark.name) : mark.name == null) &&
                (pixelLocation != null ? pixelLocation.equals(mark.pixelLocation) : mark.pixelLocation == null);
    }

    /**
     * Generates a hashcode with a built in algorithm from Intellij
     * @return int a hashcode
     */
    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (pixelLocation != null ? pixelLocation.hashCode() : 0);
        result = 31 * result + (isFinish ? 1 : 0);
        return result;
    }

    public double getExitHeading() {
        return this.exitHeading;
    }

    public void setExitHeading(Double exitHeading) {
        this.exitHeading = exitHeading;
    }

    @Override
    public MutablePoint getGPSPoint() {
        return GPSLocation;
    }

    @Override
    public String toString() {
        return "Mark{" +
                "name='" + name + '\'' +
                ", pixelLocation=" + pixelLocation +
                ", index=" + index +
                '}';
    }
}