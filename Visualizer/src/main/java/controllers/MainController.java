package controllers;

import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.SplitPane;
import utilities.DataSource;

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


    /**
     * Begins the race loop which updates child controllers at ~60fps
     * @param dataSource DataSource the data to display
     * @param width double the screen width
     * @param height double the screen height
     */
    void beginRace(DataSource dataSource, double width, double height) {
        raceViewController.begin(width, height, dataSource);
        timerController.begin(dataSource);
        sparklinesController.setCompetitors(dataSource);

        AnimationTimer timer = new AnimationTimer() {

            @Override
            public void handle(long now) {
                raceViewController.refresh(dataSource);
                tableController.refresh(dataSource);
                windController.refresh(dataSource.getWindDirection());
                sparklinesController.refresh();

            }
        };

        timer.start();
    }
}
