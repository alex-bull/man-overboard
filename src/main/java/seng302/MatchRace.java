package seng302;

import java.util.*;
import java.util.List;

/**
 * Created by mgo65 on 3/03/17.
 * Race object
 */
public class MatchRace implements Race {

    private List<Competitor> competitors = new ArrayList<>();
    private List<CoursePoint> points = new ArrayList<>();
    private Map<Integer, List<RaceEvent>> raceTimeline = new HashMap<>();


    /**
     * Default constructor for MatchRace
     */
    public MatchRace () {

    }

    /**
     * Sets the competitors who are entered in the race
     * @param competitors ArrayList the competing teams
     */
    public void setCompetitors(List<Competitor> competitors) {
        this.competitors = competitors;
    }

    /**
     * Gets the competitors who are entered in the race
     * @return ArrayList the competing teams
     */
    public List<Competitor> getCompetitors() {
        return this.competitors;
    }

    /**
     * Sets the course for the race
     * @param points ArrayList the points on the course
     */
    public void setCourse(List<CoursePoint> points) {
        this.points = points;
    }


    /**
     * Fills out the race time line by generating events for when competitors pass course points
     */
    private void generateTimeline() {

        //for each competitor
        for (int i = 0; i < this.competitors.size(); i++) {
            Competitor comp = competitors.get(i);
            System.out.println("#" + (i + 1) + " " + comp.getTeamName() + ", Velocity: " +  comp.getVelocity() + "m/s");
            Integer time = 0;

            //for each course point
            for (int j = 0; j < this.points.size() - 1; j++) {

                //calculate total time for competitor to reach the point
                CoursePoint startPoint = points.get(j);
                CoursePoint endPoint = points.get(j + 1);
                time += this.calculateTime(comp.getVelocity(), startPoint.getLocation(), endPoint.getLocation());

                //create the event
                RaceEvent event = new RaceEvent(comp.getTeamName(), time, endPoint.getName(), endPoint.getExitHeading());
//                String event = "Time: " + time.toString() + "s, Event: " + comp.getTeamName() + " passed the " + endPoint.getName();
//                if (endPoint.getExitHeading() != null) {
//                    event += ", Heading: " + String.format("%.2f", endPoint.getExitHeading());
//                }

                //place the event on the timeline
                if (raceTimeline.get(time) != null) {
                    raceTimeline.get(time).add(event);
                } else {
                    List<RaceEvent> events = new ArrayList<>();
                    events.add(event);
                    raceTimeline.put(time, events);
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
    private Integer calculateTime (Integer velocity, Point start, Point end) {

        Double xDistance = Math.pow((start.getX() - end.getX()), 2);
        Double yDistance = Math.pow((start.getY() - end.getY()), 2);
        Double distance = Math.sqrt(xDistance + yDistance);
        Double time = (distance / velocity);
        return time.intValue();
    }



    /**
     * Prints out the race timeline in real time
     * @throws InterruptedException
     */
    private void printRace() throws InterruptedException {

        int finishedBoats = 0;
        int i = 0;

        while (finishedBoats < this.competitors.size()) {
            if (raceTimeline.get(i) != null) {
                for (RaceEvent event: raceTimeline.get(i)) {
                    System.out.println(event.getEventString());
                    if (event.getIsFinish()) {
                        finishedBoats++;
                    }
                }
            }
            Thread.sleep(1000);
            i++;
        }
    }

    /**
     * Outputs the starting line up and outputs the finishing order
     */
    public void start () {

        System.out.println("Entrants:");

        generateTimeline();

        try {
            printRace();
        }
        catch (Exception e) {
            System.out.println("Thread interrupted");
        }

    }

}
