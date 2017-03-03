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
     * Assigns the Competitors for each race
     */
    private void createMatches() {

        //create a list of integers for each competitor for indexing
        ArrayList<Integer> list = new ArrayList<>();
        for(int i = 0; i < competitors.size(); i++) {
            list.add(i);
        }

        Collections.shuffle(list);

        //assign two unique competitors for each match
        for (int i = 0; i < races.size(); i++) {
            races.get(i).setCompetitors(competitors.get(list.get(0)), competitors.get(list.get(1)));
            Collections.shuffle(list);
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
