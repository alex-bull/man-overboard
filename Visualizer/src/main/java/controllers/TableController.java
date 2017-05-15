package controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import models.Competitor;
import models.RaceEvent;
import utilities.DataSource;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Created by msl47 on 21/03/17.
 * The controller for the table
 */

public class TableController implements Initializable {

    @FXML private TableView<RaceEvent> raceTable;
    @FXML private TableColumn<RaceEvent, Integer> positionCol;
    @FXML private TableColumn<RaceEvent, String> featureCol;
    @FXML private TableColumn<RaceEvent, String> nameCol;
    @FXML private TableColumn<RaceEvent, Integer> speedCol;

    private ObservableList<RaceEvent> events = FXCollections.observableArrayList();


    /**
     * Initialiser for the TableViewController
     */
    @FXML
    public void initialize(URL location, ResourceBundle resources) {

        // initialise race table
        positionCol.setCellValueFactory(new PropertyValueFactory<>("position"));
        nameCol.setCellValueFactory(new PropertyValueFactory<>("teamName"));
        featureCol.setCellValueFactory(new PropertyValueFactory<>("featureName"));
        speedCol.setCellValueFactory(new PropertyValueFactory<>("speed"));
        raceTable.setItems(events);

    }

    /**
     * Called when the race display updates
     * @param dataSource DataSource the latest race data
     */
    void refresh(DataSource dataSource) {
        this.setTable(dataSource.getCompetitorsPosition());
    }

    /**
     * Sets the data in the table
     * @param competitors List the competitors in the race
     */
    private void setTable(List<Competitor> competitors) {
        events.clear();
        for (int i = 0; i < competitors.size(); i++) {
            String teamName = competitors.get(i).getTeamName();
            Double speed = competitors.get(i).getVelocity();
            String featureName = competitors.get(i).getLastMarkPassed();
            RaceEvent raceEvent = new RaceEvent(teamName, speed, featureName, i + 1);
            events.add(raceEvent);
        }
    }


}
