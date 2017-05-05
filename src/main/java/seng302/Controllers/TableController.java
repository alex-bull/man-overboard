package seng302.Controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import seng302.Model.Competitor;
import seng302.Model.DataReceiver;
import seng302.Model.RaceEvent;

import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Created by msl47 on 21/03/17.
 * The controller for the table
 */

public class TableController implements Initializable {

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

    //    private List<Competitor> competitors;
    private DataReceiver dataReceiver;
    private ObservableList<RaceEvent> events = FXCollections.observableArrayList();


    /**
     * Initialiser for the raceViewController
     */
    @FXML
    public void initialize(URL location, ResourceBundle resources) {

        // initialise race table
        positionCol.setCellValueFactory(new PropertyValueFactory<RaceEvent, Integer>("position"));
        nameCol.setCellValueFactory(new PropertyValueFactory<RaceEvent, String>("teamName"));
        featureCol.setCellValueFactory(new PropertyValueFactory<RaceEvent, String>("featureName"));
        speedCol.setCellValueFactory(new PropertyValueFactory<RaceEvent, Integer>("speed"));
        raceTable.setItems(events);

    }


    public void setTable(List<Competitor> competitors) {
        List<Competitor> cpy = new ArrayList<>(competitors);
        cpy.sort(new Comparator<Competitor>() {
            @Override
            public int compare(Competitor o1, Competitor o2) {
                return (o1.getLegIndex() < o2.getLegIndex()) ? 1 : ((o1.getLegIndex() == o2.getLegIndex()) ? 0 : -1);
            }
        });
        events.clear();
        for (int i = 0; i < cpy.size(); i++) {
            String teamName = cpy.get(i).getTeamName();
            Double speed = cpy.get(i).getVelocity();
            String featureName = cpy.get(i).getLastMarkPassed();
            RaceEvent raceEvent = new RaceEvent(teamName, speed, featureName, i + 1);
            events.add(raceEvent);
        }
    }


}
