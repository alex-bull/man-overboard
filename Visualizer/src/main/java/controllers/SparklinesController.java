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

    @FXML private LineChart sparkChart;

    private List<Competitor> competitors;

    private ArrayList<XYChart.Series> seriesList;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        sparkChart.isVisible();
    }


    public void setCompetitors(List<Competitor> competitors){

        seriesList = new ArrayList<>();
        System.out.println("init: " + seriesList);

        this.competitors = competitors;

        for(Competitor boat: competitors){
            System.out.println("setComp: " + seriesList);
            XYChart.Series series = new XYChart.Series();
            seriesList.add(series);
            System.out.println(competitors.indexOf(boat));
            //seriesList.get(competitors.indexOf(boat)).getNode().setStyle("-fx-stroke: " + boat.getColor());
            seriesList.get(competitors.indexOf(boat)).getData().add(new XYChart.Data(seriesList.size() + 1, 3)); //replace y with team position
            sparkChart.getData().add(seriesList.get(competitors.indexOf(boat)));
        }
    }


    public void setSparklinesChart(){

        for(Competitor boat: competitors){
            //seriesList.get(competitors.indexOf(boat)).getNode().setStyle("-fx-stroke: " + boat.getColor());
            seriesList.get(competitors.indexOf(boat)).getData().add(new XYChart.Data(seriesList.size() + 1, 3)); //replace y with team position
            sparkChart.getData().add(seriesList.get(competitors.indexOf(boat)));
        }
    }


}
