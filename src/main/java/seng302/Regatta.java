package seng302;

import java.util.ArrayList;

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
     *
     */
    private void createMatches() {

    }

    public void begin() {
        races.get(0).start();
    }
}
