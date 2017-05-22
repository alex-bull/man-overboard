package controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Polygon;
import javafx.scene.text.Text;
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
    @FXML private Text speed;
    private Polygon arrow;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.arrow = new Polygon();
        arrow.setLayoutX(80);
        arrow.setLayoutY(80);
        arrow.getPoints().addAll(
                -5., -10.0, //tail left
                5., -10., //tail right
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
     * @param windSpeed double the wind speed in m/s
     */
    void refresh(double angle, double windSpeed) {
        int minWind = 5;
        int maxWind = 7;
        double factor = 40.0;
        Double windFactor = windSpeed * factor;
        double offset = minWind * factor;
        arrow.toFront();
        arrow.getTransforms().clear();
        arrow.getTransforms().add(new Rotate(angle, 0, 25));
        if(windSpeed >= minWind && windSpeed <= maxWind) {
            arrow.getPoints().remove(0,14);
            arrow.getPoints().addAll(
                    -5., -offset + windFactor, //tail left
                    5., -offset + windFactor, //tail right
                    5., -20.,
                    15., -20.,
                    0., -40., // tip
                    -15., -20.,
                    -5.,-20.);
        }
        speed.setText(Double.toString(windSpeed));
    }



}
