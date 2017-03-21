package seng302.Model;

import java.util.List;

/**
 * Created by psu43 on 15/03/17.
 * Represents a course for a Race
 */
public class RaceCourse implements Course {

    private List<CourseFeature> points;
    private double windDirection;

    /**
     * A constructor for the RaceCourse
     * @param points List points on the course
     */
    public RaceCourse(List<CourseFeature> points,double windDirection) {
        this.points = points;
        this.windDirection=windDirection;
        this.calculateHeadings();
    }

    public double getWindDirection() {
        return windDirection;
    }

    /**
     * Getter for the points in the course
     * @return List a list of course points
     */
    public List<CourseFeature> getPoints() {
        return points;
    }

    /**
     * Calculates exit headings of each course point and sets the course point property
     */
    private void calculateHeadings () {

        for (int j = 1; j < this.points.size() - 1; j++) {
            Double heading = calculateAngle(points.get(j).getCentre(), points.get(j + 1).getCentre());
            points.get(j).setExitHeading(heading);
        }
    }

    /**
     * Calculates the angle between two course points
     * @param start MutablePoint the coordinates of the first point
     * @param end MutablePoint the coordinates of the second point
     * @return Double the angle between the points from the y axis
     */
    private Double calculateAngle(MutablePoint start, MutablePoint end) {
        Double angle = Math.toDegrees(Math.atan2(end.getXValue() - start.getXValue(), end.getYValue() - start.getYValue()));

        if(angle < 0){
            angle += 360;
        }
        return angle;
    }


}