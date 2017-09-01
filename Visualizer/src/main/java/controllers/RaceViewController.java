package controllers;

import Animations.BorderAnimation;
import Animations.CollisionRipple;
import Animations.RandomShake;
import Elements.*;
import javafx.collections.ObservableList;
import javafx.concurrent.Worker;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Shape;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
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
import static javafx.scene.paint.Color.ORANGERED;
import static parsers.BoatStatusEnum.DSQ;


/**
 * Controller for the race view.
 */
public class RaceViewController implements Initializable, TableObserver {


    //VIEW ELEMENTS
    @FXML private AnchorPane raceView;
    @FXML private Pane raceViewPane;
    @FXML private Canvas raceViewCanvas;
    @FXML private WebView mapView;
    @FXML private Pane raceParentPane;
    @FXML private ImageView controlsView;
    @FXML private HBox controlsBox;
    @FXML private GridPane finisherListPane;
    @FXML private ListView<String> finisherListView;


    private Map<Integer, BoatModel> boatModels = new HashMap<>();
    private Map<Integer, Wake> wakeModels = new HashMap<>();
    private Map<Double, HealthBar> healthBars = new HashMap<>();
    private Map<Integer, Annotation> annotations = new HashMap<>();
    private Map<String, Shape> markModels = new HashMap<>();
    private Track track = new Track();
    private Line startLine;
    private Line finishLine;
    private GuideArrow guideArrow;
    private Sail sailLine;

    //FLAGS
    private Boolean finisherListDisplayed = false;
    private boolean isLoaded = false;
    private boolean zoom = false;

    //CONTROL VARIABLES
    private int counter = 0;
    private Integer selectedBoatSourceId = 0;
    private double boatPositionX; //current position in screen coords
    private double boatPositionY; //current position in screen coords
    private MutablePoint currentPosition17; //boat position in screen coordinates with zoom level 17
    private ObservableList<String> observableFinisherList = observableArrayList();

    //CONFIG
    private static final Color backgroundColor = Color.POWDERBLUE;

    //OTHER
    private WebEngine mapEngine;
    private DataSource dataSource;
    private GraphicsContext gc;







    //================================================================================================================
    // SETUP
    //================================================================================================================



    @Override
    public void initialize(URL location, ResourceBundle resources) {

        startLine = new Line();
        finishLine = new Line();

        //draws the sail on the boat
        sailLine = new Sail(Color.WHITE);

        raceViewPane.getChildren().add(startLine);
        raceViewPane.getChildren().add(finishLine);
        raceViewPane.getChildren().add(sailLine);

        finisherListPane.setVisible(false);

        this.guideArrow = new GuideArrow(backgroundColor.brighter(), 90.0);
        raceViewPane.getChildren().add(guideArrow);
        controlsView = new ImageView(new Image("controls.png"));

        gc = raceViewCanvas.getGraphicsContext2D();

        mapEngine = mapView.getEngine();
        mapView.setVisible(true);
        mapEngine.setJavaScriptEnabled(true);
        mapView.toBack();

        isLoaded = true; //TODO:- TAKE THIS OUT AGAIN

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
//        gamerTagLabel.setText(dataSource.getCompetitor().getTeamName());

        this.dataSource = dataSource;

        while (dataSource.getCompetitorsPosition() == null) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }






    //================================================================================================================
    // EVENTS
    //================================================================================================================


    /**
     * Observer method for table observer
     * Updates the selected boat property
     * @param sourceId Integer the sourceId of the selected boat
     */
    public void boatSelected(Integer sourceId) {
        this.selectedBoatSourceId = sourceId;
    }



    /**
     * Remove dead boat and attachments from the view
     * @param boat
     */
    private void killBoat(Competitor boat) {
        this.dataSource.send(new BinaryPackager().packageBoatAction(Keys.RIP.getValue(), boat.getSourceID()));
        if(dataSource.getSourceID() == boat.getSourceID()){
            sailLine.setVisible(false);
            this.raceViewPane.getChildren().remove(guideArrow);
        }
        boatModels.get(boat.getSourceID()).setVisible(false);
        wakeModels.get(boat.getSourceID()).setVisible(false);
    }


    /**
     * Check to see if the race is finished and display finisher list if it is
     */
    private void checkRaceFinished(){
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





    //================================================================================================================
    // DRAWING
    //================================================================================================================


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
        } else alive = healthBar.update(boat, boat.getPosition().getXValue(), boat.getPosition().getYValue(), false);
        if (!alive) this.killBoat(boat);

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
     * Draws the course features on the canvas
     */
    private void drawCourse(Map<Integer,CourseFeature> courseFeatures) {

        // loops through all course features
        for (CourseFeature courseFeature : courseFeatures.values()) {
            drawMark(courseFeature);
        }
        MutablePoint startLine1=courseFeatures.get(dataSource.getStartMarks().get(0)).getPixelLocations().get(0);
        MutablePoint startLine2=courseFeatures.get(dataSource.getStartMarks().get(1)).getPixelLocations().get(0);
        MutablePoint finishLine1=courseFeatures.get(dataSource.getFinishMarks().get(0)).getPixelLocations().get(0);
        MutablePoint finishLine2=courseFeatures.get(dataSource.getFinishMarks().get(1)).getPixelLocations().get(0);
        drawLine(startLine, startLine1,startLine2);
        drawLine(finishLine,finishLine1,finishLine2);
    }


    /**
     * Update the given line
     * @param line Line the line to be drawn
     * @param p1 one of the point on the line
     * @param p2 the other point
     */
    private void drawLine(Line line, MutablePoint p1, MutablePoint p2) {
        ShapeDraw.line(line, p1, p2);
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
     * Draw boundary
     */
    private void drawBoundary(List<MutablePoint> courseBoundary) {
        ShapeDraw.polygon(gc, courseBoundary, backgroundColor);
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
     * Draw or move a boat model for a competitor
     * @param boat Competitor a competing boat
     */
    private void drawBoat(Competitor boat) {
        Integer sourceId = boat.getSourceID();

        MutablePoint point = setRelativePosition(boat);
        Boolean player = sourceId == dataSource.getSourceID();
        BoatModel boatModel = boatModels.get(sourceId);
        if (boatModel == null) {
            boatModel = new BoatModel(boat.getColor(), player);
            this.raceViewPane.getChildren().add(boatModel);
            this.boatModels.put(sourceId, boatModel);
        }
        boatModel.update(point, boat.getCurrentHeading());
    }


    /**
     * Draw a boats wake
     * @param boat Competitor, the boat
     * @param boatLength double, the length of the boat
     * @param startWakeOffset double, the offset of the wake
     * @param wakeWidthFactor double, the width scale
     * @param wakeLengthFactor double, the length scale
     */
    private void drawWake(Competitor boat, double boatLength,double startWakeOffset, double wakeWidthFactor, double wakeLengthFactor) {
        MutablePoint point= setRelativePosition(boat);

        Wake wake = wakeModels.get(boat.getSourceID());
        if (wake == null) {
            wake = new Wake(boat, boatLength, startWakeOffset, wakeWidthFactor, wakeLengthFactor);
            this.wakeModels.put(boat.getSourceID(), wake);
            this.raceViewPane.getChildren().add(wake);
        }
        wake.update(boat, boatLength, startWakeOffset, wakeWidthFactor, wakeLengthFactor, point);
    }


    /**
     * Draw the next dot of track for the boat on the canvas
     * @param boat Competitor
     */
    private void drawTrack(Competitor boat) {
        this.track.addDot(boat.getPosition(), boat.getColor());
    }


    /**
     * Update the position of the guide arrow
     */
    private void drawGuidingArrow() {
        Map<Integer, List<Integer>> indexMap = dataSource.getIndexToSourceIdCourseFeatures();
        Map<Integer, CourseFeature> featureMap = dataSource.getCourseFeatureMap();

        Competitor boat = dataSource.getCompetitor();
        int currentIndex = boat.getCurrentLegIndex();
        MutablePoint nextMarkLocation = RaceCalculator.getGateCentre(currentIndex + 1, indexMap, featureMap);

        if (nextMarkLocation == null) { //end of race
            this.raceViewPane.getChildren().remove(guideArrow);
            return;
        }
        if (isZoom()) guideArrow.updateArrowZoomed(boat, boatPositionX, boatPositionY, nextMarkLocation);
        else guideArrow.updateArrow(RaceCalculator.getGateCentre(currentIndex, indexMap, featureMap), nextMarkLocation);
    }


    /**
     * Draw annotations and move with boat positions
     */
    private void drawAnnotations(Competitor boat) {

        if (boat.getSourceID() == dataSource.getSourceID()) return; //don't draw annotations for the player
        Annotation annotation = annotations.get(boat.getSourceID());
        if (annotation == null) {
            annotation = new Annotation(boat);
            this.annotations.put(boat.getSourceID(), annotation);
            this.raceViewPane.getChildren().add(annotation);
        }
        this.annotations.get(boat.getSourceID()).update(boat, setRelativePosition(boat), isZoom());
    }


    /**
     * draws collisions at the location passed in
     * @param centerX the x coordinate of the collision
     * @param centerY the y coordinate of the collision
     */
    private void drawCollision(double centerX, double centerY){
        int radius=20;
        if(isZoom()) radius *= 2;
        CollisionRipple ripple = new CollisionRipple(centerX, centerY,radius );
        raceViewPane.getChildren().add(ripple);
        ripple.animate().setOnFinished(event -> raceViewPane.getChildren().remove(ripple));
    }






    //================================================================================================================
    // VIEW SCALING
    //================================================================================================================



    /**
     * adds scaling to all shapes in the scene
     */
    private void setScale(double scale){
        for(Group model : boatModels.values()) {
            model.setScaleX(scale);
            model.setScaleY(scale);
        }
        for(Shape model: markModels.values()){
            model.setScaleX(scale);
            model.setScaleY(scale);
        }

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







    //================================================================================================================
    // MAIN
    //================================================================================================================


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

        for (Competitor boat : dataSource.getCompetitorsPosition()) {

            if (counter % 70 == 0) {
                drawTrack(boat);
            }
            this.drawWake(boat, boatLength, startWakeOffset, wakeWidthFactor, wakeLengthFactor);
            this.drawBoat(boat);
            this.drawHealthBar(boat);
            this.drawAnnotations(boat);
        }
        this.drawSail(width, length, dataSource.getCompetitor());
        this.drawGuidingArrow();
        counter++;
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
    }

    boolean isLoaded() {
        return isLoaded;
    }

}