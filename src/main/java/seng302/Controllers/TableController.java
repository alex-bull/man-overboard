package seng302.Controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import seng302.Model.*;

import java.util.Collections;

/**
 * Created by msl47 on 21/03/17.
 * The controller for the table
 */

public class TableController implements RaceEventHandler {

    @FXML
    private TableView raceTable;

    @FXML
    private TableColumn positionCol;

    @FXML
    private TableColumn featureCol;

    @FXML
    private TableColumn nameCol;

    @FXML
    private TableColumn speedCol;

    private int order = 1;
    private int numBoats;
    private int finalOrder = 1;
    private ObservableList<RaceEvent> events = FXCollections.observableArrayList();


    /**
     * Initialiser for the raceViewController
     */
    @FXML
    void initialize() {

        // initialise race table
        positionCol.setCellValueFactory(new PropertyValueFactory<RaceEvent, Integer>("position"));
        nameCol.setCellValueFactory(new PropertyValueFactory<RaceEvent,String>("teamName"));
        featureCol.setCellValueFactory(new PropertyValueFactory<RaceEvent,String>("featureName"));
        speedCol.setCellValueFactory(new PropertyValueFactory<RaceEvent,Integer>("speed"));
        raceTable.setItems(events);

    }


    void setNumBoats(int numBoats) {
        this.numBoats = numBoats;
    }

    /**
     * Adds an event to table, also removes redundant event and sort them based on last feature passed by boat.
     * Can change compareTo in race event to make it compare time instead.
     * @param event RaceEvent an event in the race
     */
   public void handleRaceEvent(RaceEvent event) {

       // loop through all events in the table and remove events with the same team name as this event
       for (int i = 0; i < events.size(); i++) {
           if (events.get(i).getTeamName().equals(event.getTeamName())) {
               events.remove(i);
           }
       }

       // add this event to the race table
       events.add(event);
       Collections.sort(events);  //events are sorted by index of the features

       for (RaceEvent e: events) {
           e.setPosition(events.indexOf(e) + 1);
       }

   }

}
