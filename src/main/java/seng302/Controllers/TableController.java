package seng302.Controllers;

import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import seng302.Model.Race;

/**
 * Created by msl47 on 21/03/17.
 */
public class TableController {
    private Race race;

    @FXML
    private TableView raceTable;

    @FXML
    private TableColumn positionCol;

    @FXML
    private TableColumn colorCol;

    @FXML
    private TableColumn nameCol;

    @FXML
    private TableColumn timeCol;

    @FXML
    private TableColumn speedCol;

    /**
     * Initialiser for the raceViewController
     */
    @FXML
    void initialize() {

    }

    void setRace(Race race){
        this.race=race;
        //System.out.println(race.getWindDirection());
    }

}
