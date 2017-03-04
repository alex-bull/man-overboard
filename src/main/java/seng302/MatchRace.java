package seng302;

import javafx.util.Pair;

import java.awt.*;
import java.util.*;

/**
 * Created by mgo65 on 3/03/17.
 * Race object
 */
public class MatchRace implements Race {

    private ArrayList<Competitor> competitors = new ArrayList<>();
    private ArrayList<CoursePoint> points = new ArrayList<>();
    private HashMap<Integer, ArrayList<String>> raceMap = new HashMap<>();


    /**
     * Default constructor for MatchRace
     */
    public MatchRace () {

    }

    /**
     * Sets the competitors who are entered in the race
     * @param competitors ArrayList the competing teams
     */
    public void setCompetitors(ArrayList<Competitor> competitors) {
        this.competitors = competitors;
    }

    /**
     * Gets the competitors who are entered in the race
     * @return ArrayList the competing teams
     */
    public ArrayList<Competitor> getCompetitors() {
        return this.competitors;
    }

    /**
     * Sets the course for the race
     * @param points ArrayList the points on the course
     */
    public void setCourse(ArrayList<CoursePoint> points) {
        this.points = points;
    }


    /**
     * Fills out the raceMap by generating events for when competitors pass course points
     */
    private void generateEvents() {

        for (int i = 0; i < this.competitors.size(); i++) {
            Competitor comp = competitors.get(i);
            System.out.println("#" + (i + 1) + " " + comp.getTeamName() + ", Velocity: " +  comp.getVelocity() + "m/s");
            Integer time = 0;

            for (int j = 0; j < this.points.size() - 1; j++) {

                CoursePoint startPoint = points.get(j);
                CoursePoint endPoint = points.get(j + 1);
                time += this.calculateTime(comp.getVelocity(), startPoint.getLocation(), endPoint.getLocation());

                String event = "Time: " + time.toString() + "s, Event: " + comp.getTeamName() + " passed the " + endPoint.getName();
                if (endPoint.getExitHeading() != null) {
                    event += ", Heading: " + String.format("%.2f", endPoint.getExitHeading());
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
                for (String event: raceMap.get(i)) {
                    System.out.println(event);
                }
            }
            Thread.sleep(1000);
        }
    }

    /**
     * Outputs the starting line up and outputs the finishing order
     */
    public void start () {

        System.out.println("Entrants:");

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
