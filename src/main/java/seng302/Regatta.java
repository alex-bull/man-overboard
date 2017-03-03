package seng302;

import java.util.ArrayList;
import java.util.Random;

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
        //randomly pair competitors ignoring multiple races for now
        Random rand = new Random();
        ArrayList<Integer> s = new ArrayList<>();
        s.add(0);
        s.add(1);
        s.add(2);
        s.add(3);
        s.add(4);
        s.add(5);

        //generate two random numbers
        int randOne = rand.nextInt(6);
        int randTwo = rand.nextInt(5);

        //get unique indexes
        int indexOne = s.get(randOne);
        s.remove(randOne);
        int indexTwo = s.get(randTwo);


        races.get(0).setCompetitors(competitors.get(indexOne), competitors.get(indexTwo));
    }

    /**
     * Creates the matches and starts the races.
     */
    public void begin() {
        this.createMatches();
        races.get(0).start();
    }
}
