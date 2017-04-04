package seng302.Controllers;

import com.google.common.primitives.Doubles;
import javafx.animation.AnimationTimer;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Scale;
import seng302.Model.*;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import static java.lang.Math.abs;
import static java.lang.Math.cos;
import static java.lang.Math.sin;

/**
 * Controller for the race view.
 */
public class RaceViewController implements ClockHandler {

    @FXML private Canvas mycanvas;
    @FXML private Text timerText;
    @FXML private Label fpsCounter;


    private Clock raceClock;
    private Race race;
    private boolean showAnnotations = true;
    private boolean showImportantAnnotations = false;
    private boolean setSpeedLabel = false;
    private boolean setNameLabel = false;




    /**
     * Sets the race and the race start time and then animates the race
     * @param race Race  group of competitors across multiple races on a course
     * @param width double the width of the canvas
     * @param height double the height of the canvas
     */
    public void begin(Race race, double width, double height) {
        this.race=race;
        this.raceClock = new RaceClock(this, race.getVelocityScaleFactor(), 27000);
        mycanvas.setHeight(height);
        mycanvas.setWidth(width);
        raceClock.start();
        animate(width, height);

    }


    /**
     * Draws an arrow on the screen at top left corner
     * @param gc GraphicsContext graphics context
     * @param angle double the angle of rotation
     */
    void drawArrow(GraphicsContext gc, double angle) {
        gc.save();
        gc.setFill(Color.BLACK);
        Rotate r = new Rotate(angle, 55, 90); // rotate object
        gc.setTransform(r.getMxx(), r.getMyx(), r.getMxy(), r.getMyy(), r.getTx(), r.getTy());

        gc.fillPolygon(new double[]{40,50,50,60,60,70,55}, new double[]{70,70,110,110,70,70,50},
                7);

        gc.restore();
    }


    /**
     * Draws the course features on the canvas
     * @param gc GraphicContext the context to draw on
     */
    private void drawCourse(GraphicsContext gc) {

        drawBoundary(gc);

        // loops through all course features
        for (CourseFeature courseFeature : this.race.getCourseFeatures().subList(1, race.getCourseFeatures().size())) {
            gc.setFill(Color.ORANGERED); // buoy colour
            gc.setStroke(Color.BLUE); // line colour between gates

            // get the pixel locations of the course feature
            List<MutablePoint> marks = courseFeature.getPixelLocations();
            Double x1 = marks.get(0).getXValue(); // first x pixel coordinate
            Double y1 = marks.get(0).getYValue(); // first y pixel coordinate
            int d = 15; // diameter of the circle
            double r = d/2; // radius of the circle

            // if it is a gate it will have two pixel location coordinates
            if (marks.size() == 2) {
                gc.setLineWidth(3);

                double x2 = marks.get(1).getXValue(); // second x pixel coordinate
                double y2 = marks.get(1).getYValue(); // second y pixel coordinate

                // check if gate needs line
                if(courseFeature.isLine()){
                    gc.strokeLine(x1, y1, x2, y2);
                }

                // draw gate points
                gc.fillOval(x1 - r, y1 - r, d, d);
                gc.fillOval(x2 - r, y2 - r, d, d);

            } else {
                gc.fillOval(x1 - r, y1 - r, d, d); // draw mark point
            }

        }
        drawArrow(gc, race.getWindDirection()); // draw wind direction arrow

    }


    /**
     * Draw boundary
     * @param gc GraphicsContext
     */
    public void drawBoundary(GraphicsContext gc) {
        gc.save();
        ArrayList<Double> boundaryX = new ArrayList<>();
        ArrayList<Double> boundaryY = new ArrayList<>();
        for (MutablePoint point: this.race.getCourseBoundary()) {

            boundaryX.add(point.getXValue());
            boundaryY.add(point.getYValue());

        }
        gc.setLineDashes(5);
        gc.setLineWidth(0.8);
        gc.strokePolygon(Doubles.toArray(boundaryX),Doubles.toArray(boundaryY),boundaryX.size());
        gc.setFill(Color.POWDERBLUE);
        //shade inside the boundary
        gc.fillPolygon(Doubles.toArray(boundaryX),Doubles.toArray(boundaryY),boundaryX.size());
        gc.restore();

    }



    /**
     * Draw annotations
     * @param boat Competitor a competing boat
     * @param gc Graphics Context
     */
    private void drawAnnotations(Competitor boat, GraphicsContext gc) {
        double xValue = boat.getPosition().getXValue();
        double yValue = boat.getPosition().getYValue();
        //set font to monospaced for easier layout formatting
        gc.setFont(Font.font("Monospaced"));

        //draw labels if show all annotations is toggled
        if (showAnnotations) {
            gc.fillText(boat.getAbbreName(), xValue - 10, yValue - 20);
            gc.fillText(boat.getVelocity() + " m/s", xValue - 20, yValue + 20);
        }
        //draws only the selected labels
        if (showImportantAnnotations) {
            if(setNameLabel) {
                gc.fillText(boat.getAbbreName(), xValue - 10, yValue - 20);
            }
            if(setSpeedLabel) {
                gc.fillText(boat.getVelocity() + " m/s", xValue - 20, yValue + 20);
            }
        }
    }

    /**
     * Draw boat competitor
     * @param boat Competitor a competing boat
     * @param gc GraphicsContext
     */
    private void drawBoat(Competitor boat, GraphicsContext gc) {
        double xValue = boat.getPosition().getXValue();
        double yValue = boat.getPosition().getYValue();
        double d = 10.0;
        double h = 10.0;

        gc.setFill(boat.getColor());
        double[] xPoints = new double[] {
                xValue, xValue - (d/2), xValue + (d/2)
        };
        double[] yPoints = new double[] {
                yValue - h, yValue, yValue
        };

        gc.save();
        Rotate r = new Rotate(boat.getCurrentHeading(), xValue, yValue); // rotate object
        gc.setTransform(r.getMxx(), r.getMyx(), r.getMxy(), r.getMyy(), r.getTx(), r.getTy());
        gc.fillPolygon(xPoints, yPoints, 3);
        gc.setStroke(boat.getColor());
        gc.strokeLine(xValue, yValue, xValue, yValue -h);
        gc.restore();
    }


    /**
     * Draw boat wakes and factor it with its velocity
     * @param boat Competitor a competitor
     * @param gc Graphics Context
     */
    private void drawWake(Competitor boat, GraphicsContext gc) {
        double xValue = boat.getPosition().getXValue();
        double yValue = boat.getPosition().getYValue();
        double wakeDirection = Math.toRadians(boat.getCurrentHeading());
        double wakeLength = boat.getVelocity();
        double x_len = wakeLength * sin(wakeDirection);
        double y_len = wakeLength * cos(wakeDirection);
        double x2 = xValue - x_len;
        double y2 = yValue + y_len;
        gc.setStroke(Color.CORNFLOWERBLUE);
        gc.strokeLine(xValue, yValue, x2, y2);
    }


    /**
     * Implementation of ClockHandler interface method
     * @param newTime The currentTime of the clock
     */
    public void clockTicked(String newTime) {
        timerText.setText(newTime);
    }


    /**
     * Starts the animation timer to animate the race
     * @param width the width of the canvas
     * @param height the height of the canvas
     */
    public void animate(double width, double height){

        // start the race using the timeline
        Timeline t = race.generateTimeline();
        List<Competitor> competitors = race.getCompetitors();

        AnimationTimer timer = new AnimationTimer() {

            long startTimeNano = System.nanoTime();
            long currentTimeNano = System.nanoTime();
            int counter = 0;

            @Override
            public void handle(long now) {
                counter++; // increment fps counter

                // clear the canvas
                GraphicsContext gc = mycanvas.getGraphicsContext2D();
                gc.clearRect(0,0,width,height);

                // calculate fps
                currentTimeNano = System.nanoTime();
                if (currentTimeNano > startTimeNano + 1000000000){
                    startTimeNano = System.nanoTime();
                    fpsCounter.setText(String.format("FPS: %d",counter));
                    counter = 0;
                }

                // draw course
                gc.setFill(Color.LIGHTBLUE);
                gc.fillRect(0,0,width,height);
                drawCourse(gc);


                // draw wake - separate loop so all wakes drawn underneath boats
                for(int i =0; i< competitors.size(); i++)  {
                    Competitor boat = competitors.get(i);
                    drawWake(boat, gc);
                }

                // draw competitors and annotations
                for(int i =0; i< competitors.size(); i++)  {
                    Competitor boat = competitors.get(i);
                    drawBoat(boat, gc);
                    drawAnnotations(boat, gc);

                }

                // show race time
                //timerText.setText(formatDisplayTime(System.currentTimeMillis() - startTime));

            }
        };

        timer.start();
        t.play();

    }



    /**
     * Called when the user clicks toggle fps from the view menu bar.
     */
    @FXML
    public void toggleFPS(){
        fpsCounter.setVisible(!fpsCounter.visibleProperty().getValue());
    }

    /**
     * Called when the user clicks toggle annotations from the view menu bar.
     */
    @FXML
    public void toggleAnnotations() {
        if(showAnnotations) {
            showAnnotations = false;

        }
        else {
            showAnnotations = true;
        }
    }

    /**
     * Called when the user clicks toggle important annotations from the view menu bar.
     */
    @FXML
    public void toggleImportantAnnotations() {
        if (showImportantAnnotations) {
            showImportantAnnotations = false;
        }
        else {
            showImportantAnnotations = true;


        }
    }

    /**
     * Called when the user clicks Name label from the Set important annotations menu bar.
     */
    @FXML
    public void setBoatNameLabel(){
        if(setNameLabel) {
            setNameLabel = false;
        }else setNameLabel =true;
    }

    /**
     * Called when the user clicks Speed label from the Set important annotations menu bar.
     */

    @FXML
    public void setBoatSpeedLabel(){
        if(setSpeedLabel) {
            setSpeedLabel = false;
        }else setSpeedLabel =true;
    }


}
