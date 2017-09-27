package Elements;

import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.transform.Rotate;
import models.Competitor;
import models.MutablePoint;

import static java.lang.Math.*;
import static utilities.RaceCalculator.calculateAngleBetweenMarks;


/**
 * Created by mattgoodson on 30/08/17.
 * An arrow that points towards the next mark to round
 */
public class GuideArrow extends Polygon {


    /**
     * Initialize a guide arrow
     *
     * @param color      Color, the color of the arrow
     * @param startAngle double, the angle which the arrow points initially
     */
    public GuideArrow(Color color, Double startAngle) {
        this.setVisible(false);
        double arrowLength = -60; // default arrow points vertically in the -y direction (upwards)
        double arrowHeadLength = -20;
        double offsetFromOrigin = -1 * (arrowLength + arrowHeadLength) + 30;

        this.getPoints().addAll(
                -5., offsetFromOrigin, //tail left
                5., offsetFromOrigin, //tail right
                5., arrowLength + offsetFromOrigin,
                15., arrowLength + offsetFromOrigin,
                0., arrowLength + arrowHeadLength + offsetFromOrigin, // tip
                -15., arrowLength + offsetFromOrigin,
                -5., arrowLength + offsetFromOrigin);
        this.setFill(color);
        this.getTransforms().add(new Rotate(startAngle, 0, 0));
    }


    /**
     * Update the arrow position when zoomed in
     *
     * @param boat             Competitor, the boat to position the arrow for
     * @param boatX            double, the x coord of the boat in screen coords
     * @param boatY            double, the y coord of the boat in screen coords
     * @param nextMarkLocation MutablePoint, the location of the next mark
     */
    public void updateArrowZoomed(Competitor boat, Double boatX, Double boatY, MutablePoint nextMarkLocation) {


        // arrow points from boat to next mark
        Double xDist;
        Double yDist;
        xDist = boat.getPosition().getXValue() - nextMarkLocation.getXValue();
        yDist = boat.getPosition().getYValue() - nextMarkLocation.getYValue();
        double angle = calculateAngleBetweenMarks(xDist, yDist);
        double xOffset = 150 * cos(toRadians(angle - 90));
        double yOffset = 150 * sin(toRadians(angle - 90));
        double x = boatX + xOffset;
        double y = boatY + yOffset;
        applyTransformsToArrow(angle, x, y);
    }


    /**
     * Update the arrow position when not zoomed in
     *
     * @param prevMarkLocation MutablePoint, the location of the last mark passed
     * @param nextMarkLocation MutablePoint, the location of the next mark
     */
    public void updateArrow(MutablePoint prevMarkLocation, MutablePoint nextMarkLocation) {


        double angle;
        if (prevMarkLocation == null) { //before first mark
            angle = 90;
        } else {
            Double xDist;
            Double yDist;
            // arrow points from previous mark to next mark
            xDist = prevMarkLocation.getXValue() - nextMarkLocation.getXValue();
            yDist = prevMarkLocation.getYValue() - nextMarkLocation.getYValue();
            angle = calculateAngleBetweenMarks(xDist, yDist);
        }
        double x = nextMarkLocation.getXValue();
        double y = nextMarkLocation.getYValue();
        applyTransformsToArrow(angle, x, y);
    }


    /**
     * Apply translation and rotation transforms to the guiding arrow
     *
     * @param angle double the angle by which to rotate the arrow, from north
     * @param x     double the x coordinate for the arrow's origin
     * @param y     double the y coordinate for the arrow's origin
     */
    private void applyTransformsToArrow(double angle, double x, double y) {
        this.getTransforms().clear();
        this.setLayoutX(x);
        this.setLayoutY(y);
        this.getTransforms().add(new Rotate(angle, 0, 0));
    }


}
