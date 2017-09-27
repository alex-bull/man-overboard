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
 * An arrow that circles the next mark to round
 */
public class CurvedGuideArrow extends Polygon {

    private double angle;
    private boolean rotatesClockwise;
    /**
     * Initialize a curved guide arrow
     *
     * @param isClockwise boolean, whether the arrow turns clockwise
     * @param color      Color, the color of the arrow
     */
    public CurvedGuideArrow(boolean isClockwise, Color color) {

        double arrowLength = -50; // default arrow points vertically in the -y direction (upwards)
        double arrowHeadLength = -20;
        double offsetFromOrigin = -1 * (arrowLength + arrowHeadLength) + 10; // 80
        double moveRight = 70;
        double moveLeft = 0;

        if (isClockwise) {
            // flip
            moveLeft = -moveLeft;
            moveRight = -moveRight;
            this.getPoints().addAll(
                    10. + moveRight, offsetFromOrigin, //tail left
                    moveLeft, offsetFromOrigin,
                    moveLeft, offsetFromOrigin + 10,
                    moveRight, offsetFromOrigin + 10, //tail right
                    0. + moveRight, arrowLength + offsetFromOrigin, // base head left
                    -10. + moveRight, arrowLength + offsetFromOrigin, // point head left
                    5. + moveRight, arrowLength + arrowHeadLength + offsetFromOrigin, // tip
                    20. + moveRight, arrowLength + offsetFromOrigin, // point head right
                    10. + moveRight, arrowLength + offsetFromOrigin); // base head right

            rotatesClockwise = true;
        } else {
            this.getPoints().addAll(
                    0. + moveRight, offsetFromOrigin, //tail left
                    moveLeft, offsetFromOrigin,
                    moveLeft, offsetFromOrigin + 10,
                    10 + moveRight, offsetFromOrigin + 10, //tail right
                    10. + moveRight, arrowLength + offsetFromOrigin, // base head right
                    20. + moveRight, arrowLength + offsetFromOrigin, // point head right
                    5. + moveRight, arrowLength + arrowHeadLength + offsetFromOrigin, // tip
                    -10. + moveRight, arrowLength + offsetFromOrigin, // point head left
                    0. + moveRight, arrowLength + offsetFromOrigin); // base head left
        }
        this.setFill(color);
    }


    /**
     * Update the arrow position when either zoomed or not zoomed
     *
     * @param markX Double, the x coordinate of the mark
     * @param markY Double, the y coordinate of the mark
     */
    public void updateArrow(Double markX, Double markY) {

        if (rotatesClockwise) {
            angle += 2;
        } else {
            angle -= 2;
        }
        applyTransformsToArrow(angle, markX, markY);
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

    public void hide() {
        this.setVisible(false);
    }

    public void show() {
        this.setVisible(true);
    }
}
