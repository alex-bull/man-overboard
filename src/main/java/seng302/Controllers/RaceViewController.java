package seng302.Controllers;

import com.google.common.primitives.Doubles;
import javafx.animation.AnimationTimer;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Polyline;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Translate;
import seng302.Model.*;

import java.util.ArrayList;
import java.util.List;

import static javafx.scene.paint.Color.BLACK;
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
    private List<Label> nameAnnotations = new ArrayList<>();
    private List<Label> speedAnnotations = new ArrayList<>();




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
    void drawArrow(double angle, GraphicsContext gc) {
        gc.save();
        gc.setFill(Color.BLACK);
        Rotate r = new Rotate(angle, 55, 105); // rotate object
        gc.setTransform(r.getMxx(), r.getMyx(), r.getMxy(), r.getMyy(), r.getTx(), r.getTy());
        gc.translate(0.0, 30.0);
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

        Double xValue = boat.getPosition().getXValue();
        Double yValue = boat.getPosition().getYValue();

        //name annotation
        Label nameLabel = new Label(boat.getAbbreName());
        nameLabel.setFont(Font.font("Monospaced"));
        nameLabel.setTextFill(boat.getColor());
        this.raceViewPane.getChildren().add(nameLabel);
        this.nameAnnotations.add(nameLabel);

        //speed annotation
        Label speedLabel = new Label(String.valueOf(boat.getVelocity()) + "m/s");
        speedLabel.setFont(Font.font("Monospaced"));
        speedLabel.setTextFill(boat.getColor());
        this.raceViewPane.getChildren().add(speedLabel);
        this.speedAnnotations.add(speedLabel);
    }

    /**
     * Moves the annotation labels with the boat
     * @param boat The boat to position the labels with
     * @param index The index of the annotations in the list
     */
    private void moveAnnotations(Competitor boat, Integer index) {

        Double xValue = boat.getPosition().getXValue();
        Double yValue = boat.getPosition().getYValue();
        Label nameLabel = this.nameAnnotations.get(index);
        Label speedLabel = this.speedAnnotations.get(index);
        nameLabel.toFront();
        speedLabel.toFront();
        speedLabel.setText(String.valueOf(boat.getVelocity()) + "m/s");
        nameLabel.setLayoutX(xValue - 25);
        nameLabel.setLayoutY(yValue - 25);
        speedLabel.setLayoutX(xValue + 5);
        speedLabel.setLayoutY(yValue + 15);
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
        boatModel.setStroke(BLACK);
        boatModel.setStrokeWidth(1);
        //add to the pane and store a reference
        this.raceViewPane.getChildren().add(boatModel);
        this.boatModels.add(boatModel);
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
        boatModels.get(index).toFront();
        boatModels.get(index).getTransforms().clear();
        boatModels.get(index).getTransforms().add(new Rotate(boat.getCurrentHeading(), 0,0));

    }




    /**
     * Draw boat competitor
     * @param boat Competitor a competing boat
     */
    private void drawWake(Competitor boat) {

        //draw the line
        double wakeLength = boat.getVelocity();
        Polyline wake = new Polyline();
        double boatLength=20;
        wake.getPoints().addAll(
                0.0, boatLength,
                0.0, wakeLength+boatLength);
        wake.setStrokeWidth(4);
        wake.setStroke(DARKBLUE);

        //add to pane and store a reference
        this.raceViewPane.getChildren().add(wake);
        this.wakeModels.add(wake);
    }


    /**
     * Draw boat wakes and factor it with its velocity
     * @param boat Competitor a competitor
     * @param index
     */
    private void moveWake(Competitor boat, Integer index) {

        double newLength = boat.getVelocity();
        double boatLength=20;

        Polyline wakeModel = wakeModels.get(index);
        wakeModel.getTransforms().clear();
        wakeModel.getPoints().clear();
        wakeModel.getPoints().addAll(0.0, boatLength,0.0, newLength+boatLength);
        wakeModel.getTransforms().add(new Translate(boat.getPosition().getXValue(), boat.getPosition().getYValue()));
        wakeModel.getTransforms().add(new Rotate(boat.getCurrentHeading(), 0, 0));
    }


    /**
     * Draw the next dot of track for the boat on the canvas
     * @param boat Competitor
     * @param gc GraphicsContext the gc to draw the track on
     */
    private void drawTrack(Competitor boat, GraphicsContext gc) {
        gc.setFill(boat.getColor());
        gc.save();
        Rotate r=new Rotate(boat.getCurrentHeading(),boat.getPosition().getXValue(),boat.getPosition().getYValue());
        Rotate rr=new Rotate(-boat.getCurrentHeading(),boat.getPosition().getXValue(),boat.getPosition().getYValue());

        gc.setTransform(r.getMxx(), r.getMyx(), r.getMxy(), r.getMyy(), r.getTx(), r.getTy());
        gc.translate(0,3);
        gc.fillOval(boat.getPosition().getXValue() - 1, boat.getPosition().getYValue() - 1, 2, 2);
        gc.setTransform(rr.getMxx(), rr.getMyx(), rr.getMxy(), rr.getMyy(), rr.getTx(), rr.getTy());

        gc.restore();
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
    private void animate(double width, double height){

        // start the race using the timeline
        Timeline t = race.generateTimeline();
        List<Competitor> competitors = race.getCompetitors();
        GraphicsContext gc = raceViewCanvas.getGraphicsContext2D();

        //draw the course
        gc.setFill(Color.LIGHTBLUE);
        gc.fillRect(0,0,width,height);
        drawCourse(gc);

        //draw wind direction arrow
        drawArrow(race.getWindDirection(), gc);

        //draw moving entities
        for(int i =0; i< competitors.size(); i++)  {
            Competitor boat = competitors.get(i);
            drawWake(boat);
            drawBoat(boat);
            drawAnnotations(boat);
        }

        AnimationTimer timer = new AnimationTimer() {

            long startTimeNano = System.nanoTime();
            long currentTimeNano = System.nanoTime();
            int counter = 0;

            @Override
            public void handle(long now) {
                counter++; // increment fps counter

                // calculate fps
                currentTimeNano = System.nanoTime();
                if (currentTimeNano > startTimeNano + 1000000000){
                    startTimeNano = System.nanoTime();
                    fpsCounter.setText(String.format("FPS: %d",counter));
                    counter = 0;
                }

                //move competitors and draw tracks
                for (int i = 0; i < competitors.size(); i++) {
                    Competitor boat = competitors.get(i);
                    if (counter % 20 == 0) drawTrack(boat, gc);
                    moveWake(boat, i);
                    moveBoat(boat, i);
                    moveAnnotations(boat, i);
                }
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
