package controllers;

import com.google.common.primitives.Doubles;
import javafx.animation.FadeTransition;
import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
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
import javafx.scene.shape.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.util.Duration;
import javafx.util.Pair;
import models.Competitor;
import models.CourseFeature;
import models.Dot;
import models.MutablePoint;
import netscape.javascript.JSException;
import parsers.Converter;
import utilities.*;


import java.awt.geom.Line2D;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;

import static java.lang.Math.abs;
import static javafx.scene.paint.Color.*;
import static parsers.RaceStatusEnum.PREPARATORY;
import static parsers.RaceStatusEnum.STARTED;

/**
 * Controller for the race view.
 */
public class RaceViewController implements Initializable, TableObserver {

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

    private Map<Integer, Polygon> boatModels = new HashMap<>();
    private Shape playerMarker;
    private Map<Integer, Polygon> wakeModels = new HashMap<>();
    private Map<Double, Line> healthBars = new HashMap<>();
    private Map<Double, Line> healthBarBackgrounds = new HashMap<>();
    private Map<Integer, Label> nameAnnotations = new HashMap<>();
    private Map<Integer, Label> speedAnnotations = new HashMap<>();
    private Map<Integer, Label> timeToMarkAnnotations = new HashMap<>();
    private Map<Integer, Label> timeFromMarkAnnotations = new HashMap<>();
    private Map<Integer, Label> timeToStartlineAnnotations = new HashMap<>();
    private List<MutablePoint> courseBoundary = null;
    private List<CourseFeature> courseFeatures = null;
    private Map<String, Shape> markModels = new HashMap<>();
    private List<Polyline> layLines = new ArrayList<>();
    private Label startLabel;
    private Line startLine;
    private Line finishLine;
    private Line virtualLine;

    private RaceCalculator raceCalculator;
    private WebEngine mapEngine;
    private DataSource dataSource;
    private PolarTable polarTable;
    private int counter;
    private long startTimeNano = System.nanoTime();
    private long timeFromLastMark;
    private String startAnnotation;
    private Line sailLine;
    private Integer selectedBoatSourceId = 0;
    private boolean isLoaded = false;
    private boolean isCenterSet=false;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        startLine = new Line();
        finishLine = new Line();
        virtualLine = new Line();
        sailLine = new Line();
        raceCalculator = new RaceCalculator();
        raceViewPane.getChildren().add(startLine);
        raceViewPane.getChildren().add(finishLine);
        raceViewPane.getChildren().add(virtualLine);
        raceViewPane.getChildren().add(sailLine);
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
        raceCalculator.setDataSource(dataSource);
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
     * Observer method for table observer
     * Updates the selected boat property
     * @param sourceId Integer the sourceId of the selected boat
     */
    public void boatSelected(Integer sourceId) {
        this.selectedBoatSourceId = sourceId;
    }

    /**
     * Draws the line representing the sail of the boat
     */

    private void drawSail() {
        Competitor boat = dataSource.getStoredCompetitors().get(dataSource.getSourceID());
        double windAngle = dataSource.getWindDirection();
        double boatXval = boat.getPosition().getXValue();
        double boatYval = boat.getPosition().getYValue();
        sailLine.setStroke(Color.WHITE);
        sailLine.setStrokeWidth(2.5);
        sailLine.setStartX(boatXval);
        sailLine.setStartY(boatYval);
        sailLine.setEndX(boatXval);
        sailLine.setEndY(boatYval + 15);
        sailLine.getTransforms().clear();
        if (boat.hasSailsOut()) {
            sailLine.getTransforms().add(new Rotate(boat.getCurrentHeading(), boatXval, boatYval));
        } else {
            sailLine.getTransforms().add(new Rotate(windAngle, boatXval, boatYval));
        }
        sailLine.toFront();
    }

    /**
     * Draws the health bar representing the health of the boat
     * @param boat Competitor the boat in the race
     */
    private void drawHealthBar(Competitor boat) {
        if(boat.getHealthLevel() > 0) {
            double boatXval = boat.getPosition().getXValue();
            double boatYval = boat.getPosition().getYValue();
            double strokeWidth = 5;
            double offset = 20;
            double maxBarLength = 30;
            double sourceId = boat.getSourceID();

            if (healthBars.get(sourceId) == null) {

                Line healthBarBackground = new Line();

                healthBarBackground.setStrokeWidth(strokeWidth);
                healthBarBackground.setStartX(boatXval);
                healthBarBackground.setStartY(boatYval - offset);
                healthBarBackground.setEndX(boatXval + maxBarLength);
                healthBarBackground.setEndY(boatYval - offset);
                healthBarBackground.setStroke(Color.WHITE);

                raceViewPane.getChildren().add(healthBarBackground);
                this.healthBarBackgrounds.put(sourceId, healthBarBackground);

                Line healthBar = new Line();

                healthBar.setStrokeWidth(strokeWidth);
                healthBar.setStartX(boatXval);
                healthBar.setStartY(boatYval - offset);
                healthBar.setEndX(boatXval + boat.getHealthLevel());
                healthBar.setEndY(boatYval - offset); // boat.getHealthLength
                //        LinearGradient colourGradient = new LinearGradient(healthBar.getStartX(), healthBar.getStartY(),
//                healthBar.getEndX(), healthBar.getEndY(), false, CycleMethod.NO_CYCLE, new Stop(0, Color.RED), new Stop(1, Color.GREEN));

                healthBar.setStroke(Color.GREEN);
                healthBar.toFront();
                raceViewPane.getChildren().add(healthBar);
                this.healthBars.put(sourceId, healthBar);
            }

            Line healthBarBackground = healthBarBackgrounds.get(sourceId);
            healthBarBackground.setStrokeWidth(strokeWidth);
            healthBarBackground.setStartX(boatXval);
            healthBarBackground.setStartY(boatYval - offset);
            healthBarBackground.setEndX(boatXval + maxBarLength);
            healthBarBackground.setEndY(boatYval - offset);
            healthBarBackground.setStroke(Color.WHITE);


//            double newLength = boat.getVelocity() * 2;
            Line healthBar = healthBars.get(sourceId);
            healthBar.setStrokeWidth(strokeWidth);
            healthBar.setStartX(boatXval);
            healthBar.setStartY(boatYval - offset);
            healthBar.setEndX(boatXval + boat.getHealthLevel());
            healthBar.setEndY(boatYval - offset); // boat.getHealthLength
            //        LinearGradient colourGradient = new LinearGradient(healthBar.getStartX(), healthBar.getStartY(),
//                healthBar.getEndX(), healthBar.getEndY(), false, CycleMethod.NO_CYCLE, new Stop(0, Color.RED), new Stop(1, Color.GREEN));

            healthBar.setStroke(Color.GREEN);
            healthBar.toFront();


        }
        else {
            // rip boat
//            boat.setLatitude(boat.getPosition().getXValue());
//        dataSource.getStoredCompetitors().remove(boat.getSourceID());
            System.out.println("Game over");
        }



    }

    /**
     * Draws the virtual line of the selected boat with the same color
     * @param boatColor color of the boat
     * @param selectedBoat selected boat
     */
    private void drawVirtualLine(Color boatColor, Competitor selectedBoat) {
        raceCalculator.setBoatModels(boatModels);
        List<MutablePoint> virtualLinePoints = raceCalculator.calcVirtualLinePoints(selectedBoat);
        if (!virtualLinePoints.isEmpty()) {
            MutablePoint virtualLine1 = virtualLinePoints.get(0);
            MutablePoint virtualLine2 = virtualLinePoints.get(1);
            virtualLine.setStroke(boatColor);
            virtualLine.setStartX(virtualLine1.getXValue());
            virtualLine.setStartY(virtualLine1.getYValue());
            virtualLine.setEndX(virtualLine2.getXValue());
            virtualLine.setEndY(virtualLine2.getYValue());
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
     * @param line Line the line to be drawn
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

           // mapEngine.executeScript(String.format("relocate(%.9f,%.9f,%.9f,%.9f);", bounds.get(0), bounds.get(1), bounds.get(2), bounds.get(3)));
            mapEngine.executeScript(String.format("relocate(%.9f,%.9f,%.9f,%.9f);",-48.305459,-137.7900947 ,-48.305459,-137.7900947 ));
            //mapEngine.executeScript(String.format("shift(%.2f);", dataSource.getShiftDistance()));
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
            gc.setLineWidth(1);
            gc.clearRect(0, 0, 4000, 4000);

            //draw center once only to keep trails drawn properly
            if(!isCenterSet) {
                drawBackgroundImage(dataSource.getGPSbounds());
                isCenterSet=true;
            }
            gc.setStroke(new Color(1,0,0,0.9));
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

        someAnnotationsRadio.setSelected(((allSelected || !noneSelected) && someAnnotationsRadio.isSelected()) ||
                !allSelected && noneSelected);
        allAnnotationsRadio.setSelected(allSelected && !someAnnotationsRadio.isSelected());
        noAnnotationsRadio.setSelected(!noneSelected && !someAnnotationsRadio.isSelected());

        // draws FPS counter
        fpsCounter.setVisible(fpsToggle.isSelected());

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
                    0.0, -10.0, //top
                    -5.0, 10.0, //left
                    5.0, 10.0); //right
            boatModel.setFill(boat.getColor());

            boatModel.setStroke(BLACK);

            if (boat.getSourceID() == dataSource.getSourceID()) {
                playerMarker = new Circle(0, 0, 15);
                playerMarker.setStrokeWidth(2.5);
                playerMarker.setStroke(Color.rgb(255,255,255,0.5));
                playerMarker.setFill(Color.rgb(0,0,0,0.2));
                this.raceViewPane.getChildren().add(playerMarker);
            }

            //add to the pane and store a reference
            this.raceViewPane.getChildren().add(boatModel);


            this.boatModels.put(boat.getSourceID(), boatModel);
            //Boats selected can be selected/unselected by clicking on them
            boatModel.setOnMouseClicked(event -> {
                if (!Objects.equals(selectedBoatSourceId, sourceId)) {
                    selectedBoatSourceId = sourceId;
                } else {
                    selectedBoatSourceId = 0;
                }
            });
        }
        //Translate and rotate the corresponding boat models
        boatModels.get(sourceId).setLayoutX(boat.getPosition().getXValue());
        boatModels.get(sourceId).setLayoutY(boat.getPosition().getYValue());
        boatModels.get(sourceId).toFront();
        boatModels.get(sourceId).getTransforms().clear();
        boatModels.get(sourceId).getTransforms().add(new Rotate(boat.getCurrentHeading(), 0, 0));

        if (boat.getSourceID() == dataSource.getSourceID()) {
            playerMarker.setLayoutX(boat.getPosition().getXValue());
            playerMarker.setLayoutY(boat.getPosition().getYValue());
            playerMarker.getTransforms().clear();
            playerMarker.getTransforms().add(new Rotate(boat.getCurrentHeading(), 0, 0));
        }
    }

    /**
     * Draw boat competitor
     *
     * @param boat Competitor a competing boat
     */
    private void drawWake(Competitor boat) {
        //not really the boat length but the offset of the wake from the y axis
        double boatLength = 10;
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
     * Calculates and draws laylines on the pane for the given boat
     * @param boat Competitor the boat to draw the laylines for
     */
    private void drawLaylines(Competitor boat) {

        if (this.polarTable == null) {
            try {
                polarTable = new PolarTable("/polars/VO70_polar.txt", 12);
            } catch (IOException e) {
                System.out.println("Could not find polar file");
                return;
            }
        }

        Integer upWindAngle = (int) polarTable.getMinimalTwa(this.dataSource.getWindSpeed(), true);
        Integer downWindAngle = (int) polarTable.getMinimalTwa(this.dataSource.getWindSpeed(), false);

        Pair<Double, Double> markCentre = this.getNextGateCentre(boat);
        if (markCentre == null) return;
        Double markX = markCentre.getKey();
        Double markY = markCentre.getValue();

        Double boatX = boat.getPosition().getXValue();
        Double boatY = boat.getPosition().getYValue();
        Double heading = boat.getCurrentHeading();
        Double windAngle = dataSource.getWindDirection();
        Double windSpeed = dataSource.getWindSpeed();

        //semi arbitrary point in front of the boat along the heading angle
        Double bx = boatX + (2000 * Math.sin(Math.toRadians(heading)));
        Double by = boatY - (2000 * Math.cos(Math.toRadians(heading)));

        //Difference between heading and wind angle- used to check if boat is going upwind or downwind
        Double anglediff = abs((heading - windAngle + 180 + 360) % 360 - 180);
        Double layLineStarboardAngle = 0.0;
        Double layLinePortAngle = 0.0;

        //CASE: upwind
        if (anglediff > 90) {
            layLineStarboardAngle = windAngle - upWindAngle;
            layLinePortAngle = windAngle + upWindAngle;
        }
        //CASE: downwind
        else if (anglediff < 90) {
            layLineStarboardAngle = windAngle + downWindAngle;
            layLinePortAngle = windAngle - downWindAngle;
        }

        //normalize angles
        if (layLineStarboardAngle > 360) layLineStarboardAngle = layLineStarboardAngle - 360;
        if (layLinePortAngle > 360) layLinePortAngle = layLinePortAngle - 360;

        //Starboard layline
        Double mx = markX + 2000 * Math.sin(Math.toRadians(layLineStarboardAngle));
        Double my = markY - 2000 * Math.cos(Math.toRadians(layLineStarboardAngle));
        Double mx2 = markX -2000 * Math.sin(Math.toRadians(layLineStarboardAngle));
        Double my2 = markY + 2000 * Math.cos(Math.toRadians(layLineStarboardAngle));
        //Port layline
        Double mx3 = markX + 2000 * Math.sin(Math.toRadians(layLinePortAngle));
        Double my3 = markY - 2000 * Math.cos(Math.toRadians(layLinePortAngle));
        Double mx4 = markX -2000 * Math.sin(Math.toRadians(layLinePortAngle));
        Double my4 = markY + 2000 * Math.cos(Math.toRadians(layLinePortAngle));

        //Intersections of lay lines and boat heading
        Pair<Double, Double> intersectionPoint = new Pair<>(0.0, 0.0);
        Pair<Double, Double> intersectionOne = new Pair<>(0.0, 0.0);
        Pair<Double, Double> intersectionTwo = new Pair<>(0.0, 0.0);

        Boolean intersectsStarboardLayline = Line2D.linesIntersect(boatX, boatY, bx, by, mx2, my2, mx, my);
        Boolean intersectsPortLayline = Line2D.linesIntersect(boatX, boatY, bx, by, mx3, my3, mx4, my4);
        Boolean draw = false;

        raceViewPane.getChildren().removeAll(layLines);
        layLines.clear();

        //FIND INTERSECTIONS
        if (intersectsStarboardLayline) {
            draw = true;
            //If the line segments intersect then find the intersection point to draw the lines with
            intersectionOne = this.calculateIntersection(boatX, boatY, bx, by, markX, markY, mx, my);
            intersectionPoint = intersectionOne;
//            yIntersection = y;
        }
        if (intersectsPortLayline) {
            draw = true;
            intersectionTwo = this.calculateIntersection(boatX, boatY, bx, by, markX, markY, mx3, my3);
            intersectionPoint = intersectionTwo;
        }
        if (intersectsStarboardLayline && intersectsPortLayline) {
            //FIND THE CLOSEST INTERSECTION
            Double distance1 = Math.hypot(boatX-intersectionOne.getKey(), boatY-intersectionOne.getValue());
            Double distance2 = Math.hypot(boatX-intersectionTwo.getKey(), boatY-intersectionTwo.getValue());
            if (distance1 < distance2) {
                intersectionPoint = intersectionOne;
            }
        }
        //DRAW
        if (draw) {
            this.drawLayLine(boatX, boatY, intersectionPoint.getKey(), intersectionPoint.getValue(), boat.getColor());
            this.drawLayLine(markX, markY, intersectionPoint.getKey(), intersectionPoint.getValue(), boat.getColor());
        }
    }


    /**
     * Gets the centre coords for the next mark a boat will round
     * @param boat Competitor
     * @return Pair the coords
     */
    private Pair<Double, Double> getNextGateCentre(Competitor boat) {

        Integer markId = boat.getCurrentLegIndex() + 1;
        // if (EnvironmentConfig.currentStream.equals(EnvironmentConfig.liveStream)) markId += 1; //HACKY :- The livestream seq numbers are 1 place off the csse numbers

        Map<Integer, List<Integer>> features = this.dataSource.getIndexToSourceIdCourseFeatures();
        if (markId > features.size()) return null; //passed the finish line

        List<Integer> ids = features.get(markId);
        CourseFeature featureOne = this.dataSource.getCourseFeatureMap().get(ids.get(0));
        Double markX = featureOne.getPixelCentre().getXValue();
        Double markY = featureOne.getPixelCentre().getYValue();

        if (ids.size() > 1) { //Get the centre point of gates
            CourseFeature featureTwo = this.dataSource.getCourseFeatureMap().get(ids.get(1));
            markX = (featureOne.getPixelCentre().getXValue() + featureTwo.getPixelCentre().getXValue()) / 2;
            markY = (featureOne.getPixelCentre().getYValue() + featureTwo.getPixelCentre().getYValue()) / 2;
        }
        return new Pair<>(markX, markY);
    }


    /**
     * Calculates the intersection point of two lines. Result is undefined for non intersecting lines
     * @param x1 Double line one
     * @param y1 Double line one
     * @param x2 Double line one
     * @param y2 Double line one
     * @param x3 Double line two
     * @param y3 Double line two
     * @param x4 Double line two
     * @param y4 Double line two
     * @return Pair the intersection point x, y
     */
    private Pair<Double, Double> calculateIntersection(Double x1, Double y1, Double x2, Double y2, Double x3, Double y3, Double x4, Double y4) {
        //first convert to slope intercept y = mx + b
        Double m1 = (y2 - y1) / (x2 - x1);
        Double m2 = (y4 - y3) / (x4 - x3);
        //b = y - mx
        Double b1 = y1 - m1 * x1;
        Double b2 = y3 - m2 * x3;
        //the intersection point of the lines
        Double x = (b2 - b1) / (m1 - m2);
        Double y = m1 * x + b1;
        return new Pair<>(x, y);
    }




    /**
     * Draws a layline on the pane
     * @param x Double, The first x value
     * @param y Double, The first y value
     * @param x2 Double, The second x value
     * @param y2 Double, The second y value
     * @param colour Color the colour of the line
     */
    private void drawLayLine(Double x, Double y, Double x2, Double y2, Color colour) {

        Polyline line = new Polyline();
        line.getPoints().addAll(
                x, y, //top
                x2, y2);
        line.setFill(colour);
        line.setStroke(colour);
        line.setStrokeWidth(1);
        raceViewPane.getChildren().add(line);
        layLines.add(line);
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

        Point2D boatPoint = new Point2D(boatX, boatY);
        double distanceToStart = boatPoint.distance(startLineStartX, startLineStartY);
        double distanceToEnd = boatPoint.distance(startLineEndX, startLineEndY);
        long timeUntilStart = Converter.convertToRelativeTime(dataSource.getExpectedStartTime(), dataSource.getMessageTime()) * -1;

        return raceCalculator.calculateStartSymbol(distanceToStart, distanceToEnd, boat.getVelocity(), timeUntilStart);
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
            timeFromLastMark = Converter.convertToRelativeTime(boat.getTimeAtLastMark(), dataSource.getMessageTime());
            if (dataSource.getRaceStatus().equals(PREPARATORY)) {
                startAnnotation = getStartSymbol(boat);
            }
            else{
                startAnnotation="";
            }
            if (counter % 70 == 0) {
                drawTrack(boat, gc);
                if (selectedBoatSourceId != 0
                        && selectedBoatSourceId == boat.getSourceID()
                        && dataSource.getRaceStatus() == PREPARATORY) {
                    Color boatColor = boat.getColor();
                    drawVirtualLine(boatColor, boat);
                } else if (dataSource.getRaceStatus() == STARTED) {
                    raceViewPane.getChildren().remove(virtualLine);
                }
            }
            this.drawWake(boat);
            this.drawBoat(boat);
            this.drawHealthBar(boat);
            this.moveAnnotations(boat);
            if (boat.getSourceID() == this.selectedBoatSourceId) this.drawLaylines(boat);
            if (this.selectedBoatSourceId == 0) raceViewPane.getChildren().removeAll(layLines);
        }
    }

    /**
     * checks collisions and draws them
     */
    public void checkCollision(){
        for(int sourceID:new HashSet<>(dataSource.getCollisions())){
            MutablePoint position=dataSource.getStoredCompetitors().get(sourceID).getPosition();
            drawCollision(position.getXValue(),position.getYValue());
            dataSource.removeCollsions(sourceID);

            Competitor boat = dataSource.getStoredCompetitors().get(sourceID);
            boat.decreaseHealth();
        }

    }

    /**
     * draws collisions at the location passed in
     * @param centerX the x coordinate of the collision
     * @param centerY the y coordinate of the collision
     */
    public void drawCollision(double centerX,double centerY){
        CollisionRipple ripple = new CollisionRipple(centerX, centerY, 20);
        raceViewPane.getChildren().add(ripple);
        ripple.animate().setOnFinished(event -> raceViewPane.getChildren().remove(ripple));
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
        drawSail();
        checkCollision();


    }

    boolean isLoaded() {
        return isLoaded;
    }
}