package controllers;

import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.scene.control.SplitPane;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import utilities.DataSource;
import utility.BinaryPackager;
import utility.DataSender;

import java.io.IOException;


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
    private DataSender dataSender;
    private BinaryPackager binaryPackager;


    @FXML public void keyPressed(KeyEvent event) {

        try {
            switch (event.getCode()) {
                case UP:
                    System.out.println("Upp");
                    this.dataSender.sendData(this.binaryPackager.packageBoatAction(5));
                    break;
                case DOWN:
                    System.out.println("Down");
                    this.dataSender.sendData(this.binaryPackager.packageBoatAction(6));
                    break;
            }
        }
        catch (IOException e) {
            System.out.println("Failed to send boat action message");
        }
    }


    /**
     * Begins the race loop which updates child controllers at ~60fps
     * @param dataSource DataSource the data to display
     * @param width double the screen width
     * @param height double the screen height
     */
    void beginRace(DataSender dataSender, DataSource dataSource, double width, double height) {
        raceViewController.begin(width, height, dataSource);
        timerController.begin(dataSource);
        tableController.addObserver(raceViewController);
        sparklinesController.setCompetitors(dataSource, width);
        this.dataSender = dataSender;
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
                else{
                    loadingPane.toFront();
                }

            }
        };

        timer.start();
    }
}
