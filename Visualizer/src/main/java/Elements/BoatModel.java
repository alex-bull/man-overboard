package Elements;

import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.transform.Rotate;
import models.MutablePoint;

/**
 * Created by mattgoodson on 1/09/17.
 * A visual representation of a boat
 */
public class BoatModel extends Group {

    private Polygon boatModel;
    private Circle playerMarker;
    private ImageView ripImage;
    private boolean isDead = false;

    /**
     * Initialize a boat model
     *
     * @param boatType String, type of boat image to be displayed for player
     * @param player   boolean, true if the player marker should be shown
     */
    public BoatModel(Integer boatType, boolean player) {

        this.boatModel = new Polygon();
//        boatModel.getPoints().addAll(
//                -6.0, -14.0, //top
//                -6.0, 14.0, //left
//                6.0, 14.0,
//                6.0, -14.0); //right
        boatModel.getPoints().addAll(
                -11.0, -21.0, //top
                -11.0, 21.0, //left
                11.0, 21.0,
                11.0, -21.0); //right

        if (boatType == 0) {
            Image boatImage = new Image(getClass().getClassLoader().getResource("images/yacht.png").toString());
            boatModel.setFill(new ImagePattern(boatImage));
        } else if (boatType == 1) {
            Image boatImage = new Image(getClass().getClassLoader().getResource("images/cog.png").toString());
            boatModel.setFill(new ImagePattern(boatImage));
        } else if (boatType == 2) {
            Image boatImage = new Image(getClass().getClassLoader().getResource("images/frigate.png").toString());
            boatModel.setFill(new ImagePattern(boatImage));
        } else if (boatType == 3) {
            Image boatImage = new Image(getClass().getClassLoader().getResource("images/galleon.png").toString());
            boatModel.setFill(new ImagePattern(boatImage));
        } else if (boatType == 4) {
            Image boatImage = new Image(getClass().getClassLoader().getResource("images/boat.png").toString());
            boatModel.setFill(new ImagePattern(boatImage));
        } else if (boatType == 5) {
            Image boatImage = new Image(getClass().getClassLoader().getResource("images/cat.png").toString());
            boatModel.setFill(new ImagePattern(boatImage));
        } else if (boatType == 6) {
            Image boatImage = new Image(getClass().getClassLoader().getResource("images/pirate.png").toString());
            boatModel.setFill(new ImagePattern(boatImage));
        }


        //boatModel.setStroke(BLACK);


        if (player) {
            this.playerMarker = new Circle(0, 0, 15);
            playerMarker.setStrokeWidth(2.5);
            playerMarker.setStroke(Color.rgb(255, 255, 255, 0.5));
            playerMarker.setFill(Color.rgb(0, 0, 0, 0.2));
            this.getChildren().add(playerMarker);
        }
        this.ripImage = new ImageView(new Image("images/cross-small.png"));
        this.ripImage.setPreserveRatio(true);
        this.ripImage.setFitHeight(20);
        this.getChildren().add(ripImage);
        ripImage.setVisible(false);

        this.getChildren().add(boatModel);
    }


    /**
     * Update the position of the boat model
     *
     * @param position MutablePoint the new location
     * @param heading  double the heading of the boat in degrees
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

        if (!isDead) {
            System.out.println("Die");
            isDead = true;
            boatModel.setVisible(false);
            this.getChildren().remove(playerMarker);
            ripImage.setVisible(true);
            this.getChildren().remove(boatModel);
        }

    }


}
