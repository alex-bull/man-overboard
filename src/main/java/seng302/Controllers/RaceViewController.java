package seng302.Controllers;

import com.google.common.collect.Iterables;
import com.google.common.math.DoubleMath;
import com.google.common.math.Quantiles;
import com.google.common.math.Stats;
import com.google.common.primitives.Doubles;
import com.sun.deploy.util.ArrayUtil;
import edu.princeton.cs.algorithms.GrahamScan;
import edu.princeton.cs.algorithms.Point2D;
import javafx.animation.AnimationTimer;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.Font;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Scale;
import seng302.Model.*;

import java.awt.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;

import static java.lang.Math.abs;

/**
 * Controller for the race view.
 */
public class RaceViewController implements Initializable{

    @FXML private Canvas mycanvas;
    @FXML private Text timerText;
    @FXML private Label fpsCounter;
    @FXML private TableController tableController;

    private long startTime;
    private Race race;
    private boolean showAnnotations = true;
    private List<Double> boundaryX;
    private List<Double> boundaryY;

    /**
     * Initialiser for the raceViewController
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        boundaryX=new ArrayList<>();
        boundaryY=new ArrayList<>();
    }


    /**
     * Sets the race and the race start time and then animates the race
     * @param race Race  group of competitors across multiple races on a course
     * @param width double the width of the canvas
     * @param height double the height of the canvas
     */
    public void begin(Race race, double width, double height) {
        this.race=race;
        startTime = System.currentTimeMillis();
        mycanvas.setHeight(height);
        mycanvas.setWidth(width);
        calcBoundary();
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

        //draw the course boundary
        //calculate the mid point
        double pivotX= Quantiles.median().compute(boundaryX);
        //double pivotX2=(Collections.min(boundaryX)+Collections.max(boundaryX))/2;
        double pivotY=Quantiles.median().compute(boundaryY);
        //double pivotY2=(Collections.min(boundaryY)+Collections.max(boundaryY))/2;
        gc.save();

        Scale s=new Scale(1.15,1.15,pivotX,pivotY);
        //Scale s2=new Scale(1.15,1.15,pivotX2,pivotY2);
        gc.setLineDashes(5);
        gc.setLineWidth(0.8);
        //scale the boundary
        gc.setTransform(s.getMxx(), s.getMyx(), s.getMxy(), s.getMyy(), s.getTx(), s.getTy());
        //gc.setTransform(s2.getMxx(), s2.getMyx(), s2.getMxy(), s2.getMyy(), s2.getTx(), s2.getTy());
        //draw boundary
        gc.strokePolygon(Doubles.toArray(boundaryX),Doubles.toArray(boundaryY),boundaryX.size());
        gc.setFill(Color.DEEPSKYBLUE);
        //shade inside the boundary
        gc.fillPolygon(Doubles.toArray(boundaryX),Doubles.toArray(boundaryY),boundaryX.size());
        gc.restore();
        //draw pivot for testing
//        gc.setFill(Color.BLACK);
//        gc.fillOval(pivotX,pivotY,10,10);


        // loops through all course features
        for (CourseFeature courseFeature : this.race.getCourseFeatures().subList(1, race.getCourseFeatures().size())) {
            gc.setFill(Color.ORANGERED); // buoy colour
            gc.setStroke(Color.BLUE); // line colour between gates

            // get the pixel locations of the course feature
            List<MutablePoint> marks = courseFeature.getPixelLocations();
            Double x1 = marks.get(0).getXValue(); // first x pixel coordinate
            Double y1 = marks.get(0).getYValue(); // first y pixel coordinate

            // if it is a gate it will have two pixel location coordinates
            if (marks.size() == 2) {
                gc.setLineWidth(3);
                int d = 15; // diameter of the circle
                double r = d/2; // radius of the circle
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
                gc.fillOval(x1, y1, 20, 20); // draw mark point
            }

        }
        drawArrow(gc, race.getWindDirection()); // draw wind direction arrow



    }


    /**
     * Starts the animation timer to animate the race
     * @param width the width of the canvas
     * @param height the height of the canvas
     */
    public void animate(double width, double height){

        // arraylists to store coordinates
        ArrayList<Double> xCoords = new ArrayList<>();
        ArrayList<Double> yCoords = new ArrayList<>();
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





                // draw competitors
                for(int i =0; i< competitors.size(); i++)  {
                    Competitor boat=competitors.get(i);
                    double xValue=boat.getPosition().getXValue();
                    double yValue=boat.getPosition().getYValue();
                    gc.setFill(boat.getColor());
                    gc.fillOval(
                            xValue,
                            yValue,
                            10,
                            10
                    );

                    //set font to monospaced for easier layout formatting
                    gc.setFont(Font.font("Monospaced"));

                    //draw labels if show all annotations is toggled
                    if (showAnnotations) {
                        gc.fillText(boat.getAbbreName(),xValue-10,yValue);
                        gc.fillText(boat.getVelocity()+" m/s",xValue-20,yValue+20);
                        yCoords.add(yValue);
                        xCoords.add(xValue);
                    }


                }
                // show race time
                timerText.setText(formatDisplayTime(System.currentTimeMillis() - startTime));



            }
        };

        timer.start();
        t.play();

    }


    /**
     * Creates a formatted display time string in mm:ss and takes into account the scale factor
     * @param display long the current duration of the race
     * @return String the time string
     * */
    private String formatDisplayTime (long display) {

        int scaleFactor = race.getVelocityScaleFactor();
        // calculate the actual race time using the scale factor
        display = (display - (27000 / scaleFactor)) * scaleFactor;

        // format the time shown
        int displayTime = abs((int)display/1000);
        int minutes = displayTime / 60;
        int seconds = displayTime - (60 * minutes);
        String formattedTime = "";

        if (display < 0 && displayTime != 0) {
            formattedTime += "-";
        }

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
     * Calculates the boundary of the course by running graham scan algorithm on all points in course feature.
     * Once it is done it adds the points on the convex hull to boundaryX and boundaryY.
     */
    public void calcBoundary(){
        ArrayList<Point2D> coords=new ArrayList<>();
        for(CourseFeature feature : race.getCourseFeatures()){
            if (feature.getName().equals("Prestart")){
                continue;
            }
            for(MutablePoint p:feature.getPixelLocations()){
                Point2D point=new Point2D(p.getXValue(),p.getYValue());
                coords.add(point);
            }
        }
        GrahamScan gs=new GrahamScan(coords.toArray(new Point2D[coords.size()]));

        gs.hull().forEach(point2D -> {
//            System.out.println(point2D);
            boundaryX.add(point2D.x());
            boundaryY.add(point2D.y());});
    }

}
