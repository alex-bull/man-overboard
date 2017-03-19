package seng302.Controllers;

import javafx.animation.AnimationTimer;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.transform.Affine;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Transform;
import seng302.Model.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Controller for the race view.
 */
public class RaceViewController implements RaceDelegate{


    @FXML
    private Canvas mycanvas;
    private Race race;

    /**
     * Initialiser for the raceViewController
     */
    @FXML
    void initialize() {

    }

    /**
     * Draws an arrow on the screen at top left corner
     * @param gc graphics context
     * @param angle the angle of rotation
     */
    void drawArrow(GraphicsContext gc, double angle) {
        gc.save();
        gc.setFill(Color.BLACK);
//        System.out.println(angle);
        Rotate r = new Rotate(angle, 35, 40);

        gc.setTransform(r.getMxx(), r.getMyx(), r.getMxy(), r.getMyy(), r.getTx(), r.getTy());


        gc.fillPolygon(new double[]{20,30,30,40,40,50,35}, new double[]{30,30,70,70,30,30,10},
                7);
        gc.restore();
    }

    /**
     * Draws the course features on the canvas
     * @param gc GraphicContext the context to draw on
     */
    private void drawCourse(GraphicsContext gc) {

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

                drawArrow(gc, race.getWindDirection());
    }


    /**
     * Starts the animation timer to animate the race
     * @param width the width of the canvas
     * @param height the height of the canvas
     */
    public void begin(double width, double height){

        // start the race using the timeline
        Timeline t = race.generateTimeline();
        List<Competitor> competitors = race.getCompetitors();
        ArrayList<Color> colors=new ArrayList<>();
        colors.add(Color.BLACK);
        colors.add(Color.BLUE);
        colors.add(Color.YELLOW);
        colors.add(Color.RED);
        colors.add(Color.DARKGRAY);
        colors.add(Color.WHEAT);

        AnimationTimer timer = new AnimationTimer() {

            @Override
            public void handle(long now) {
                GraphicsContext gc = mycanvas.getGraphicsContext2D();
                gc.clearRect(0,0,width,height);

                gc.setFill(Color.LIGHTBLUE);
                gc.fillRect(0,0,width,height);
                drawCourse(gc);

                for(int i =0; i< competitors.size(); i++)  {
                    gc.setFill(colors.get(i));
                    gc.fillOval(
                            competitors.get(i).getPosition().getXValue(),
                            competitors.get(i).getPosition().getYValue(),
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
