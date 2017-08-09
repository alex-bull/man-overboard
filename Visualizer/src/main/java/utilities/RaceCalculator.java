package utilities;

import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.util.converter.PercentageStringConverter;
import models.Competitor;
import models.CourseFeature;
import models.MutablePoint;
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


    private DataSource dataSource;
    private Competitor boat;
    private Map<Integer, Polygon> boatModels = new HashMap<>();

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void setBoatModels(Map boatModels) {
        this.boatModels = boatModels;
    }

    /**
     * Calculates whether boat is heading to the start line
     * and if it does calculates the virtual line points and returns them so they can be used for drawing
     * returns empty list if boat is not heading to the start line
     * @param selectedBoat selected boat
     * @return List virtualLinePoints
     */
    public List<MutablePoint> calcVirtualLinePoints(Competitor selectedBoat) {
        List<MutablePoint> virtualLinePoints = new ArrayList<>();
        this.boat = selectedBoat;

        Polygon boatModel = boatModels.get(boat.getSourceID());
        Point2D boatFront = boatModel.localToParent(0, 0);
        Point2D boatBack = boatModel.localToParent(0, -2000);

        MutablePoint startMark1 = dataSource.getStoredFeatures().get(dataSource.getStartMarks().get(0)).getPixelLocations().get(0);
        MutablePoint startMark2 = dataSource.getStoredFeatures().get(dataSource.getStartMarks().get(1)).getPixelLocations().get(0);

        Line2D startLine = new Line2D.Double(startMark1.getXValue(), startMark1.getYValue(), startMark2.getXValue(), startMark2.getYValue());
        Line2D headingLine = new Line2D.Double(boatFront.getX(), boatFront.getY(), boatBack.getX(), boatBack.getY());
        boolean intersects = headingLine.intersectsLine(startLine);

        if (intersects) {
            MutablePoint intersection = calcStartLineIntersection(boatFront, boatBack, startMark1, startMark2);
            double xDifference = boatFront.getX() - intersection.getXValue();
            double yDifference = boatFront.getY() - intersection.getYValue();

            double distanceToStartLine = calcDistToStart(selectedBoat);
            double distanceToVirtualLine = calcDistToVirtual(selectedBoat);

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
    public MutablePoint calcStartLineIntersection(javafx.geometry.Point2D boatFront, Point2D boatBack, MutablePoint startMark1, MutablePoint startMark2) {
        double infinity = 1000000;
        double headingGradient;
        double xDiffBoat = boatFront.getX() - boatBack.getX();
        if (xDiffBoat == 0) {
            headingGradient = infinity;
        } else {
            headingGradient = (boatFront.getY() - boatBack.getY()) / xDiffBoat;
        }
        double headingIntercept = boatFront.getY() - (headingGradient * boatFront.getX());

        double startLineGradient;
        double xDiffStart = startMark1.getXValue() - startMark2.getXValue();
        if (xDiffStart == 0) {
            startLineGradient = infinity;
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
    public MutablePoint calcVirtualLinePoint(double ratio, double xDifference, double yDifference, MutablePoint startMark) {
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
    public String calculateStartSymbol(double distanceToStart, double distanceToEnd, double velocity, long timeUntilStart) {
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
     * Calculates the distance in metres from the selected boat to its virtual start line.
     * @param selectedBoat selected boat
     * @return double distance (m)
     */
    public double calcDistToVirtual(Competitor selectedBoat) {
        long expectedStartTime = dataSource.getExpectedStartTime();
        long messageTime = dataSource.getMessageTime();
        long timeUntilStart = Converter.convertToRelativeTime(expectedStartTime, messageTime) * -1; // seconds, negative because race hasn't started
        double velocity = selectedBoat.getVelocity(); // metres per seconds
        return velocity * timeUntilStart; // metres
    }

    /**
     * Calculates the distance in metres from the selected boat to the race start line.
     * @param selectedBoat selected boat
     * @return double distance (m)
     */
    public double calcDistToStart(Competitor selectedBoat) {
        double boatLat = selectedBoat.getLatitude();
        double boatLon = selectedBoat.getLongitude();

        CourseFeature startLine1 = dataSource.getStoredFeatures().get(dataSource.getStartMarks().get(0));
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
    public double calcDistBetweenGPSPoints(double latitude1, double longitude1, double latitude2, double longitude2) {
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
     * Returns the colour of the health scale of the given boat
     * @param boat Competitor a boat
     * @return Color the colour of the health bar
     */
    public Color calculateHealthColour(Competitor boat) {
        double healthLevel = boat.getHealthLevel();
        double maxHealth = boat.getMaxHealth();
        double percentage = healthLevel/maxHealth;
        if(percentage > 0.7) {
            return Color.GREEN;
        }
        else if(percentage > 0.6) {
            return Color.GREENYELLOW;
        }
        else if(percentage > 0.5) {
            return Color.YELLOW;
        }
        else if(percentage > 0.4) {
            return Color.ORANGE;
        }
        else {
            return Color.RED;
        }
    }

}
