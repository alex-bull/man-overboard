package seng302;

import com.sun.org.apache.xerces.internal.impl.xpath.regex.Match;

import java.awt.geom.Arc2D;
import java.util.*;
import java.util.List;

/**
 * Created by mgo65 on 3/03/17.
 * Represents one race in a regatta
 */
public class MatchRace implements Race {

    private List<Competitor> competitors = new ArrayList<>();
    private List<CoursePoint> points = new ArrayList<>();
    private double velocityScaleFactor;
    private Map<Integer, List<RaceEvent>> raceTimeline = new HashMap<>();
    private List<String> finishingOrder = new ArrayList<>();

    /**
     * Creates a match race with an approximate duration
     * @param duration int the approximate duration of the race in minutes
     */
    public MatchRace (int duration) {
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
     * Sets the course for the race
     * @param points List the points on the course
     */
    public void setCourse(List<CoursePoint> points) {
        this.points = points;
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
                Double scaleTime = time * velocityScaleFactor;
                RaceEvent event = new RaceEvent(comp.getTeamName(), time, scaleTime.intValue(), endPoint.getName(), endPoint.getExitHeading(), endPoint.getIsFinish());

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
     * @param start Point the coordinates of the first course point
     * @param end Point the coordinates of the second course point
     * @return Integer the time taken
     */
    private Integer calculateTime (Integer velocity, Point start, Point end) {

        Double xDistance = Math.pow((start.getX() - end.getX()), 2);
        Double yDistance = Math.pow((start.getY() - end.getY()), 2);
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
     * Outputs the entire race
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
