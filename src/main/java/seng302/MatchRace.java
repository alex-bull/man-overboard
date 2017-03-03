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
    private ArrayList<Competitor> placings = new ArrayList<>();


    public MatchRace () {

    }

    /**
     * Creates a MatchRace with two participants
     * @param competitor1 Competitor a participant
     * @param competitor2 Competitor a participant
     */
    public MatchRace (Competitor competitor1, Competitor competitor2) {
        this.competitor1 = competitor1;
        this.competitor2 = competitor2;
    }

    /**
     * sets the competitors who are entered in the race
     * @param comp1 Competitor the first entrant
     * @param comp2 Competitor the second entrant
     */
    public void setCompetitors(Competitor comp1, Competitor comp2) {
        this.competitor1 = comp1;
        this.competitor2 = comp2;
    }

    /**
     * Getter for the race placings
     * @return ArrayList the finishing order of the competitors
     */
    public ArrayList<Competitor> getPlacings () {
        return this.placings;
    }

    /**
     * Outputs the starting line up and outputs the finishing order
     */
    public void start () {

        System.out.println("Entrants:");
        System.out.println("#1: " + competitor1.getTeamName());
        System.out.println("#2: " + competitor2.getTeamName());
        generatePlacings();
        System.out.println("Finishing order:");
        System.out.println("#1: " + placings.get(0).getTeamName());
        System.out.println("#2: " + placings.get(1).getTeamName());
    }

    /**
     * Randomly chooses finishing order
     * @return List the placings
     */
    private void generatePlacings() {
        placings.add(competitor1);
        placings.add(competitor2);
        Collections.shuffle(placings);
    }

}
