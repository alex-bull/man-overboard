package seng302.Model;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.DoubleProperty;
import javafx.util.Duration;

import java.util.*;
import java.util.List;

/**
 * Created by mgo65 on 3/03/17.
 * Represents one race in a regatta
 */
public class MatchRace implements Race {

    private List<Competitor> competitors = new ArrayList<>();
    private double velocityScaleFactor;

    private RaceDelegate delegate;
    private Map<Integer, List<RaceEvent>> raceTimeline = new HashMap<>();
    private List<String> finishingOrder = new ArrayList<>();
    private Course raceCourse;

//    DoubleProperty x  = new SimpleDoubleProperty();
//    DoubleProperty y  = new SimpleDoubleProperty();

    /**
     * Creates a match race with an approximate duration
     * @param duration int the approximate duration of the race in minutes
     * @param delegate RaceDelegate a delegate class to be notified of race events
     * @param raceCourse Course the course for the race
     * @param competitors List the list of competing boats
     */
    public MatchRace (int duration, RaceDelegate delegate, Course raceCourse, List<Competitor> competitors) {
        if (duration == 5) {
            velocityScaleFactor = 1;
        }
        else if (duration == 1) {
            velocityScaleFactor = 4;
        }
        else {
            //for testing
            velocityScaleFactor = 1000;
        }
        this.delegate = delegate;
        this.raceCourse = raceCourse;
        this.competitors = competitors;
    }

//    public DoubleProperty getX() {
//        return this.x;
//    }
//
//    public DoubleProperty getY() {
//        return this.y;
//    }

    /***
     * Getter for the course features in the race course
     * @return List the features
     */
    public List<CourseFeature> getCourseFeatures() {
        return this.raceCourse.getPoints();
    }

    /**
     * Get race timeline
     * @return Map the timeline of race events
     */
    public Map<Integer, List<RaceEvent>> getRaceTimeline() {
        return raceTimeline;
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
     * Fills out the race time line by generating events for when competitors pass course points
     */
//    private void generateTimeline() {
//
//        //for each competitor
//        for (int i = 0; i < this.competitors.size(); i++) {
//            Competitor comp = competitors.get(i);
//            System.out.println("#" + (i + 1) + " " + comp.getTeamName() + ", Velocity: " +  comp.getVelocity() + "m/s");
//            Integer time = 0;
//            List<CourseFeature> points = raceCourse.getPoints();
//            //for each course point
//            for (int j = 0; j < points.size() - 1; j++) {
//
//                //calculate total time for competitor to reach the point
//                CourseFeature startPoint = points.get(j);
//                CourseFeature endPoint = points.get(j + 1);
//                time += this.calculateTime(comp.getVelocity(), startPoint.getLocation(), endPoint.getLocation());
//
//                //create the event
//                Double scaleTime = time * velocityScaleFactor;
//                RaceEvent event = new RaceEvent(comp, time, scaleTime.intValue(), endPoint.getName(), endPoint.getExitHeading(), endPoint.getIsFinish(), endPoint);
//
//                //place the event on the timeline
//                if (raceTimeline.get(time) != null) {
//                    raceTimeline.get(time).add(event);
//                } else {
//                    List<RaceEvent> events = new ArrayList<>();
//                    events.add(event);
//                    raceTimeline.put(time, events);
//                }
//            }
//        }
//    }

    /**
     *
     */
    public Timeline generateTimeline() {

        Timeline timeline = new Timeline();
        Integer time = 0;
        Competitor comp = this.competitors.get(0);
        List<CourseFeature> points = raceCourse.getPoints();

        timeline.getKeyFrames().add(new KeyFrame(
                Duration.seconds(0),
                new KeyValue(comp.getPosition().getX(), comp.getPosition().getXValue()),
                new KeyValue(comp.getPosition().getY(), comp.getPosition().getYValue())
        ));

        for (int j = 0; j < points.size() - 1; j++) {

            //calculate total time for competitor to reach the point
            CourseFeature startPoint = points.get(j);
            CourseFeature endPoint = points.get(j + 1);
            time += this.calculateTime(comp.getVelocity(), startPoint.getCentre(), endPoint.getCentre());

            timeline.getKeyFrames().add(new KeyFrame(
                    Duration.seconds(time),
                    new KeyValue(comp.getPosition().getX(), endPoint.getCentre().getXValue()),
                    new KeyValue(comp.getPosition().getY(), endPoint.getCentre().getYValue())
            ));
        }
        return timeline;
    }


    /**
     * Calculates the time for a competitor to travel between course points
     * @param velocity Integer the linear velocity of the competitor in m/s
     * @param start MutablePoint the coordinates of the first course point
     * @param end MutablePoint the coordinates of the second course point
     * @return Integer the time taken
     */
    private Integer calculateTime (Integer velocity, MutablePoint start, MutablePoint end) {

        Double xDistance = Math.pow((start.getXValue() - end.getXValue()), 2);
        Double yDistance = Math.pow((start.getYValue() - end.getYValue()), 2);
        Double distance = Math.sqrt(xDistance + yDistance);
        Double time = (distance / (velocity * velocityScaleFactor));
        return time.intValue();
    }



    /**
     * Prints out the race timeline in real time
     * @throws InterruptedException if thread sleep interrupted
     */
    private void printRace() throws InterruptedException {

        int i = 0;

        while (finishingOrder.size() < this.competitors.size()) {
            if (raceTimeline.get(i) != null) {
                for (RaceEvent event: raceTimeline.get(i)) {
                    delegate.boatMoved();
                    System.out.println(event.getEventString());
                    if (event.getIsFinish()) {
                        finishingOrder.add(event.getTeamName());
                    }
                }
            }
            Thread.sleep(1000);
            i++;
        }

        int placing = 1;
        System.out.println("Finishing order:");
        for (String team: finishingOrder) {
            System.out.println("#" + placing + " " + team);
            placing++;
        }

    }

    /**
     * Starts the race
     */
    public void start () {

        System.out.println("Entrants:");

//        generateTimeline();
        try {

            printRace();

        }
        catch (Exception e) {
            System.out.println("Thread interrupted");
        }
    }

}
