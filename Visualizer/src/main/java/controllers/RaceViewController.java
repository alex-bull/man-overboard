package controllers;

import com.google.common.primitives.Doubles;
import com.sun.javafx.geom.Point2D;
import javafx.animation.FadeTransition;
import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Shape;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.util.Duration;
import models.Competitor;
import models.CourseFeature;
import models.Dot;
import models.MutablePoint;
import netscape.javascript.JSException;
import parsers.Converter;
import utilities.Annotation;
import utilities.DataSource;
import utilities.RaceCalculator;

import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;

import static javafx.scene.paint.Color.*;
import static parsers.RaceStatusEnum.PREPARATORY;

/**
 * Controller for the race view.
 */
public class RaceViewController implements Initializable {

    @FXML private Pane raceViewPane;
    @FXML private Canvas raceViewCanvas;
    @FXML private Label fpsCounter;
    @FXML private RadioButton allAnnotationsRadio;
    @FXML private RadioButton noAnnotationsRadio;
    @FXML private RadioButton someAnnotationsRadio;
    @FXML private CheckBox speedButton;
    @FXML private CheckBox nameButton;
    @FXML private CheckBox timeToMarkButton;
    @FXML private CheckBox timeFromMarkButton;
    @FXML private CheckBox fpsToggle;
    @FXML private Text status;
    @FXML private Group annotationGroup;
    @FXML private WebView mapView;

    private RaceCalculator raceCalculator;
    private WebEngine mapEngine;
    private Map<Integer, Polygon> boatModels = new HashMap<>();
    private Map<Integer, Polygon> wakeModels = new HashMap<>();
    private Map<Integer, Label> nameAnnotations = new HashMap<>();
    private Map<Integer, Label> speedAnnotations = new HashMap<>();
    private Map<Integer, Label> timeToMarkAnnotations = new HashMap<>();
    private Map<Integer, Label> timeFromMarkAnnotations = new HashMap<>();
    private Map<Integer, Label> timeToStartlineAnnotations = new HashMap<>();
    private List<MutablePoint> courseBoundary = null;
    private List<CourseFeature> courseFeatures = null;
    private Map<String, Shape> markModels = new HashMap<>();
    private DataSource dataSource;
    private long startTimeNano = System.nanoTime();
    private long timeFromLastMark;
    private String startAnnotation;
    private int counter = 0;
    private Label startLabel;
    private Line startLine;
    private Line finishLine;
    private boolean isLoaded = false;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        startLine = new Line();
        finishLine = new Line();
        raceCalculator = new RaceCalculator();
        raceViewPane.getChildren().add(startLine);
        raceViewPane.getChildren().add(finishLine);
        final ToggleGroup annotations = new ToggleGroup();
        allAnnotationsRadio.setToggleGroup(annotations);
        noAnnotationsRadio.setToggleGroup(annotations);
        someAnnotationsRadio.setToggleGroup(annotations);
        allAnnotationsRadio.setSelected(true);
        showAllAnnotations();
        fpsToggle.setSelected(true);


        mapEngine = mapView.getEngine();
        mapView.setVisible(true);
        mapEngine.setJavaScriptEnabled(true);
        mapView.toBack();
        try {
            mapEngine.load(getClass().getClassLoader().getResource("maps.html").toURI().toString());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        mapEngine.getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
            if (newState == Worker.State.SUCCEEDED) {
                // new page has loaded, process:
                isLoaded = true;

            }

        });
    }

    /**
     * Called when the user clicks no annotations button.
     * Clears individual annotations
     */
    @FXML
    public void clearAnnotations() {
        speedButton.setSelected(false);
        nameButton.setSelected(false);
        timeToMarkButton.setSelected(false);
        timeFromMarkButton.setSelected(false);
    }

    /**
     * Called when the user clicks all Annotations button.
     * Clears individual annotations
     */
    @FXML
    public void showAllAnnotations() {
        speedButton.setSelected(true);
        nameButton.setSelected(true);
        timeToMarkButton.setSelected(true);
        timeFromMarkButton.setSelected(true);
    }

    /**
     * Sets the race and the race start time and then animates the race
     *
     * @param width  double the width of the canvas
     * @param height double the height of the canvas
     */
    void begin(double width, double height, DataSource dataSource) {
        raceViewCanvas.setHeight(height);
        raceViewCanvas.setWidth(width);
        raceViewPane.setPrefHeight(height);
        raceViewPane.setPrefWidth(width);

        this.dataSource = dataSource;
        drawAnnotations();
        while (dataSource.getCompetitorsPosition() == null) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

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
     * @param gatesID List of integer of the gates
     */
    private void drawLine(Line line, List<Integer> gatesID) {

        double x1 = dataSource.getStoredFeatures().get(gatesID.get(0)).getPixelLocations().get(0).getXValue();
        double y1 = dataSource.getStoredFeatures().get(gatesID.get(0)).getPixelLocations().get(0).getYValue();
        double x2 = dataSource.getStoredFeatures().get(gatesID.get(1)).getPixelLocations().get(0).getXValue();
        double y2 = dataSource.getStoredFeatures().get(gatesID.get(1)).getPixelLocations().get(0).getYValue();
        line.setStartX(x1);
        line.setStartY(y1);
        line.setEndX(x2);
        line.setEndY(y2);
    }

    /**
     * Draw the mark of the course feature
     *
     * @param courseFeature CourseFeature
     */
    private void drawMark(CourseFeature courseFeature) {
        double x = courseFeature.getPixelLocations().get(0).getXValue();
        double y = courseFeature.getPixelLocations().get(0).getYValue();
        Circle circle = new Circle(x, y, 4.5, ORANGERED);
        this.raceViewPane.getChildren().add(circle);
        this.markModels.put(courseFeature.getName(), circle);


    }

    /**
     * Draw the background as the map and relocate it to the screen bounds
     *
     * @param bounds List GPS bounds from the data source
     */
    private void drawBackgroundImage(List<Double> bounds) {
        try {
            mapEngine.executeScript(String.format("relocate(%.9f,%.9f,%.9f,%.9f);", bounds.get(0), bounds.get(1), bounds.get(2), bounds.get(3)));
            mapEngine.executeScript(String.format("shift(%.2f);", dataSource.getShiftDistance()));
        } catch (JSException e) {
            e.printStackTrace();
        }
    }

    /**
     * Draw boundary
     *
     * @param gc GraphicsContext
     */
    private void drawBoundary(GraphicsContext gc) {

        if (courseBoundary != null) {
            gc.save();
            ArrayList<Double> boundaryX = new ArrayList<>();
            ArrayList<Double> boundaryY = new ArrayList<>();

            for (MutablePoint point : courseBoundary) {
                boundaryX.add(point.getXValue());
                boundaryY.add(point.getYValue());
            }

            // set zoom level
            mapEngine.executeScript(String.format("setZoom(%d)", dataSource.getMapZoomLevel()));
            gc.setLineDashes(5);
            gc.setLineWidth(0.8);
            gc.clearRect(0, 0, 4000, 4000);

            drawBackgroundImage(dataSource.getGPSbounds());
            gc.strokePolygon(Doubles.toArray(boundaryX), Doubles.toArray(boundaryY), boundaryX.size());
            gc.setGlobalAlpha(0.4);
            gc.setFill(Color.POWDERBLUE);
            //shade inside the boundary
            gc.fillPolygon(Doubles.toArray(boundaryX), Doubles.toArray(boundaryY), boundaryX.size());
            gc.setGlobalAlpha(1.0);
            gc.restore();

        }

    }


    /**
     * Draw annotations and move with boat positions
     */
    private void drawAnnotations() {
        List<Competitor> competitors = dataSource.getCompetitorsPosition();
        //move competitors and draw tracks
        for (Competitor boat : competitors) {
            int sourceID = boat.getSourceID();

            for (int i = 0; i < annotationGroup.getChildren().size(); i++) {
                String annotationType = ((CheckBox) annotationGroup.getChildren().get(i)).getText();
                Annotation annotation = Annotation.stringToAnnotation(annotationType);
                Label label = null;
                assert annotation != null;
                switch (annotation) {
                    case TEAM_NAME:
                        label = new Label(boat.getAbbreName());
                        this.nameAnnotations.put(sourceID, label);
                        break;
                    case BOAT_SPEED:
                        label = new Label(String.valueOf(boat.getVelocity()) + "m/s");
                        this.speedAnnotations.put(sourceID, label);
                        break;
                    case EST_TIME_TO_NEXT_MARK:
                        label = new Label(String.valueOf(boat.getTimeToNextMark()) + " seconds");
                        this.timeToMarkAnnotations.put(sourceID, label);
                        break;
                    case TIME_FROM_LAST_MARK:
                        label = new Label(String.valueOf(timeFromLastMark) + " seconds");
                        this.timeFromMarkAnnotations.put(sourceID, label);
                        break;
                }

                startLabel = new Label(startAnnotation);
                startLabel.setFont(Font.font(null, FontWeight.BOLD, 20));
                startLabel.setTextFill(boat.getColor());
                this.timeToStartlineAnnotations.put(sourceID, startLabel);
                this.raceViewPane.getChildren().add(startLabel);

                label.setFont(Font.font("Monospaced"));
                label.setTextFill(boat.getColor());
                this.raceViewPane.getChildren().add(label);

            }
        }
    }


    /**
     * Move annotations with competing boat positions
     *
     * @param boat Competitor of competing boat
     */
    private void moveAnnotations(Competitor boat) {
        int sourceID = boat.getSourceID();

        int offset = 10;
        Double xValue = boat.getPosition().getXValue();
        Double yValue = boat.getPosition().getYValue();

        //all selected will be true if all selected
        boolean allSelected = true;
        //none selected will be false if none selected
        boolean noneSelected = false;
        //change radio button depending on what is selected
        for (int i = 0; i < annotationGroup.getChildren().size(); i++) {
            CheckBox checkBox = (CheckBox) annotationGroup.getChildren().get(i);
            Annotation annotation = Annotation.stringToAnnotation(checkBox.getText());
            Label label = null;
            allSelected = allSelected && checkBox.isSelected();
            noneSelected = noneSelected || checkBox.isSelected();
            assert annotation != null;
            switch (annotation) {
                case TEAM_NAME:
                    label = this.nameAnnotations.get(sourceID);
                    break;
                case BOAT_SPEED:
                    label = this.speedAnnotations.get(sourceID);
                    label.setText(String.valueOf(boat.getVelocity()) + "m/s");
                    break;
                case EST_TIME_TO_NEXT_MARK:
                    label = this.timeToMarkAnnotations.get(sourceID);
                    if (boat.getTimeToNextMark() > 0) {
                        label.setText(String.valueOf(boat.getTimeToNextMark()) + "s to Next Mark");
                    } else {
                        label.setText("--");
                    }
                    break;
                case TIME_FROM_LAST_MARK:
                    label = this.timeFromMarkAnnotations.get(sourceID);
                    if (timeFromLastMark > 0) {
                        label.setText(String.valueOf(timeFromLastMark) + "s from Last Mark");
                    } else {
                        label.setText("--");
                    }
                    break;

            }

            label.setVisible(checkBox.isSelected());
            label.setLayoutX(xValue + 5);
            label.setLayoutY(yValue + offset);
            if (checkBox.isSelected()) {
                offset += 12;
            }

            startLabel = this.timeToStartlineAnnotations.get(sourceID);
            startLabel.setText(startAnnotation);
            startLabel.setLayoutX(xValue + 5);
            startLabel.setLayoutY(yValue + offset);

        }

        allAnnotationsRadio.setSelected(allSelected);
        noAnnotationsRadio.setSelected(!noneSelected);
        someAnnotationsRadio.setSelected(!allSelected && noneSelected);

        // draws FPS counter
        fpsCounter.setVisible(fpsToggle.isSelected());

    }


    /**
     * Draw or move a boat model for a competitor
     *
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
     * Draw boat competitor
     *
     * @param boat Competitor a competing boat
     */
    private void drawWake(Competitor boat) {
        double boatLength = 20;
        double startWakeOffset = 3;
        double wakeWidthFactor = 0.2;
        if (wakeModels.get(boat.getSourceID()) == null) {
            double wakeLength = boat.getVelocity();
            Polygon wake = new Polygon();
            wake.getPoints().addAll(-startWakeOffset, boatLength, startWakeOffset, boatLength, startWakeOffset + wakeLength * wakeWidthFactor, wakeLength + boatLength, -startWakeOffset - wakeLength * wakeWidthFactor, wakeLength + boatLength);
            LinearGradient linearGradient = new LinearGradient(0.0, 0, 0.0, 1, true, CycleMethod.NO_CYCLE, new Stop(0.0f, Color.rgb(0, 0, 255, 0.7)), new Stop(1.0f, TRANSPARENT));
            wake.setFill(linearGradient);
            raceViewPane.getChildren().add(wake);
            this.wakeModels.put(boat.getSourceID(), wake);
        }
        double newLength = boat.getVelocity() * 2;
        Polygon wakeModel = wakeModels.get(boat.getSourceID());
        wakeModel.getTransforms().clear();
        wakeModel.getPoints().clear();
        wakeModel.getPoints().addAll(-startWakeOffset, boatLength, startWakeOffset, boatLength, startWakeOffset + newLength * wakeWidthFactor, newLength + boatLength, -startWakeOffset - newLength * wakeWidthFactor, newLength + boatLength);
        wakeModel.getTransforms().add(new Translate(boat.getPosition().getXValue(), boat.getPosition().getYValue()));
        wakeModel.getTransforms().add(new Rotate(boat.getCurrentHeading(), 0, 0));
        wakeModel.toFront();

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
        Dot dot = new Dot(boat.getPosition().getXValue(), boat.getPosition().getYValue());
        Circle circle = new Circle(dot.getX(), dot.getY(), 1.5, boat.getColor());
        //add fade transition
        FadeTransition ft = new FadeTransition(Duration.millis(20000), circle);
        ft.setFromValue(1);
        ft.setToValue(0.15);
//        ft.setOnFinished(event -> raceViewPane.getChildren().remove(circle));
        ft.play();
        this.raceViewPane.getChildren().add(circle);
        gc.restore();
    }


    /**
     * Calculate the time to the start line from the given boat
     *
     * @param boat Competitor
     * @return the time to the start line of the competitor
     */
    private String getStartSymbol(Competitor boat) {
        float boatX = (float) boat.getPosition().getXValue();
        float boatY = (float) boat.getPosition().getYValue();

        float startLineEndX = (float) startLine.getEndX();
        float startLineEndY = (float) startLine.getEndY();
        float startLineStartX = (float) startLine.getStartX();
        float startLineStartY = (float) startLine.getStartY();

        double distanceToStart = Point2D.distance(boatX, boatY, startLineStartX, startLineStartY);
        double distanceToEnd = Point2D.distance(boatX, boatY, startLineEndX, startLineEndY);
        long expectedStartTime = Converter.convertToRelativeTime(dataSource.getExpectedStartTime(), dataSource.getMessageTime());

        return raceCalculator.calculateStartSymbol(distanceToStart, distanceToEnd, boat, expectedStartTime);
    }




    /**
     * Updates the FPS counter
     */
    private void updateFPS() {
        counter++; // increment fps counter
        // calculate fps
        long currentTimeNano = System.nanoTime();
        if (currentTimeNano > startTimeNano + 1000000000) {
            startTimeNano = System.nanoTime();
            fpsCounter.setText(String.format("FPS: %d", counter));
            counter = 0;
        }
    }

    /**
     * Updates the race status
     */
    private void updateRaceStatus() {
        String statusString = "Race status: " + dataSource.getRaceStatus();
        if (!statusString.equals(status.getText())) {
            status.setText("Race status: " + dataSource.getRaceStatus());
        }
    }

    /**
     * Check if course need to be redrawn and draws the course features and the course boundary
     * @param gc GraphicsContext
     */
    private void updateCourse(GraphicsContext gc) {
        if (dataSource.getCourseFeatures() != courseFeatures) {
            courseFeatures = dataSource.getCourseFeatures();
            drawCourse();
            drawLine(startLine, dataSource.getStartMarks());
            drawLine(finishLine, dataSource.getFinishMarks());
            if (dataSource.getCourseBoundary() != courseBoundary) {
                courseBoundary = dataSource.getCourseBoundary();
                drawBoundary(gc);
            }
        }
    }

    /**
     * Draws the race. This includes the boat, wakes, track and annotations.
     */
    private void updateRace(GraphicsContext gc) {
        List<Competitor> competitors = dataSource.getCompetitorsPosition();
        for (Competitor boat : competitors) {
            timeFromLastMark = Converter.convertToRelativeTime(dataSource.getMessageTime(), boat.getTimeAtLastMark());
            if (dataSource.getRaceStatus().equals(PREPARATORY)) {
                startAnnotation = getStartSymbol(boat);
            }

            if (counter % 70 == 0) {
                drawTrack(boat, gc);
            }
            this.drawWake(boat);
            this.drawBoat(boat);
            this.moveAnnotations(boat);
        }
    }

    /**
     * Refreshes the contents of the display to match the datasource
     *
     * @param dataSource DataSource the data to display
     */
    void refresh(DataSource dataSource) {
        GraphicsContext gc = raceViewCanvas.getGraphicsContext2D();
        this.dataSource = dataSource;
        updateRaceStatus();
        updateFPS();
        updateCourse(gc);
        updateRace(gc);
    }

    boolean isLoaded() {
        return isLoaded;
    }
}