package seng302.Model;


import java.util.ArrayList;
import java.util.List;

/**
 * Created by mgo65 on 4/03/17.
 * Represents a mark course feature
 */
public class Mark implements CourseFeature {

    private String name;
    private MutablePoint location;
    private Double exitHeading;
    private boolean isFinish;
    private boolean isLine = false;
    private int index;


    /**
     * Creates a course mark
     * @param name String the name of the mark
     * @param location MutablePoint the coordinates of the mark
     * @param isFinish boolean true if mark is finish gate
     */
    public Mark (String name, MutablePoint location, boolean isFinish) {
        this.name = name;
        this.location = location;
        this.isFinish = isFinish;
    }

    /**
     * Always returns false for a mark
     * @return boolean false there is no line for a mark
     */
    public boolean isLine() {
        return false;
    }

    /**
     * Creates a course mark
     * @param name String the name of the mark
     * @param location MutablePoint the coordinates of the mark
     */
    public Mark (String name, MutablePoint location) {
        this.name = name;
        this.location = location;
        this.isFinish = false;
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
     * Getter for the course location
     * @return MutablePoint, the coordinates of the mark
     */
    public List<MutablePoint> getLocations () {

        List<MutablePoint> points = new ArrayList<>();
        points.add(this.location);
        return points;
    }

    /**
     * Getter for the centre location of the marker
     * @return MutablePoint the location
     */
    public MutablePoint getCentre () {

        return this.location;
    }

    /**
     *
     * @return
     */

    public int getIndex () {
        return this.index;
    }

    /**
     *
     * @param index
     */

    public void setIndex (int index) {
        this.index = index;
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
        location.factor(xFactor,yFactor,minX,minY,xBuffer,yBuffer,width,height);
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
        return location != null ? location.equals(mark.location) : mark.location == null;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (location != null ? location.hashCode() : 0);
        result = 31 * result + (isFinish ? 1 : 0);
        return result;
    }

    /**
     * Getter for the exitHeading property
     * @return Double the heading
     */
    public Double getExitHeading () {
        return this.exitHeading;
    }

}