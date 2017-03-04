package seng302;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by mgo65 on 3/03/17.
 */
public class Regatta {

    private ArrayList<Competitor> competitors = new ArrayList<>();
    private ArrayList<Race> races = new ArrayList<>();

    /**
     * Creates a regatta with a set of competitors and races
     * @param competitors Competitor the teams competing in the regatta
     * @param races Race the races in the regatta
     */
    public Regatta (ArrayList<Competitor> competitors, ArrayList<Race> races) {
        this.competitors = competitors;
        this.races = races;
    }

    /**
     * Returns the list of races in the regatta
     * @return ArrayList the list of races
     */
    public ArrayList<Race> getRaces() {
        return this.races;
    }

    /**
     * Assigns the Competitors for each race randomly
     */
    private void createMatches() {

        //shuffle the competitors
        Collections.shuffle(competitors);

        //assign two unique competitors for each match
        for (int i = 0; i < races.size(); i++) {
            races.get(i).setCompetitors(competitors.get(0), competitors.get(1));
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
