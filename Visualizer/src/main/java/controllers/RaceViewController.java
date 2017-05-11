package controllers;

import com.google.common.primitives.Doubles;
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
import models.*;
import parsers.xml.race.MarkData;
import utilities.DataSource;

import java.net.URL;
import java.util.*;

import static javafx.scene.paint.Color.*;

/**
 * Controller for the race view.
 */
public class RaceViewController implements ClockHandler, Initializable {

    @FXML public Text worldClockValue;
    @FXML private AnchorPane raceView;
    @FXML private Pane raceViewPane;
    @FXML private Canvas raceViewCanvas;
    @FXML private Text timerText;
    @FXML private Label fpsCounter;
    @FXML private RadioButton allAnnotationsRadio;
    @FXML private RadioButton noAnnotationsRadio;
    @FXML private RadioButton someAnnotationsRadio;
    @FXML private CheckBox speedButton;
    @FXML private CheckBox nameButton;
    @FXML private CheckBox fpsToggle;
    @FXML private Text status;

    private Clock raceClock;
    private Clock worldClock;
    private Map<Integer, Polygon> boatModels = new HashMap<>();
    private Map<Integer, Polyline> wakeModels = new HashMap<>();
    private Map<Integer, Label> nameAnnotations = new HashMap<>();
    private Map<Integer, Label> speedAnnotations = new HashMap<>();
    private List<MutablePoint> courseBoundary = null;
    private List<CourseFeature> courseFeatures = null;
    private Map<String, Shape> markModels = new HashMap<>();
    private DataSource dataSource;

    private long startTimeNano = System.nanoTime();
    private int counter = 0;


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

    /*
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
    public void begin(double width, double height, DataSource dataSource) {

        raceViewCanvas.setHeight(height);
        raceViewCanvas.setWidth(width);
        raceViewPane.setPrefHeight(height);
        raceViewPane.setPrefWidth(width);

        this.dataSource = dataSource;

        long expectedStartTime = dataSource.getExpectedStartTime();
        long firstMessageTime = dataSource.getMessageTime();
        if (expectedStartTime != 0 && firstMessageTime != 0) {
            this.raceClock = new RaceClock(this, 1, 0);
            long raceTime = firstMessageTime - expectedStartTime;
            long startTime = System.currentTimeMillis() - raceTime;
            raceClock.start(startTime);
        } else {
            this.raceClock = new RaceClock(this, 1, 27000);
            raceClock.start();
        }

        String timezone = dataSource.getCourseTimezone();
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

        Double xValue = boat.getPosition().getXValue();
        Double yValue = boat.getPosition().getYValue();
        Label nameLabel;
        Label speedLabel;

        if (nameAnnotations.get(boat.getSourceID()) == null) {
            nameLabel = new Label(boat.getAbbreName());
            nameLabel.setFont(Font.font("Monospaced"));
            nameLabel.setTextFill(boat.getColor());
            this.raceViewPane.getChildren().add(nameLabel);
            this.nameAnnotations.put(boat.getSourceID(), nameLabel);
        }

        if (speedAnnotations.get(boat.getSourceID()) == null) {
            speedLabel = new Label(String.valueOf(boat.getVelocity()) + "m/s");
            speedLabel.setFont(Font.font("Monospaced"));
            speedLabel.setTextFill(boat.getColor());
            this.raceViewPane.getChildren().add(speedLabel);
            this.speedAnnotations.put(boat.getSourceID(), speedLabel);
        }

        nameLabel = nameAnnotations.get(boat.getSourceID());
        speedLabel = speedAnnotations.get(boat.getSourceID());

        if (nameButton.isSelected()) {
            nameLabel.toFront();
            nameLabel.setText(boat.getAbbreName());
            nameLabel.setLayoutX(xValue - 25);
            nameLabel.setLayoutY(yValue - 25);
        } else {
            nameLabel.setText("");
        }

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
     * Draw or move a boat model for a competitor
     * @param boat Competitor a competing boat
     */
    private void drawBoat(Competitor boat) {
        Integer sourceId = boat.getSourceID();

        if (boatModels.get(sourceId) == null) {
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
            this.boatModels.put(boat.getSourceID(), boatModel);
        }
        //Translate and rotate the corresponding boat models
        boatModels.get(sourceId).setLayoutX(boat.getPosition().getXValue());
        boatModels.get(sourceId).setLayoutY(boat.getPosition().getYValue());
        boatModels.get(sourceId).toFront();
        boatModels.get(sourceId).getTransforms().clear();
        boatModels.get(sourceId).getTransforms().add(new Rotate(boat.getCurrentHeading(), 0, 0));
    }



    /**
     * Draw or move a wake for a competitor
     * @param boat Competitor a competing boat
     */
    private void drawWake(Competitor boat) {

        if (wakeModels.get(boat.getSourceID()) == null) {
            double wakeLength = boat.getVelocity();
            Polyline wake = new Polyline();
            double boatLength = 20;
            wake.getPoints().addAll(0.0, boatLength, 0.0, wakeLength + boatLength);
            wake.setStrokeWidth(4);
            wake.setStroke(DARKBLUE);
            //add to pane and store a reference
            this.raceViewPane.getChildren().add(wake);
            this.wakeModels.put(boat.getSourceID(), wake);
        }
        double newLength = boat.getVelocity() * 1.5;
        double boatLength = 20;

        Polyline wakeModel = wakeModels.get(boat.getSourceID());
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
     * Refreshes the contents of the display to match the datasource
     * @param dataSource DataSource the data to display
     */
    public void refresh(DataSource dataSource) {

        GraphicsContext gc = raceViewCanvas.getGraphicsContext2D();
        this.dataSource = dataSource;
        String statusString = "Race status: " + dataSource.getRaceStatus();
        if (!statusString.equals(status.getText())) {
            status.setText("Race status: " + dataSource.getRaceStatus());
        }

        counter++; // increment fps counter

        // calculate fps
        long currentTimeNano = System.nanoTime();
        if (currentTimeNano > startTimeNano + 1000000000) {
            startTimeNano = System.nanoTime();
            fpsCounter.setText(String.format("FPS: %d", counter));
            counter = 0;
        }

        // check if course features need to be redrawn
        if (dataSource.getCourseFeatures() != (courseFeatures)) {
            courseFeatures = dataSource.getCourseFeatures();
            drawCourse();

            // check if boundary needs to be redrawn
            if (dataSource.getCourseBoundary() != courseBoundary) {
                courseBoundary = dataSource.getCourseBoundary();
                drawBoundary(gc);
                drawLine(dataSource.getStartMarks());
                drawLine(dataSource.getFinishMarks());
            }
        }
        List<Competitor> competitors = dataSource.getCompetitors();
        //move competitors and draw tracks
        for (Competitor boat : competitors) {
            if (counter % 70 == 0) {
                drawTrack(boat, gc);
            }
            this.drawWake(boat);
            this.drawBoat(boat);
            this.drawAnnotations(boat);

        }
    }


    /**
     * Starts the animation timer to animate the race
     *
     * @param width  the width of the canvas
     * @param height the height of the canvas
     */
    private void animate(double width, double height) {

        GraphicsContext gc = raceViewCanvas.getGraphicsContext2D();
        gc.setFill(Color.LIGHTBLUE);
        gc.fillRect(0, 0, width, height);

        //draw wind direction arrow
        drawArrow(dataSource.getWindDirection(), gc);

        while (dataSource.getCompetitors() == null) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
