package seng302;

import javafx.util.Pair;

import java.awt.*;
import java.util.*;

/**
 * Created by mgo65 on 3/03/17.
 * Race object
 */
public class MatchRace implements Race {

    private Competitor competitor1;
    private Competitor competitor2;
    private ArrayList<Competitor> order = new ArrayList<>();
    private ArrayList<CoursePoint> points = new ArrayList<>();
    private HashMap<Integer, ArrayList<String>> raceMap = new HashMap<>();


    /**
     * Default constructor for MatchRace
     */
    public MatchRace () {

    }

    /**
     * Sets the competitors who are entered in the race
     * @param comp1 Competitor the first entrant
     * @param comp2 Competitor the second entrant
     */
    public void setCompetitors(Competitor comp1, Competitor comp2) {
        this.competitor1 = comp1;
        this.competitor2 = comp2;
        this.order.add(comp1);
        this.order.add(comp2);
    }

    /**
     * Sets the course for the race
     * @param points ArrayList the points on the course
     */
    public void setCourse(ArrayList<CoursePoint> points) {
        this.points = points;
    }

    /**
     * Returns the first competitor
     * @return Competitor
     */
    public Competitor getCompetitor1() {
        return this.competitor1;
    }

    /**
     * Returns the second competitor
     * @return Competitor
     */
    public Competitor getCompetitor2() {
        return this.competitor2;
    }

    /**
     * Getter for the race placings
     * @return ArrayList the finishing order of the competitors
     */
    public ArrayList<Competitor> getPlacings () {
        return this.order;
    }


    /**
     * Fills out the raceMap by generating events for when competitors pass course points
     */
    private void generateEvents() {
        Random rand = new Random();
        for (int i = 0; i < this.order.size(); i++) {
            Integer time = 0;

            for (int j = 0; j < this.points.size() - 1; j++) {

                CoursePoint startPoint = points.get(j);
                CoursePoint endPoint = points.get(j + 1);
                Competitor comp = order.get(i);
                time += this.calculateTime(comp.getVelocity(), startPoint.getLocation(), endPoint.getLocation());

                String event = "Time: " + time.toString() + "s, Event: " + comp.getTeamName() + " Passed the " + endPoint.getName();
                if (endPoint.getExitHeading() != null) {
                    event += ", heading: " + String.format("%.2f", endPoint.getExitHeading());
                }

                if (raceMap.get(time) != null) {
                    raceMap.get(time).add(event);
                } else {
                    ArrayList<String> events = new ArrayList<>();
                    events.add(event);
                    raceMap.put(time, events);
                }
            }
        }
    }

    /**
     * Calculates the time for a competitor to travel between course points
     * @param velocity Integer the linear velocity of the competitor in m/s
     * @param start Pair the coordinates of the first course point
     * @param end Pair the coordinates of the second course point
     * @return Integer the time taken
     */
    private Integer calculateTime (Integer velocity, Pair<Double, Double> start, Pair<Double, Double> end) {

        Double xDistance = Math.pow((start.getKey() - end.getKey()), 2);
        Double yDistance = Math.pow((start.getValue() - end.getValue()), 2);
        Double distance = Math.sqrt(xDistance + yDistance);
        Double time = (distance / velocity);
        return time.intValue();
    }

    /**
     * Calculates exit headings of each course point and sets the course point property
     */
    private void calculateHeadings () {

        for (int j = 1; j < this.points.size() - 1; j++) {
            Double heading = calculateAngle(points.get(j).getLocation(), points.get(j + 1).getLocation());
            points.get(j).setExitHeading(heading);
        }
    }

    /**
     * Calculates the angle between two course points
     * @param start Pair the coordinates of the first point
     * @param end Pair the coordinates of the second point
     * @return Double the angle between the points from the y axis
     */
    public Double calculateAngle(Pair<Double, Double> start, Pair<Double, Double> end) {
        Double angle = Math.toDegrees(Math.atan2(end.getKey() - start.getKey(), end.getValue() - start.getValue()));

        if(angle < 0){
            angle += 360;
        }
        return angle;
    }


    /**
     *
     * @throws InterruptedException
     */
    private void printRaceMap() throws InterruptedException {
        for (int i = 0; i < 80; i++) {
            if (raceMap.get(i) != null) {
                System.out.println(raceMap.get(i));
            }
            Thread.sleep(1000);
        }
    }

    /**
     * Outputs the starting line up and outputs the finishing order
     */
    public void start () {

        System.out.println("Entrants:");
        System.out.println("#1: " + competitor1.getTeamName() + ", velocity: " + competitor1.getVelocity() + "m/s");
        System.out.println("#2: " + competitor2.getTeamName() + ", velocity: " + competitor2.getVelocity() + "m/s");
        calculateHeadings();
        generateEvents();

        try {
            printRaceMap();
        }
        catch (Exception e) {
            System.out.println("Thread interrupted");
        }

    }

}
