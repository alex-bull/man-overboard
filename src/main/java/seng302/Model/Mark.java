package seng302.Model;


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
    private boolean isFinish=false;
    private boolean isLine = false;
    private int index;


    /**
     * Creates a course mark
     * @param name String the name of the mark
     * @param pixelLocation MutablePoint the coordinates of the mark

     */
    public Mark (String name, MutablePoint pixelLocation, MutablePoint GPSLocation, int index) {
        this.name = name;
        this.pixelLocation = pixelLocation;
        this.GPSLocation = GPSLocation;
        this.index=index;
    }

    /**
     * Always returns false for a mark
     * @return boolean false there is no line for a mark
     */
    public boolean isLine() {
        return false;
    }

    /**
     * Getter for isFinish flag
     * @return boolean isFinish
     */
    public boolean isFinish() {
        return this.isFinish;
    }

    /**
     * Getter for the mark name
     * @return String the name
     */
    public String getName () {
        return this.name;
    }

    /**
     * Getter for the course pixelLocation
     * @return MutablePoint, the coordinates of the mark
     */
    public List<MutablePoint> getPixelLocations() {

        List<MutablePoint> points = new ArrayList<>();
        points.add(this.pixelLocation);
        return points;
    }

    /**
     * Getter for the centre GPS Location of the marker
     * @return MutablePoint the GPS Location
     */
    public MutablePoint getGPSCentre() {

        return this.GPSLocation;
    }

    /**
     * Setter for the index of the mark
     * @return int index of the mark
     */

    public int getIndex () {
        return this.index;
    }


    /**
     * Factors point to fit the screen
     * @param xFactor double the factor to scale by in the x axis
     * @param yFactor double the factor to scale by in the y axis
     * @param minX double the min x value
     * @param minY double the min y value
     */
    @Override
    public void factor(double xFactor, double yFactor,double minX, double minY,double xBuffer,double yBuffer,double width,double height) {
        System.out.println("name: "+name);
        pixelLocation.factor(xFactor,yFactor,minX,minY,xBuffer,yBuffer,width,height);
    }

    /**
     * Sets the exitHeading property
     * @param exitHeading Double the direction in which competitors exit the mark.
     */
    public void setExitHeading(Double exitHeading) {
        this.exitHeading = exitHeading;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Mark mark = (Mark) o;

        if (isFinish != mark.isFinish) return false;
        if (name != null ? !name.equals(mark.name) : mark.name != null) return false;
        return pixelLocation != null ? pixelLocation.equals(mark.pixelLocation) : mark.pixelLocation == null;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (pixelLocation != null ? pixelLocation.hashCode() : 0);
        result = 31 * result + (isFinish ? 1 : 0);
        return result;
    }

    /**
     * Getter for the exitHeading property
     * @return Double the heading
     */
    public double getExitHeading () {
        return this.exitHeading;
    }

}