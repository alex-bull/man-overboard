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
    private DataSource dataSource;
    private Map<Integer, XYChart.Series<String, Double>> seriesMap = new HashMap<>();


    void setCompetitors(DataSource dataSource){
        this.dataSource = dataSource;
        this.competitors = dataSource.getCompetitorsPosition();
        for(Competitor boat: competitors){

            XYChart.Series<String, Double> series = new XYChart.Series<>();
            seriesMap.put(boat.getSourceID(), series);
            //XYChart.Series<String, Double> currentSeries = seriesMap.get(boat.getSourceID());
            series.getData().add(new XYChart.Data<>(Integer.toString(seriesMap.size() + 1), 3.0)); //replace y with team position
            sparkChart.getData().add(series);
            series.setName(boat.getAbbreName());

        }



    }


    void refresh(){

    }


}
