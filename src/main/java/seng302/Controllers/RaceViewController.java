package seng302.Controllers;

import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import seng302.Model.Competitor;
import seng302.Model.CourseFeature;
import seng302.Model.Regatta;

import java.util.DoubleSummaryStatistics;

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
            gc.setStroke(Color.BLUE);

            if (b.isGate()) {
                gc.setLineWidth(3);
                int w = 15;
                int h = 15;
                Double x1 = b.getLocations().get(0).getX();
                Double y1 =  b.getLocations().get(0).getY();
                Double x2 = b.getLocations().get(1).getX();
                Double y2 = b.getLocations().get(1).getY();
                gc.strokeLine(x1 , y1 , x2+w, y2+h);
                gc.fillOval(x1, y1, w, h);
                gc.fillOval(x2, y2, w, h);
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
