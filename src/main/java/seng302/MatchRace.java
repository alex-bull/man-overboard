package seng302;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by mgo65 on 3/03/17.
 * Race object
 */
public class MatchRace implements Race {

    private Competitor competitor1;
    private Competitor competitor2;
    private ArrayList<Competitor> order = new ArrayList<>();
    private ArrayList<CoursePoint> points = new ArrayList<>();


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
     * Randomly orders the placings
     */
    private void generateOrder() {
        Collections.shuffle(order);
    }

    /**
     * Outputs the starting line up and outputs the finishing order
     */
    public void start () {

        System.out.println("Entrants:");
        System.out.println("#1: " + competitor1.getTeamName());
        System.out.println("#2: " + competitor2.getTeamName());

        for (int i = 0; i < points.size(); i++) {
            CoursePoint point = points.get(i);
            System.out.println(point.getName());
            generateOrder();
            System.out.println("#1: " + order.get(0).getTeamName());
            System.out.println("#2: " + order.get(1).getTeamName());
        }
    }

}
