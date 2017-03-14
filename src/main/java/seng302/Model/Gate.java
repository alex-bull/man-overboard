package seng302.Model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by msl47 on 15/03/17.
 * Represents a gate.
 */


public class Gate implements CourseFeature {

    private String name;
    private Point point1;
    private Point point2;
    private Double exitHeading;
    private boolean isFinish;

    /**
     * Creates a course gate
     * @param name String the name of the gate
     * @param point1 Point the coordinates of one end.
     * @param point2 Point the coordinates of the other end.
     */
    public Gate (String name, Point point1, Point point2) {
        this.name = name;
        this.point1 = point1;
        this.point2 = point2;
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
     * Getter for the gate name
     * @return String the name
     */
    public String getName () {
        return this.name;
    }

    /**
     * Creates a list of points
     * Adds two points to an array
     * @return List of points
     */

    public List<Point> getLocations () {

        List<Point> points = new ArrayList<>();
        points.add(this.point1);
        points.add(this.point2);
        return points;
    }


    /**
     * Sets the exitHeading property
     * @param exitHeading Double the direction in which competitors exit the gate.
     */
    public void setExitHeading(Double exitHeading) {
        this.exitHeading = exitHeading;
    }

//    @Override
//    public boolean equals(Object o) {
//        if (this == o) return true;
//        if (o == null || getClass() != o.getClass()) return false;
//
//        Gate gate = (Gate) o;
//
//        if (isFinish != gate.isFinish) return false;
//        if (name != null ? !name.equals(gate.name) : gate.name != null) return false;
//        return location != null ? location.equals(gate.location) : gate.location == null;
//    }
//
//    @Override
//    public int hashCode() {
//        int result = name != null ? name.hashCode() : 0;
//        result = 31 * result + (location != null ? location.hashCode() : 0);
//        result = 31 * result + (isFinish ? 1 : 0);
//        return result;
//    }

    /**
     * Getter for the exitHeading property

     * @return Double the heading
     */
    public Double getExitHeading () {
        return this.exitHeading;
    }

}