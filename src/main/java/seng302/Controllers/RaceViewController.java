package seng302.Controllers;

import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import seng302.Model.Competitor;
import seng302.Model.CourseFeature;
import seng302.Model.Regatta;

/**
 * Controller for the race view.
 */
public class RaceViewController {


    @FXML
    private Canvas mycanvas;
    private GraphicsContext gc;
    private Regatta regatta;
    @FXML
    void initialize() {
         gc = mycanvas.getGraphicsContext2D();

    }

    private void draw(GraphicsContext gc) {
        for(CourseFeature b:regatta.getPoints()){
            gc.setFill(Color.GREEN);
            System.out.printf(b.getName());
            if (b.isGate()) {
                gc.fillOval(b.getLocations().get(0).getX(), b.getLocations().get(0).getY(), 10, 10);
                gc.fillOval(b.getLocations().get(1).getX(), b.getLocations().get(1).getY(), 10, 10);
            }
            else {
                gc.fillOval(b.getLocations().get(0).getX(), b.getLocations().get(0).getY(), 20, 20);
            }

        }

    }


    public void begin(){
        draw(gc);
    }
    public void setRegatta(Regatta regatta) {
        this.regatta=regatta;

    }
}
