package controllers;

import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import models.Competitor;
import utilities.DataSource;

import javax.xml.crypto.Data;
import java.util.ArrayList;

import java.util.List;

/**
 * Created by abu59
 * The controller for the sparklines chart
 */

public class SparklinesController {

    @FXML private LineChart<String, Double> sparkChart;

    private List<Competitor> competitors;
    private ArrayList<XYChart.Series> seriesList;
    private DataSource dataSource;


    void setCompetitors(DataSource dataSource){
        this.dataSource = dataSource;
        this.competitors = dataSource.getCompetitorsPosition();

        seriesList = new ArrayList<>();
        System.out.println("init: " + seriesList);

        XYChart.Series<String, Double> series = new XYChart.Series<>();

        //series.getData().add(new XYChart.Data<>("A", 15.1));
        System.out.println(competitors.size());
        //series.getData().add(new XYChart.Data(2,2));


        for(Competitor boat: competitors) {
            series.getData().add(new XYChart.Data<>(boat.getAbbreName(),2.2));

        }

        sparkChart.getData().add(series);
        /*
        for(Competitor boat: competitors){
            System.out.println("setComp: " + seriesList);
            seriesList.add(series);
            System.out.println(competitors.indexOf(boat));
            //seriesList.get(competitors.indexOf(boat)).getNode().setStyle("-fx-stroke: " + boat.getColor());
            seriesList.get(competitors.indexOf(boat)).getData().add(new XYChart.Data(seriesList.size() + 1, 3)); //replace y with team position
            sparkChart.getData().add(seriesList.get(competitors.indexOf(boat)));
        }

        */

    }


    public void refresh(){

        for(Competitor boat: competitors){
            //seriesList.get(competitors.indexOf(boat)).getNode().setStyle("-fx-stroke: " + boat.getColor());
            seriesList.get(competitors.indexOf(boat)).getData().add(new XYChart.Data(seriesList.size() + 1, 3)); //replace y with team position
            sparkChart.getData().add(seriesList.get(competitors.indexOf(boat)));
        }
    }


}
