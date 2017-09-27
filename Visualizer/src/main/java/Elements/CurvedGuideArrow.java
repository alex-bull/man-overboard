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

    /**
     * Initialize a curved guide arrow
     *
     * @param isClockwise boolean, whether the arrow turns clockwise
     * @param color      Color, the color of the arrow
     */
    public CurvedGuideArrow(boolean isClockwise, Color color) {

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
    }


    /**
     * Update the arrow position when either zoomed or not zoomed
     *
     * @param markLocation MutablePoint, the location of the next mark
     */
    public void updateArrow(MutablePoint markLocation) {

        if (markLocation == null) { //before first mark
            hide();
            return;
        }


    }

    public void hide() {
        this.setVisible(false);
    }

    public void show() {
        this.setVisible(true);
    }
}
