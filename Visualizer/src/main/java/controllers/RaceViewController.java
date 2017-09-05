package controllers;

import Animations.CollisionRipple;
import Animations.RandomShake;
import com.rits.cloning.Cloner;
import javafx.animation.FadeTransition;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.concurrent.Worker;
import javafx.event.ActionEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.RotateEvent;
import javafx.scene.input.TouchEvent;
import javafx.scene.input.ZoomEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.AnchorPane;
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
import mockDatafeed.Keys;
import models.Competitor;
import models.CourseFeature;
import models.MutablePoint;
import models.Vector2D;
import netscape.javascript.JSException;
import parsers.Converter;
import parsers.RaceStatusEnum;
import utilities.*;
import utilities.*;

import utilities.Annotation;

import utilities.DataSource;
import utilities.PolarTable;
import utility.BinaryPackager;

import java.awt.geom.Line2D;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;

import static java.lang.Math.*;
import static javafx.collections.FXCollections.observableArrayList;
import static javafx.collections.FXCollections.observableList;
import static javafx.scene.paint.Color.*;
import static parsers.BoatStatusEnum.DSQ;
import static parsers.RaceStatusEnum.PREPARATORY;
import static parsers.RaceStatusEnum.STARTED;
import static utilities.RaceCalculator.*;

/**
 * Controller for the race view.
 */
public class RaceViewController implements Initializable, TableObserver {

    @FXML private AnchorPane raceView;
    @FXML private Pane raceViewPane;
    @FXML private Canvas raceViewCanvas;
    @FXML private Label fpsCounter;
    @FXML private Slider sailSlider;
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
    @FXML private Pane raceParentPane;
    @FXML private ImageView controlsView;
    @FXML private HBox controlsBox;
    @FXML
    private GridPane finisherListPane;
    @FXML
    private ListView finisherListView;

    public GridPane getFinisherListPane() {
        return finisherListPane;
    }

    public ListView getFinisherListView() {
        return finisherListView;
    }

    private ObservableList<String> observableFinisherList = observableArrayList();
    private Boolean finisherListDisplayed = false;

    static final Color backgroundColor = Color.POWDERBLUE;

    private Map<Integer, Polygon> boatModels = new HashMap<>();
    private Shape playerMarker;
    private Map<Integer, Polygon> wakeModels = new HashMap<>();
    private Map<Double, Line> healthBars = new HashMap<>();
    private Map<Integer, ImageView> ripImages = new HashMap<>();
    private Map<Double, Line> healthBarBackgrounds = new HashMap<>();
    private Map<Integer, Label> nameAnnotations = new HashMap<>();
    private Map<Integer, Label> speedAnnotations = new HashMap<>();
    private Map<Integer, Label> timeToMarkAnnotations = new HashMap<>();
    private Map<Integer, Label> timeFromMarkAnnotations = new HashMap<>();
    private Map<Integer, Label> timeToStartlineAnnotations = new HashMap<>();
    private Map<String, Shape> markModels = new HashMap<>();
    private Group track=new Group();
    private List<Polyline> layLines = new ArrayList<>();
    private Label startLabel;
    private Line startLine;
    private Line finishLine;
    private Line virtualLine;
    private Polygon guideArrow;

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

    //if state of zooming
    private boolean zoom = false;

    //current boat position in screen coordinates
    private double boatPositionX;
    private double boatPositionY;

    //boat position in screen coordinates with zoom level 17
    private MutablePoint currentPosition17;

    //Deep cloner
//    private Cloner cloner=new Cloner();

    //graphics context
    private GraphicsContext gc;
    private double sailValue;
    @Override
    public void initialize(URL location, ResourceBundle resources) {

        sailValue = 5;
        startLine = new Line();
        finishLine = new Line();
        virtualLine = new Line();

        //draws the sail on the boat
        sailLine = new Line();
        sailLine.setStroke(Color.WHITE);

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

        finisherListPane.setVisible(false);

        initialiseGuideArrow();
        gc=raceViewCanvas.getGraphicsContext2D();

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
                drawBackgroundImage();
            }
        });

    }

    /**
     * Initialise arrow to guide the user where to go to round the next mark
     */
    private void initialiseGuideArrow() {
        guideArrow = new Polygon();
        double arrowLength = -60; // default arrow points vertically in the -y direction (upwards)
        double arrowHeadLength = -20;
        double offsetFromOrigin = -1 * (arrowLength + arrowHeadLength) + 30;

        controlsView = new ImageView();
        Image image = new Image("controls.png");
        controlsView.setImage(image);
        guideArrow.getPoints().addAll(
                -5., offsetFromOrigin, //tail left
                5., offsetFromOrigin, //tail right
                5., arrowLength + offsetFromOrigin,
                15., arrowLength + offsetFromOrigin,
                0., arrowLength + arrowHeadLength + offsetFromOrigin, // tip
                -15., arrowLength + offsetFromOrigin,
                -5.,arrowLength + offsetFromOrigin);
        guideArrow.setFill(backgroundColor.brighter());
        this.raceViewPane.getChildren().add(guideArrow);
        guideArrow.toBack();
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
        controlsBox.setPrefHeight(height);
        controlsBox.setPrefWidth(width);
        raceViewPane.getChildren().add(track);

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

    private void drawSail( double width, double length) {
        Competitor boat = dataSource.getStoredCompetitors().get(dataSource.getSourceID());
        double windAngle = dataSource.getWindDirection();

        sailLine.setStrokeWidth(width);
        sailLine.setStartX(boatPositionX);
        sailLine.setStartY(boatPositionY);
        sailLine.setEndX(boatPositionX);
        sailLine.setEndY(boatPositionY + length);
        sailLine.getTransforms().clear();


        if (boat.getSailValue() == 0) {
            sailLine.getTransforms().add(new Rotate(windAngle, boatPositionX, boatPositionY));

        } else {
            sailLine.getTransforms().add(new Rotate(boat.getCurrentHeading(), boatPositionX, boatPositionY));
        }

        sailLine.toFront();

    }

    /**
     * Draws the health bar representing the health of the boat
     * @param boat Competitor the boat in the race
     */
    private void drawHealthBar(Competitor boat) {

        double boatX = boat.getPosition().getXValue();
        double boatY = boat.getPosition().getYValue();

        double strokeWidth = 5;
        double offset = 20;
        double tombstoneSize = 30;
        double maxBarLength = 30;
        double sourceId = boat.getSourceID();
        int healthLevel = boat.getHealthLevel();
        double healthSize = ((healthLevel / (double) boat.getMaxHealth()) * maxBarLength);
        if(this.zoom) {
            offset = offset * 2;
            strokeWidth *= 2;
            healthLevel *= 2;
            maxBarLength *= 2;
            tombstoneSize *= 2;
            boatX = getBoatLocation(boat).getXValue();
            boatY = getBoatLocation(boat).getYValue();
            healthSize*=2;
        }
        if(healthLevel > 0) {
            Color healthColour = calculateHealthColour(boat);
            if (healthBars.get(sourceId) == null) {

                Line healthBarBackground = new Line();
                raceViewPane.getChildren().add(healthBarBackground);
                this.healthBarBackgrounds.put(sourceId, healthBarBackground);

                Line healthBar = new Line();
                raceViewPane.getChildren().add(healthBar);
                this.healthBars.put(sourceId, healthBar);
            }

            Line healthBarBackground = healthBarBackgrounds.get(sourceId);
            healthBarBackground.setStrokeWidth(strokeWidth);
            healthBarBackground.setStartX(boatX);
            healthBarBackground.setStartY(boatY - offset);
            healthBarBackground.setEndX(boatX + maxBarLength);
            healthBarBackground.setEndY(boatY - offset);
            healthBarBackground.setStroke(Color.WHITE);

            Line healthBar = healthBars.get(sourceId);
            healthBar.setStrokeWidth(strokeWidth);
            healthBar.setStartX(boatX);
            healthBar.setStartY(boatY - offset);
            healthBar.setEndX(boatX + healthSize);
            healthBar.setEndY(boatY - offset);

            healthBar.setStroke(healthColour);
            healthBar.toFront();


        }
        else {
            ImageView ripImage = ripImages.get((int) sourceId);

            if(boat.getStatus() != DSQ) {
                boat.setStatus(DSQ);
                ripImage.setVisible(true);
                BinaryPackager binaryPackager = new BinaryPackager();
                this.dataSource.send(binaryPackager.packageBoatAction(Keys.RIP.getValue(), boat.getSourceID()));

                if(dataSource.getSourceID() == boat.getSourceID()){
                    sailLine.setVisible(false);
                    playerMarker.setVisible(false);
                    this.raceViewPane.getChildren().remove(guideArrow);
                }

                healthBars.get(sourceId).setVisible(false);
                healthBarBackgrounds.get(sourceId).setVisible(false);
                boatModels.get((int) sourceId).setVisible(false);
            }

            ripImage.setX(boatX);
            ripImage.setY(boatY);
            ripImage.setFitHeight(tombstoneSize);
            ripImage.setFitHeight(tombstoneSize);

        }
    }


    /**
     * Draws the virtual line of the selected boat with the same color
     * @param boatColor color of the boat
     * @param selectedBoat selected boat
     */
    private void drawVirtualLine(Color boatColor, Competitor selectedBoat) {

        //get things we need
        MutablePoint startMark1 = dataSource.getStoredFeatures().get(dataSource.getStartMarks().get(0)).getPixelLocations().get(0);
        MutablePoint startMark2 = dataSource.getStoredFeatures().get(dataSource.getStartMarks().get(1)).getPixelLocations().get(0);
        CourseFeature startLine1 = dataSource.getStoredFeatures().get(dataSource.getStartMarks().get(0));
        long expectedStartTime = dataSource.getExpectedStartTime();
        long messageTime = dataSource.getMessageTime();

        List<MutablePoint> virtualLinePoints = calcVirtualLinePoints(selectedBoat, boatModels.get(selectedBoat.getSourceID()),startMark1,startMark2,startLine1,expectedStartTime,messageTime);
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
    private void drawCourse(Map<Integer,CourseFeature> courseFeatures) {

        // loops through all course features
        for (CourseFeature courseFeature : courseFeatures.values()) {
            drawMark(courseFeature);
//            mapEngine.executeScript(String.format("drawMarker(%.9f,%.9f);",courseFeature.getGPSPoint().getXValue(),courseFeature.getGPSPoint().getYValue()));
        }

        MutablePoint startLine1=courseFeatures.get(dataSource.getStartMarks().get(0)).getPixelLocations().get(0);
        MutablePoint startLine2=courseFeatures.get(dataSource.getStartMarks().get(1)).getPixelLocations().get(0);
        MutablePoint finishLine1=courseFeatures.get(dataSource.getFinishMarks().get(0)).getPixelLocations().get(0);
        MutablePoint finishLine2=courseFeatures.get(dataSource.getFinishMarks().get(1)).getPixelLocations().get(0);
        drawLine(startLine, startLine1,startLine2);
        drawLine(finishLine,finishLine1,finishLine2);

    }

    /**
     * Draws the line for gates
     * @param line Line the line to be drawn
     *             @param p1 one of the point on the line
*                       @param p2 the other point
     */
    private void drawLine(Line line, MutablePoint p1, MutablePoint p2) {

        double x1 = p1.getXValue();
        double y1 = p1.getYValue();
        double x2 = p2.getXValue();
        double y2 = p2.getYValue();
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

        if(!markModels.containsKey(courseFeature.getName())){
            Shape mark=new Circle(x,y,4.5,ORANGERED);
            markModels.put(courseFeature.getName(),mark);
            raceViewPane.getChildren().add(mark);
        }
        else {
            Circle mark = (Circle) markModels.get(courseFeature.getName());
            mark.setCenterX(x);
            mark.setCenterY(y);
        }
    }

    /**
     * Draw the background as the map and relocate it to the screen bounds
     *
     */
    private void drawBackgroundImage() {
        try {
            mapEngine.executeScript(String.format("setZoom(%d);",dataSource.getMapZoomLevel()));
            List<Double> bounds=dataSource.getGPSbounds();
            mapEngine.executeScript(String.format("relocate(%.9f,%.9f,%.9f,%.9f);", bounds.get(0), bounds.get(1), bounds.get(2), bounds.get(3)));
            mapEngine.executeScript(String.format("shift(%.2f);", dataSource.getShiftDistance()));

        } catch (JSException e) {
            e.printStackTrace();
        }
    }


    /**
     * adds scaling to all shapes in the scene
     */
    private void setScale(double scale){
        for(Polygon model : boatModels.values()) {
            model.setScaleX(scale);
            model.setScaleY(scale);
        }
        for(Shape model: markModels.values()){
            model.setScaleX(scale);
            model.setScaleY(scale);
        }
        playerMarker.setScaleX(scale);
        playerMarker.setScaleY(scale);

    }


    /**
     * Check to see if the race is finished and display finisher list if it is
     */
    public void checkRaceFinished(){
        if (dataSource.getRaceStatus().equals(RaceStatusEnum.FINISHED) && !finisherListDisplayed) {
            for (Competitor aCompetitor : dataSource.getCompetitorsPosition()){
                if (aCompetitor.getStatus() == DSQ) {
                    observableFinisherList.add("RIP " + aCompetitor.getTeamName());
                } else {
                    observableFinisherList.add((dataSource.getCompetitorsPosition().indexOf(aCompetitor) + 1 ) + ". " + aCompetitor.getTeamName());
                }
            }
            finisherListView.setItems(observableFinisherList);
            finisherListView.refresh();
            finisherListPane.setVisible(true);
            finisherListDisplayed = true;

            double width = raceViewPane.getWidth();
            double height = raceViewPane.getHeight();
            finisherListPane.setLayoutX(width/2 - finisherListPane.getWidth()/2);
            finisherListPane.setLayoutY(height/2 - finisherListPane.getHeight()/2);
        }
    }


    /**
     * Zooms in on your boat
     */
    public void zoomIn(){
        zoom=true;
        mapEngine.executeScript("setZoom(17);");
        updateRace();
        setScale(2);
        track.setVisible(!isZoom());
    }



    /**
     * Zooms out from your boat
     */
    public void zoomOut(){
        zoom=false;

        drawBackgroundImage();
        updateRace();
        setScale(1);
        track.setVisible(!isZoom());
    }

    public boolean isZoom() {
        return zoom;
    }

    /**
     * Draw boundary
     */
    private void drawBoundary(List<MutablePoint> courseBoundary) {

        if (courseBoundary != null) {
            gc.save();

            double[] boundaryX=new double[courseBoundary.size()];
            double[] boundaryY=new double[courseBoundary.size()];
            for(int i=0;i<courseBoundary.size();i++){
                boundaryX[i]=courseBoundary.get(i).getXValue();
                boundaryY[i]=courseBoundary.get(i).getYValue();
            }

            gc.setLineDashes(5);
            gc.setLineWidth(0.8);
            gc.clearRect(0, 0, 4000, 4000);

            gc.strokePolygon(boundaryX, boundaryY, boundaryX.length);
            gc.setGlobalAlpha(0.4);
            gc.setFill(backgroundColor);
            //shade inside the boundary
            gc.fillPolygon(boundaryX,boundaryY, boundaryX.length);
            gc.setGlobalAlpha(1.0);
            gc.restore();
        }
    }


    /**
     * Draw annotations and move with boat positions
     */
    private void drawAnnotations() {

        //add labels for each competitor
        for (Competitor boat : dataSource.getStoredCompetitors().values()) {
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

        MutablePoint point = setRelativePosition(boat);

        int offset = 15;
        if(isZoom()){
            offset*=2;
        }

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

            if(boat.getStatus() == DSQ) {
                label.setText("");
            }

            label.setVisible(checkBox.isSelected());
            label.setLayoutX(point.getXValue() + 5);
            label.setLayoutY(point.getYValue()+ offset);
            if (checkBox.isSelected()) {
                offset += 12;
            }

            startLabel = this.timeToStartlineAnnotations.get(sourceID);
            startLabel.setText(startAnnotation);
            startLabel.setLayoutX(point.getXValue() + 5);
            startLabel.setLayoutY(point.getYValue() + offset);

        }

        someAnnotationsRadio.setSelected(((allSelected || !noneSelected) && someAnnotationsRadio.isSelected()) ||
                !allSelected && noneSelected);
        allAnnotationsRadio.setSelected(allSelected && !someAnnotationsRadio.isSelected());
        noAnnotationsRadio.setSelected(!noneSelected && !someAnnotationsRadio.isSelected());

        // draws FPS counter
        fpsCounter.setVisible(fpsToggle.isSelected());

    }

    /**
     * sets the current boat position of the current boat controlled by visualizer
     */
    private void setBoatLocation(){
        Competitor boat=dataSource.getCompetitor();
        currentPosition17 =boat.getPosition17();
        if(isZoom()){
            boatPositionX=raceViewCanvas.getWidth()/2;
            boatPositionY=raceViewCanvas.getHeight()/2;
        }
        else{
            boatPositionX=boat.getPosition().getXValue();
            boatPositionY=boat.getPosition().getYValue();
        }
    }

    /**
     * returns the position of boat relative to the current boat, assume zoomed in
     * @param boat location of the boat to be calculated
     * @return the relative position
     */
    private MutablePoint getBoatLocation(Competitor boat){
        return boat.getPosition17().shift(-currentPosition17.getXValue()+raceViewCanvas.getWidth()/2,-currentPosition17.getYValue()+raceViewCanvas.getHeight()/2);
    }

    /**
     * sets the relative position of other boats compared to the visualizers boat
     * @param boat the boat to be calculated
     * @return point of the boat compared to visualizers boat
     */
    private MutablePoint setRelativePosition(Competitor boat){
        Integer sourceId = boat.getSourceID();
        double pointX;
        double pointY;

        if(sourceId==dataSource.getSourceID()){
            pointX=boatPositionX;
            pointY=boatPositionY;
        }else{
            if(isZoom()){
                MutablePoint point=getBoatLocation(boat);
                pointX=point.getXValue();
                pointY=point.getYValue();

            }else{
                pointX=boat.getPosition().getXValue();
                pointY=boat.getPosition().getYValue();

            }
        }
        return new MutablePoint(pointX,pointY);
    }

    /**
     * Draw or move a boat model for a competitor
     * @param boat Competitor a competing boat
     */
    private void drawBoat(Competitor boat) {
        Integer sourceId = boat.getSourceID();

        MutablePoint point = setRelativePosition(boat);

        if (boatModels.get(sourceId) == null) {
            Polygon boatModel = new Polygon();
            boatModel.getPoints().addAll(
                    0.0, -10.0, //top
                    -5.0, 10.0, //left
                    5.0, 10.0); //right
            boatModel.setFill(boat.getColor());

            boatModel.setStroke(BLACK);

            if (sourceId== dataSource.getSourceID()) {
                playerMarker = new Circle(0, 0, 15);
                playerMarker.setStrokeWidth(2.5);
                playerMarker.setStroke(Color.rgb(255,255,255,0.5));
                playerMarker.setFill(Color.rgb(0,0,0,0.2));
                this.raceViewPane.getChildren().add(playerMarker);
            }

            //add to the pane and store a reference
            this.raceViewPane.getChildren().add(boatModel);

            ImageView ripImage = new ImageView();
            Image tombstone = new Image("/cross.png");
            ripImage.setImage(tombstone);
            ripImage.setPreserveRatio(true);
            ripImage.setVisible(false);
            this.ripImages.put(sourceId, ripImage);
            raceViewPane.getChildren().add(ripImage);

            this.boatModels.put(sourceId, boatModel);
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
        boatModels.get(sourceId).setLayoutX(point.getXValue());
        boatModels.get(sourceId).setLayoutY(point.getYValue());
        boatModels.get(sourceId).toFront();
        boatModels.get(sourceId).getTransforms().clear();
        boatModels.get(sourceId).getTransforms().add(new Rotate(boat.getCurrentHeading(), 0, 0));

        if (sourceId == dataSource.getSourceID()) {
            playerMarker.setLayoutX(point.getXValue());
            playerMarker.setLayoutY(point.getYValue());
        }
    }

    /**
     * Draw boat's wake
     *
     * @param boat Competitor a competing boat
     */
    private void drawWake(Competitor boat, double boatLength,double startWakeOffset, double wakeWidthFactor, double wakeLengthFactor) {
        MutablePoint point= setRelativePosition(boat);

        if (!wakeModels.containsKey(boat.getSourceID())) {
            double wakeLength = boat.getVelocity()*wakeLengthFactor;
            Polygon wake = new Polygon();
            wake.getPoints().addAll(-startWakeOffset, boatLength, startWakeOffset, boatLength, startWakeOffset + wakeLength * wakeWidthFactor, wakeLength + boatLength, -startWakeOffset - wakeLength * wakeWidthFactor, wakeLength + boatLength);
            LinearGradient linearGradient = new LinearGradient(0.0, 0, 0.0, 1, true, CycleMethod.NO_CYCLE, new Stop(0.0f, Color.rgb(0, 0, 255, 0.7)), new Stop(1.0f, TRANSPARENT));
            wake.setFill(linearGradient);
            raceViewPane.getChildren().add(wake);
            this.wakeModels.put(boat.getSourceID(), wake);
        }

        double newLength = boat.getVelocity() * 2 * wakeLengthFactor;
        Polygon wakeModel = wakeModels.get(boat.getSourceID());
        wakeModel.getTransforms().clear();
        wakeModel.getPoints().clear();
        wakeModel.getPoints().addAll(-startWakeOffset, boatLength, startWakeOffset, boatLength, startWakeOffset + newLength * wakeWidthFactor, newLength + boatLength, -startWakeOffset - newLength * wakeWidthFactor, newLength + boatLength);
        wakeModel.getTransforms().add(new Translate(point.getXValue(), point.getYValue()));
        wakeModel.getTransforms().add(new Rotate(boat.getCurrentHeading(), 0, 0));
        wakeModel.toFront();


    }

    /**
     * Draw the next dot of track for the boat on the canvas
     *
     * @param boat Competitor
     */
    private void drawTrack(Competitor boat) {

        MutablePoint point=boat.getPosition();
        Circle circle = new Circle(point.getXValue(), point.getYValue(), 1.5, boat.getColor());
        //add fade transition
        FadeTransition ft = new FadeTransition(Duration.millis(20000), circle);
        ft.setFromValue(1);
        ft.setToValue(0.15);
        ft.setOnFinished(event -> {
            track.getChildren().remove(circle);
        });
        ft.play();
        track.getChildren().add(circle);


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

        MutablePoint markCentre = this.getNextGateCentre(boat);
        if (markCentre == null) return;
        Double markX = markCentre.getXValue();
        Double markY = markCentre.getYValue();

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
        MutablePoint intersectionPoint = new MutablePoint(0.0, 0.0);
        MutablePoint intersectionOne = new MutablePoint(0.0, 0.0);
        MutablePoint intersectionTwo = new MutablePoint(0.0, 0.0);

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
            Double distance1 = Math.hypot(boatX-intersectionOne.getXValue(), boatY-intersectionOne.getYValue());
            Double distance2 = Math.hypot(boatX-intersectionTwo.getXValue(), boatY-intersectionTwo.getYValue());
            if (distance1 < distance2) {
                intersectionPoint = intersectionOne;
            }
        }
        //DRAW
        if (draw) {
            this.drawLayLine(boatX, boatY, intersectionPoint.getXValue(), intersectionPoint.getYValue(), boat.getColor());
            this.drawLayLine(markX, markY, intersectionPoint.getXValue(), intersectionPoint.getYValue(), boat.getColor());
        }
    }


    /**
     * Gets the centre coordinates for a mark or gate

     * @return MutablePoint (x,y) coordinates
     */
    private MutablePoint getNextGateCentre(Competitor boat) {

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
        return new MutablePoint(markX, markY);
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
     * @return MutablePoint the intersection point x, y
     */
    private MutablePoint calculateIntersection(Double x1, Double y1, Double x2, Double y2, Double x3, Double y3, Double x4, Double y4) {
        //first convert to slope intercept y = mx + b
        Double m1 = (y2 - y1) / (x2 - x1);
        Double m2 = (y4 - y3) / (x4 - x3);
        //b = y - mx
        Double b1 = y1 - m1 * x1;
        Double b2 = y3 - m2 * x3;
        //the intersection point of the lines
        Double x = (b2 - b1) / (m1 - m2);
        Double y = m1 * x + b1;
        return new MutablePoint(x, y);
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
     * Gets the centre coordinates for a mark or gate
     * @param markIndex index of the mark (based on the order they are rounded)
     * @return MutablePoint (x,y) coordinates
     */
    private MutablePoint getGateCentre(Integer markIndex) {

        Map<Integer, List<Integer>> features = this.dataSource.getIndexToSourceIdCourseFeatures();
        if (markIndex > features.size()) return null; //passed the finish line

        List<Integer> ids = features.get(markIndex);
        CourseFeature featureOne = this.dataSource.getCourseFeatureMap().get(ids.get(0));
        Double markX = featureOne.getPixelCentre().getXValue();
        Double markY = featureOne.getPixelCentre().getYValue();

        if (ids.size() > 1) { //Get the centre point of gates
            CourseFeature featureTwo = this.dataSource.getCourseFeatureMap().get(ids.get(1));
            markX = (featureOne.getPixelCentre().getXValue() + featureTwo.getPixelCentre().getXValue()) / 2;
            markY = (featureOne.getPixelCentre().getYValue() + featureTwo.getPixelCentre().getYValue()) / 2;
        }
        return new MutablePoint(markX, markY);
    }


    /**
     * Draw a directional arrow on the canvas to guide the boat in the right direction to the next mark
     *
     */
    private void updateGuidingArrow() {
        Competitor boat = dataSource.getStoredCompetitors().get(dataSource.getSourceID());
        int currentIndex = boat.getCurrentLegIndex();
        double xOffset = 0, yOffset = 0;
        double angle;
        MutablePoint nextMarkLocation = getGateCentre(currentIndex + 1);

        if (nextMarkLocation == null) {
            // end of race
            this.raceViewPane.getChildren().remove(guideArrow);
            return;
        }

        if (currentIndex == 0 && !isZoom()) {
            // boat has not yet rounded first mark
            angle = 90;
        } else {
            Double xDist;
            Double yDist;
            if (isZoom()) {
                // arrow points from boat to next mark
                xDist = boat.getPosition().getXValue() - nextMarkLocation.getXValue();
                yDist = boat.getPosition().getYValue() - nextMarkLocation.getYValue();
            } else {
                // arrow points from previous mark to next mark
                MutablePoint prevMarkLocation = getGateCentre(currentIndex);
                assert prevMarkLocation != null;
                xDist = prevMarkLocation.getXValue() - nextMarkLocation.getXValue();
                yDist = prevMarkLocation.getYValue() - nextMarkLocation.getYValue();
            }

            angle = calculateAngleBetweenMarks(xDist, yDist);
            xOffset = 150 * cos(toRadians(angle - 90));
            yOffset = 150 * sin(toRadians(angle - 90));
        }

        double x, y;
        if (isZoom()) {
            x = boatPositionX + xOffset;
            y = boatPositionY + yOffset;
        } else {
            x = nextMarkLocation.getXValue();
            y = nextMarkLocation.getYValue();
        }
        applyTransformsToArrow(angle, x, y);
    }

    /**
     * Apply translation and rotation transforms to the guiding arrow
     *
     * @param angle double the angle by which to rotate the arrow, from north
     * @param x double the x coordinate for the arrow's origin
     * @param y double the y coordinate for the arrow's origin
     */
    private void applyTransformsToArrow(double angle, double x, double y) {
        guideArrow.getTransforms().clear();
        guideArrow.setLayoutX(x);
        guideArrow.setLayoutY(y);
        guideArrow.getTransforms().add(new Rotate(angle, 0, 0));
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

        return calculateStartSymbol(distanceToStart, distanceToEnd, boat.getVelocity(), timeUntilStart);
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
        checkRaceFinished();
    }

    /**
     * Check if course need to be redrawn and draws the course features and the course boundary
     */
    private void updateCourse() {
        Map<Integer,CourseFeature> courseFeatures;
        List<MutablePoint> courseBoundary;
        if(isZoom()){
            mapEngine.executeScript(String.format("setCenter(%.9f,%.9f);",dataSource.getCompetitor().getLatitude(),dataSource.getCompetitor().getLongitude()));
            courseFeatures=new HashMap<>();
            for(Integer id: dataSource.getStoredFeatures17().keySet()){
                courseFeatures.put(id, dataSource.getStoredFeatures17().get(id).shift(-currentPosition17.getXValue()+raceViewCanvas.getWidth()/2,-currentPosition17.getYValue()+raceViewCanvas.getHeight()/2));
            }
            courseBoundary=new ArrayList<>();
            for(MutablePoint p: dataSource.getCourseBoundary17()){
                courseBoundary.add(p.shift(-currentPosition17.getXValue()+raceViewCanvas.getWidth()/2,-currentPosition17.getYValue()+raceViewCanvas.getHeight()/2));
            }

        }else{
            courseFeatures=dataSource.getStoredFeatures();
            courseBoundary = dataSource.getCourseBoundary();
        }
        drawCourse(courseFeatures);
        drawBoundary(courseBoundary);
    }

    /**
     * Draws the race. This includes the boat, wakes, track and annotations.
     */
    private void updateRace() {
        //needs to redraw if zoomed in
        double width=2;
        double length=15;
        //not really the boat length but the offset of the wake from the y axis
        double boatLength = 10;
        double startWakeOffset = 3;
        double wakeWidthFactor = 0.2;
        double wakeLengthFactor=1;

        if(isZoom()){
            width*=2;
            length*=2;
            boatLength *= 2;
            startWakeOffset*= 2;
//            wakeWidthFactor*= 1;
            wakeLengthFactor*=2;
        }


        updateCourse();

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

                drawTrack(boat);
                if (selectedBoatSourceId != 0
                        && selectedBoatSourceId == boat.getSourceID()
                        && dataSource.getRaceStatus() == PREPARATORY) {
                    Color boatColor = boat.getColor();
                    drawVirtualLine(boatColor, boat);
                } else if (dataSource.getRaceStatus() == STARTED) {
                    raceViewPane.getChildren().remove(virtualLine);
                    virtualLine=null;
                }
            }

            this.drawWake(boat, boatLength, startWakeOffset, wakeWidthFactor, wakeLengthFactor);
            this.drawBoat(boat);
            this.drawHealthBar(boat);
            this.moveAnnotations(boat);

//            if (boat.getSourceID() == this.selectedBoatSourceId) this.drawLaylines(boat);
//            if (this.selectedBoatSourceId == 0) raceViewPane.getChildren().removeAll(layLines);


        }
        this.drawSail(width, length);


    }


    /**
     * draws a collision border
     * @param width the width of the screen
     * @param height the height of the screen
     * @param thickness the thickness of the border
     */
    public void drawBorder(double width, double height, double thickness){
        List<Shape> border=new ArrayList<>();
        //offset which the screen shakes by
        double shakeOffset=10;
        LinearGradient linearGradient=new LinearGradient(0,0,0,1,true,CycleMethod.NO_CYCLE,new Stop(0.0f, Color.RED), new Stop(1.0f, TRANSPARENT));
//        LinearGradient linearGradient2=new LinearGradient(0,1,0,0,true,CycleMethod.NO_CYCLE,new Stop(0.0f, Color.RED), new Stop(1.0f, TRANSPARENT));
        LinearGradient linearGradient3=new LinearGradient(0,0,1,0,true,CycleMethod.NO_CYCLE,new Stop(0.0f, Color.RED), new Stop(1.0f, TRANSPARENT));
        LinearGradient linearGradient4=new LinearGradient(1,0,0,0,true,CycleMethod.NO_CYCLE,new Stop(0.0f, Color.RED), new Stop(1.0f, TRANSPARENT));

        Rectangle rectTop =new Rectangle(-shakeOffset,-shakeOffset,width+shakeOffset*2,thickness+shakeOffset);
        rectTop.setFill(linearGradient);

        Rectangle rectLeft =new Rectangle(-shakeOffset,-shakeOffset,thickness+shakeOffset,height+shakeOffset*2);
        rectLeft.setFill(linearGradient3);
        Rectangle rectRight =new Rectangle(width-thickness,-shakeOffset,thickness+shakeOffset,height+shakeOffset*2);
        rectRight.setFill(linearGradient4);

        border.add(rectTop);
//        border.add(rectBot);
        border.add(rectLeft);
        border.add(rectRight);

        for(Shape rect: border ){
            FadeTransition fadeTransition=new FadeTransition(Duration.millis(500),rect);
            fadeTransition.setOnFinished(event -> raceParentPane.getChildren().remove(rect));
            fadeTransition.setFromValue(1.0);
            fadeTransition.setToValue(0.0);
            raceParentPane.getChildren().add(rect);
            fadeTransition.play();
            rect.toFront();

        }
    }

    /**
     * checks collisions and draws them
     */
    public void checkCollision(){
        for(int sourceID:new HashSet<>(dataSource.getCollisions())){
            MutablePoint point=setRelativePosition(dataSource.getStoredCompetitors().get(sourceID));
            if (sourceID == dataSource.getSourceID()) {
                drawBorder(raceViewPane.getWidth(),raceViewPane.getHeight(),25);


                new RandomShake(raceParentPane).animate();

            }
            drawCollision(point.getXValue(), point.getYValue());
            dataSource.removeCollsions(sourceID);
//            Competitor boat=dataSource.getStoredCompetitors().get(sourceID);
//            mapEngine.executeScript(String.format("create_collision(%.9f,%.9f)",boat.getLatitude(),boat.getLongitude()));

        }

    }

    /**
     * draws collisions at the location passed in
     * @param centerX the x coordinate of the collision
     * @param centerY the y coordinate of the collision
     */
    public void drawCollision(double centerX,double centerY){
        int radius=20;
        if(isZoom()){
            radius*=2;
        }
        CollisionRipple ripple = new CollisionRipple(centerX, centerY,radius );
        raceViewPane.getChildren().add(ripple);

        ripple.animate().setOnFinished(event -> raceViewPane.getChildren().remove(ripple));

    }

    /**
     * Toggles a control layout of the game
     * @param actionEvent
     */
    public void toggleControls(ActionEvent actionEvent) {
        if (!raceViewPane.getChildren().contains(controlsBox)){
            controlsBox.getChildren().add(controlsView);
            raceViewPane.getChildren().add(controlsBox);
        }
        else {
            controlsBox.getChildren().remove(controlsView);
            raceViewPane.getChildren().remove(controlsBox);
        }
    }

    /**
     * Checks the slider position and updates the sail of the boat
     */
    private void updateSails(){
        BinaryPackager binaryPackager = new BinaryPackager();
        Competitor boat = dataSource.getStoredCompetitors().get(dataSource.getSourceID());
        double blocksMoved;

        if(sailValue > sailSlider.getValue() && sailSlider.getValue()%1 == 0){
            blocksMoved = sailValue - sailSlider.getValue();
            sailValue = sailSlider.getValue();
            for(int i = 0; i < blocksMoved; i++){
                this.dataSource.send(binaryPackager.packageBoatAction(Keys.SAILSIN.getValue(), boat.getSourceID()));
            }
        }else if(sailValue < sailSlider.getValue() && sailSlider.getValue()%1 == 0){
            blocksMoved = sailSlider.getValue() - sailValue;
            sailValue = sailSlider.getValue();
            for(int i = 0; i < blocksMoved; i++){
                this.dataSource.send(binaryPackager.packageBoatAction(Keys.SAILSOUT.getValue(), boat.getSourceID()));
            }

        }

    }




    /**
     * Refreshes the contents of the display to match the datasource
     *
     */
    void refresh() {

        updateRaceStatus();
        updateFPS();
        setBoatLocation();
        updateRace();
        updateSails();
        checkCollision();
        updateGuidingArrow();
    }

    boolean isLoaded() {
        return isLoaded;
    }

    /**
     * Turn the boat when a touch pressed stationary event is sent
     * @param touchEvent pressed touch event
     */
    public void turnBoat(TouchEvent touchEvent) {

        Competitor boat = dataSource.getStoredCompetitors().get(dataSource.getSourceID());
        BinaryPackager binaryPackager = new BinaryPackager();
        double heading = boat.getCurrentHeading();
        double windAngle = (dataSource.getWindDirection())%360;
        double downWind = (boat.getDownWind(windAngle))%360;
        double touchX = touchEvent.getTouchPoint().getX();
        double touchY = touchEvent.getTouchPoint().getY();

        Vector2D boatDirection = new Vector2D(boatPositionX, boatPositionY, touchX, touchY);
        double theta = atan2(boatDirection.getY(), boatDirection.getX());
        theta = (theta * 180 / PI) + 90;
        if (theta < 0) { theta = 360 + theta;}
        double difference = theta - heading;

        int UP = 5;
        int DOWN = 6;
        boolean westOfWind = (heading > downWind) || (heading < windAngle);
        System.out.println(westOfWind);

        if (heading < 180) {
            if (difference > 0 && difference < 180) {
                if (heading - windAngle < -3) {
                    this.dataSource.send(binaryPackager.packageBoatAction(UP, boat.getSourceID()));
                }
                else if (westOfWind) {
                    this.dataSource.send(binaryPackager.packageBoatAction(UP, boat.getSourceID()));
                }
                else {
                    this.dataSource.send(binaryPackager.packageBoatAction(DOWN, boat.getSourceID()));
                }
            } else if (difference > 0 && difference > 180) {
                if (heading - windAngle < 3) {
                    this.dataSource.send(binaryPackager.packageBoatAction(DOWN, boat.getSourceID()));
                }
                else {
                    this.dataSource.send(binaryPackager.packageBoatAction(Keys.UP.getValue(), boat.getSourceID()));
                }
            } else if (difference < 0) {
                if (westOfWind) {
                    this.dataSource.send(binaryPackager.packageBoatAction(Keys.DOWN.getValue(), boat.getSourceID()));
                }
                else {
                    this.dataSource.send(binaryPackager.packageBoatAction(Keys.UP.getValue(), boat.getSourceID()));
                }
            }
        } else {
            if (difference > 0 && difference < 180) {
                if (westOfWind) {
                    this.dataSource.send(binaryPackager.packageBoatAction(Keys.UP.getValue(), boat.getSourceID()));
                }
                else {
                    this.dataSource.send(binaryPackager.packageBoatAction(Keys.DOWN.getValue(), boat.getSourceID()));
                }
            }
            else if(difference > 0 && difference > 180) {
                if (westOfWind) {
                    this.dataSource.send(binaryPackager.packageBoatAction(Keys.DOWN.getValue(), boat.getSourceID()));
                }
                else {
                    this.dataSource.send(binaryPackager.packageBoatAction(Keys.UP.getValue(), boat.getSourceID()));

                }
            }
            else if (difference < 0 && difference > -180) {
                if (westOfWind) {
                    this.dataSource.send(binaryPackager.packageBoatAction(Keys.DOWN.getValue(), boat.getSourceID()));
                }
                else {
                    this.dataSource.send(binaryPackager.packageBoatAction(Keys.UP.getValue(), boat.getSourceID()));

                }
            } else if (difference < 0 && difference < -180) {
                if (westOfWind) {
                    this.dataSource.send(binaryPackager.packageBoatAction(Keys.UP.getValue(), boat.getSourceID()));
                }
                else {
                    this.dataSource.send(binaryPackager.packageBoatAction(Keys.DOWN.getValue(), boat.getSourceID()));
                }
            }
        }
//
//        if (downWind < 180) {
//            if (heading < 180) {
//                if (difference > 0 && difference < 180) {
//                    this.dataSource.send(binaryPackager.packageBoatAction(Keys.UP.getValue(), boat.getSourceID()));
//                } else if (difference > 0 && difference > 180) {
//                    this.dataSource.send(binaryPackager.packageBoatAction(Keys.DOWN.getValue(), boat.getSourceID()));
//                } else if (difference < 0) {
//                    this.dataSource.send(binaryPackager.packageBoatAction(Keys.DOWN.getValue(), boat.getSourceID()));
//                }
//            } else {
//                if (difference > 0) {
//                    this.dataSource.send(binaryPackager.packageBoatAction(Keys.UP.getValue(), boat.getSourceID()));
//                } else if (difference < 0 && difference > -180) {
//                    this.dataSource.send(binaryPackager.packageBoatAction(Keys.DOWN.getValue(), boat.getSourceID()));
//                } else if (difference < 0 && difference < -180) {
//                    this.dataSource.send(binaryPackager.packageBoatAction(Keys.UP.getValue(), boat.getSourceID()));
//                }
//            }
//        }
//        else {
//            if (heading < 180) {
//                if (difference > 0 && difference < 180) {
//                    this.dataSource.send(binaryPackager.packageBoatAction(Keys.DOWN.getValue(), boat.getSourceID()));
//                } else if (difference > 0 && difference > 180) {
//                    this.dataSource.send(binaryPackager.packageBoatAction(Keys.UP.getValue(), boat.getSourceID()));
//                } else if (difference < 0) {
//                    this.dataSource.send(binaryPackager.packageBoatAction(Keys.UP.getValue(), boat.getSourceID()));
//                }
//            } else {
//                if (difference > 0) {
//                    this.dataSource.send(binaryPackager.packageBoatAction(Keys.DOWN.getValue(), boat.getSourceID()));
//                } else if (difference < 0 && difference > -180) {
//                    this.dataSource.send(binaryPackager.packageBoatAction(Keys.UP.getValue(), boat.getSourceID()));
//                } else if (difference < 0 && difference < -180) {
//                    this.dataSource.send(binaryPackager.packageBoatAction(Keys.DOWN.getValue(), boat.getSourceID()));
//                }
//            }
//        }
//



//        if(theta < 0){ theta = 360 + theta; }
//        boolean westOfWind;
//

////        System.out.println("\n\nHeading : " + heading + "\nTheta : " + theta + "\nWindAngle : " + windAngle + "\nDownwind : "+ downWind);
//        if (!(heading <= theta + 10 && heading >= theta -10)) {
//
//            westOfWind = (heading > downWind && heading < downWind + 180) || (heading < windAngle && heading - windAngle < 0) || (heading < windAngle && heading > downWind);
//            if(westOfWind) {
//                if ((heading - theta) % 360 > (theta - heading) % 360 && theta > heading) {
////                    System.out.println("downwind LEFT");
//                    this.dataSource.send(binaryPackager.packageBoatAction(Keys.DOWN.getValue(), boat.getSourceID()));
//                } else {
////                    System.out.println("upwind LEFT");
//                    this.dataSource.send(binaryPackager.packageBoatAction(Keys.UP.getValue(), boat.getSourceID()));
//                }
//            } else{
//                if((heading - theta)%360 > (theta - heading)%360){
////                    System.out.println("upwind RIGHT");
//                    this.dataSource.send(binaryPackager.packageBoatAction(Keys.UP.getValue(), boat.getSourceID()));
//                }
//                else {
////                    System.out.println("downwind RIGHT");
//                    this.dataSource.send(binaryPackager.packageBoatAction(Keys.DOWN.getValue(), boat.getSourceID()));
//                }
//            }
//        }

    }


    /**
     * Zoom the screen in and out upon touch zoom event
     * @param zoomEvent zoom event
     */
    public void zoom(ZoomEvent zoomEvent){
        if(isZoom()){
            zoomOut();
        } else{
            zoomIn();
        }
    }





}
