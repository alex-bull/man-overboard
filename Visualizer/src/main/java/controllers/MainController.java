package controllers;

import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.scene.control.SplitPane;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
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
    @FXML private TimerController timerController;
    @FXML private SparklinesController sparklinesController;
    @FXML private GridPane loadingPane;
    private DataSource dataSource;
    private BinaryPackager binaryPackager;

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
                    this.dataSource.send(this.binaryPackager.packageBoatAction(Keys.SAILS.getValue(), dataSource.getSourceID()));
                    break;
                case ENTER:
                    this.dataSource.send(this.binaryPackager.packageBoatAction(Keys.TACK.getValue(), dataSource.getSourceID()));
                    break;
                case Q:
                    if(raceViewController.isZoom()) {
                        raceViewController.zoomOut();
                        if (!tableController.isVisible()) {
                            tableController.makeVisible();
                            sparklinesController.makeVisible();
                        }
                    }
                    else{
                        raceViewController.zoomIn();
                    }
                    break;
                case BACK_QUOTE:
                    if (raceViewController.isZoom() && tableController.isVisible()){
                        tableController.makeInvisible();
                        sparklinesController.makeInvisible();
                    }
                    else if (raceViewController.isZoom()) {
                        tableController.makeVisible();
                        sparklinesController.makeVisible();
                    }
                    break;

                case UP:
                    dataSource.changeScaling(1);
                    raceViewController.zoomIn();
                    break;
                case DOWN:
                    dataSource.changeScaling(-1);
                    raceViewController.zoomIn();
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
        timerController.begin(dataSource);
        tableController.addObserver(raceViewController);
        sparklinesController.setCompetitors(dataSource, width);
        this.binaryPackager = new BinaryPackager();

        AnimationTimer timer = new AnimationTimer() {

            @Override
            public void handle(long now) {
                if (raceViewController.isLoaded()) {
                    raceViewController.refresh();
                    tableController.refresh(dataSource);
                    windController.refresh(dataSource.getWindDirection(), dataSource.getWindSpeed());
                    sparklinesController.refresh();
                    loadingPane.toBack();
                }
                else {
                    loadingPane.toFront();
                }
            }
        };

        timer.start();
    }
}
