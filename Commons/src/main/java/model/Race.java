package model;

import javafx.animation.Timeline;

import java.util.List;

/**
 * Created by mgo65 on 3/03/17.
 * An interface for a race
 */
public interface Race {
    List<Competitor> getCompetitors();

    void setCompetitors(List<Competitor> competitors);

    Timeline generateTimeline();

    List<CourseFeature> getCourseFeatures();

    int getVelocityScaleFactor();

    double getWindDirection();

    void setRaceEventHandler(RaceEventHandler raceEventHandler);

    List<MutablePoint> getCourseBoundary();
}
