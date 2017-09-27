package Elements;

import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.transform.Rotate;
import models.Competitor;

/**
 * Created by mgo65 on 31/08/17.
 * A visual representation of a sail on a boat
 */
public class Sail extends Line {

    /**
     * initialize a sail with a color
     *
     * @param color Color the line color
     */
    public Sail(Color color) {
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

        this.setStrokeWidth(width);
        this.setStartX(boatX);
        this.setStartY(boatY);
        this.setEndX(boatX);
        this.setEndY(boatY + length);
        this.getTransforms().clear();
        if (boat.hasSailsOut()) {
            this.setVisible(false);
        } else {
            this.setVisible(true);
            this.getTransforms().add(new Rotate(boat.getCurrentHeading(), boatX, boatY));
        }
        this.toFront();
    }

}
