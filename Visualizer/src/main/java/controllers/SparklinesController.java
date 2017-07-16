package controllers;

import com.sun.javafx.charts.Legend;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
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
     * @param width double the width of the screen
     */
    void setCompetitors(DataSource dataSource, double width){
        sparkChart.setMaxWidth(width/4);
        this.dataSource = dataSource;
        sparkChart.getYAxis().setAutoRanging(true);
        List<Competitor> comps = new ArrayList<>(dataSource.getCompetitorsPosition());
        comps.sort((o1, o2) -> (o1.getCurrentLegIndex() < o2.getCurrentLegIndex()) ? 1 : ((o1.getCurrentLegIndex() == o2.getCurrentLegIndex()) ? 0 : -1));

        for(int i = 0; i < comps.size(); i++){
            Competitor boat = comps.get(i);
            XYChart.Series<String, Double> series = new XYChart.Series<>();
            seriesMap.put(boat.getSourceID(), series);
            series.getData().add(new XYChart.Data<>("-", (double) -i - 1)); //replace y with team position
            sparkChart.getData().add(series);
            series.setName(boat.getAbbreName());

            String boatHexColour = "#" + boat.getColor().toString().substring(2);
            series.getNode().setStyle("-fx-stroke: " + boatHexColour);
        }

    }

    /**
     * Refreshes the spark line with the new received data
     */
    void refresh(){
        long expectedStartTime = dataSource.getExpectedStartTime();
        long firstMessageTime = dataSource.getMessageTime();
        long raceTime = firstMessageTime - expectedStartTime;
        long waitTime = 45000;
        int sparklinePoints = 5;
        List<Competitor> comps = new ArrayList<>(dataSource.getCompetitorsPosition());
        comps.sort((o1, o2) -> (o1.getCurrentLegIndex() < o2.getCurrentLegIndex()) ? 1 : ((o1.getCurrentLegIndex() == o2.getCurrentLegIndex()) ? 0 : -1));
        for (int i = 0; i < comps.size(); i++) {
            XYChart.Series<String, Double> series = seriesMap.get(comps.get(i).getSourceID());
            int pos = i + 1;
            if(series.getData().size() < sparklinePoints) {
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

            String boatHexColour = "#" + comps.get(i).getColor().toString().substring(2);
            String backgroundHexColour = "#" + Color.LIGHTBLUE.toString().substring(2);

            for (int index = 0; index < series.getData().size(); index++) {
                XYChart.Data dataPoint = series.getData().get(index);
                Node lineSymbol = dataPoint.getNode().lookup(".chart-line-symbol");
                lineSymbol.setStyle("-fx-background-color: " + boatHexColour + ", " + backgroundHexColour);

            }

            for (Node node : sparkChart.getChildrenUnmodifiable()) {
                if (node instanceof Legend) {
                    final Legend legend = (Legend) node;
                    legend.setVisible(false);
                }
            }
        }

    }


}
