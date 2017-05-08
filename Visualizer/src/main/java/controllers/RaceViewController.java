package controllers;

import model.*;
import parsers.MarkData;
import com.google.common.primitives.Doubles;
import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import utilities.DataReceiver;

import java.net.URL;
import java.util.*;

import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static javafx.scene.paint.Color.*;

/**
 * Controller for the race view.
 */
public class RaceViewController implements ClockHandler, Initializable {

    @FXML
    public Text worldClockValue;
    @FXML
    private AnchorPane raceView;
    @FXML
    private Pane raceViewPane;
    @FXML
    private Canvas raceViewCanvas;
    @FXML
    private Text timerText;
    @FXML
    private Label fpsCounter;
    @FXML
    private RadioButton allAnnotationsRadio;
    @FXML
    private RadioButton noAnnotationsRadio;
    @FXML
    private RadioButton someAnnotationsRadio;
    @FXML
    private CheckBox speedButton;
    @FXML
    private CheckBox nameButton;
    @FXML
    private CheckBox fpsToggle;
    @FXML
    private Text status;

    private TableController tableController;
    private Clock raceClock;
    private Clock worldClock;
    private List<Polygon> boatModels = new ArrayList<>();
    private List<Polyline> wakeModels = new ArrayList<>();
    private List<Label> nameAnnotations = new ArrayList<>();
    private List<Label> speedAnnotations = new ArrayList<>();
    private DataReceiver dataReceiver;
    private List<MutablePoint> courseBoundary = null;
    private List<CourseFeature> courseFeatures = null;
    private List<MarkData> startMarks = null;
    private List<MarkData> finishMarks = null;
    private Map<String, Shape> markModels = new HashMap<>();
    private List<Line> lineModels = new ArrayList<>();


    @Override
    public void initialize(URL location, ResourceBundle resources) {

        final ToggleGroup annotations = new ToggleGroup();
        allAnnotationsRadio.setToggleGroup(annotations);
        noAnnotationsRadio.setToggleGroup(annotations);
        someAnnotationsRadio.setToggleGroup(annotations);

        allAnnotationsRadio.setSelected(true);
        showAllAnnotations();

        fpsToggle.setSelected(true);
    }

    public void setTableController(TableController tb) {
        tableController = tb;
    }

    /**
     * Called when the user clicks no annotations button.
     * Clears individual annotations
     */
    @FXML
    public void clearAnnotations() {
        speedButton.setSelected(false);
        nameButton.setSelected(false);
    }

    /**
     * Called when the user clicks all Annotations button.
     * Clears individual annotations
     */
    @FXML
    public void showAllAnnotations() {
        speedButton.setSelected(true);
        nameButton.setSelected(true);
    }

    /**
     * Sets the race and the race start time and then animates the race
     *
     * @param width  double the width of the canvas
     * @param height double the height of the canvas
     */
    public void begin(double width, double height, DataReceiver dataReceiver) {

        raceViewCanvas.setHeight(height);
        raceViewCanvas.setWidth(width);
        raceViewPane.setPrefHeight(height);
        raceViewPane.setPrefWidth(width);

        this.dataReceiver = dataReceiver;

        long expectedStartTime = dataReceiver.getExpectedStartTime();
        long firstMessageTime = dataReceiver.getMessageTime();
        if (expectedStartTime != 0 && firstMessageTime != 0) {
            this.raceClock = new RaceClock(this, 1, 0);
            long raceTime = firstMessageTime - expectedStartTime;
            long startTime = System.currentTimeMillis() - raceTime;
            raceClock.start(startTime);
        } else {
            this.raceClock = new RaceClock(this, 1, 27000);
            raceClock.start();
        }

        String timezone = dataReceiver.getCourseTimezone();
        this.worldClock = new WorldClock(this, timezone);
        worldClock.start();


        animate(width, height);
    }


    /**
     * Implementation of ClockHandler interface method
     *
     * @param newTime The currentTime of the clock
     */
    public void clockTicked(String newTime, Clock clock) {
        if (clock == raceClock) {
            timerText.setText(newTime);
        }
        if (clock == worldClock) {
            worldClockValue.setText(newTime);
        }
    }

    /**
     * Draws an arrow on the screen at top left corner
     *
     * @param angle double the angle of rotation
     */
    void drawArrow(double angle, GraphicsContext gc) {
        gc.save();
        gc.setFill(Color.BLACK);
        Rotate r = new Rotate(angle, 55, 105); // rotate object
        gc.setTransform(r.getMxx(), r.getMyx(), r.getMxy(), r.getMyy(), r.getTx(), r.getTy());
        gc.translate(0.0, 30.0);
        gc.fillPolygon(new double[]{40, 50, 50, 60, 60, 70, 55}, new double[]{70, 70, 110, 110, 70, 70, 50},
                7);
        gc.restore();
    }


    /**
     * Draws the course features on the canvas
     */
    private void drawCourse() {

        // loops through all course features
        for (CourseFeature courseFeature : courseFeatures) {
            Shape mark = this.markModels.get(courseFeature.getName());
            if (mark != null) {
                this.raceViewPane.getChildren().remove(mark);
            }
            drawMark(courseFeature);
        }
    }

    /**
     * Draws the line for gates
     *
     * @param gates List of MarkData
     */
    private void drawLine(List<MarkData> gates) {
        double x1 = gates.get(0).getTargetLat();
        double y1 = gates.get(0).getTargetLon();
        double x2 = gates.get(1).getTargetLat();
        double y2 = gates.get(1).getTargetLon();
        Line line = new Line(x1, y1, x2, y2);
        this.raceViewPane.getChildren().add(line);
        this.lineModels.add(line);

    }

    private void drawMark(CourseFeature courseFeature) {


        double x = courseFeature.getPixelLocations().get(0).getXValue();
        double y = courseFeature.getPixelLocations().get(0).getYValue();
        Circle circle = new Circle(x, y, 7.5, ORANGERED);
        this.raceViewPane.getChildren().add(circle);
        this.markModels.put(courseFeature.getName(), circle);


    }

    /**
     * Draw boundary
     *
     * @param gc GraphicsContext
     */
    public void drawBoundary(GraphicsContext gc) {

        if (courseBoundary != null) {
            gc.save();
            ArrayList<Double> boundaryX = new ArrayList<>();
            ArrayList<Double> boundaryY = new ArrayList<>();

            for (MutablePoint point : courseBoundary) {
                boundaryX.add(point.getXValue());
                boundaryY.add(point.getYValue());
            }
            gc.setLineDashes(5);
            gc.setLineWidth(0.8);
            gc.strokePolygon(Doubles.toArray(boundaryX), Doubles.toArray(boundaryY), boundaryX.size());
            gc.setFill(Color.POWDERBLUE);

            //shade inside the boundary
            gc.fillPolygon(Doubles.toArray(boundaryX), Doubles.toArray(boundaryY), boundaryX.size());
            gc.restore();

        }

    }


    /**
     * Draw annotations
     *
     * @param boat Competitor a competing boat
     */
    private void drawAnnotations(Competitor boat) {

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
     *
     * @param boat  The boat to position the labels with
     * @param index The index of the annotations in the list
     */
    private void moveAnnotations(Competitor boat, Integer index) {

        Double xValue = boat.getPosition().getXValue();
        Double yValue = boat.getPosition().getYValue();
        Label nameLabel = this.nameAnnotations.get(index);
        Label speedLabel = this.speedAnnotations.get(index);

        //draws name
        if (nameButton.isSelected()) {
            nameLabel.toFront();
            nameLabel.setText(boat.getAbbreName());
            nameLabel.setLayoutX(xValue - 25);
            nameLabel.setLayoutY(yValue - 25);
        } else {
            nameLabel.setText("");
        }

        //draws speed
        if (speedButton.isSelected()) {
            speedLabel.toFront();
            speedLabel.setText(String.valueOf(boat.getVelocity()) + "m/s");
            speedLabel.setLayoutX(xValue + 5);
            speedLabel.setLayoutY(yValue + 15);
        } else {
            speedLabel.setText("");
        }

        if (!(nameButton.isSelected() && speedButton.isSelected() || !nameButton.isSelected() && !speedButton.isSelected())) {
            someAnnotationsRadio.setSelected(true);
        }

        // draws FPS counter
        if (fpsToggle.isSelected()) {
            fpsCounter.setVisible(true);
        } else {
            fpsCounter.setVisible(false);
        }
    }

    /**
     * Draw boat competitor
     *
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
     *
     * @param boat  Boat the boat to move
     * @param index The index of the boat model in the list
     */
    private void moveBoat(Competitor boat, Integer index) {

        //Translate and rotate the corresponding boat model
        boatModels.get(index).setLayoutX(boat.getPosition().getXValue());
        boatModels.get(index).setLayoutY(boat.getPosition().getYValue());
        boatModels.get(index).toFront();
        boatModels.get(index).getTransforms().clear();
        boatModels.get(index).getTransforms().add(new Rotate(boat.getCurrentHeading(), 0, 0));

    }


    /**
     * Draw boat competitor
     *
     * @param boat Competitor a competing boat
     */
    private void drawWake(Competitor boat) {

        //draw the line
        double wakeLength = boat.getVelocity();
        Polyline wake = new Polyline();
        double boatLength = 20;
        wake.getPoints().addAll(
                0.0, boatLength,
                0.0, wakeLength + boatLength);
        wake.setStrokeWidth(4);
        wake.setStroke(DARKBLUE);

        //add to pane and store a reference
        this.raceViewPane.getChildren().add(wake);
        this.wakeModels.add(wake);
    }


    /**
     * Draw boat wakes and factor it with its velocity
     *
     * @param boat  Competitor a competitor
     * @param index Index
     */
    private void moveWake(Competitor boat, Integer index) {

        double newLength = boat.getVelocity() * 1.5;
        double boatLength = 20;

        Polyline wakeModel = wakeModels.get(index);
        wakeModel.getTransforms().clear();
        wakeModel.getPoints().clear();
        wakeModel.getPoints().addAll(0.0, boatLength, 0.0, newLength + boatLength);
        wakeModel.getTransforms().add(new Translate(boat.getPosition().getXValue(), boat.getPosition().getYValue()));
        wakeModel.getTransforms().add(new Rotate(boat.getCurrentHeading(), 0, 0));
    }


    /**
     * Draw the next dot of track for the boat on the canvas
     *
     * @param boat Competitor
     * @param gc   GraphicsContext the gc to draw the track on
     */
    private void drawTrack(Competitor boat, GraphicsContext gc) {
        gc.setFill(boat.getColor());
        gc.save();
        Rotate r = new Rotate(boat.getCurrentHeading(), boat.getPosition().getXValue(), boat.getPosition().getYValue());
        Rotate rr = new Rotate(-boat.getCurrentHeading(), boat.getPosition().getXValue(), boat.getPosition().getYValue());

        gc.setTransform(r.getMxx(), r.getMyx(), r.getMxy(), r.getMyy(), r.getTx(), r.getTy());
        gc.translate(0, 3);
        gc.fillOval(boat.getPosition().getXValue() - 1, boat.getPosition().getYValue() - 1, 2, 2);
        gc.setTransform(rr.getMxx(), rr.getMyx(), rr.getMxy(), rr.getMyy(), rr.getTx(), rr.getTy());

        gc.restore();
    }


    /**
     * Draw boat wakes and factor it with its velocity
     *
     * @param boat Competitor a competitor
     * @param gc   Graphics Context
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

    private void moveMark(String name, double x, double y) {
        this.markModels.get(name).setLayoutX(x);
        this.markModels.get(name).setLayoutY(y);
    }


    /**
     * Starts the animation timer to animate the race
     *
     * @param width  the width of the canvas
     * @param height the height of the canvas
     */
    private void animate(double width, double height) {

        GraphicsContext gc = raceViewCanvas.getGraphicsContext2D();

        //draw the course
        gc.setFill(Color.LIGHTBLUE);
        gc.fillRect(0, 0, width, height);

        //draw wind direction arrow
        drawArrow(dataReceiver.getWindDirection(), gc);

        while (dataReceiver.getCompetitors() == null) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        List<Competitor> competitors = dataReceiver.getCompetitors();
        for (Competitor boat : competitors) {
            this.drawWake(boat);
            this.drawBoat(boat);
            this.drawAnnotations(boat);
        }

        AnimationTimer timer = new AnimationTimer() {

            long startTimeNano = System.nanoTime();
            long currentTimeNano = System.nanoTime();
            int counter = 0;
            int count = 0;

            @Override
            public void handle(long now) {

                // update race status string if it changed
                String statusString = "Race status: " + dataReceiver.getRaceStatus();
                if (!statusString.equals(status.getText())) {
                    status.setText("Race status: " + dataReceiver.getRaceStatus());
                }

                counter++; // increment fps counter

                // calculate fps
                currentTimeNano = System.nanoTime();
                if (currentTimeNano > startTimeNano + 1000000000) {
                    startTimeNano = System.nanoTime();
                    fpsCounter.setText(String.format("FPS: %d", counter));
                    counter = 0;
                }

                // check if course features need to be redrawn
                count++;
                if (dataReceiver.getCourseFeatures() != (courseFeatures)) {
                    courseFeatures = dataReceiver.getCourseFeatures();
                    drawCourse();

                    // check if boundary needs to be redrawn
                    if (dataReceiver.getCourseBoundary() != courseBoundary) {
                        courseBoundary = dataReceiver.getCourseBoundary();
                        drawBoundary(gc);
                        drawLine(dataReceiver.getStartMarks());
                        drawLine(dataReceiver.getFinishMarks());
                    }
                }

                //move competitors and draw tracks
                for (int i = 0; i < competitors.size(); i++) {
                    Competitor boat = competitors.get(i);
                    if (counter % 70 == 0) {
                        drawTrack(boat, gc);
                    }
                    moveWake(boat, i);
                    moveBoat(boat, i);
                    moveAnnotations(boat, i);

                }
                //update table
                tableController.setTable(competitors);
            }
        };

        timer.start();
    }


}
