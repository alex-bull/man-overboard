package seng302;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by mgo65 on 3/03/17.
 */
public class Regatta {

    private List<Competitor> competitors = new ArrayList<>();
    private List<Race> races = new ArrayList<>();
    private List<CoursePoint> points = new ArrayList<>();

    /**
     * Creates a regatta with a set of competitors and races
     * @param competitors ArrayList the teams competing in the regatta
     * @param races ArrayList the races in the regatta
     * @param points ArrayList the points that make up the course
     */
    public Regatta (List<Competitor> competitors, List<Race> races, List<CoursePoint> points) {
        this.competitors = competitors;
        this.races = races;
        this.points = points;
    }

    /**
     * Returns the list of races in the regatta
     * @return ArrayList the list of races
     */
    public List<Race> getRaces() {
        return this.races;
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

        //start a race
        races.get(0).start();

//        //start all races
//        for(int i = 0; i < races.size(); i++) {
//            System.out.println("Race is starting...");
//            races.get(i).start();
//        }
    }
}
