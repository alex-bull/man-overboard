package seng302.Model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by msl47 on 15/03/17.
 * Represents a gate course feature.
 */


public class Gate implements CourseFeature {

    private String name;
    private MutablePoint point1;
    private MutablePoint point2;
    private Double exitHeading;
    private boolean isFinish;
    private boolean isLine;
    private int index;



    /**
     * Creates a course gate
     * @param name String the name of the gate
     * @param point1 Point the coordinates of one end.
     * @param point2 Point the coordinates of the other end.
     * @param isFinish boolean true if the gate is a finishing gate
     * @param isLine boolean true if the gate needs a line
     */
    public Gate (String name, MutablePoint point1, MutablePoint point2, boolean isFinish, boolean isLine) {
        this.name = name;
        this.point1 = point1;
        this.point2 = point2;
        this.isFinish = isFinish;
        this.isLine = isLine;
    }

    /**
     * Getter for isLine flag
     * @return boolean isLine if gate needs line
     */
    public boolean isLine(){
        return isLine;
    }

    /**
     * Getter for isFinish flag
     * @return boolean isFinish
     */
    public boolean isFinish() {
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
     * Getter for index of the gate
     * @return int the index
     */

    public int getIndex () {
        return this.index;
    }

    /**
     * Setter for the index of the gate
     * @param index int the index of the gate
     */
    public void setIndex (int index) {
        this.index = index;
    }

    /**
     * Creates a list of points
     * Adds two points to an array
     * @return List of points
     */

    public List<MutablePoint> getLocations () {

        List<MutablePoint> points = new ArrayList<>();
        points.add(this.point1);
        points.add(this.point2);
        return points;
    }

    /**
     * Getter for the centre location of the marker
     * @return MutablePoint the location
     */
    public MutablePoint getCentre () {

        return new MutablePoint((this.point1.getXValue()+this.point2.getXValue())/2,
                (this.point1.getYValue()+this.point2.getYValue())/2);
    }


    /**
     * Sets the exitHeading property
     * @param exitHeading Double the direction in which competitors exit the gate.
     */
    public void setExitHeading(Double exitHeading) {
        this.exitHeading = exitHeading;
    }

    /**
     * Scales the points to fit the screen
     * @param xFactor double the factor to scale by in the x axis
     * @param yFactor double the factor to scale by in the y axis
     * @param minX double the min x value
     * @param minY double the min y value
     */
    @Override
    public void factor(double xFactor, double yFactor, double minX,double minY,double xBuffer,double yBuffer,double width,double height) {
        System.out.println("name: "+name);
        point1.factor(xFactor,yFactor,minX,minY,xBuffer,yBuffer,width,height);
        point2.factor(xFactor,yFactor,minX,minY,xBuffer,yBuffer,width,height);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Gate gate = (Gate) o;

        if (isFinish != gate.isFinish) return false;
        if (name != null ? !name.equals(gate.name) : gate.name != null) return false;
        return (point1 != null ? point1.equals(gate.point1) : gate.point1 == null) && (point2 != null ? point2.equals(gate.point2) : gate.point2 == null);
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (point1 != null ? point1.hashCode() : 0);
        result = 31 * result + (point2 != null ? point2.hashCode() : 0);
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