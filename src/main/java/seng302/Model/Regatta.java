package seng302.Model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by mgo65 on 3/03/17.
 * Represents a group of competitors across multiple races on a course
 */
public class Regatta {

    private List<Competitor> competitors = new ArrayList<>();
    private List<Race> races = new ArrayList<>();
    private List<CourseFeature> points = new ArrayList<>();

    /**
     * Creates a regatta with a set of competitors and races
     * @param competitors List the teams competing in the regatta
     * @param races List the races in the regatta
     * @param points List the points that make up the course
     */
    public Regatta (List<Competitor> competitors, List<Race> races, List<CourseFeature> points) {
        this.competitors = competitors;
        this.races = races;
        this.points = points;
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
     * @param start Point the coordinates of the first point
     * @param end Point the coordinates of the second point
     * @return Double the angle between the points from the y axis
     */
    private Double calculateAngle(Point start, Point end) {
        Double angle = Math.toDegrees(Math.atan2(end.getX() - start.getX(), end.getY() - start.getY()));

        if(angle < 0){
            angle += 360;
        }
        return angle;
    }

    /**
     * Assigns the Competitors for each race randomly
     */
    private void createMatches() {

        Collections.shuffle(competitors);

        for (int i = 0; i < races.size(); i++) {
            races.get(i).setCourse(this.points);
            races.get(i).setCompetitors(this.competitors);
            Collections.shuffle(competitors);
        }
    }

    /**
     * Creates the matches and starts the races.
     */
    public void begin() {
        this.createMatches();
        this.calculateHeadings();
        //start a race
        races.get(0).start();

//        //start all races
//        for(int i = 0; i < races.size(); i++) {
//            System.out.println("Race is starting...");
//            races.get(i).start();
//        }
    }
}
