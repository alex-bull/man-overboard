package seng302.Model;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.DoubleProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.util.Duration;
import seng302.Controllers.TableController;

import java.util.*;
import java.util.List;

/**
 * Created by mgo65 on 3/03/17.
 * Represents one race in a regatta
 */
public class MatchRace implements Race {

    private List<Competitor> competitors = new ArrayList<>();
    private double velocityScaleFactor;
    private Map<Integer, List<RaceEvent>> raceTimeline = new HashMap<>();
    private List<String> finishingOrder = new ArrayList<>();
    private Course raceCourse;
    private RaceEventHandler raceEventHandler;


    /**
     * Creates a match race with an approximate duration
     *
     * @param duration    int the approximate duration of the race in minutes
     * @param raceCourse  Course the course for the race
     * @param competitors List the list of competing boats
     */
    public MatchRace(int duration, Course raceCourse, List<Competitor> competitors) {
        if (duration == 5) {
            velocityScaleFactor = 1;
        } else if (duration == 1) {
            velocityScaleFactor = 4;
        } else {
            //for testing
            velocityScaleFactor = 1000;
        }
        this.raceCourse = raceCourse;
        this.competitors = competitors;
    }


    /**
     * Returns the angle of wind direction
     * @return double wind direction angle
     */
    public double getWindDirection() {
        return raceCourse.getWindDirection();
    }



    /***
     * Getter for the course features in the race course
     * @return List the features
     */
    public List<CourseFeature> getCourseFeatures() {
        return this.raceCourse.getPoints();
    }

    /**
     * Sets the race event handler
     * @param raceEventHandler RaceEventHandler
     */
    public void setRaceEventHandler(RaceEventHandler raceEventHandler) {
        this.raceEventHandler = raceEventHandler;
    }

    /**
     * Sets the competitors who are entered in the race
     *
     * @param competitors List the competing teams
     */
    public void setCompetitors(List<Competitor> competitors) {
        this.competitors = competitors;
    }

    /**
     * Gets the competitors who are entered in the race
     *
     * @return List the competing teams
     */
    public List<Competitor> getCompetitors() {
        return this.competitors;
    }


    /**
     * Getter for the finishing order of the race
     *
     * @return List the team names in finishing order
     */
    public List<String> getFinishingOrder() {
        return this.finishingOrder;
    }


    /**
     * Generates a timeline of events in the race where competitors pass course features
     *
     * @return Timeline the timeline of events
     */
    public Timeline generateTimeline(TableController tableController) {

        Timeline timeline = new Timeline();
        List<CourseFeature> points = raceCourse.getPoints();

        for (Competitor comp : competitors) {
            int time = 0;
            timeline.getKeyFrames().add(new KeyFrame(
                    Duration.millis(0),
                    new KeyValue(comp.getPosition().getX(), comp.getPosition().getXValue()),
                    new KeyValue(comp.getPosition().getY(), comp.getPosition().getYValue())
            ));

            for (int j = 0; j < points.size() - 1; j++) {

                //calculate total time for competitor to reach the point
                CourseFeature startPoint = points.get(j);
                CourseFeature endPoint = points.get(j + 1);
                time += this.calculateTime(comp.getVelocity(), startPoint.getCentre(), endPoint.getCentre());
                System.out.println(time);

                timeline.getKeyFrames().add(new KeyFrame(
                        Duration.millis(time), t -> {

                            RaceEvent e = new RaceEvent(comp,System.currentTimeMillis(), endPoint);
                            raceEventHandler.handleRaceEvent(e);
                        },
                        new KeyValue(comp.getPosition().getX(), endPoint.getCentre().getXValue()),
                        new KeyValue(comp.getPosition().getY(), endPoint.getCentre().getYValue())
                ));

            }
        }
        return timeline;
    }


    /**
     * Calculates the time for a competitor to travel between course points
     *
     * @param velocity Integer the linear velocity of the competitor in m/s
     * @param start    MutablePoint the coordinates of the first course point
     * @param end      MutablePoint the coordinates of the second course point
     * @return Integer the time taken in milliseconds
     */
    private Integer calculateTime(Integer velocity, MutablePoint start, MutablePoint end) {
        Double xDistance = Math.pow((start.getXValue() - end.getXValue()), 2);
        Double yDistance = Math.pow((start.getYValue() - end.getYValue()), 2);
        Double distance = Math.sqrt(xDistance + yDistance);
        Double time = (distance / (velocity * velocityScaleFactor));
        time = time * 1000;
        return time.intValue();
    }


}
