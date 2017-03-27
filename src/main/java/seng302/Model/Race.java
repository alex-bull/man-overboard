package seng302.Model;

import javafx.animation.Timeline;
import javafx.beans.property.DoubleProperty;
import seng302.Controllers.TableController;

import java.util.List;
import java.util.Map;

/**
 * Created by mgo65 on 3/03/17.
 * An interface for a race
 */
public interface Race {
    void setCompetitors(List<Competitor> competitors);
    List<Competitor> getCompetitors();
    Timeline generateTimeline();
    List<CourseFeature> getCourseFeatures();
    int getVelocityScaleFactor();
    double getWindDirection();
    void setRaceEventHandler(RaceEventHandler raceEventHandler);
}
