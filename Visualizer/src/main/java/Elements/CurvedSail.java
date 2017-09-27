package Elements;

import javafx.scene.paint.Color;
import javafx.scene.shape.CubicCurve;
import javafx.scene.transform.Rotate;
import models.Competitor;

/**
 * Created by msl47.
 */
public class CurvedSail extends CubicCurve {

    double sailMovement = 10;
    boolean reverse = false;
    /**
     * initialize a sail with a color
     *
     * @param color Color the line color
     */
    public CurvedSail(Color color) {
        this.setStroke(color);
    }

    /**
     * update the position of the sale
     *
     * @param width     double the width of the line
     * @param length    double the length of the line
     * @param boat      Competitor the boat with the sail
     * @param windAngle double the windangle
     * @param boatX     double the screen x coord of the boat
     * @param boatY     double the screen y coord of the boat
     */
    public void update(double width, double length, Competitor boat, double windAngle, double boatX, double boatY) {

        if(reverse) {
            sailMovement += 0.5;
        }
        else {
            sailMovement -= 0.5;
        }

        if (sailMovement > 10 || sailMovement < -10) {
            reverse = !reverse;
        }

        this.setStrokeWidth(width);
        this.setStartX(boatX);
        this.setStartY(boatY);
        this.setControlX1(boatX + sailMovement);
        this.setControlY1(boatY + (length/4));
        this.setControlX2(boatX - sailMovement);
        this.setControlY2(boatY + (3*length/4));
        this.setEndX(boatX);
        this.setEndY(boatY + length);

        this.getTransforms().clear();

        if (boat.hasSailsOut()) {
            this.setVisible(true);
            this.getTransforms().add(new Rotate(windAngle, boatX, boatY));
        } else {
            this.setVisible(false);
        }
        this.toFront();
    }

}
