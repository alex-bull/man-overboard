package utilities;

import controllers.RaceViewController;
import javafx.geometry.Point2D;
import models.MutablePoint;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * Created by jar156 on 25/05/17.
 */
public class LineCalculationTest {
    RaceViewController rvc;
    MutablePoint startPoint;

    @Before
    public void setRvc() {
        rvc = new RaceViewController();
    }

    @Before
    public void setStartPt() {
        startPoint = new MutablePoint(10.0, 20.0);
    }

    @Test
    public void returnsSamePointWhenNormalRatioAndNoXYDifference() {
        double ratio = 1; // virtual distance same as real distance
        double xDifference = 0; // boat is on start line
        double yDifference = 0;
        MutablePoint virtualPoint = rvc.calcVirtualLinePoint(ratio, xDifference, yDifference, startPoint);
        assertTrue(virtualPoint.getXValue() == startPoint.getXValue());
        assertTrue(virtualPoint.getYValue() == startPoint.getYValue());
    }

    @Test
    public void returnsSamePointWhenNormalRatioEvenWithXYDifference() {
        double ratio = 1;
        double xDifference = 10;
        double yDifference = 15;
        MutablePoint virtualPoint = rvc.calcVirtualLinePoint(ratio, xDifference, yDifference, startPoint);
        assertTrue(virtualPoint.getXValue() == startPoint.getXValue());
        assertTrue(virtualPoint.getYValue() == startPoint.getYValue());
    }

    @Test
    public void returnsSamePointWhenLowRatioAndBoatOnStartLine() {
        double ratio = 0.7; // virtual distance closer than real distance
        double xDifference = 0;
        double yDifference = 0;
        MutablePoint virtualPoint = rvc.calcVirtualLinePoint(ratio, xDifference, yDifference, startPoint);
        assertTrue(virtualPoint.getXValue() == startPoint.getXValue());
        assertTrue(virtualPoint.getYValue() == startPoint.getYValue());
    }

    @Test
    public void returnsCloserPointWhenRatioLowAndBoatOnLeft() {
        double ratio = 0.7;
        double xDifference = -1; // difference is boat position minus intersection so negative means boat on left of start line
        double yDifference = -1;
        MutablePoint virtualPoint = rvc.calcVirtualLinePoint(ratio, xDifference, yDifference, startPoint);
        assertTrue(virtualPoint.getXValue() < startPoint.getXValue());
        assertTrue(virtualPoint.getYValue() < startPoint.getYValue());
    }

    @Test
    public void returnsFurtherPointWhenRatioHighAndBoatOnLeft() {
        double ratio = 1.2; // virtual distance further than real distance
        double xDifference = -1;
        double yDifference = -1;
        MutablePoint virtualPoint = rvc.calcVirtualLinePoint(ratio, xDifference, yDifference, startPoint);
        assertTrue(virtualPoint.getXValue() > startPoint.getXValue());
        assertTrue(virtualPoint.getYValue() > startPoint.getYValue());
    }

    @Test
    public void returnsCloserPointWhenRatioLowAndBoatOnRight() {
        double ratio = 0.7;
        double xDifference = 1; // difference is boat position minus intersection so negative means boat on left of start line
        double yDifference = 1;
        MutablePoint virtualPoint = rvc.calcVirtualLinePoint(ratio, xDifference, yDifference, startPoint);
        assertTrue(virtualPoint.getXValue() > startPoint.getXValue());
        assertTrue(virtualPoint.getYValue() > startPoint.getYValue());
    }

    @Test
    public void returnsFurtherPointWhenRatioHighAndBoatOnRight() {
        double ratio = 1.2; // virtual distance further than real distance
        double xDifference = 1;
        double yDifference = 1;
        MutablePoint virtualPoint = rvc.calcVirtualLinePoint(ratio, xDifference, yDifference, startPoint);
        assertTrue(virtualPoint.getXValue() < startPoint.getXValue());
        assertTrue(virtualPoint.getYValue() < startPoint.getYValue());
    }

    @Test
    public void intersectionOnVerticalLineSegment() {
        Point2D boatFront = new Point2D(30, 20);
        Point2D boatBack = new Point2D(15, 20);
        MutablePoint startUpper = new MutablePoint(25.0, 22.0);
        MutablePoint startLower = new MutablePoint(25.0, 18.0);
        MutablePoint intersection = rvc.calcStartLineIntersection(boatFront, boatBack, startUpper, startLower);
        assertTrue(intersection.getYValue() <= startUpper.getYValue());
        assertTrue(intersection.getYValue() >= startLower.getYValue());
    }
}
