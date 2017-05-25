package controllers;

import models.Competitor;
import models.RaceEvent;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import utilities.DataSource;
import java.net.URL;
import java.util.ArrayList;
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

    private TableObserver observer;
    private ObservableList<RaceEvent> events = FXCollections.observableArrayList();


    /**
     * Initialiser for the TableViewController
     */
    @FXML
    public void initialize(URL location, ResourceBundle resources) {

        // initialise race table
        positionCol.setCellValueFactory(new PropertyValueFactory<>("position"));
        nameCol.setCellValueFactory(new PropertyValueFactory<>("teamName"));
        featureCol.setCellValueFactory(new PropertyValueFactory<>("feature"));
        speedCol.setCellValueFactory(new PropertyValueFactory<>("speed"));
        raceTable.setItems(events);

        raceTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                Integer sourceId = newSelection.getBoatSourceId();
                this.observer.boatSelected(sourceId);
            }
        });
    }

    public ObservableList<RaceEvent> getEvents() {
        return events;
    }

    /**
     * Sets the observer
     * @param observer TableObserver
     */
    public void addObserver(TableObserver observer) {
        this.observer = observer;
    }


    /**
     * Called when the race display updates
     * Gets table selection and resets the data in the table.
     * @param dataSource DataSource the latest race data
     */
    void refresh(DataSource dataSource) {
        this.setTable(dataSource.getCompetitorsPosition());
    }

    /**
     * Sets the data in the table
     * @param competitors List the competitors in the race
     */
    List<Competitor> setTable(List<Competitor> competitors) {
        List<Competitor> comps = new ArrayList<>(competitors);
        comps.sort((o1, o2) -> (o1.getCurrentLegIndex() < o2.getCurrentLegIndex()) ? 1 : ((o1.getCurrentLegIndex() == o2.getCurrentLegIndex()) ? 0 : -1));

        events.clear();
        for (int i = 0; i < comps.size(); i++) {
            String teamName = comps.get(i).getTeamName();
            Double speed = comps.get(i).getVelocity();
            String featureName = comps.get(i).getLastMarkPassed();
            Integer sourceId = comps.get(i).getSourceID();
            RaceEvent raceEvent = new RaceEvent(sourceId, teamName, featureName, speed, i + 1);
            events.add(raceEvent);
        }
        return comps;
    }

    void printTable(){
        for(RaceEvent raceEvent:events){
            System.out.println(raceEvent.getTeamName());
        }
        System.out.println();
    }

}
