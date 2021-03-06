package utilities;

import javafx.geometry.Point2D;
import javafx.scene.shape.Polygon;
import models.Competitor;
import models.CourseFeature;
import models.MutablePoint;
import models.Vector2D;
import parsers.Converter;

import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.lang.Math.*;

/**
 * Created by psu43 on 25/05/17.
 * Holds all of the calculations for the race
 */
public class RaceCalculator {

    /**
     * Calculates the boat's direction when screen was touched
     *
     * @param boatX  x value of boat's position
     * @param boatY  y value of boat's position
     * @param touchX x value of touch point's position
     * @param touchY y value of touch point's position
     * @return double theta
     */
    public static double calcBoatDirection(double boatX, double boatY, double touchX, double touchY) {
        Vector2D boatDirection = new Vector2D(boatX, boatY, touchX, touchY);
        double theta = atan2(boatDirection.getY(), boatDirection.getX());
        theta = (theta * 180 / PI) + 90;
        if (theta < 0) {
            theta = 360 + theta;
        }

        return theta;
    }

    /**
     * Calculates whether the boat is currently west of wind or not
     *
     * @param heading   boat's heading
     * @param downWind  angle of down wind
     * @param windAngle angle of wind
     * @return boolean
     */
    public static boolean isWestOfWind(double heading, double downWind, double windAngle) {

        if (windAngle > 180) {
            return (heading < windAngle) && (heading > downWind);
        }
        else {
            return (heading > downWind) || (heading < windAngle);
        }
    }

    /**
     * Calculates the point of intersection of the boat's heading line and the start line.
     *
     * @param boatFront  Point2D The front of the boat.
     * @param boatBack   Point2D The back of the boat.
     * @param startMark1 MutablePoint One end of the start line.
     * @param startMark2 MutablePoint The other end of the start line.
     * @return MutablePoint Point of intersection.
     */
    public static MutablePoint calcStartLineIntersection(Point2D boatFront, Point2D boatBack, MutablePoint startMark1, MutablePoint startMark2) {

        double headingGradient;
        double xDiffBoat = boatFront.getX() - boatBack.getX();
        if (xDiffBoat == 0) {

//           TODO: make this work properly with Double.MAX_VALUE or POSITIVE_INFINITY
//            headingGradient = Double.MAX_VALUE;
            headingGradient = 1000000;
        } else {
            headingGradient = (boatFront.getY() - boatBack.getY()) / xDiffBoat;
        }
        double headingIntercept = boatFront.getY() - (headingGradient * boatFront.getX());

        double startLineGradient;
        double xDiffStart = startMark1.getXValue() - startMark2.getXValue();
        if (xDiffStart == 0) {
            startLineGradient = 10000000;
        } else {
            startLineGradient = (startMark1.getYValue() - startMark2.getYValue()) / xDiffStart;
        }
        double startLineIntercept = startMark1.getYValue() - (startLineGradient * startMark1.getXValue());

        double gradDiff = headingGradient - startLineGradient;

        double intersectionX;
        if (gradDiff == 0) {
            intersectionX = boatFront.getX();
        } else {
            intersectionX = (startLineIntercept - headingIntercept) / gradDiff;
        }
        double intersectionY = headingGradient * intersectionX + headingIntercept;


        return new MutablePoint(intersectionX, intersectionY);
    }

    /**
     * Calculates the position of a point on the virtual start line corresponding to a point on the real start line.
     *
     * @param ratio       double The ratio of the distance from the boat to virtual and real start lines.
     * @param xDifference double The difference along the x-axis between the front of the boat and point of intersection with the start line.
     * @param yDifference double The difference along the y-axis between the front of the boat and point of intersection with the start line.
     * @param startMark   MutablePoint A point on the real start line.
     * @return MutablePoint A point on the virtual start line.
     */
    public static MutablePoint calcVirtualLinePoint(double ratio, double xDifference, double yDifference, MutablePoint startMark) {
        double startToVirtualX = (1 - ratio) * xDifference;
        double startToVirtualY = (1 - ratio) * yDifference;

        double virtualLineX = startMark.getXValue() + startToVirtualX;
        double virtualLineY = startMark.getYValue() + startToVirtualY;

        return new MutablePoint(virtualLineX, virtualLineY);
    }

    /**
     * Calculates the estimated time to the mark and compares it to expected race start time.
     *
     * @param distanceToStart double the Distance to the a point on a start line
     * @param distanceToEnd   double the Distance to the a point on a start line
     * @param velocity        double the speed of the boat
     * @param timeUntilStart  long the time until start
     * @return String "-" if the boat is going to cross early, "+" if late and "" if within 5 seconds.
     */
    public static String calculateStartSymbol(double distanceToStart, double distanceToEnd, double velocity, long timeUntilStart) {
        int timeBound = 5;
        double selectedTime;

        if (distanceToStart < distanceToEnd) {
            selectedTime = (distanceToStart / velocity);
        } else {
            selectedTime = (distanceToEnd / velocity);
        }

        if (selectedTime < timeUntilStart) {
            return "-";
        } else if (selectedTime > (timeUntilStart + timeBound)) {
            return "+";
        } else {
            return "";
        }
    }

    /**
     * Calculates the distance in metres between a pair of coordinates.
     *
     * @param latitude1  first point's latitude
     * @param longitude1 first point's longitude
     * @param latitude2  second point's latitude
     * @param longitude2 second point's longitude
     * @return double distance (m)
     */
    public static double calcDistBetweenGPSPoints(double latitude1, double longitude1, double latitude2, double longitude2) {
        long earthRadius = 6371000;
        double phiStart = Math.toRadians(latitude2);
        double phiBoat = Math.toRadians(latitude1);

        double deltaPhi = Math.toRadians(latitude1 - latitude2);
        double deltaLambda = Math.toRadians(longitude1 - longitude2);

        double a = sin(deltaPhi / 2) * sin(deltaPhi / 2) + cos(phiStart) * cos(phiBoat) * sin(deltaLambda / 2) * sin(deltaLambda / 2);
        double c = 2 * atan2(sqrt(a), sqrt(1 - a));
        return earthRadius * c;
    }


    /**
     * Calculates the angle between marks
     *
     * @param xDist Double the distance between marks in the x direction
     * @param yDist Double the distance between marks in the y direction
     * @return double the angle
     */
    public static double calculateAngleBetweenMarks(Double xDist, Double yDist) {
        double arctan = atan(yDist / xDist);
        if (arctan < 0) {
            arctan += 2 * Math.PI;
        }
        double angle = toDegrees(arctan);

        if (xDist < 0) {
            angle += 90;
        } else {
            angle += 270;
        }
        return angle;
    }


    /**
     * Gets the centre coordinates for a mark or gate
     *
     * @param markIndex  index of the mark (based on the order they are rounded)
     * @param indexMap   Map of index to sourceId
     * @param featureMap Map of sourceId to courseFeature
     * @return MutablePoint (x,y) coordinates
     */
    public static MutablePoint getGateCentre(Integer markIndex, Map<Integer, List<Integer>> indexMap, Map<Integer, CourseFeature> featureMap) {

        Map<Integer, List<Integer>> features = indexMap;
        if (markIndex > features.size()) return null; //passed the finish line

        List<Integer> ids = features.get(markIndex);
        if (ids == null) return null;
        CourseFeature featureOne = featureMap.get(ids.get(0));
        Double markX = featureOne.getPixelCentre().getXValue();
        Double markY = featureOne.getPixelCentre().getYValue();

        if (ids.size() > 1) { //Get the centre point of gates
            CourseFeature featureTwo = featureMap.get(ids.get(1));
            markX = (featureOne.getPixelCentre().getXValue() + featureTwo.getPixelCentre().getXValue()) / 2;
            markY = (featureOne.getPixelCentre().getYValue() + featureTwo.getPixelCentre().getYValue()) / 2;
        }
        return new MutablePoint(markX, markY);
    }


    public static List<MutablePoint> getMarkCentres(Integer markIndex, Map<Integer, List<Integer>> indexMap, Map<Integer, CourseFeature> featureMap) {
        if (markIndex > indexMap.size()) return null; //passed the finish line

        List<MutablePoint> centres = new ArrayList<>();

        List<Integer> ids = indexMap.get(markIndex);
        if (ids == null) return null;
        CourseFeature featureOne = featureMap.get(ids.get(0));

        Double markX = featureOne.getPixelCentre().getXValue();
        Double markY = featureOne.getPixelCentre().getYValue();
        centres.add(new MutablePoint(markX, markY));

        if (ids.size() > 1) { //Get the centre point of gates
            CourseFeature featureTwo = featureMap.get(ids.get(1));
            Double markI = featureTwo.getPixelCentre().getXValue();
            Double markK = featureTwo.getPixelCentre().getYValue();
            centres.add(new MutablePoint(markI, markK));
        }
        return centres;
    }


}
