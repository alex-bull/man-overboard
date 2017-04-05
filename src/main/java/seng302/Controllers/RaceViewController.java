package seng302.Controllers;

import com.google.common.primitives.Doubles;
import javafx.animation.AnimationTimer;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Polyline;
import javafx.scene.text.Text;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import seng302.Model.*;

import java.util.ArrayList;
import java.util.List;

import static javafx.scene.paint.Color.DARKBLUE;

/**
 * Controller for the race view.
 */
public class RaceViewController implements ClockHandler {

    @FXML private AnchorPane raceView;
    @FXML private Pane raceViewPane;
    @FXML private Canvas raceViewCanvas;
    @FXML private Text timerText;
    @FXML private Label fpsCounter;

    private Race race;
    private boolean showAnnotations = true;
    private List<Polygon> boatModels = new ArrayList<>();
    private List<Polyline> wakeModels = new ArrayList<>();




    /**
     * Sets the race and the race start time and then animates the race
     * @param race Race  group of competitors across multiple races on a course
     * @param width double the width of the canvas
     * @param height double the height of the canvas
     */
    public void begin(Race race, double width, double height) {
        this.race=race;
        Clock raceClock = new RaceClock(this, race.getVelocityScaleFactor(), 27000);
        raceViewCanvas.setHeight(height);
        raceViewCanvas.setWidth(width);
        raceViewPane.setPrefHeight(height);
        raceViewPane.setPrefWidth(width);
        raceClock.start();
        animate(width, height);


    }


    /**
     * Draws an arrow on the screen at top left corner
     * @param angle double the angle of rotation
     */
    void drawArrow(double angle) {
//        gc.save();
//        gc.setFill(Color.BLACK);
//        Rotate r = new Rotate(angle, 55, 90); // rotate object
//        gc.setTransform(r.getMxx(), r.getMyx(), r.getMxy(), r.getMyy(), r.getTx(), r.getTy());
//
//        gc.fillPolygon(new double[]{40,50,50,60,60,70,55}, new double[]{70,70,110,110,70,70,50},
//                7);
//
//        gc.restore();
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
     */
    private void drawAnnotations(Competitor boat) {
//        double xValue = boat.getPosition().getXValue();
//        double yValue = boat.getPosition().getYValue();
//        //set font to monospaced for easier layout formatting
//        gc.setFont(Font.font("Monospaced"));
//
//        //draw labels if show all annotations is toggled
//        if (showAnnotations) {
//            gc.fillText(boat.getAbbreName(), xValue - 10, yValue - 20);
//            gc.fillText(boat.getVelocity() + " m/s", xValue - 20, yValue + 20);
//        }
    }

    /**
     * Draw boat competitor
     * @param boat Competitor a competing boat
     */
    private void drawBoat(Competitor boat) {

        //Draw the boat triangle
        Polygon boatModel = new Polygon();
        boatModel.getPoints().addAll(
                0.0, 0.0, //top
                -5.0, 20.0, //left
                5.0, 20.0); //right
        boatModel.setFill(boat.getColor());
        boatModel.setLayoutX(boat.getPosition().getXValue());
        boatModel.setLayoutY(boat.getPosition().getYValue());
        System.out.println(boatModel.getLayoutBounds());

        boatModel.getTransforms().add(new Rotate(boat.getCurrentHeading(), 0, 0));
        //add the boat to the view
        this.raceViewPane.getChildren().add(boatModel);
        //keep a reference to the boat
        this.boatModels.add(boatModel);


    }

    /**
     * Draw boat competitor
     * @param boat Competitor a competing boat
     */
    private void drawWake(Competitor boat) {

        double wakeLength = boat.getVelocity();
        Polyline wake = new Polyline();
        wake.getPoints().addAll(
                0.0, 0.0,
                0.0, wakeLength);
        wake.setStrokeWidth(4);
        wake.setStroke(DARKBLUE);

//        wake.setLayoutX(boat.getPosition().getXValue());
//        wake.setLayoutY(boat.getPosition().getYValue());

        wake.getTransforms().add(new Rotate(boat.getCurrentHeading(), 0, 0));
        wake.getTransforms().add(new Translate(boat.getPosition().getXValue(), boat.getPosition().getYValue()));

        this.raceViewPane.getChildren().add(wake);
        this.wakeModels.add(wake);


    }


    /**
     * Moves the visual boat model to the current position in boat
     * @param boat Boat the boat to move
     * @param index The index of the boat model in the list
     */
    private void moveBoat(Competitor boat, Integer index) {

        //Translate and rotate the corresponding boat model
        boatModels.get(index).setLayoutX(boat.getPosition().getXValue());
        boatModels.get(index).setLayoutY(boat.getPosition().getYValue());
//        boatModels.get(index).setRotate(boat.getCurrentHeading());
        boatModels.get(index).toFront();
        boatModels.get(index).getTransforms().remove(boatModels.get(index).getTransforms().size() - 1);
        boatModels.get(index).getTransforms().add(new Rotate(boat.getCurrentHeading(), 0,0));

    }


    /**
     * Draw the next section of track for the boat on the canvas
     * @param boat
     */
    private void drawTrack(Competitor boat, GraphicsContext gc) {
        gc.setFill(boat.getColor());
        gc.fillOval(boat.getPosition().getXValue(), boat.getPosition().getYValue(), 2,2);
    }


    /**
     * Draw boat wakes and factor it with its velocity
     * @param boat Competitor a competitor
     * @param index
     */
    private void moveWake(Competitor boat, int index) {

//        double xValue = boat.getPosition().getXValue();
//        double yValue = boat.getPosition().getYValue();
//        double wakeDirection = Math.toRadians(boat.getCurrentHeading());
//        double wakeLength = boat.getVelocity();
//

//        double x_len = wakeLength * sin(wakeDirection);
//        double y_len = wakeLength * cos(wakeDirection);
//        double x1 = xValue - (2* sin(wakeDirection));
//        double y1 = yValue - (20 * cos(wakeDirection));
//        double x2 = x1 - x_len;
//        double y2 = y1 + y_len;


//        wakeModels.get(index).setLayoutX(boat.getPosition().getXValue());
//        wakeModels.get(index).setLayoutY(boat.getPosition().getYValue()-20);

        wakeModels.get(index).getTransforms().remove(boatModels.get(index).getTransforms().size() - 1);
        wakeModels.get(index).getTransforms().add(new Translate(boat.getPosition().getXValue(), boat.getPosition().getYValue()));

        wakeModels.get(index).getTransforms().remove(boatModels.get(index).getTransforms().size() - 1);
        wakeModels.get(index).getTransforms().add(new Rotate(boat.getCurrentHeading(), 0, 0));

//        if (index >= this.wakeModels.size()) {
//            wakeModels.add(wake);
//        }
//        else {
//            Polyline oldWake = wakeModels.get(index);
//            this.raceViewPane.getChildren().remove(oldWake);
//        }
//        wakeModels.set(index, wake);
//        this.raceViewPane.getChildren().add(wake);

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

        GraphicsContext gc = raceViewCanvas.getGraphicsContext2D();

        //draw the course
        gc.setFill(Color.LIGHTBLUE);
        gc.fillRect(0,0,width,height);
        drawCourse(gc);



        //draw competitors and annotations
        for(int i =0; i< competitors.size(); i++)  {
            Competitor boat = competitors.get(i);
            drawBoat(boat);
            //drawAnnotations(boat);
        }

        for(int i =0; i< competitors.size(); i++)  {
            Competitor boat = competitors.get(i);
            drawWake(boat);
            //drawAnnotations(boat);
        }


        AnimationTimer timer = new AnimationTimer() {

            long startTimeNano = System.nanoTime();
            long currentTimeNano = System.nanoTime();
            int counter = 0;

            @Override
            public void handle(long now) {
                counter++; // increment fps counter

                // clear the canvas
//                gc.clearRect(0,0,width,height);

                // calculate fps
                currentTimeNano = System.nanoTime();
                if (currentTimeNano > startTimeNano + 1000000000){
                    startTimeNano = System.nanoTime();
                    fpsCounter.setText(String.format("FPS: %d",counter));
                    counter = 0;
                }

                //draw the tracks- separate loop so all tracks drawn underneath boats
                for(int i =0; i< competitors.size(); i++)  {
                    Competitor boat = competitors.get(i);
                    drawTrack(boat, gc);
                }
//
                // draw wake - separate loop so all wakes drawn underneath boats
                for(int i =0; i< competitors.size(); i++)  {
                    Competitor boat = competitors.get(i);
                    moveWake(boat, i);
                }
//
                // move competitors and annotations
                for(int i =0; i< competitors.size(); i++)  {
                    Competitor boat = competitors.get(i);
                    moveBoat(boat, i);
                }
//
//                //draw wind direction
//                drawArrow(race.getWindDirection()); // draw wind direction arrow

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


}
