package controllers;

import Animations.SoundPlayer;
import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.scene.control.Slider;
import javafx.scene.control.SplitPane;
import javafx.scene.input.*;
import javafx.scene.layout.GridPane;
import mockDatafeed.Keys;
import parsers.RaceStatusEnum;
import utilities.DataSource;
import utility.BinaryPackager;


/**
 * Created by psu43 on 22/03/17.
 * The parent controller for the view
 */
public class MainController {

    @FXML private TableController tableController;
    @FXML private RaceViewController raceViewController;
    @FXML private SplitPane splitPane;
    @FXML private WindController windController;
    @FXML private PlayerController playerController;
    @FXML private GridPane loadingPane;
    @FXML private Slider sailSlider;
    private DataSource dataSource;
    private BinaryPackager binaryPackager;
    private SoundPlayer soundPlayer;

    /**
     * updates the slider and sends corresponding packet
     */
    @FXML public void updateSlider(){
        if(sailSlider.getValue()<0.5){
            this.dataSource.send(this.binaryPackager.packageBoatAction(Keys.SAILSOUT.getValue(), dataSource.getSourceID()));
        }
        else{
            this.dataSource.send(this.binaryPackager.packageBoatAction(Keys.SAILSIN.getValue(), dataSource.getSourceID()));
        }
    }

    /**
     * Handle control key events
     * @param event KeyEvent
     */
    @FXML public void keyPressed(KeyEvent event) {

            switch (event.getCode()) {
                case W:
                    this.dataSource.send(this.binaryPackager.packageBoatAction(Keys.UP.getValue(), dataSource.getSourceID()));
                    break;
                case S:
                    this.dataSource.send(this.binaryPackager.packageBoatAction(Keys.DOWN.getValue(), dataSource.getSourceID()));
                    break;
                case SPACE:
                    this.dataSource.send(this.binaryPackager.packageBoatAction(Keys.VMG.getValue(), dataSource.getSourceID()));
                    break;
                case SHIFT:
                    this.dataSource.send(this.binaryPackager.packageBoatAction(Keys.SWITCHSAILS.getValue(), dataSource.getSourceID()));
                    sailSlider.setValue(this.dataSource.getCompetitor().getSailValue());
                    System.out.println(this.dataSource.getCompetitor().getSailValue());
                    break;
                case ENTER:
                    this.dataSource.send(this.binaryPackager.packageBoatAction(Keys.TACK.getValue(), dataSource.getSourceID()));
                    break;
                case Q:
                    raceViewController.toggleZoom();
//                    if(raceViewController.isZoom()) {
//                        raceViewController.zoomOut();
//                        if (!tableController.isVisible()) {
//                            tableController.makeVisible();
//                        }
//                    }
//                    else{
//                        raceViewController.zoomIn();
//                    }
                    break;
                case BACK_QUOTE:
                    if (raceViewController.isZoom() && tableController.isVisible()){
                        tableController.makeInvisible();
                    }
                    else if (raceViewController.isZoom()) {
                        tableController.makeVisible();
                    }
                    break;

                case A:
                    if(dataSource.getZoomLevel() < 18 && raceViewController.isZoom()) {
                        dataSource.changeScaling(1);
                        raceViewController.zoomIn();
                    }
                    break;
                case D:
                    if(dataSource.getZoomLevel() > 12 && raceViewController.isZoom()) {
                        dataSource.changeScaling(-1);
                        raceViewController.zoomIn();
                    }
                    break;
            }
    }


    /**
     * Begins the race loop which updates child controllers at ~60fps
     * @param dataSource DataSource the data to display
     * @param width double the screen width
     * @param height double the screen height
     */
    void beginRace(DataSource dataSource, double width, double height) {
        this.dataSource = dataSource;
        raceViewController.begin(width, height, dataSource);
        tableController.addObserver(raceViewController);
        playerController.setuo(dataSource);
        this.binaryPackager = new BinaryPackager();



        AnimationTimer timer = new AnimationTimer() {

            @Override
            public void handle(long now) {
                dataSource.update();
                if (raceViewController.isLoaded()) {
                    raceViewController.refresh();
                    tableController.refresh(dataSource);
                    windController.refresh(dataSource.getWindDirection(), dataSource.getWindSpeed());
                    playerController.refresh();
                    sailSlider.toFront();
                    loadingPane.toBack();
                }
                else {
                    loadingPane.toFront();
                }
            }
        };
        timer.start();
        soundPlayer=new SoundPlayer();
        soundPlayer.playMP3("sounds/bensound-epic.mp3");
    }
}
