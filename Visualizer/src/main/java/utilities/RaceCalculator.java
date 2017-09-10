package utilities;

import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.util.converter.PercentageStringConverter;
import models.*;
import parsers.Converter;

import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.Math.*;
import static java.lang.Math.sqrt;

/**
 * Created by psu43 on 25/05/17.
 * Holds all of the calculations for the race
 */
public class RaceCalculator {

    /**
     * Calculates the boat's direction when screen was touched
     * @param boatX x value of boat's position
     * @param boatY y value of boat's position
     * @param touchX x value of touch point's position
     * @param touchY y value of touch point's position
     * @return double theta
     */
    public double calcBoatDirection(double boatX, double boatY, double touchX, double touchY) {
        Vector2D boatDirection = new Vector2D(boatX, boatY, touchX, touchY);
        double theta = atan2(boatDirection.getY(), boatDirection.getX());
        theta = (theta * 180 / PI) + 90;
        if (theta < 0) { theta = 360 + theta;}

        return theta;
    }

    /**
     * Calculates whether the boat is currently west of wind or not
     * @param heading boat's heading
     * @param downWind angle of down wind
     * @param windAngle angle of wind
     * @return boolean
     */
    public boolean isWestOfWind(double heading, double downWind, double windAngle) {
        return (heading > downWind) || (heading < windAngle);
    }


    /**
     * Calculates whether boat is heading to the start line
     * and if it does calculates the virtual line points and returns them so they can be used for drawing
     * returns empty list if boat is not heading to the start line
     * @param boatModel boat model
     * @param startMark1 start mark 1
     * @param startMark2 start mark 2
     * @param startLine1 start line 1
     * @param expectedStartTime expected start time
     * @param selectedBoat selected boat
     * @return List virtualLinePoints
     */
    public static List<MutablePoint> calcVirtualLinePoints(Competitor selectedBoat,Polygon boatModel, MutablePoint startMark1, MutablePoint startMark2, CourseFeature startLine1, long expectedStartTime,long messageTime) {
        List<MutablePoint> virtualLinePoints = new ArrayList<>();

//
//        Polygon boatModel = boatModels.get(boat.getSourceID());
        Point2D boatFront = boatModel.localToParent(0, 0);
        Point2D boatBack = boatModel.localToParent(0, -2000);

//        MutablePoint startMark1 = dataSource.getStoredFeatures().get(dataSource.getStartMarks().get(0)).getPixelLocations().get(0);
//        MutablePoint startMark2 = dataSource.getStoredFeatures().get(dataSource.getStartMarks().get(1)).getPixelLocations().get(0);

        Line2D startLine = new Line2D.Double(startMark1.getXValue(), startMark1.getYValue(), startMark2.getXValue(), startMark2.getYValue());
        Line2D headingLine = new Line2D.Double(boatFront.getX(), boatFront.getY(), boatBack.getX(), boatBack.getY());
        boolean intersects = headingLine.intersectsLine(startLine);

        if (intersects) {
            MutablePoint intersection = calcStartLineIntersection(boatFront, boatBack, startMark1, startMark2);
            double xDifference = boatFront.getX() - intersection.getXValue();
            double yDifference = boatFront.getY() - intersection.getYValue();


//            CourseFeature startLine1 = dataSource.getStoredFeatures().get(dataSource.getStartMarks().get(0));
            double distanceToStartLine = calcDistToStart(selectedBoat,startLine1);

//            long expectedStartTime = dataSource.getExpectedStartTime();
//            long messageTime = dataSource.getMessageTime();
            long timeUntilStart = Converter.convertToRelativeTime(expectedStartTime, messageTime) * -1; // seconds, negative because race hasn't started

            double distanceToVirtualLine = calcDistToVirtual(selectedBoat,timeUntilStart);

            if (distanceToStartLine != 0) {
                double ratio = distanceToVirtualLine / distanceToStartLine;

                virtualLinePoints.add(calcVirtualLinePoint(ratio, xDifference, yDifference, startMark1));
                virtualLinePoints.add(calcVirtualLinePoint(ratio, xDifference, yDifference, startMark2));
            }
        }
        return virtualLinePoints;
    }

    /**
     * Calculates the point of intersection of the boat's heading line and the start line.
     * @param boatFront Point2D The front of the boat.
     * @param boatBack Point2D The back of the boat.
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
//            startLineGradient = Double.MAX_VALUE;
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
     * @param ratio double The ratio of the distance from the boat to virtual and real start lines.
     * @param xDifference double The difference along the x-axis between the front of the boat and point of intersection with the start line.
     * @param yDifference double The difference along the y-axis between the front of the boat and point of intersection with the start line.
     * @param startMark MutablePoint A point on the real start line.
     * @return MutablePoint A point on the virtual start line.
     */
    public  static MutablePoint calcVirtualLinePoint(double ratio, double xDifference, double yDifference, MutablePoint startMark) {
        double startToVirtualX = (1 - ratio) * xDifference;
        double startToVirtualY = (1 - ratio) * yDifference;

        double virtualLineX = startMark.getXValue() + startToVirtualX;
        double virtualLineY = startMark.getYValue() + startToVirtualY;

        return new MutablePoint(virtualLineX, virtualLineY);
    }

    /**
     * Calculates the estimated time to the mark and compares it to expected race start time.
     * @param distanceToStart double the Distance to the a point on a start line
     * @param distanceToEnd double the Distance to the a point on a start line
     * @param velocity double the speed of the boat
     * @param timeUntilStart long the time until start
     * @return String "-" if the boat is going to cross early, "+" if late and "" if within 5 seconds.
     */
    public  static String calculateStartSymbol(double distanceToStart, double distanceToEnd, double velocity, long timeUntilStart) {
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
     * Calculates distance between the player's boat and the virtual line
     * @param selectedBoat selected boat
     * @param timeUntilStart time until start
     * @return double distance (m)
     */
    public static double calcDistToVirtual(Competitor selectedBoat, long timeUntilStart) {
        return selectedBoat.getVelocity() * timeUntilStart; // metres
    }

    /**
     * Calculates distance between the player's boat and the start line
     * @param selectedBoat selected boat
     * @param startLine1 start line
     * @return double distance (m)
     */
    public static double calcDistToStart(Competitor selectedBoat, CourseFeature startLine1) {
        double boatLat = selectedBoat.getLatitude();
        double boatLon = selectedBoat.getLongitude();

        double startLat = startLine1.getGPSPoint().getXValue();
        double startLon = startLine1.getGPSPoint().getYValue();

        return calcDistBetweenGPSPoints(boatLat, boatLon, startLat, startLon);
    }

    /**
     * Calculates the distance in metres between a pair of coordinates.
     * @param latitude1 first point's latitude
     * @param longitude1 first point's longitude
     * @param latitude2 second point's latitude
     * @param longitude2 second point's longitude
     * @return double distance (m)
     */
    public  static double calcDistBetweenGPSPoints(double latitude1, double longitude1, double latitude2, double longitude2) {
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
     * @param xDist Double the distance between marks in the x direction
     * @param yDist Double the distance between marks in the y direction
     * @return double the angle
     */
    public static double calculateAngleBetweenMarks(Double xDist, Double yDist) {
        double arctan = atan(yDist/xDist);
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
     * @param markIndex index of the mark (based on the order they are rounded)
     * @param indexMap Map of index to sourceId
     * @param featureMap Map of sourceId to courseFeature
     * @return MutablePoint (x,y) coordinates
     */
    public static MutablePoint getGateCentre(Integer markIndex, Map<Integer, List<Integer>> indexMap,  Map<Integer, CourseFeature> featureMap) {

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


}
