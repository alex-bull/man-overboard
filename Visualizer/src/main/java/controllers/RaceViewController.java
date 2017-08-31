package controllers;

import Animations.BorderAnimation;
import Animations.CollisionRipple;
import Animations.RandomShake;
import Elements.GuideArrow;
import Elements.HealthBar;
import Elements.Sail;
import javafx.animation.FadeTransition;
import javafx.collections.ObservableList;
import javafx.concurrent.Worker;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
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
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.util.Duration;
import mockDatafeed.Keys;
import models.Competitor;
import models.CourseFeature;
import models.MutablePoint;
import netscape.javascript.JSException;
import parsers.RaceStatusEnum;
import utilities.DataSource;
import utilities.RaceCalculator;
import utility.BinaryPackager;

import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;

import static javafx.collections.FXCollections.observableArrayList;
import static javafx.scene.paint.Color.*;
import static parsers.BoatStatusEnum.DSQ;
import static parsers.RaceStatusEnum.PREPARATORY;
import static parsers.RaceStatusEnum.STARTED;


/**
 * Controller for the race view.
 */
public class RaceViewController implements Initializable, TableObserver {

    @FXML private AnchorPane raceView;
    @FXML private Pane raceViewPane;
    @FXML private Canvas raceViewCanvas;
    @FXML private WebView mapView;
    @FXML private Pane raceParentPane;
    @FXML private ImageView controlsView;
    @FXML private HBox controlsBox;
    @FXML private GridPane finisherListPane;
    @FXML private ListView finisherListView;


    private ObservableList<String> observableFinisherList = observableArrayList();
    private Boolean finisherListDisplayed = false;

    private static final Color backgroundColor = Color.POWDERBLUE;

    private Map<Integer, Polygon> boatModels = new HashMap<>();
    private Shape playerMarker;
    private Map<Integer, Polygon> wakeModels = new HashMap<>();
    private Map<Double, HealthBar> healthBars = new HashMap<>();
    private Map<Integer, Label> nameAnnotations = new HashMap<>();
    private Map<Integer, Label> speedAnnotations = new HashMap<>();
    private Map<String, Shape> markModels = new HashMap<>();
    private Group track=new Group();
    private Line startLine;
    private Line finishLine;
    private Line virtualLine;
    private GuideArrow guideArrow;
    private WebEngine mapEngine;
    private DataSource dataSource;
    private Sail sailLine;
    private Integer selectedBoatSourceId = 0;
    private boolean isLoaded = false;
    private int counter = 0;

    //if state of zooming
    private boolean zoom = false;

    //current boat position in screen coordinates
    private double boatPositionX;
    private double boatPositionY;

    //boat position in screen coordinates with zoom level 17
    private MutablePoint currentPosition17;
    private GraphicsContext gc;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        startLine = new Line();
        finishLine = new Line();
        virtualLine = new Line();

        //draws the sail on the boat
        sailLine = new Sail(Color.WHITE);

        raceViewPane.getChildren().add(startLine);
        raceViewPane.getChildren().add(finishLine);
        raceViewPane.getChildren().add(virtualLine);
        raceViewPane.getChildren().add(sailLine);

        finisherListPane.setVisible(false);

//        initialiseGuideArrow();
        this.guideArrow = new GuideArrow(backgroundColor.brighter(), 90.0);
        raceViewPane.getChildren().add(guideArrow);
        controlsView = new ImageView(new Image("controls.png"));

        gc = raceViewCanvas.getGraphicsContext2D();

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
    private void drawSail( double width, double length, Competitor boat) {
        this.sailLine.update(width, length, boat, dataSource.getWindDirection(), boatPositionX, boatPositionY);
    }


    /**
     * Draw the health bar for a boat
     * @param boat
     */
    private void drawHealthBar(Competitor boat) {

        double sourceId = boat.getSourceID();
        HealthBar healthBar = healthBars.get(sourceId);
        if (healthBar == null) {
            healthBar = new HealthBar();
            this.healthBars.put(sourceId, healthBar);
            this.raceViewPane.getChildren().add(healthBar);
            return;
        }
        boolean alive;
        if (isZoom()) {
            MutablePoint location = getZoomedBoatLocation(boat);
            alive = healthBar.update(boat, location.getXValue(), location.getYValue(), true);
        } else {
            alive = healthBar.update(boat, boat.getPosition().getXValue(), boat.getPosition().getYValue(), false);
        }
        if (!alive) this.killBoat(boat);
    }


    /**
     * Remove dead boat and attachments from the view
     * @param boat
     */
    private void killBoat(Competitor boat) {
        this.dataSource.send(new BinaryPackager().packageBoatAction(Keys.RIP.getValue(), boat.getSourceID()));
        if(dataSource.getSourceID() == boat.getSourceID()){
            sailLine.setVisible(false);
            playerMarker.setVisible(false);
            this.raceViewPane.getChildren().remove(guideArrow);
        }
        boatModels.get(boat.getSourceID()).setVisible(false);
        wakeModels.get(boat.getSourceID()).setVisible(false);
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

        List<MutablePoint> virtualLinePoints = RaceCalculator.calcVirtualLinePoints(selectedBoat, boatModels.get(selectedBoat.getSourceID()),startMark1,startMark2,startLine1,expectedStartTime,messageTime);
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
    void zoomIn(){
        zoom=true;
        mapEngine.executeScript("setZoom(17);");
        updateRace();
        setScale(2);
        track.setVisible(!isZoom());
    }



    /**
     * Zooms out from your boat
     */
    void zoomOut(){
        zoom=false;
        drawBackgroundImage();
        updateRace();
        setScale(1);
        track.setVisible(!isZoom());
    }

    boolean isZoom() {
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

            Label name = new Label(boat.getAbbreName());
            this.nameAnnotations.put(sourceID, name);

            Label speed = new Label(String.valueOf(boat.getVelocity()) + "m/s");
            this.speedAnnotations.put(sourceID, speed);

            name.setFont(Font.font("Monospaced"));
            name.setTextFill(boat.getColor());
            this.raceViewPane.getChildren().add(name);
            speed.setFont(Font.font("Monospaced"));
            speed.setTextFill(boat.getColor());
            this.raceViewPane.getChildren().add(speed);

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
        if(isZoom()) offset *= 2;

        Label name = this.nameAnnotations.get(sourceID);
        Label speed = this.speedAnnotations.get(sourceID);
        speed.setText(String.valueOf(boat.getVelocity()) + "m/s");

        if(boat.getStatus() == DSQ) {
            name.setText("");
            speed.setText("");
        }

        name.setLayoutX(point.getXValue() + 5);
        name.setLayoutY(point.getYValue()+ offset);
        offset += 12;
        speed.setLayoutX(point.getXValue() + 5);
        speed.setLayoutY(point.getYValue()+ offset);
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
    private MutablePoint getZoomedBoatLocation(Competitor boat){
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
                MutablePoint point= getZoomedBoatLocation(boat);
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
     * Gets the centre coordinates for a mark or gate
     * @param markIndex index of the mark (based on the order they are rounded)
     * @return MutablePoint (x,y) coordinates
     */
    private MutablePoint getGateCentre(Integer markIndex) {

        Map<Integer, List<Integer>> features = this.dataSource.getIndexToSourceIdCourseFeatures();
        if (markIndex > features.size()) return null; //passed the finish line

        List<Integer> ids = features.get(markIndex);
        if (ids == null) return null;
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
     * Update the position of the guide arrow
     */
    private void updateGuidingArrow() {

        Competitor boat = dataSource.getStoredCompetitors().get(dataSource.getSourceID());
        int currentIndex = boat.getCurrentLegIndex();
        MutablePoint nextMarkLocation = getGateCentre(currentIndex + 1);

        if (nextMarkLocation == null) { //end of race
            this.raceViewPane.getChildren().remove(guideArrow);
            return;
        }
        if (isZoom()) guideArrow.updateArrowZoomed(boat, boatPositionX, boatPositionY, nextMarkLocation);
        else guideArrow.updateArrow(getGateCentre(currentIndex), nextMarkLocation);
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
        }
        this.drawSail(width, length, dataSource.getStoredCompetitors().get(dataSource.getSourceID()));
        counter++;
    }



    /**
     * checks collisions and draws them
     */
    private void checkCollision(){
        for(int sourceID:new HashSet<>(dataSource.getCollisions())){
            MutablePoint point=setRelativePosition(dataSource.getStoredCompetitors().get(sourceID));
            if (sourceID == dataSource.getSourceID()) {
                //drawBorder(raceViewPane.getWidth(),raceViewPane.getHeight(),25);
                new BorderAnimation(raceParentPane, 25).animate();
                new RandomShake(raceParentPane).animate();
            }
            drawCollision(point.getXValue(), point.getYValue());
            dataSource.removeCollsions(sourceID);
        }
    }


    /**
     * draws collisions at the location passed in
     * @param centerX the x coordinate of the collision
     * @param centerY the y coordinate of the collision
     */
    public void drawCollision(double centerX,double centerY){
        int radius=20;
        if(isZoom()) radius *= 2;
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
     * Refreshes the contents of the display to match the datasource
     *
     */
    void refresh() {
        checkRaceFinished();
        setBoatLocation();
        updateRace();
        checkCollision();
        updateGuidingArrow();

    }

    boolean isLoaded() {
        return isLoaded;
    }

}