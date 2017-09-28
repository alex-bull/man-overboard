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
    private Rotate rotate;
    private boolean isDead = false;

    /**
     * Initialize a boat model
     *
     * @param boatType String, type of boat image to be displayed for player
     * @param player   boolean, true if the player marker should be shown
     */
    public BoatModel(Integer boatType, boolean player) {
        boatModel = new Polygon();

        this.boatModel = new Polygon();
        boatModel.getPoints().addAll(
                -12.0, -22.5, //top
                -12.0, 22.5, //left
                12.0, 22.5,
                12.0, -22.5); //right
        String[] imagePaths={"images/boats/yacht.png",
                "images/boats/cog.png",
                "images/boats/frigate.png",
                "images/boats/galleon.png",
                "images/boats/boat.png",
                "images/boats/cat.png",
                "images/boats/pirate.png"
        };
        Image boatImage;
        try {
            boatImage = new Image(getClass().getClassLoader().getResource(imagePaths[boatType]).toString());

        }
        catch (IndexOutOfBoundsException e){
            boatImage=new Image(getClass().getClassLoader().getResource("images/cross-small.png").toString());
            System.out.println("invalid boat image index");

        }
        boatModel.setFill(new ImagePattern(boatImage));
        //boatModel.setStroke(BLACK);


        if (player) {
            this.playerMarker = new Circle(0, 0, 23);
            playerMarker.setStrokeWidth(2.5);
            playerMarker.setStroke(Color.rgb(255, 255, 255, 0.5));
            playerMarker.setFill(Color.rgb(0, 0, 0, 0.2));
            this.getChildren().add(playerMarker);
        }
        this.ripImage = new ImageView(new Image(getClass().getClassLoader().getResource("images/cross-small.png").toString()));
        this.ripImage.setPreserveRatio(true);
        this.ripImage.setFitHeight(20);
        this.getChildren().add(ripImage);
        ripImage.setVisible(false);
        ripImage.toFront();

        rotate=new Rotate(0,0,0);
        getTransforms().add(rotate);

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
        rotate.setAngle(heading);
        this.toFront();
    }


    /**
     * The boat is dead so hide player marker and boat shape then show rip image
     */
    public void die() {
        if (!isDead) {
            isDead = true;
            boatModel.setVisible(false);
            this.getChildren().remove(playerMarker);
            ripImage.setVisible(true);
            this.ripImage.toFront();
            this.getChildren().remove(boatModel);
        }

    }


}
