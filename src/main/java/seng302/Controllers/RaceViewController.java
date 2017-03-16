package seng302.Controllers;

import javafx.animation.AnimationTimer;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import seng302.Model.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Controller for the race view.
 */
public class RaceViewController implements RaceDelegate{


    @FXML
    private Canvas mycanvas;

    //private GraphicsContext gc;
    private Race race;
    private List<String> finishingOrder = new ArrayList<>();

    /**
     * Initialiser for the raceViewController
     */
    @FXML
    void initialize() {
         //gc = mycanvas.getGraphicsContext2D();
        //draw(mycanvas.getGraphicsContext2D());

//        mycanvas.setHeight(1000);
//        mycanvas.setWidth(1000);

    }

    private void draw(GraphicsContext gc) {

        for (CourseFeature b : this.race.getCourseFeatures()) {
            gc.setFill(Color.GREEN);
            gc.setStroke(Color.BLUE);

            List<MutablePoint> marks = b.getLocations();
            Double x1 = marks.get(0).getXValue();
            Double y1 = marks.get(0).getYValue();

            if (marks.size() == 2) {
                gc.setLineWidth(3);
                int d = 15;
                double r = d/2;
                Double x2 = marks.get(1).getXValue();
                double y2 = marks.get(1).getYValue();

                gc.strokeLine(x1, y1, x2, y2);

                gc.fillOval(x1 - r, y1 - r, d, d);
                gc.fillOval(x2 - r, y2 - r, d, d);
            } else {
                gc.fillOval(x1, y1, 20, 20);
            }

        }
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
        Competitor comp = competitors.get(0);

        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                GraphicsContext gc = mycanvas.getGraphicsContext2D();

                gc.clearRect(0,0,width,height);
                draw(gc);
                gc.setFill(Color.FORESTGREEN);
                gc.fillOval(
                        comp.getPosition().getXValue(),
                        comp.getPosition().getYValue(),
                        10,
                        10
                );

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
