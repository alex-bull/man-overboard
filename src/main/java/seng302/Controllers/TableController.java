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

    ObservableList<RaceEvent> events= FXCollections.observableArrayList();

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
    private int finalOrder = 1;


    /**
     * Initialiser for the raceViewController
     */
    @FXML
    void initialize() {
        positionCol.setCellValueFactory(new PropertyValueFactory<RaceEvent, Integer>("position"));
        nameCol.setCellValueFactory(new PropertyValueFactory<RaceEvent,String>("teamName"));
        featureCol.setCellValueFactory(new PropertyValueFactory<RaceEvent,String>("endPointName"));
        speedCol.setCellValueFactory(new PropertyValueFactory<RaceEvent,Integer>("speed"));
        raceTable.setItems(events);

    }



    /**
     * Adds an event to table, also removes redundant event and sort them based on last feature passed by boat
     * Can change compareTo in race event to make it compare time instead
     * @param event RaceEvent an event in the race
     */
   public void handleRaceEvent(RaceEvent event) {
       for (int i = 0; i < events.size(); i++) {
           if (events.get(i).getTeamName().equals(event.getTeamName())) {
               events.remove(i);
           }
       }

       events.add(event);
       Collections.sort(events);  //events are sorted by index of the features
       if (order == 7) {
           order = 1;
       }
       if (event.getEndPointName().equals("Finishline")) {
           event.setPosition(finalOrder);
           finalOrder++;
       }
       else {
           event.setPosition(order);
           order++;
       }

   }

}
