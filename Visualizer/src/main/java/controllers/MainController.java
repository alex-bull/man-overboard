package controllers;

import models.Race;
import javafx.fxml.FXML;
import javafx.scene.control.SplitPane;
import utilities.DataReceiver;
import utilities.Interpreter;

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
    public void setRace(Interpreter interpreter, double width, double height, int numBoats) {
        raceViewController.setTableController(tableController);
        raceViewController.begin(width, height, interpreter);


    }
}
