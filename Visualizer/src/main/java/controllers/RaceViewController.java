package controllers;

import Animations.BorderAnimation;
import Animations.CollisionRipple;
import Animations.RandomShake;
import Elements.*;
import com.rits.cloning.Cloner;
import javafx.collections.ObservableList;
import javafx.concurrent.Worker;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TouchEvent;
import javafx.scene.input.ZoomEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import models.*;
import netscape.javascript.JSException;
import parsers.RaceStatusEnum;
import parsers.boatAction.BoatAction;
import parsers.powerUp.PowerUp;
import parsers.xml.race.Decoration;
import utilities.DataSource;
import utilities.RaceCalculator;
import utilities.Sounds;
import utility.BinaryPackager;

import java.net.URISyntaxException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.*;

import static java.lang.Math.pow;
import static javafx.collections.FXCollections.observableArrayList;
import static parsers.BoatStatusEnum.DSQ;
import static parsers.xml.race.ThemeEnum.*;


/**
 * Controller for the race view.
 */
public class RaceViewController implements Initializable, TableObserver {


    //CONFIG
    private static final Color backgroundColor = Color.web("B0E0E6",0.4); //powder blue
    //VIEW ELEMENTS
    @FXML
    private Button muteButton;
    @FXML
    private Button soundButton;
    @FXML
    private Button leaveButton;
    @FXML
    private Button zoomButton;
    @FXML
    private TableController tableController = new TableController();
    @FXML
    private AnchorPane raceView;
    @FXML
    private Pane raceViewPane;
    @FXML
    private Canvas raceViewCanvas;
    @FXML
    private WebView mapView;
    @FXML
    private Pane raceParentPane;
    @FXML
    private HBox controlsBox;
    @FXML
    private HBox quitBox;
    @FXML
    private GridPane finisherListPane;
    @FXML
    private ListView<String> finisherListView;

    @FXML
    private ImageView zoomIcon;

    private Map<Integer, FallenCrew> fallenCrews = new HashMap<>();
    //    private Map<Integer, ImageView> blood = new HashMap<>();
//    private Map<Integer, Image> crewImages = new HashMap<>();
    private Map<String, DecorationModel> decorations = new HashMap<>();

    private Map<Integer, PowerUpModel> powerUps = new HashMap<>();
    private Map<Integer, BoatModel> boatModels = new HashMap<>();
    private Map<Integer, Wake> wakeModels = new HashMap<>();
    private Map<Integer, HealthBar> healthBars = new HashMap<>();
    private Map<Integer, Annotation> annotations = new HashMap<>();
    private Map<String, MarkModel> markModels = new HashMap<>();
    private Track track = new Track();
    private Line startLine;
    private Line finishLine;
    private GuideArrow guideArrow;
    private CurvedGuideArrow curvedArrowClockwise;
    private CurvedGuideArrow curvedArrowAnticlockwise;
    private Sail sailLine;
    private CurvedSail curvedSailLine;
    private SharkModel sharkModel;
    private Map<Integer, WhirlpoolModel> whirlpools = new HashMap<>();
    private Polygon boundaryPolygon;


    //FLAGS
    private Boolean finisherListDisplayed = false;
    private boolean isLoaded = false;
    private boolean zoom = false;
    private boolean muted = false;
    public boolean finishFlag = false; //game finished
    public boolean exit = false; //quit game
    //CONTROL VARIABLES
    private int counter = 0;
    private Integer selectedBoatSourceId = 0;
    private double boatPositionX; //current position in screen coords
    private double boatPositionY; //current position in screen coords
    private MutablePoint currentPosition17; //boat position in screen coordinates with zoom level 17
    private ObservableList<String> observableFinisherList = observableArrayList();
    //OTHER
    private WebEngine mapEngine;
    private DataSource dataSource;
    private GraphicsContext gc;
    private Cloner cloner=new Cloner();

    //images
    private final String[] crewImages = {"/Animations/boyCantSwim.gif", "/Animations/girlCantSwim.gif"};
    private final String[] bloodImages = {"/images/blood2.png", "/images/blood1.png", "/images/blood.png"};
    private Image zoomInImage;
    private Image zoomOutImage;

    private Map<Integer, CourseFeature> currentFeaturePositions;


    //================================================================================================================
    // SETUP
    //================================================================================================================


    @Override
    public void initialize(URL location, ResourceBundle resources) {

        startLine = new Line();
        finishLine = new Line();

        //draws the sail on the boat
        sailLine = new Sail(Color.WHITE);
        curvedSailLine = new CurvedSail(Color.WHITE);



        raceViewPane.getChildren().add(startLine);
        raceViewPane.getChildren().add(finishLine);
        raceViewPane.getChildren().add(sailLine);
        raceViewPane.getChildren().add(curvedSailLine);

        controlsBox.setVisible(false);
        quitBox.setVisible(false);

        this.zoomInImage = new Image(getClass().getClassLoader().getResource("images/zoomInIcon.png").toString());
        this.zoomOutImage = new Image(getClass().getClassLoader().getResource("images/zoomOutIcon.png").toString());

        finisherListPane.setVisible(false);

        this.guideArrow = new GuideArrow(backgroundColor.brighter(), 90.0);
        curvedArrowClockwise = new CurvedGuideArrow(true);
        curvedArrowAnticlockwise = new CurvedGuideArrow(false);
        raceViewPane.getChildren().add(guideArrow);
        raceViewPane.getChildren().add(curvedArrowClockwise);
        raceViewPane.getChildren().add(curvedArrowAnticlockwise);

        gc = raceViewCanvas.getGraphicsContext2D();

        mapEngine = mapView.getEngine();
        mapView.setVisible(true);
        mapEngine.setJavaScriptEnabled(true);
        mapView.toBack();

        try {
            mapEngine.load(getClass().getClassLoader().getResource("mapsBermuda.html").toURI().toString());
        } catch (URISyntaxException e) {
           // e.printStackTrace();
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
        controlsBox.toBack();
        quitBox.toBack();
        raceViewPane.getChildren().add(track);
        this.dataSource = dataSource;

        try {
            if (dataSource.getThemeId() == ANTARCTICA) {
                mapEngine.load(getClass().getClassLoader().getResource("mapsAntarctica.html").toURI().toString());
                sharkModel = new SharkModel(new Image("/Animations/sharkMoving.gif"));
            } else if (dataSource.getThemeId() == AMAZON) {
                mapEngine.load(getClass().getClassLoader().getResource("mapsAmazon.html").toURI().toString());
                sharkModel = new SharkModel(new Image("/Animations/crocMoving.gif"));
            }
            else if (dataSource.getThemeId() == NILE) {
                mapEngine.load(getClass().getClassLoader().getResource("mapsNile.html").toURI().toString());
                sharkModel = new SharkModel(new Image("/Animations/crocMoving.gif"));
            }
            else {
                sharkModel = new SharkModel(new Image("/Animations/sharkMoving.gif"));
            }
            raceViewPane.getChildren().add(sharkModel);
        } catch (URISyntaxException e) {
           // e.printStackTrace();
        }


        while (dataSource.getCompetitorsPosition() == null) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
              //  e.printStackTrace();
            }
        }
    }


    //================================================================================================================
    // EVENTS
    //================================================================================================================


    /**
     * Observer method for table observer
     * Updates the selected boat property
     *
     * @param sourceId Integer the sourceId of the selected boat
     */
    public void boatSelected(Integer sourceId) {
        this.selectedBoatSourceId = sourceId;
    }


    /**
     * Handle a boat dying
     *
     * @param boat
     */
    private void killBoat(Competitor boat) {
        showRIP(boat);
        this.dataSource.send(new BinaryPackager().packageBoatAction(BoatAction.RIP.getValue(), boat.getSourceID()));
    }

    /**
     * Set view of dead boat
     * @param boat
     */
    private void showRIP(Competitor boat) {
        boatModels.get(boat.getSourceID()).die();
        wakeModels.get(boat.getSourceID()).setVisible(false);
        if (dataSource.getSourceID() == boat.getSourceID()) {
            sailLine.setVisible(false);
            this.raceViewPane.getChildren().remove(guideArrow);
            this.raceViewPane.getChildren().remove(curvedArrowClockwise);
            this.raceViewPane.getChildren().remove(curvedArrowAnticlockwise);
        }
    }


    /**
     * Check to see if the race is finished and display finisher list if it is
     */
    private void checkRaceFinished() {
        if (dataSource.getRaceStatus().equals(RaceStatusEnum.FINISHED) && !finisherListDisplayed) {
            for (Competitor aCompetitor : dataSource.getCompetitorsPosition()) {
                if (aCompetitor.getStatus() == DSQ) {
                    observableFinisherList.add("RIP " + aCompetitor.getTeamName() + "      " + getPlayerRecord());
                } else {
                    observableFinisherList.add((dataSource.getCompetitorsPosition().indexOf(aCompetitor) + 1) + ". " +
                            aCompetitor.getTeamName() + "      " + getPlayerRecord());
                }
            }
            finisherListView.setItems(observableFinisherList);
            finisherListView.refresh();
            finisherListPane.setVisible(true);
            finisherListDisplayed = true;
            leaveButton.setVisible(false);

            double width = raceViewPane.getWidth();
            double height = raceViewPane.getHeight();
            finisherListPane.setLayoutX(width / 2 - finisherListPane.getWidth() / 2);
            finisherListPane.setLayoutY(height / 2 - finisherListPane.getHeight() / 2);
            this.finishFlag = true;
        }
    }

    /**
     * Calculates the time taken for players to finish the race / die in the race
     * @return player's record
     */
    public String getPlayerRecord() {
        double time = (System.currentTimeMillis()- dataSource.getMessageTime());
        time = time/1000.0;
        double seconds = time % 60;
        DecimalFormat df = new DecimalFormat("#");
        if (time > 60) {
            return (int) Math.floor(time/ 60) + " : " + df.format(seconds) + " seconds";
        } else {
            return df.format(seconds) + " seconds";
        }
    }


    /**
     * Zooms in on your boat
     */
    public void zoomIn() {
        if(dataSource.isSpectating()) {return;}
        zoom = true;
        mapEngine.executeScript(String.format("setZoom(%d);", dataSource.getZoomLevel()));
        updateRace();
        setScale(nodeSizeFunc(dataSource.getZoomLevel()));
        track.setVisible(!isZoom());
        zoomIcon.setImage(zoomOutImage);
    }


    /**
     * Zooms out from your boat
     */
    public void zoomOut() {
        if(dataSource.isSpectating()) {return;}
        zoom = false;
        drawBackgroundImage();
        updateRace();
        setScale(1);
        track.setVisible(!isZoom());
        this.zoomIcon.setImage(this.zoomInImage);
    }

    public boolean isZoom() {
        return zoom;
    }

    /**
     * toggles the state of the zoom
     */
    public void toggleZoom() {
        if (isZoom()) {
            zoomOut();
            if (!tableController.isVisible()) {
                tableController.makeVisible();
            }
        } else {
            zoomIn();
        }
    }


    /**
     * returns the node size scaling corresponding to zoom level
     *
     * @param zoomLevel the current zoom level of the map
     * @return the node size to be scaled by
     */
    public double nodeSizeFunc(int zoomLevel) {
        return pow(2, zoomLevel - 16);
    }


    /**
     * checks collisions and draws them
     */
    private void checkCollision() {
        HashMap<Integer, Integer> collisions = new HashMap<>(dataSource.getCollisions());
        for (int sourceID : collisions.keySet()) {
            MutablePoint point = getRelativePosition(dataSource.getStoredCompetitors().get(sourceID));
            if (sourceID == dataSource.getSourceID() && collisions.get(sourceID) == 1) {
                new BorderAnimation(raceParentPane, 25).animate();
                new RandomShake(raceParentPane).animate();
                drawCollision(point.getXValue(), point.getYValue());
            } else if (sourceID == dataSource.getSourceID() && collisions.get(sourceID) == 2) {
                new RandomShake(raceParentPane).spin();
            }
            Sounds.player.playSoundEffect("sounds/impact.mp3");
            drawCollision(point.getXValue(), point.getYValue());
            dataSource.removeCollsions(sourceID);
        }
    }


    /**
     * Toggles a control layout of the game
     */
    public void toggleControls() {
        controlsBox.setVisible(true);
        controlsBox.toFront();
    }

    /**
     * Sends the controls box to back
     * @param mouseEvent mouse clicked event
     */
    public void closeControls(MouseEvent mouseEvent) {
        controlsBox.setVisible(false);
        controlsBox.toBack();
    }




    /**
     * Brings the quit box to front
     * @param actionEvent button pressed event
     */
    @FXML
    public void showQuitBox(ActionEvent actionEvent) {
        quitBox.setVisible(true);
        quitBox.toFront();
    }


    /**
     * hides the quit box
     * @param actionEvent button pressed event
     */
    @FXML
    public void hideQuitBox(ActionEvent actionEvent) {
        quitBox.setVisible(false);
        quitBox.toBack();
    }


    /**
     * Set the exit flag to tell main to leave the game when possible
     * @param actionEvent event
     */
    @FXML
    public void leaveGame(ActionEvent actionEvent) {
        this.exit = true;
    }


    /**
     * Toggles the sounds when the button is clicked
     *
     * @param actionEvent button pressed event
     */
    public void toggleSound(ActionEvent actionEvent) {

        muted = !muted;
        if (muted) {
            soundButton.setVisible(false);
            muteButton.setVisible(true);
            Sounds.player.muteSound();
        } else {
            soundButton.setVisible(true);
            muteButton.setVisible(false);
            Sounds.player.setAllMusicVolume();
            Sounds.player.setAllEffectVolume();
        }
    }

    /**
     * Turn the boat when a touch pressed stationary event is sent
     *
     * @param touchEvent pressed touch event
     */
    public void turnBoat(TouchEvent touchEvent) {
        BinaryPackager binaryPackager = new BinaryPackager();
        int UP = 5;
        int DOWN = 6;
        Competitor boat = dataSource.getStoredCompetitors().get(dataSource.getSourceID());
        double heading = boat.getCurrentHeading();
        double windAngle = (dataSource.getWindDirection()) % 360;
        double downWind = (boat.getDownWind(windAngle)) % 360;
        double touchX = touchEvent.getTouchPoint().getX();
        double touchY = touchEvent.getTouchPoint().getY();
        double theta = RaceCalculator.calcBoatDirection(boatPositionX, boatPositionY, touchX, touchY);
        double difference = theta - heading;

        if (RaceCalculator.isWestOfWind(heading, downWind, windAngle)) {
            UP = 6;
            DOWN = 5;
        }

        if (difference > 0 && difference < 180) {
            this.dataSource.send(binaryPackager.packageBoatAction(DOWN, boat.getSourceID()));
        } else if (difference > 0 && difference > 180) {
            this.dataSource.send(binaryPackager.packageBoatAction(UP, boat.getSourceID()));
        } else if (difference < 0 && difference > -180) {
            this.dataSource.send(binaryPackager.packageBoatAction(UP, boat.getSourceID()));
        } else if (difference < 0 && difference < -180) {
            this.dataSource.send(binaryPackager.packageBoatAction(DOWN, boat.getSourceID()));
        }
    }


    /**
     * Zoom the screen in and out upon touch zoom event
     *
     * @param zoomEvent zoom event
     */
    public void touchZoom(ZoomEvent zoomEvent) {

        if (zoom) {
            if (dataSource.getZoomLevel() < 18 && zoomEvent.getTotalZoomFactor() > 1) {
                long zoomFactor = Math.round(zoomEvent.getTotalZoomFactor() / 2);
                dataSource.changeScaling(zoomFactor);
                zoomIn();
            }
            if (dataSource.getZoomLevel() > 13 && zoomEvent.getTotalZoomFactor() < 1) {
                long zoomFactor = Math.round((1 - zoomEvent.getTotalZoomFactor()) / 0.4);
                dataSource.changeScaling(-zoomFactor);
                zoomIn();
            }
        }
    }

    //================================================================================================================
    // DRAWING
    //================================================================================================================


    /**
     * Draws the line representing the sail of the boat
     */
    private void drawSail(double width, double length, Competitor boat) {
        if(boat.getStatus() == DSQ) {
            this.sailLine.setVisible(false);
            this.curvedSailLine.setVisible(false);
        }
        else {
            this.sailLine.update(width, length, boat, dataSource.getWindDirection(), boatPositionX, boatPositionY);
            this.curvedSailLine.update(width, length, boat, dataSource.getWindDirection(), boatPositionX, boatPositionY);
        }

    }


    /**
     * Draw the health bar for a boat
     *
     * @param boat Competitor
     */
    private void drawHealthBar(Competitor boat) {

        int sourceId = boat.getSourceID();
        HealthBar healthBar = healthBars.get(sourceId);

        if (healthBar == null) {
            healthBar = new HealthBar();
            this.healthBars.put(sourceId, healthBar);
            this.raceViewPane.getChildren().add(healthBar);

        }

        if (boat.getStatus() == DSQ) {
            healthBar.setVisible(false);
            return;
        }

        boolean alive;
        if (isZoom()) {
            MutablePoint location = getZoomedBoatLocation(boat);
            alive = healthBar.update(boat, location.getXValue(), location.getYValue(), true, nodeSizeFunc(dataSource.getZoomLevel()));
        } else {
            alive = healthBar.update(boat, boat.getPosition().getXValue(), boat.getPosition().getYValue(), false, nodeSizeFunc(dataSource.getZoomLevel()));
        }
        if (!alive) this.killBoat(boat);

    }

    private Map<Integer, CourseFeature> courseFeatures = new HashMap<>();
    private List<MutablePoint> courseBoundary = new ArrayList<>();

    /**
     * Check if course need to be redrawn and draws the course features and the course boundary
     */
    private void updateCourse() {

        if (isZoom()) {

            if (dataSource.isSpectating()) {
                MutablePoint position = new ArrayList<>(dataSource.getStoredFeatures17().values()).get(2).getGPSPoint();
                mapEngine.executeScript(String.format("setCenter(%.9f,%.9f);", position.getXValue(), position.getYValue()));
            }
            else mapEngine.executeScript(String.format("setCenter(%.9f,%.9f);", dataSource.getCompetitor().getLatitude(), dataSource.getCompetitor().getLongitude()));
            for (Integer id : dataSource.getStoredFeatures17().keySet()) {
                if (!courseFeatures.containsKey(id)) {
                    courseFeatures.put(id, cloner.deepClone(dataSource.getStoredFeatures17().get(id)));
                }
                courseFeatures.get(id).setPixelLocation(dataSource.getStoredFeatures17().get(id).getPixelCentre().getXValue()-currentPosition17.getXValue() + raceViewCanvas.getWidth() / 2,
                        dataSource.getStoredFeatures17().get(id).getPixelCentre().getYValue()-currentPosition17.getYValue() + raceViewCanvas.getHeight() / 2);

            }


            for(int i=0;i<dataSource.getCourseBoundary17().size();i++){
                if (courseBoundary.size()<dataSource.getCourseBoundary17().size()) {
                    courseBoundary.add(new MutablePoint(0.0,0.0));
                }
                courseBoundary.get(i).setX(dataSource.getCourseBoundary17().get(i).getXValue()-currentPosition17.getXValue() + raceViewCanvas.getWidth() / 2);
                courseBoundary.get(i).setY(dataSource.getCourseBoundary17().get(i).getYValue()-currentPosition17.getYValue() + raceViewCanvas.getHeight() / 2);
            }
            currentFeaturePositions=courseFeatures;
            drawCourse(courseFeatures);
            drawBoundary(courseBoundary);

        } else {
            currentFeaturePositions=dataSource.getStoredFeatures();
            drawCourse(dataSource.getStoredFeatures());
            drawBoundary(dataSource.getCourseBoundary());
        }

    }


    /**
     * Draws the course features on the canvas
     */
    private void drawCourse(Map<Integer, CourseFeature> courseFeatures) {

        // loops through all course features
        for (CourseFeature courseFeature : courseFeatures.values()) {
            drawMark(courseFeature);
        }

        MutablePoint startLine1 = courseFeatures.get(dataSource.getStartMarks().get(0)).getPixelLocations().get(0);
        MutablePoint startLine2 = courseFeatures.get(dataSource.getStartMarks().get(1)).getPixelLocations().get(0);
        MutablePoint finishLine1 = courseFeatures.get(dataSource.getFinishMarks().get(0)).getPixelLocations().get(0);
        MutablePoint finishLine2 = courseFeatures.get(dataSource.getFinishMarks().get(1)).getPixelLocations().get(0);
        drawLine(startLine, startLine1, startLine2);
        drawLine(finishLine, finishLine1, finishLine2);
    }


    /**
     * Update the given line
     *
     * @param line Line the line to be drawn
     * @param p1   one of the point on the line
     * @param p2   the other point
     */
    private void drawLine(Line line, MutablePoint p1, MutablePoint p2) {

        int lineTweak;      //how far line has to be moved to accommodate zooming

        if (!isZoom()) lineTweak = 15;
        else if (dataSource.getZoomLevel() == 18) lineTweak = 60;
        else if (dataSource.getZoomLevel() == 17) lineTweak = 35;
        else if (dataSource.getZoomLevel() == 16) lineTweak = 15;
        else if (dataSource.getZoomLevel() == 15) lineTweak = 7;
        else if (dataSource.getZoomLevel() == 14) lineTweak = 0;
        else lineTweak = 0;

        MutablePoint point1 = new MutablePoint(p1.getXValue(), p1.getYValue() + lineTweak);
        MutablePoint point2 = new MutablePoint(p2.getXValue(), p2.getYValue() + lineTweak);
        ShapeDraw.line(line, point1, point2);
    }


    /**
     * Draw the mark of the course feature
     *
     * @param courseFeature CourseFeature
     */
    private void drawMark(CourseFeature courseFeature) {
        double x = courseFeature.getPixelLocations().get(0).getXValue();
        double y = courseFeature.getPixelLocations().get(0).getYValue();

        if (!markModels.containsKey(courseFeature.getName())) {
            MarkModel markModel = new MarkModel(dataSource.getThemeId(), x, y);
            markModels.put(courseFeature.getName(), markModel);
            raceViewPane.getChildren().add(markModel);
        } else {
            MarkModel mark = markModels.get(courseFeature.getName());
            mark.setCentres(x, y);
        }
    }

    /**
     * Draw course decorations
     */
    private void drawThemeDecorations() {
        Map<String, Decoration> receivedDecorations = dataSource.getDecorations();

        for (Decoration receivedDecoration : receivedDecorations.values()) {
            String id = receivedDecoration.getId();
            if (!decorations.containsKey(id)) {
                MutablePoint location = receivedDecoration.getLocation();
                receivedDecoration.setPosition(dataSource.evaluatePosition(location));
                receivedDecoration.setPositionOriginal(dataSource.evaluateOriginalPosition(location));
                receivedDecoration.setPosition17(dataSource.evaluatePosition17(receivedDecoration.getPosition()));

                DecorationModel decorationModel = new DecorationModel(receivedDecoration);
                decorations.put(id, decorationModel);
                raceViewPane.getChildren().add(decorationModel);
            }
            decorations.get(id).update(isZoom(), currentPosition17, raceViewCanvas.getWidth(), raceViewCanvas.getHeight());
        }

    }


    private void createBoundary(Double[] vertices){
        boundaryPolygon=new Polygon();
        boundaryPolygon.getPoints().addAll(vertices);
        boundaryPolygon.getStrokeDashArray().addAll(10d,8d);
        boundaryPolygon.setStroke(Color.BLACK);
        boundaryPolygon.setStrokeWidth(0.8);
        boundaryPolygon.setFill(backgroundColor);
        raceViewPane.getChildren().add(boundaryPolygon);
    }

    /**
     * Draw boundary
     */
    private void drawBoundary(List<MutablePoint> courseBoundary) {
        Double[]  boundary = new Double[courseBoundary.size() * 2];

        for(int i=0;i<courseBoundary.size();i++){
            boundary[i*2]=courseBoundary.get(i).getXValue();
            boundary[i*2+1]=courseBoundary.get(i).getYValue();
        }
        if(boundaryPolygon==null){

            createBoundary(boundary);
        }
        ShapeDraw.movePolygon(boundaryPolygon,boundary);
    }


    /**
     * Draw the background as the map and relocate it to the screen bounds
     */
    private void drawBackgroundImage() {
        try {
            mapEngine.executeScript(String.format("setZoom(%d);", dataSource.getMapZoomLevel()));
            List<Double> bounds = dataSource.getGPSbounds();
            mapEngine.executeScript(String.format("relocate(%.9f,%.9f,%.9f,%.9f);", bounds.get(0), bounds.get(1), bounds.get(2), bounds.get(3)));
            mapEngine.executeScript(String.format("shift(%.2f);", dataSource.getShiftDistance()));
        } catch (JSException e) {
            //e.printStackTrace();
        }
    }


    /**
     * Draw or move a boat model for a competitor
     *
     * @param boat Competitor a competing boat
     */
    private void drawBoat(Competitor boat) {
        Integer sourceId = boat.getSourceID();

        MutablePoint point = getRelativePosition(boat);
        Boolean player = sourceId == dataSource.getSourceID();
        BoatModel boatModel = boatModels.get(sourceId);
        if (boatModel == null) {
            boatModel = new BoatModel(boat.getBoatType(), player);
            this.raceViewPane.getChildren().add(boatModel);
            this.boatModels.put(sourceId, boatModel);
        }
        if (boat.getStatus() == DSQ) {
            boatModel.die();
            boatModel.update(point, 0);
        } else {
            boatModel.update(point, boat.getCurrentHeading());
        }
    }


    /**
     * Draw a boats wake
     *
     * @param boat             Competitor, the boat
     * @param boatLength       double, the length of the boat
     * @param startWakeOffset  double, the offset of the wake
     * @param wakeWidthFactor  double, the width scale
     * @param wakeLengthFactor double, the length scale
     */
    private void drawWake(Competitor boat, double boatLength, double startWakeOffset, double wakeWidthFactor, double wakeLengthFactor) {
        MutablePoint point = getRelativePosition(boat);

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
     *
     * @param boat Competitor
     */
    private void drawTrack(Competitor boat) {
        this.track.addDot(boat.getPosition(), boat.getColor());
    }


    /**
     * Checks if a mark in within the screen bounds in view
     *
     * @param markLocation mark coordinates
     * @return Boolean, is the mark in view
     */
    private boolean ismarkInView(MutablePoint markLocation) {
        Double width = raceViewPane.getWidth();
        Double height = raceViewPane.getHeight();
        if (markLocation.getXValue() > 0 && markLocation.getXValue() < width && markLocation.getYValue() > 0 && markLocation.getYValue() < height) {
            return true;
        }
        return false;
    }

    /**
     * Update the position of the guide arrow
     */
    private void drawGuidingArrow() {
        Map<Integer, List<Integer>> indexMap = dataSource.getIndexToSourceIdCourseFeatures();
        Map<Integer, CourseFeature> featureMap = dataSource.getCourseFeatureMap();

        Competitor boat = dataSource.getCompetitor();
        int currentIndex = boat.getCurrentLegIndex();
        List<MutablePoint> nextMarkLocations = RaceCalculator.getMarkCentres(currentIndex + 1, indexMap, this.currentFeaturePositions);



        if (nextMarkLocations == null || nextMarkLocations.size() == 0) { //end of race
            this.raceViewPane.getChildren().remove(guideArrow);
            this.raceViewPane.getChildren().remove(curvedArrowAnticlockwise);
            this.raceViewPane.getChildren().remove(curvedArrowClockwise);
            return;
        }

        List<Integer> sourceIds = indexMap.get(currentIndex + 1);

        double scale = nodeSizeFunc(dataSource.getZoomLevel());

        if (!isZoom()) {
            curvedArrowClockwise.hide();
            curvedArrowAnticlockwise.hide();
            guideArrow.hide();
            return;
        }
        if (ismarkInView(nextMarkLocations.get(0)) || (nextMarkLocations.size() > 1 && ismarkInView(nextMarkLocations.get(1)))) {
            guideArrow.hide();
            String direction = dataSource.getMarkSourceIdToRoundingDirection().get(sourceIds.get(0));
            if (direction.equals("CCW")) {
                curvedArrowAnticlockwise.show();
                curvedArrowAnticlockwise.updateArrow(nextMarkLocations.get(0).getXValue(), nextMarkLocations.get(0).getYValue(), scale);
            } else {
                curvedArrowClockwise.show();
                curvedArrowClockwise.updateArrow(nextMarkLocations.get(0).getXValue(), nextMarkLocations.get(0).getYValue(), scale);
            }
            if (nextMarkLocations.size() > 1) {
                String direction2 = dataSource.getMarkSourceIdToRoundingDirection().get(sourceIds.get(1));
                if (direction2.equals("CCW")) {
                    curvedArrowAnticlockwise.show();
                    curvedArrowAnticlockwise.updateArrow(nextMarkLocations.get(1).getXValue(), nextMarkLocations.get(1).getYValue(), scale);
                } else {
                    curvedArrowClockwise.show();
                    curvedArrowClockwise.updateArrow(nextMarkLocations.get(1).getXValue(), nextMarkLocations.get(1).getYValue(), scale);
                }
            }
        }
        else {
            curvedArrowAnticlockwise.hide();
            curvedArrowClockwise.hide();
            guideArrow.show();
            MutablePoint nextMarkLocation = RaceCalculator.getGateCentre(currentIndex + 1, indexMap, currentFeaturePositions);
            if (isZoom()) {
                guideArrow.updateArrowZoomed(boat, boatPositionX, boatPositionY, nextMarkLocation);
            } else {
                guideArrow.updateArrow(RaceCalculator.getGateCentre(currentIndex, indexMap, featureMap), nextMarkLocation);
            }
        }
    }


    /**
     * Draw annotations and move with boat positions
     *
     * @param boat competitor's boat
     */
    private void drawAnnotations(Competitor boat) {

        if (boat.getSourceID() == dataSource.getSourceID()) return; //don't draw annotations for the player
        Annotation annotation = annotations.get(boat.getSourceID());
        if (annotation == null) {
            annotation = new Annotation(boat);
            this.annotations.put(boat.getSourceID(), annotation);
            this.raceViewPane.getChildren().add(annotation);
        }
        annotation.update(boat, getRelativePosition(boat), isZoom());
    }


    /**
     * draws collisions at the location passed in
     *
     * @param centerX the x coordinate of the collision
     * @param centerY the y coordinate of the collision
     */
    private void drawCollision(double centerX, double centerY) {
        int radius = 20;
        if (isZoom()) radius *= 2;
        CollisionRipple ripple = new CollisionRipple(centerX, centerY, radius);
        raceViewPane.getChildren().add(ripple);
        ripple.animate().setOnFinished(event -> raceViewPane.getChildren().remove(ripple));
    }


    /**
     * Draw crew members in the water
     */
    private void drawFallenCrew() {

        Map<Integer, CrewLocation> crewLocation = dataSource.getCrewLocations();

        //remove entries
        Set<Integer> removedLocation = new HashSet<>(fallenCrews.keySet());
        removedLocation.removeAll(crewLocation.keySet());
        for (int sourceId : removedLocation) {
            raceViewPane.getChildren().remove(fallenCrews.get(sourceId));
            fallenCrews.remove(sourceId);
        }

        for (int sourceID : new ArrayList<>(crewLocation.keySet())) {
            Random randomGenerator = new Random();
            if (!fallenCrews.containsKey(sourceID)) {
                FallenCrew crew = new FallenCrew(crewImages[randomGenerator.nextInt(crewImages.length)], nodeSizeFunc(dataSource.getZoomLevel()));
                fallenCrews.put(sourceID, crew);
                raceViewPane.getChildren().add(crew);
            }
            if (crewLocation.get(sourceID).died())
                fallenCrews.get(sourceID).die(bloodImages[randomGenerator.nextInt(bloodImages.length)]); //show blood animation
            fallenCrews.get(sourceID).update(isZoom(), crewLocation.get(sourceID), currentPosition17, raceViewCanvas.getWidth(), raceViewCanvas.getHeight());
            if (fallenCrews.get(sourceID).getDead()) crewLocation.remove(sourceID); //remove when animation finishes
        }
    }


    /**
     * Draw powerups
     */
    private void drawPowerUps() {
        Map<Integer, PowerUp> receivedPowerUps = dataSource.getPowerUps();

        for (PowerUp receivedPowerUp : receivedPowerUps.values()) {
            int sourceId = receivedPowerUp.getId();
            if (!powerUps.containsKey(sourceId)) {
                PowerUpModel powerUpModel = new PowerUpModel(receivedPowerUp, isZoom(), nodeSizeFunc(dataSource.getZoomLevel()));
                powerUps.put(sourceId, powerUpModel);
                raceViewPane.getChildren().add(powerUpModel);
            }

            powerUps.get(sourceId).update(isZoom(), receivedPowerUp, currentPosition17, raceViewCanvas.getWidth(), raceViewCanvas.getHeight());


            Long timeout = receivedPowerUp.getTimeout();

            if (System.currentTimeMillis() > timeout || receivedPowerUp.isTaken()) {
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
    private void drawSharks() {
        Map<Integer, Shark> sharkLocation = dataSource.getSharkLocations();
        Shark shark = sharkLocation.get(0);
        sharkModel.update(isZoom(), shark, currentPosition17, raceViewPane.getWidth(), raceViewPane.getHeight(), shark.getHeading());
        sharkModel.toFront();
    }

    /**
     * Draw whirlpools in the race
     */
    private void drawWhirlpools() {
        Map<Integer, Whirlpool> whirlpoolsLocation = dataSource.getWhirlpools();
        Set<Integer> removedLocation = new HashSet<>(whirlpools.keySet());
        removedLocation.removeAll(whirlpoolsLocation.keySet());

        for (int sourceId : removedLocation) {
            raceViewPane.getChildren().remove(whirlpools.get(sourceId));
            whirlpools.remove(sourceId);
        }

        for (int id : whirlpoolsLocation.keySet()) {
            Whirlpool whirlpool = whirlpoolsLocation.get(id);
            if (!whirlpools.containsKey(id)) {
                WhirlpoolModel model = new WhirlpoolModel(new Image("/images/whirlpool.png"), isZoom(), nodeSizeFunc(dataSource.getZoomLevel()));
                model.update(whirlpool.getPosition());
                model.animateSpawn();
                whirlpools.put(id, model);
                raceViewPane.getChildren().add(model);
            }
            whirlpools.get(id).spin();
            whirlpools.get(id).update(isZoom(), whirlpoolsLocation.get(id), currentPosition17, raceViewPane.getWidth(), raceViewPane.getHeight());


        }
    }


    //================================================================================================================
    // VIEW SCALING
    //================================================================================================================

    /**
     * adds scaling to all shapes in the scene
     *
     * @param scale size scale factor
     */
    private void setScale(double scale) {
        for (BoatModel model : boatModels.values()) {
            model.setScaleX(scale);
            model.setScaleY(scale);
        }
        for (MarkModel model : markModels.values()) {
            model.setScaleX(scale);
            model.setScaleY(scale);
        }

        for (PowerUpModel powerUpModel : powerUps.values()) {
            powerUpModel.setPreserveRatio(true);
            powerUpModel.setFitWidth(scale * PowerUpModel.baseSize);

        }

        for (FallenCrew fallenCrew : fallenCrews.values()) {
            fallenCrew.setPreserveRatio(true);
            fallenCrew.setFitWidth(scale * FallenCrew.baseSize);

        }

        for (WhirlpoolModel model : whirlpools.values()) {
            model.setPreserveRatio(true);
            model.setFitWidth(scale * WhirlpoolModel.baseSize);
        }
        for (DecorationModel item : decorations.values()) {
            item.setScaleY(scale);
            item.setScaleX(scale);
        }
        sharkModel.setScaleX(scale);
        sharkModel.setScaleY(scale);
    }


    /**
     * sets the current boat position of the current boat controlled by visualizer
     */
    private void setBoatLocation() {

        if (dataSource.isSpectating()) {
            currentPosition17 = new MutablePoint(raceViewCanvas.getWidth() / 2, raceViewCanvas.getHeight() / 2);
            return;
        }

        Competitor boat = dataSource.getCompetitor();
        currentPosition17 = boat.getPosition17();
        if (isZoom()) {
            boatPositionX = raceViewCanvas.getWidth() / 2;
            boatPositionY = raceViewCanvas.getHeight() / 2;
        } else {
            boatPositionX = boat.getPosition().getXValue();
            boatPositionY = boat.getPosition().getYValue();
        }
    }


    /**
     * returns the position of boat relative to the current boat, assume zoomed in
     *
     * @param boat location of the boat to be calculated
     * @return the relative position
     */
    private MutablePoint getZoomedBoatLocation(Competitor boat) {
        return boat.getPosition17().shift(-currentPosition17.getXValue() + raceViewCanvas.getWidth() / 2, -currentPosition17.getYValue() + raceViewCanvas.getHeight() / 2);
    }


    /**
     * sets the relative position of other nodes compared to the visualizers boat
     *
     * @param boat the boat to be calculated
     * @return point of the boat compared to visualizers boat
     */
    private MutablePoint getRelativePosition(Competitor boat) {
        Integer sourceId = boat.getSourceID();
        double pointX;
        double pointY;

        if (sourceId == dataSource.getSourceID()) {
            pointX = boatPositionX;
            pointY = boatPositionY;
        } else {
            if (isZoom()) {
                MutablePoint point = getZoomedBoatLocation(boat);
                pointX = point.getXValue();
                pointY = point.getYValue();

            } else {
                pointX = boat.getPosition().getXValue();
                pointY = boat.getPosition().getYValue();

            }
        }
        return new MutablePoint(pointX, pointY);
    }


    //================================================================================================================
    // MAIN
    //================================================================================================================


    /**
     * Draws the race. This includes the boat, wakes, track and annotations.
     */
    private void updateRace() {
        updateCourse();
        //needs to redraw if zoomed in
        double width = 2;
        double length = 15;
        //not really the boat length but the offset of the wake from the y axis
        double boatLength = 10;
        double startWakeOffset = 3;
        double wakeWidthFactor = 0.2;
        double wakeLengthFactor = 2;

        if (isZoom()) {
            double multiplier = nodeSizeFunc(dataSource.getZoomLevel());
            width *= multiplier;
            length *= multiplier;
            boatLength *= multiplier;
            startWakeOffset *= multiplier;
            wakeLengthFactor *= multiplier;
        }
        for (Competitor boat : dataSource.getCompetitorsPosition()) {

            if (counter % 70 == 0) {
                drawTrack(boat);
            }
            this.drawWake(boat, boatLength, startWakeOffset, wakeWidthFactor, wakeLengthFactor);
            this.drawBoat(boat);
            this.drawHealthBar(boat);
            this.drawAnnotations(boat);
        }
        counter++;
        if (dataSource.isSpectating()){
            guideArrow.setVisible(false);
            curvedArrowAnticlockwise.setVisible(false);
            curvedArrowClockwise.setVisible(false);
            zoomButton.setVisible(false);
            return;
        }
        this.drawSail(width, length, dataSource.getCompetitor());
        this.drawGuidingArrow();




    }

    /**
     * Refreshes the contents of the display to match the datasource
     */
    void refresh() {
        checkRaceFinished();
        drawFallenCrew();
        drawPowerUps();
        drawSharks();
        drawWhirlpools();
        drawThemeDecorations();
        setBoatLocation();
        updateRace();
        checkCollision();
    }

    boolean isLoaded() {
        return isLoaded;
    }



}