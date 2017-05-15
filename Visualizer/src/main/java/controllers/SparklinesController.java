package controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import model.Competitor;
import model.RaceEvent;
import utilities.DataReceiver;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Created by abu59
 * The controller for the sparklines chart
 */

public class SparklinesController implements Initializable {

    @FXML
    private LineChart sparklinesChart;

    private List<Competitor> competitors;

    private List<XYChart.Series> seriesList;

    public void setCompetitors(List<Competitor> competitors){

        this.competitors = competitors;

        for(Competitor boat: competitors){
            System.out.println(seriesList);
            seriesList.add(new XYChart.Series());
            seriesList.get(competitors.indexOf(boat)).getNode().setStyle("-fx-stroke: " + boat.getColor());
            seriesList.get(competitors.indexOf(boat)).getData().add(new XYChart.Data(seriesList.size() + 1, 3)); //replace y with team position
            sparklinesChart.getData().add(seriesList.get(competitors.indexOf(boat)));
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        seriesList=new ArrayList<>();
        System.out.println(seriesList);
    }

    public void setSparklinesChart(){

        for(Competitor boat: competitors){
            //seriesList.get(competitors.indexOf(boat)).getNode().setStyle("-fx-stroke: " + boat.getColor());
            seriesList.get(competitors.indexOf(boat)).getData().add(new XYChart.Data(seriesList.size() + 1, 3)); //replace y with team position
            sparklinesChart.getData().add(seriesList.get(competitors.indexOf(boat)));
        }
    }


}
