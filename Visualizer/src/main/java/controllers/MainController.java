package controllers;

import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.scene.control.SplitPane;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import mockDatafeed.Keys;
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
            case UP:
               // System.out.println("Up");
                this.dataSource.send(this.binaryPackager.packageBoatAction(Keys.UP.getValue(), dataSource.getSourceID()));
                break;
            case DOWN:
               // System.out.println("Down");
                this.dataSource.send(this.binaryPackager.packageBoatAction(Keys.DOWN.getValue(), dataSource.getSourceID()));
                break;
            case SPACE:
               // System.out.println("VMG");
                this.dataSource.send(this.binaryPackager.packageBoatAction(Keys.VMG.getValue(), dataSource.getSourceID()));
                break;
            case SHIFT:
               // System.out.println("Sails");
                this.dataSource.send(this.binaryPackager.packageBoatAction(Keys.SAILS.getValue(), dataSource.getSourceID()));
                break;
            case ENTER:
               // System.out.println("Tack/Gybe");
                this.dataSource.send(this.binaryPackager.packageBoatAction(Keys.TACK.getValue(), dataSource.getSourceID()));
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
                    raceViewController.refresh(dataSource);
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
