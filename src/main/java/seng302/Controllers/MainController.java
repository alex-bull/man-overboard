package seng302.Controllers;

import javafx.fxml.FXML;
import javafx.scene.control.SplitPane;
import seng302.Model.DataReceiver;
import seng302.Model.Race;

/**
 * Created by psu43 on 22/03/17.
 * The parent controller for the view
 */
public class MainController {


    private Race race;

    @FXML
    private TableController tableController;

    @FXML
    private RaceViewController raceViewController;

    @FXML
    private SplitPane splitPane;


    /**
     * Sets the race
     */
    public void setRace(DataReceiver dataReceiver, double width, double height, int numBoats) {
//        this.race.setRaceEventHandler(tableController);
        this.tableController.setNumBoats(numBoats);
        raceViewController.begin(width, height, dataReceiver);

    }
}
