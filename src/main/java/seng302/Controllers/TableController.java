package seng302.Controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import seng302.Model.*;
import seng302.Parsers.CompoundMarkData;
import seng302.Parsers.MarkRoundingData;

import javax.xml.crypto.Data;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Created by msl47 on 21/03/17.
 * The controller for the table
 */

public class TableController implements Initializable{

    @FXML private TableView raceTable;
    @FXML private TableColumn positionCol;
    @FXML private TableColumn featureCol;
    @FXML private TableColumn nameCol;
    @FXML private TableColumn speedCol;

//    private List<Competitor> competitors;
    private DataReceiver dataReceiver;
    private ObservableList<RaceEvent> events = FXCollections.observableArrayList();


    /**
     * Initialiser for the raceViewController
     */
    @FXML
    public void initialize(URL location, ResourceBundle resources) {

        // initialise race table
//        positionCol.setCellValueFactory(new PropertyValueFactory<RaceEvent, Integer>("position"));
        nameCol.setCellValueFactory(new PropertyValueFactory<RaceEvent,String>("teamName"));
//        featureCol.setCellValueFactory(new PropertyValueFactory<RaceEvent,String>("featureName"));
        speedCol.setCellValueFactory(new PropertyValueFactory<RaceEvent,Integer>("speed"));
        raceTable.setItems(events);

    }



    public void setTable(DataReceiver dataReceiver) {
        this.dataReceiver=dataReceiver;

        List<Competitor> competitors = dataReceiver.getCompetitors();
        events.clear();

        for(Competitor competitor : competitors) {
            String teamName = competitor.getTeamName();
            Double speed = competitor.getVelocity();

            RaceEvent raceEvent = new RaceEvent(teamName, speed);
            events.add(raceEvent);
        }
    }



}
