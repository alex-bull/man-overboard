package seng302.Model;


import java.util.ArrayList;
import java.util.List;

/**
 * Created by mgo65 on 4/03/17.
 * Represents a course mark
 */
public class Mark implements CourseFeature {

    private String name;
    private Point location;
    private Double exitHeading;
    private boolean isFinish;

    /**
     * Creates a course mark
     * @param name String the name of the mark
     * @param location Point the coordinates of the mark
     * @param isFinish boolean true if mark is finish gate
     */
    public Mark (String name, Point location, boolean isFinish) {
        this.name = name;
        this.location = location;
        this.isFinish = isFinish;
    }

    /**
     * Creates a course mark
     * @param name String the name of the mark
     * @param location Point the coordinates of the mark
     */
    public Mark (String name, Point location) {
        this.name = name;
        this.location = location;
        this.isFinish = false;
    }

    /**
     * Getter for isFinish flag
     * @return boolean isFinish
     */
    public boolean getIsFinish () {
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
     * @return Point, the coordinates of the mark
     */
    public List<Point> getLocations () {

        List<Point> points = new ArrayList<>();
        points.add(this.location);
        return points;
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