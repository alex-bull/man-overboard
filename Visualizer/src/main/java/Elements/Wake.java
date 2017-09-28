package Elements;

import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Polygon;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import models.Competitor;
import models.MutablePoint;

import static javafx.scene.paint.Color.TRANSPARENT;

/**
 * Created by mattgoodson on 1/09/17.
 * A visual representation of a boat wake proportional to speed
 */
public class Wake extends Polygon {

    private Translate translate;
    private Rotate rotate;
    /**
     * Initialize a wake
     *
     * @param boat             Competitor, the boat
     * @param boatLength       double, the length of the boat
     * @param startWakeOffset  double, the offset of the wake
     * @param wakeWidthFactor  double, the width scale
     * @param wakeLengthFactor double, the length scale
     */
    public Wake(Competitor boat, double boatLength, double startWakeOffset, double wakeWidthFactor, double wakeLengthFactor) {

        double wakeLength = boat.getVelocity() * wakeLengthFactor;
        this.getPoints().addAll(-startWakeOffset, boatLength, startWakeOffset, boatLength, startWakeOffset + wakeLength * wakeWidthFactor, wakeLength + boatLength, -startWakeOffset - wakeLength * wakeWidthFactor, wakeLength + boatLength);
        LinearGradient linearGradient = new LinearGradient(0.0, 0, 0.0, 1, true, CycleMethod.NO_CYCLE, new Stop(0.0f, Color.rgb(0, 0, 255, 0.7)), new Stop(1.0f, TRANSPARENT));
        this.setFill(linearGradient);

        translate=new Translate(0,0);
        rotate=new Rotate(0,0,0);
        getTransforms().add(translate);
        getTransforms().add(rotate);

    }


    /**
     * Update the wake model
     *
     * @param boat             Competitor, the boat
     * @param boatLength       double, the length of the boat
     * @param startWakeOffset  double, the offset of the wake
     * @param wakeWidthFactor  double, the width scale
     * @param wakeLengthFactor double, the length scale
     * @param relativePosition MutablePoint, the relative position to the controlled boat
     */
    public void update(Competitor boat, double boatLength, double startWakeOffset, double wakeWidthFactor, double wakeLengthFactor, MutablePoint relativePosition) {

        double newLength = boat.getVelocity() * 2 * wakeLengthFactor;
//        this.getTransforms().clear();
        this.getPoints().clear();
        this.getPoints().addAll(-startWakeOffset, boatLength, startWakeOffset, boatLength, startWakeOffset + newLength * wakeWidthFactor, newLength + boatLength, -startWakeOffset - newLength * wakeWidthFactor, newLength + boatLength);
//        this.getTransforms().add(new Translate(relativePosition.getXValue(), relativePosition.getYValue()));
//
//        this.getTransforms().add(new Rotate(boat.getCurrentHeading(), 0, 0));
        translate.setX(relativePosition.getXValue());
        translate.setY(relativePosition.getYValue());
        rotate.setAngle(boat.getCurrentHeading());

        this.toFront();
    }

}
