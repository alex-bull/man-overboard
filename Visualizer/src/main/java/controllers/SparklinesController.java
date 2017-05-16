package controllers;

import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import models.Competitor;
import utilities.DataSource;
import utilities.Interpreter;

import javax.xml.crypto.Data;
import java.util.ArrayList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by abu59
 * The controller for the sparklines chart
 */

public class SparklinesController {

    @FXML private LineChart<String, Double> sparkChart;

    private List<Competitor> competitors;
    private ArrayList<XYChart.Series<String, Double>> seriesList;
    private DataSource dataSource;
    private Map<String, XYChart.Series> seriesMap = new HashMap<>();


    void setCompetitors(DataSource dataSource){
        this.dataSource = dataSource;
        this.competitors = dataSource.getCompetitorsPosition();

        seriesList = new ArrayList<>();
        System.out.println("init: " + seriesList);



        //series.getData().add(new XYChart.Data<>("A", 15.1));
        System.out.println(competitors.size());
        //series.getData().add(new XYChart.Data(2,2));
//        XYChart.Series<String, Double> series = new XYChart.Series<>();
//        series.setName("Test");
//        series.getData().add(new XYChart.Data<>("Start",0.0));
//        for(Competitor boat: competitors) {
//            series.getData().add(new XYChart.Data<>("Start",0.0));
//
//        }

//        sparkChart.getData().add(series);

        for(Competitor boat: competitors){

            System.out.println("setComp: " + seriesList);
            XYChart.Series<String, Double> series = new XYChart.Series<>();
            seriesList.add(series);
            System.out.println(competitors.indexOf(boat));

            XYChart.Series<String, Double> currentSeries = seriesList.get(competitors.indexOf(boat));
//            currentSeries.getNode().setStyle("-fx-stroke: blueviolet");
            currentSeries.getData().add(new XYChart.Data<>(Integer.toString(seriesList.size() + 1), 3.0)); //replace y with team position
            sparkChart.getData().add(currentSeries);
            currentSeries.setName(boat.getAbbreName());
            currentSeries.getNode().setStyle("-fx-stroke: " + boat.getColor());
//

        }



    }


    void refresh(){
//        for(Competitor boat: competitors) {
//            series.getData().add(new XYChart.Data<>(boat.getAbbreName(),2.2));
//
//        }
//        Competitor boat = this.competitors.get(0);
//        series.getData().add(new XYChart.Data<>(boat.getAbbreName(),2.2));
//        System.out.println(series.getData().size());

        /*
        for(Competitor boat: competitors){
            //seriesList.get(competitors.indexOf(boat)).getNode().setStyle("-fx-stroke: " + boat.getColor());
            seriesList.get(competitors.indexOf(boat)).getData().add(new XYChart.Data(seriesList.size() + 1, 3)); //replace y with team position
            sparkChart.getData().add(seriesList.get(competitors.indexOf(boat)));
        }
        */
    }


}
