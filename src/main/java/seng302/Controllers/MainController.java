package seng302.Controllers;

import javafx.fxml.FXML;
import javafx.scene.control.SplitPane;
import seng302.Model.Race;

/**
 * Created by psu43 on 22/03/17.
 * The parent controller for the view
 */
public class MainController {


    private Race race;

    @FXML private TableController tableController;

    @FXML private RaceViewController raceViewController;

    @FXML private SplitPane splitPane;

    /**
     * Initialiser for the raceViewController
     */
    @FXML
    void initialize() {

    }


    /**
     * Sets the race
     * @param race Race a group of competitors across multiple races on a course
     */
    public void setRace(Race race, double width, double height, int numBoats) {
        this.race=race;
        this.race.setRaceEventHandler(tableController);
        this.tableController.setNumBoats(numBoats);
        raceViewController.begin(race, width, height);

    }
}
