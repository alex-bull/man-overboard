package controllers;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.paint.Color;
import models.Competitor;
import utilities.DataSource;


import java.util.*;
import java.util.List;

/**
 * Created by abu59
 * The controller for the sparklines chart
 */

public class SparklinesController {

    @FXML private LineChart<String, Double> sparkChart;

    private Map<Integer, XYChart.Series<String, Double>> seriesMap = new HashMap<>();
    private DataSource dataSource;
    private long previousTime = 0;

    /**
     * Sets the competitors on the sparkline chart
     * @param dataSource DataSource the data to display
     */
    void setCompetitors(DataSource dataSource){
        this.dataSource = dataSource;
        List<Competitor> competitors = dataSource.getCompetitorsPosition();
        for(Competitor boat: competitors){
            XYChart.Series<String, Double> series = new XYChart.Series<>();
            seriesMap.put(boat.getSourceID(), series);
            series.getData().add(new XYChart.Data<>("-", (double) -(competitors.size() + 1))); //replace y with team position
            sparkChart.getData().add(series);
            series.setName(boat.getAbbreName());

            series.getNode().setStyle("-fx-stroke: #" + boat.getColor().toString().substring(2)); // convert to hex string
        }

    }

    /**
     * Refreshes the spark line with the new received data
     */
    void refresh(){
        List<Competitor> comps = new ArrayList<>(dataSource.getCompetitorsPosition());
        long expectedStartTime = dataSource.getExpectedStartTime();
        long firstMessageTime = dataSource.getMessageTime();
        long raceTime = firstMessageTime - expectedStartTime;
        long waitTime = 45000;
        comps.sort((o1, o2) -> (o1.getLegIndex() < o2.getLegIndex()) ? 1 : ((o1.getLegIndex() == o2.getLegIndex()) ? 0 : -1));
        for (int i = 0; i < comps.size(); i++) {
            XYChart.Series<String, Double> series = seriesMap.get(comps.get(i).getSourceID());
            int pos = i + 1;
            // scaling to 7 points on spark line
            if(series.getData().size() < 7) {
                if(raceTime - waitTime > previousTime){
                    series.getData().add(new XYChart.Data<>(Long.toString(raceTime), (double) -pos));
                    if(i == comps.size() - 1) {
                        previousTime = raceTime;
                    }
                }
            }
            else {
                if(raceTime - waitTime > previousTime){
                    series.getData().remove(0, 1);
                    series.getData().add(new XYChart.Data<>(Long.toString(raceTime), (double) -pos));
                    if(i == comps.size() - 1) {
                        previousTime = raceTime;
                    }
                }
            }

            for (int index = 0; index < series.getData().size(); index++) {
                XYChart.Data dataPoint = series.getData().get(index);
                Node lineSymbol = dataPoint.getNode().lookup(".chart-line-symbol");
                lineSymbol.setStyle("-fx-background-color: #" + comps.get(i).getColor().toString().substring(2) + ", " + Color.LIGHTBLUE.toString().substring(2));
            }
        }

    }


}
