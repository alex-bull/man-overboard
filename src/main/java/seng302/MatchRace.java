package seng302;

import javafx.util.Pair;

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
            for (int j = 0; j < this.points.size() - 1; j++) {

                CoursePoint startPoint = points.get(j);
                CoursePoint endPoint = points.get(j + 1);
                Competitor comp = order.get(i);
                Integer time = this.calculateTime(comp.getVelocity(), startPoint.getLocation(), endPoint.getLocation());

                String event = "Time: " + time.toString() + ", Event: " + comp.getTeamName() + " Passed the " + endPoint.getName() + ".";

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
        System.out.println(distance);
        Double time = (distance / velocity);
        System.out.println(time);
        return time.intValue();
    }

    /**
     *
     * @throws InterruptedException
     */
    private void printRaceMap() throws InterruptedException {
        for (int i = 0; i < 60; i++) {
            System.out.println(raceMap.get(i));
            Thread.sleep(1000);
        }
    }

    /**
     * Outputs the starting line up and outputs the finishing order
     */
    public void start () {

        System.out.println("Entrants:");
        System.out.println("#1: " + competitor1.getTeamName());
        System.out.println("#2: " + competitor2.getTeamName());
        System.out.println(calculateTime(competitor1.getVelocity(), points.get(0).getLocation(), points.get(1).getLocation()));
//        generateEvents();
//
//        try {
//            printRaceMap();
//        }
//        catch (Exception e) {
//            System.out.println("Thread interrupted");
//        }

    }

}
