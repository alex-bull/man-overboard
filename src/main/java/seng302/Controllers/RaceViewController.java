package seng302.Controllers;

import javafx.animation.AnimationTimer;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.transform.Rotate;
import seng302.Model.*;

import java.util.List;

/**
 * Controller for the race view.
 */
public class RaceViewController implements RaceDelegate{


    @FXML
    private Canvas mycanvas;
    @FXML
    private Text timerText;

    private long startTime;


    private Race race;


    @FXML
    private TableController tableController;



    /**
     * Initialiser for the raceViewController
     */
    @FXML
    void initialize() {

    }

    /**
     * Sets the race
     * @param race Race a group of competitors across multiple races on a course
     */
    public void begin(Race race, double width, double height) {
        this.race=race;
        startTime = System.currentTimeMillis();
        animate(width, height);



    }

    /**
     * Draws an arrow on the screen at top left corner
     * @param gc graphics context
     * @param angle the angle of rotation
     */
    void drawArrow(GraphicsContext gc, double angle) {
        gc.save();
        gc.setFill(Color.BLACK);
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

        for (CourseFeature courseFeature : this.race.getCourseFeatures()) {
            gc.setFill(Color.ORANGERED);
            gc.setStroke(Color.BLUE);

            List<MutablePoint> marks = courseFeature.getLocations();
            Double x1 = marks.get(0).getXValue();
            Double y1 = marks.get(0).getYValue();

            // if it is a gate
            if (marks.size() == 2) {
                gc.setLineWidth(3);
                int d = 15;
                double r = d/2;
                Double x2 = marks.get(1).getXValue();
                double y2 = marks.get(1).getYValue();

                // check if gate needs line
                if(courseFeature.isLine()){
                    gc.strokeLine(x1, y1, x2, y2);
                }

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
    public void animate(double width, double height){

        // start the race using the timeline
        Timeline t = race.generateTimeline(tableController);
        List<Competitor> competitors = race.getCompetitors();


        AnimationTimer timer = new AnimationTimer() {

//            long startTimeNano = System.nanoTime();

            @Override
            public void handle(long now) {
//                long currentTimeNano = System.nanoTime();
//
//                if (currentTimeNano > startTimeNano + 1000000000){
//                    startTimeNano = System.nanoTime();
//                }

                // clear the canvas
                GraphicsContext gc = mycanvas.getGraphicsContext2D();
                gc.clearRect(0,0,width,height);

                // draw course
                gc.setFill(Color.LIGHTBLUE);
                gc.fillRect(0,0,width,height);
                drawCourse(gc);

                // draw competitors
                for(int i =0; i< competitors.size(); i++)  {
                    gc.setFill(competitors.get(i).getColor());
                    gc.fillOval(
                            competitors.get(i).getPosition().getXValue(),
                            competitors.get(i).getPosition().getYValue(),
                            10,
                            10
                    );
                }


                timerText.setText(formatDisplayTime(System.currentTimeMillis() - startTime));

                //test++;


            }
        };

        timer.start();
        t.play();

    }


        /**
     * Creates a formatted display time string in mm:ss
     * @return String the time string
     */
    private String formatDisplayTime (long display) {
        int displayTime = (int)display/1000;
        int minutes = displayTime / 60;
        int seconds = displayTime - (60 * minutes);

        String formattedTime = "";
        if (minutes > 9) {
            formattedTime += minutes + ":";
        } else {
            formattedTime += "0" + minutes + ":";
        }
        if (seconds > 9) {
            formattedTime += seconds;
        } else {
            formattedTime += "0" + seconds;
        }
        return formattedTime;

    }




}
