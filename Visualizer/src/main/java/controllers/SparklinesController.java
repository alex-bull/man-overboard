package controllers;

import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import models.Competitor;
import models.RaceEvent;
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

    private Map<Integer, XYChart.Series<String, Double>> seriesMap = new HashMap<>();
    private DataSource dataSource;

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
            series.getData().add(new XYChart.Data<>("-", 3.0)); //replace y with team position
            sparkChart.getData().add(series);
            series.setName(boat.getAbbreName());

        }

    }


    void refresh(){
        List<Competitor> comps = new ArrayList<>(dataSource.getCompetitorsPosition());

        comps.sort((o1, o2) -> (o1.getLegIndex() < o2.getLegIndex()) ? 1 : ((o1.getLegIndex() == o2.getLegIndex()) ? 0 : -1));
        for (int i = 0; i < comps.size(); i++) {
            Competitor boat = comps.get(i);
            XYChart.Series<String, Double> series = seriesMap.get(comps.get(i).getSourceID());
            int pos = i + 1;
            if(boat.getLastMarkPassed() == null) {
                series.getData().add(new XYChart.Data<>("-", (double) pos));
            }
            else {
                series.getData().add(new XYChart.Data<>(boat.getLastMarkPassed(), (double) pos));
            }

        }
    }


}
