package Elements;

import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Shape;
import javafx.scene.transform.Rotate;
import models.MutablePoint;

import static javafx.scene.paint.Color.BLACK;

/**
 * Created by mattgoodson on 1/09/17.
 */
public class BoatModel extends Group {

    private Polygon boat;
    private Shape playerMarker;

    public BoatModel(Color color, boolean player) {

        Polygon boatModel = new Polygon();
        boatModel.getPoints().addAll(
                0.0, -10.0, //top
                -5.0, 10.0, //left
                5.0, 10.0); //right
        boatModel.setFill(color);

        boatModel.setStroke(BLACK);
        this.getChildren().add(boatModel);

        if (player) {
            Shape playerMarker = new Circle(0, 0, 15);
            playerMarker.setStrokeWidth(2.5);
            playerMarker.setStroke(Color.rgb(255,255,255,0.5));
            playerMarker.setFill(Color.rgb(0,0,0,0.2));
            this.getChildren().add(playerMarker);
        }
    }



    public void update(MutablePoint position, double heading) {

        this.setLayoutX(position.getXValue());
        this.setLayoutY(position.getYValue());
        this.toFront();
        this.getTransforms().clear();
        this.getTransforms().add(new Rotate(heading, 0, 0));
        if (this.playerMarker != null) {
            playerMarker.setLayoutX(position.getXValue());
            playerMarker.setLayoutY(position.getYValue());
        }
    }

}
