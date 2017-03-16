package seng302.Model;

import javafx.animation.Timeline;
import javafx.beans.property.DoubleProperty;

import java.util.List;
import java.util.Map;

/**
 * Created by mgo65 on 3/03/17.
 */
public interface Race {
    void start();
    Map getRaceTimeline();
    void setCompetitors(List<Competitor> competitors);
    List<Competitor> getCompetitors();
    Timeline generateTimeline();
//    DoubleProperty getX();
//    DoubleProperty getY();
}
