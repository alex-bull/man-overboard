package seng302.Model;

import java.util.List;

/**
 * Created by psu43 on 15/03/17.
 * Represents a course for a Race
 */
public class RaceCourse implements Course {

    private List<CourseFeature> points;
    private List<MutablePoint> boundaryPoints;
    private double windDirection;

    /**
     * A constructor for the RaceCourse
     * @param points List points on the course
     * @param boundaryPoints List the points that make up the course boundary
     * @param windDirection double the direction of the wind
     */
    public RaceCourse(List<CourseFeature> points, List<MutablePoint> boundaryPoints, double windDirection) {
        this.points = points;
        this.boundaryPoints = boundaryPoints;
        this.windDirection = windDirection;
        this.calculateHeadings();
    }

    /**
     * Get wind direction
     * @return double the angle of wind direction
     */
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
     * Getter for the boundary points
     * @return List the points that make up the boundary
     */
    public List<MutablePoint> getBoundaryPoints() {
        return this.boundaryPoints;
    }

    /**
     * Calculates exit headings of each course point and sets the course point property
     */
    private void calculateHeadings () {
        for (int j = 1; j < this.points.size() - 1; j++) {
            Double heading = calculateAngle(points.get(j).getGPSCentre(), points.get(j + 1).getGPSCentre());
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


    /**
     * Calculates the distance between two GPS points in metres
     * @param start MutablePoint the start coordinate
     * @param end MutablePoint the end coordinate
     * @return double the distance in metres
     */
    public double distanceBetweenGPSPoints(MutablePoint start, MutablePoint end) {

        double lat1 = start.getXValue();
        double lon1 = start.getYValue();
        double lat2 = end.getXValue();
        double lon2 = end.getYValue();

        final int R = 6371; // Radius of the earth

        Double latDistance = Math.toRadians(lat2 - lat1);
        Double lonDistance = Math.toRadians(lon2 - lon1);
        Double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        Double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c * 1000; // convert to metres
        return distance;
    }


}
