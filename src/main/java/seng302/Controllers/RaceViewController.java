package seng302.Controllers;

import com.sun.corba.se.impl.orbutil.graph.Graph;
import javafx.animation.AnimationTimer;
import javafx.animation.Timeline;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import seng302.Model.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Controller for the race view.
 */
public class RaceViewController implements RaceDelegate{


    @FXML
    private Canvas mycanvas;

//    private GraphicsContext gc;
    private Race race;
    private List<String> finishingOrder = new ArrayList<>();

    /**
     * Initialiser for the raceViewController
     */
    @FXML
    void initialize() {
//         gc = mycanvas.getGraphicsContext2D();

    }

    /**
     * Move a boat
     */
    public void boatMoved() {

    }

//    /**
//     * Draw a circle on the canvas
//     */
//    private void drawCircle(int x, int y) {
//
//        gc.setFill(Color.GREEN);
//        gc.fillOval(x, y, 10, 10);
//
//    }


    /**
     * Begins the race on the canvas
     */
    public void begin(double width, double height){
        //drawBoats(gc);
        // start the race using the timeline
        Timeline t = race.generateTimeline();
        List<Competitor> competitors = race.getCompetitors();


        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                GraphicsContext gc = mycanvas.getGraphicsContext2D();

                gc.clearRect(0,0,width,height);
                for(Competitor comp:competitors) {
                    gc.setFill(Color.FORESTGREEN);
                    gc.fillOval(
                            comp.getPosition().getXValue(),
                            comp.getPosition().getYValue(),
                            10,
                            10
                    );
                }
            }
        };

        timer.start();
        t.play();

    }




    /**
     * Sets the race
     * @param race Race a group of competitors across multiple races on a course
     */
    public void setRace(Race race) {
        this.race=race;

    }
}
