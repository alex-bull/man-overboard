package seng302.Model;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
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
    private int velocityScaleFactor;
    private List<String> finishingOrder = new ArrayList<>();
    private Course raceCourse;
    private RaceEventHandler raceEventHandler;
    private int numBoats;

    /**
     * Creates a match race with an approximate duration
     * @param duration    int the approximate duration of the race in minutes
     * @param raceCourse  Course the course for the race
     * @param competitors List the list of competing boats
     */
    public MatchRace(int duration, Course raceCourse, List<Competitor> competitors) {
        // adjust velocity scale factor so that the race time is approximately the given duration
        if (duration == 5) {
            velocityScaleFactor = 2;
        } else if (duration == 1) {
            velocityScaleFactor = 9;
        } else {
            //for testing
            velocityScaleFactor = 1;
        }
        this.raceCourse = raceCourse;
        this.competitors = competitors;
    }

    /**
     * Gets the velocity scale factor of the race
     * @return double the velocity scale factor
     */
    public int getVelocityScaleFactor() {
        return velocityScaleFactor;
    }

    /**
     * Returns the angle of wind direction
     * @return double wind direction angle
     */
    public double getWindDirection() {
        return raceCourse.getWindDirection();
    }


    /***
     * Getter for the course boundary in the race course
     * @return List the boundary points
     */
    public List<MutablePoint> getCourseBoundary() {
        return this.raceCourse.getBoundaryPoints();
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
     * @param competitors List the competing teams
     */
    public void setCompetitors(List<Competitor> competitors) {
        this.competitors = competitors;
    }

    /**
     * Gets the competitors who are entered in the race
     * @return List the competing teams
     */
    public List<Competitor> getCompetitors() {
        return this.competitors;
    }


    /**
     * Getter for the finishing order of the race
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
    public Timeline generateTimeline() {

        Timeline timeline = new Timeline();

        List<CourseFeature> points = raceCourse.getPoints();
        numBoats = competitors.size();
        for (Competitor comp : competitors) {
            int time = 0;
            // sets the position of boats at time 0
            timeline.getKeyFrames().add(new KeyFrame(
                    Duration.millis(0),
                    new KeyValue(comp.getPosition().getX(), points.get(0).getPixelLocations().get(0).getXValue()),
                    new KeyValue(comp.getPosition().getY(), points.get(0).getPixelLocations().get(0).getYValue()),
                    new KeyValue(comp.getHeadingProperty(), points.get(0).getExitHeading(), Interpolator.DISCRETE)
            ));


            // loops through pairs of course features
            for (int j = 0; j < points.size() - 1; j++) {
                // calculate total time for competitor to reach the point
                CourseFeature startPoint = points.get(j);
                CourseFeature endPoint = points.get(j + 1);

                double distance = raceCourse.distanceBetweenGPSPoints(startPoint.getGPSCentre(), endPoint.getGPSCentre());
                time += this.calculateTime(comp.getVelocity(), distance);


                timeline.getKeyFrames().add(new KeyFrame(
                        Duration.millis(time), t -> {
                    RaceEvent e = new RaceEvent(comp, endPoint);
                    raceEventHandler.handleRaceEvent(e);
                    if(endPoint.isFinish()){
                        comp.setVelocity(0);
                    }

                },
                        new KeyValue(comp.getPosition().getX(), endPoint.getPixelLocations().get(0).getXValue()),
                        new KeyValue(comp.getPosition().getY(), endPoint.getPixelLocations().get(0).getYValue()),
                        new KeyValue(comp.getHeadingProperty(), endPoint.getExitHeading(), Interpolator.DISCRETE)
                ));

            }

        }

        return timeline;
    }


    /**
     * Calculates the time for a competitor to travel between course points
     * @param velocity Integer the linear velocity of the competitor in m/s
     * @param distance double the distance between two course points
     * @return Integer the time taken in milliseconds
     */
    private Integer calculateTime(double velocity, double distance) {
        Double time = (distance / (velocity * velocityScaleFactor));
        time = time * 1000;
        return time.intValue();
    }


}
