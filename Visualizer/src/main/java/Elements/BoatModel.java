package Elements;

import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Shape;
import javafx.scene.transform.Rotate;
import models.MutablePoint;

import static javafx.scene.paint.Color.BLACK;
import static javafx.scene.paint.Color.YELLOW;

/**
 * Created by mattgoodson on 1/09/17.
 * A visual representation of a boat
 */
public class BoatModel extends Group {

    private Polygon boatModel;
    private Circle playerMarker;
    private ImageView ripImage;

    /**
     * Initialize a boat model
     * @param color Color the color of the boat
     * @param player boolean, true if the player marker should be shown
     */
    public BoatModel(Color color, boolean player) {

        this.boatModel = new Polygon();
        boatModel.getPoints().addAll(
                0.0, -10.0, //top
                -5.0, 10.0, //left
                5.0, 10.0); //right
        boatModel.setFill(color);

        boatModel.setStroke(BLACK);
        this.getChildren().add(boatModel);

        if (player) {
            this.playerMarker = new Circle(0, 0, 15);
            playerMarker.setStrokeWidth(2.5);
            playerMarker.setStroke(Color.rgb(255,255,255,0.5));
            playerMarker.setFill(Color.rgb(0,0,0,0.2));
            this.getChildren().add(playerMarker);
        }
        this.ripImage = new ImageView(new Image("images/cross-small.png"));
        this.ripImage.setPreserveRatio(true);
        this.ripImage.setFitHeight(20);
        this.getChildren().add(ripImage);
        ripImage.setVisible(false);

    }


    /**
     * Update the position of the boat model
     * @param position MutablePoint the new location
     * @param heading double the heading of the boat in degrees
     */
    public void update(MutablePoint position, double heading) {
        this.setLayoutX(position.getXValue());
        this.setLayoutY(position.getYValue());
        this.toFront();
        this.getTransforms().clear();
        this.getTransforms().add(new Rotate(heading, 0, 0));

    }


    /**
     * The boat is dead so hide player marker and boat shape then show rip image
     */
    public void die() {
        boatModel.setVisible(false);
        this.getChildren().remove(playerMarker);
        ripImage.setVisible(true);

    }


}
