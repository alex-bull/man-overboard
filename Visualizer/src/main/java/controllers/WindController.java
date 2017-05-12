package controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Polygon;
import javafx.scene.transform.Rotate;

import java.net.URL;
import java.util.ResourceBundle;

import static javafx.scene.paint.Color.BLACK;

/**
 * Created by psu43 on 12/05/17.
 * Controller for components for the race display that do not rely on the canvas
 */
public class WindController implements Initializable{

    @FXML Pane windPane;
    private Polygon arrow;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.arrow = new Polygon();
        arrow.setLayoutX(80);
        arrow.setLayoutY(80);
        arrow.getPoints().addAll(
                -5., 10.,
                5., 10.,
                5., -20.,
                15., -20.,
                0., -40., // tip
                -15., -20.,
                -5.,-20.);
        arrow.setFill(BLACK);
        arrow.setStroke(BLACK);
        arrow.setStrokeWidth(1);
        this.windPane.getChildren().add(arrow);
    }


    /**
     * Updates the heading of the wind direction arrow
     * @param angle double the angle of rotation
     */
    void refresh(double angle) {
        arrow.toFront();
        arrow.getTransforms().clear();
        arrow.getTransforms().add(new Rotate(angle, 0, 0));
    }



}
