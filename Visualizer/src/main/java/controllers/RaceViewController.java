package controllers;

import Animations.BorderAnimation;
import Animations.CollisionRipple;
import Animations.RandomShake;
import parsers.boatAction.BoatAction;
import utilities.Sounds;
import Elements.*;
import Elements.Annotation;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.animation.FadeTransition;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.concurrent.Worker;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ListView;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.RotateEvent;
import javafx.scene.input.TouchEvent;
import javafx.scene.input.ZoomEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Shape;
import javafx.scene.transform.Rotate;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.util.Duration;
import mockDatafeed.Keys;
import models.*;
import netscape.javascript.JSException;
import parsers.RaceStatusEnum;
import parsers.powerUp.PowerUp;
import utilities.*;
import utilities.DataSource;
import utilities.RaceCalculator;
import utility.BinaryPackager;

import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;

import static Elements.PowerUpModel.getImageWidth;
import static java.lang.Math.sqrt;
import static javafx.collections.FXCollections.observableArrayList;
import static javafx.scene.paint.Color.ORANGERED;
import static parsers.BoatStatusEnum.DSQ;
import static parsers.powerUp.PowerUpType.BOOST;
import static parsers.powerUp.PowerUpType.POTION;


/**
 * Controller for the race view.
 */
public class RaceViewController implements Initializable, TableObserver {


    //VIEW ELEMENTS
    @FXML private TableController tableController = new TableController();
    @FXML private AnchorPane raceView;
    @FXML private Pane raceViewPane;
    @FXML private Canvas raceViewCanvas;
    @FXML private WebView mapView;
    @FXML private Pane raceParentPane;
    @FXML private ImageView controlsView;
    @FXML private HBox controlsBox;
    @FXML private GridPane finisherListPane;
    @FXML private ListView<String> finisherListView;

    private Map<Integer, ImageView> fallenCrews=new HashMap<>();
    private Map<Integer, Image> bloodImages = new HashMap<>();
    private Map<Integer, ImageView> blood = new HashMap<>();
    private Map<Integer, Image> crewImages = new HashMap<>();
    private Map<Integer, PowerUpModel> powerUps=new HashMap<>();

    private Map<Integer, BoatModel> boatModels = new HashMap<>();
    private Map<Integer, Wake> wakeModels = new HashMap<>();
    private Map<Integer, HealthBar> healthBars = new HashMap<>();
    private Map<Integer, Annotation> annotations = new HashMap<>();
    private Map<String, MarkModel> markModels = new HashMap<>();
    private Track track = new Track();
    private Line startLine;
    private Line finishLine;
    private GuideArrow guideArrow;
    private Sail sailLine;
    private SharkModel sharkModel;
    private Map<Integer, WhirlpoolModel> whirlpools = new HashMap<>();
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
    private double touchZoomLevel = 0.0; // current touch zoom level

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

        sharkModel = new SharkModel(new Image("/Animations/sharkMoving.gif"));
        bloodImages.put(0, new Image("/images/blood.png"));
        bloodImages.put(1, new Image("/images/blood1.png"));
        bloodImages.put(2, new Image("/images/blood2.png"));
        crewImages.put(0, new Image("/Animations/boyCantSwim.gif"));
        crewImages.put(1, new Image("/Animations/girlCantSwim.gif"));

        raceViewPane.getChildren().add(startLine);
        raceViewPane.getChildren().add(finishLine);
        raceViewPane.getChildren().add(sailLine);
        raceViewPane.getChildren().add(sharkModel);


        finisherListPane.setVisible(false);

        this.guideArrow = new GuideArrow(backgroundColor.brighter(), 90.0);
        raceViewPane.getChildren().add(guideArrow);
        controlsView = new ImageView(new Image("images/controls.png"));

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
        this.dataSource.send(new BinaryPackager().packageBoatAction(BoatAction.RIP.getValue(), boat.getSourceID()));
        if(dataSource.getSourceID() == boat.getSourceID()){
            sailLine.setVisible(false);
            this.raceViewPane.getChildren().remove(guideArrow);
        }

        boatModels.get(boat.getSourceID()).die();
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
    public void zoomIn(){
        zoom=true;
        mapEngine.executeScript(String.format("setZoom(%d);",dataSource.getZoomLevel()));
        updateRace();
        setScale(nodeSizeFunc(dataSource.getZoomLevel()));
        dataSource.changeScaling(0);
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
        dataSource.changeScaling(0);
        track.setVisible(!isZoom());
    }

    public boolean isZoom() {
        return zoom;
    }

    /**
     * returns the node size scaling corresponding to zoom level
     * @param zoomLevel the current zoom level of the map
     * @return the node size to be scaled by
     */
    public double nodeSizeFunc(int zoomLevel){
        return 0.007*zoomLevel*zoomLevel;
    }


    /**
     * checks collisions and draws them
     */
    private void checkCollision(){
        HashMap<Integer, Integer> collisions = new HashMap<>(dataSource.getCollisions());
        for(int sourceID: collisions.keySet()){
            MutablePoint point=setRelativePosition(dataSource.getStoredCompetitors().get(sourceID));
            if (sourceID == dataSource.getSourceID() && collisions.get(sourceID) == 1) {
                //drawBorder(raceViewPane.getWidth(),raceViewPane.getHeight(),25);
                new BorderAnimation(raceParentPane, 25).animate();
                new RandomShake(raceParentPane).animate();
                drawCollision(point.getXValue(), point.getYValue());
            }
            else if (sourceID == dataSource.getSourceID() && collisions.get(sourceID) == 2) {
                new RandomShake(raceParentPane).spin();
            }
            Sounds.player.playSoundEffect("sounds/impact.mp3");
            drawCollision(point.getXValue(), point.getYValue());
            dataSource.removeCollsions(sourceID);
        }
    }


    /**
     * Toggles a control layout of the game
     * @param actionEvent action
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
     * toggles the state of the zoom
     */
    public void toggleZoom() {
        if(isZoom()) {
            zoomOut();
            if (!tableController.isVisible()) { tableController.makeVisible(); }
        } else{
            zoomIn();
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
     * @param boat Competitor
     */
    private void drawHealthBar(Competitor boat) {

        int sourceId = boat.getSourceID();
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
            MarkModel markModel = new MarkModel(x, y);
            markModels.put(courseFeature.getName(), markModel);
            raceViewPane.getChildren().add(markModel);
        }
        else {
            MarkModel mark = markModels.get(courseFeature.getName());
            mark.setCentres(x, y);
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
            boatModel = new BoatModel(boat.getBoatType(), player);
            this.raceViewPane.getChildren().add(boatModel);
            this.boatModels.put(sourceId, boatModel);
        }
        if(boat.getStatus() == DSQ) {
            boatModels.get(boat.getSourceID()).die();
            boatModel.update(point, 0);
        }
        else boatModel.update(point, boat.getCurrentHeading());
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


    /**
     * Draw crew members in the water
     */
    private void drawFallenCrew(){

        Map<Integer,CrewLocation> crewLocation = dataSource.getCrewLocations();

        //remove entries
        Set<Integer> removedLocation= new HashSet<>(fallenCrews.keySet());
        removedLocation.removeAll(crewLocation.keySet());
        for(int sourceId:removedLocation){
            raceViewPane.getChildren().remove(fallenCrews.get(sourceId));
            fallenCrews.remove(sourceId);
        }

        for(int sourceID:crewLocation.keySet()) {
            Random randomGenerator = new Random();
            if (!fallenCrews.containsKey(sourceID)) {
                ImageView crew = new ImageView();
                Image drowning = crewImages.get(randomGenerator.nextInt(crewImages.size()));
                crew.setImage(drowning);
//            Circle crew;
                fallenCrews.put(sourceID,crew);
                raceViewPane.getChildren().add(crew);
//            System.out.println(crewLocation);
            }

            Image image=fallenCrews.get(sourceID).getImage();
            if (isZoom()) {
                MutablePoint p = crewLocation.get(sourceID).getPosition17().shift(-currentPosition17.getXValue() + raceViewCanvas.getWidth() / 2, -currentPosition17.getYValue() + raceViewCanvas.getHeight() / 2);
                fallenCrews.get(sourceID).relocate(p.getXValue()-image.getWidth()/2,p.getYValue()-image.getHeight()/2);
            } else {
                MutablePoint p=crewLocation.get(sourceID).getPosition();
                fallenCrews.get(sourceID).relocate(p.getXValue()-image.getWidth()/2,p.getYValue()-image.getHeight()/2);
            }
        }
    }

    private void drawPowerUps() {
        Map<Integer,PowerUp> receivedPowerUps = dataSource.getPowerUps();


        for(PowerUp receivedPowerUp: receivedPowerUps.values()) {
            int sourceId = receivedPowerUp.getId();
            if (!powerUps.containsKey(sourceId)) {
                PowerUpModel powerUpModel = new PowerUpModel(receivedPowerUp);
                powerUps.put(sourceId, powerUpModel);
                raceViewPane.getChildren().add(powerUpModel);
            }

            powerUps.get(sourceId).update(isZoom(), receivedPowerUp, currentPosition17, raceViewCanvas.getWidth(), raceViewCanvas.getHeight());

            Long timeout = receivedPowerUp.getTimeout();

            if(System.currentTimeMillis() > timeout || receivedPowerUp.isTaken()) {
                raceViewPane.getChildren().remove(powerUps.get(sourceId));
                powerUps.remove(sourceId);
                dataSource.getPowerUps().remove(sourceId);
                break;
            }

        }
    }
    /**
     * Draw Obstacles in the water
     */
    private void drawSharks(){
        Map<Integer, Shark> sharkLocation = dataSource.getSharkLocations();
        Shark shark = sharkLocation.get(0);
        sharkModel.update(shark.getPosition().getXValue(), shark.getPosition().getYValue(), shark.getHeading());
        sharkModel.toFront();

        Image image = sharkModel.getImage();
        if (isZoom()) {
            MutablePoint p = sharkLocation.get(0).getPosition17().shift(-currentPosition17.getXValue() + raceViewCanvas.getWidth() / 2, -currentPosition17.getYValue() + raceViewCanvas.getHeight() / 2);
            sharkModel.relocate(p.getXValue()-image.getWidth()/2,p.getYValue()-image.getHeight()/2);
        } else {
            MutablePoint p = sharkLocation.get(0).getPosition();
            sharkModel.relocate(p.getXValue()-image.getWidth()/2,p.getYValue()-image.getHeight()/2);
        }
    }

    /**
     * Draw whirlpools in the race
     */
    private void drawWhirlpools() {
        Map<Integer, Whirlpool> whirlpoolsLocation = dataSource.getWhirlpools();
        Set<Integer> removedLocation= new HashSet<>(whirlpools.keySet());
        removedLocation.removeAll(whirlpoolsLocation.keySet());

        for(int sourceId:removedLocation){
            raceViewPane.getChildren().remove(whirlpools.get(sourceId));
            whirlpools.remove(sourceId);
        }

        for (int id: whirlpoolsLocation.keySet()) {
            if (!whirlpools.containsKey(id)) {
                Whirlpool whirlpool = whirlpoolsLocation.get(id);
                WhirlpoolModel model = new WhirlpoolModel(new Image("/images/whirlpool.png"));
                model.update(whirlpool.getPosition());
                model.animateSpawn();
                whirlpools.put(id, model);
                raceViewPane.getChildren().add(model);
            }
            else {
                for (WhirlpoolModel whirlpool: whirlpools.values()) {
                    whirlpool.spin();
                }
            }
            Image image = whirlpools.get(id).getImage();

            if (isZoom()) {
                MutablePoint p = whirlpoolsLocation.get(id).getPosition17().shift(-currentPosition17.getXValue() + raceViewCanvas.getWidth() / 2, -currentPosition17.getYValue() + raceViewCanvas.getHeight() / 2);
                whirlpools.get(id).relocate(p.getXValue()-image.getWidth()/2,p.getYValue()-image.getHeight()/2);
            } else {
                MutablePoint p = whirlpoolsLocation.get(id).getPosition();
                whirlpools.get(id).relocate(p.getXValue()-image.getWidth()/2,p.getYValue()-image.getHeight()/2);
            }

        }
    }

    /**
     * draw blood when a shark eats a fallen crew member
     */
    private void drawBlood(){
        Map<Integer, Blood> bloodLocation = dataSource.getBloodLocations();

        //remove entries
        Set<Integer> removedLocation= new HashSet<>(blood.keySet());
        removedLocation.removeAll(bloodLocation.keySet());
        for(int sourceId:removedLocation){
            raceViewPane.getChildren().remove(blood.get(sourceId));
            blood.remove(sourceId);
        }

        for(int sourceID : bloodLocation.keySet()){
            if(!blood.containsKey(sourceID)) {
                Random randomGenerator = new Random();
                ImageView bloodImage = new ImageView();
                Image redBlob = bloodImages.get(randomGenerator.nextInt(bloodImages.size()));
                bloodImage.setImage(redBlob);
                blood.put(sourceID,bloodImage);
                raceViewPane.getChildren().add(bloodImage);

            }
            double opacity = bloodLocation.get(sourceID).getOpacity();
            if(opacity >= 0){
                bloodLocation.get(sourceID).updateOpacity();
                blood.get(sourceID).setOpacity(opacity);
            }

            Image image = blood.get(sourceID).getImage();

            if (isZoom()) {
                MutablePoint p = bloodLocation.get(sourceID).getPosition17().shift(-currentPosition17.getXValue() + raceViewCanvas.getWidth() / 2, -currentPosition17.getYValue() + raceViewCanvas.getHeight() / 2);
                blood.get(sourceID).relocate(p.getXValue()-image.getWidth()/2,p.getYValue()-image.getHeight()/2);
            } else {
                MutablePoint p=bloodLocation.get(sourceID).getPosition();
                blood.get(sourceID).relocate(p.getXValue()-image.getWidth()/2,p.getYValue()-image.getHeight()/2);
            }

        }





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
        for(MarkModel model: markModels.values()){
            model.setScaleX(scale);
            model.setScaleY(scale);
        }

        for(ImageView imageView: powerUps.values()){
            imageView.setPreserveRatio(true);
            imageView.setFitWidth(scale*getImageWidth());

        }

        for (WhirlpoolModel model: whirlpools.values()) {
            model.setPreserveRatio(true);
            model.setFitWidth(scale*model.getImage().getWidth());
            model.setFitHeight(scale*model.getImage().getHeight());
        }
        sharkModel.setScaleX(scale);
        sharkModel.setScaleY(scale);
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
            double multiplier=nodeSizeFunc(dataSource.getZoomLevel());
            width*=multiplier;
            length*=multiplier;
            boatLength *= multiplier;
            startWakeOffset*= multiplier;
//            wakeWidthFactor*= 1;
            wakeLengthFactor*=multiplier;
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
        drawFallenCrew();
        drawPowerUps();
        drawSharks();
        drawBlood();
        drawWhirlpools();
        setBoatLocation();
        updateRace();
        checkCollision();
    }

    boolean isLoaded() {
        return isLoaded;
    }

    /**
     * Turn the boat when a touch pressed stationary event is sent
     * @param touchEvent pressed touch event
     */
    public void turnBoat(TouchEvent touchEvent) {
        BinaryPackager binaryPackager = new BinaryPackager();
        int UP = 5; int DOWN = 6;
        Competitor boat = dataSource.getStoredCompetitors().get(dataSource.getSourceID());
        double heading = boat.getCurrentHeading();
        double windAngle = (dataSource.getWindDirection())%360;
        double downWind = (boat.getDownWind(windAngle))%360;
        double touchX = touchEvent.getTouchPoint().getX();
        double touchY = touchEvent.getTouchPoint().getY();
        double theta = RaceCalculator.calcBoatDirection(boatPositionX, boatPositionY, touchX, touchY);
        double difference = theta - heading;

        if (RaceCalculator.isWestOfWind(heading, downWind, windAngle)) {UP = 6; DOWN = 5;}

        if (difference > 0 && difference < 180) {
            this.dataSource.send(binaryPackager.packageBoatAction(DOWN, boat.getSourceID()));
        }
        else if(difference > 0 && difference > 180) {
            this.dataSource.send(binaryPackager.packageBoatAction(UP, boat.getSourceID()));
        }
        else if (difference < 0 && difference > -180) {
            this.dataSource.send(binaryPackager.packageBoatAction(UP, boat.getSourceID()));
        }
        else if (difference < 0 && difference < -180) {
            this.dataSource.send(binaryPackager.packageBoatAction(DOWN, boat.getSourceID()));
        }
    }


    /**
     * Zoom the screen in and out upon touch zoom event
     * @param zoomEvent zoom event
     */
    public void zoom(ZoomEvent zoomEvent) {
        if (zoom) {
            if (dataSource.getZoomLevel() < 18 && touchZoomLevel < zoomEvent.getTotalZoomFactor()) {
                dataSource.changeScaling(1);
                zoomIn();
            }
            if (dataSource.getZoomLevel() > 12 && touchZoomLevel > zoomEvent.getTotalZoomFactor()) {
                dataSource.changeScaling(-1);
                zoomIn();
            }
            touchZoomLevel = zoomEvent.getTotalZoomFactor();

        }
    }






}