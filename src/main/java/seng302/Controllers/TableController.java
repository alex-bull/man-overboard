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
 */
public class TableController {
    private Race race;
    ObservableList<RaceEvent> events= FXCollections.observableArrayList();

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
        nameCol.setCellValueFactory(new PropertyValueFactory<RaceEvent,String>("teamName"));
        colorCol.setCellValueFactory(new PropertyValueFactory<RaceEvent,String>("color"));
        timeCol.setCellValueFactory(new PropertyValueFactory<RaceEvent,Long>("time"));
        positionCol.setCellValueFactory(new PropertyValueFactory<RaceEvent,String>("endPointName"));
        speedCol.setCellValueFactory(new PropertyValueFactory<RaceEvent,Integer>("speed"));

    }

   public void addToTable(RaceEvent event) {
       for (int i = 0; i < events.size(); i++) {
           if (events.get(i).getTeamName().equals(event.getTeamName())) {
               events.remove(i);
           }
       }
        events.add(event);
       Collections.sort(events);


           raceTable.setItems(events);
       }

}
