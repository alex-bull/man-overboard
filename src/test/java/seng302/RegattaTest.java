package seng302;

import org.junit.Test;

import java.util.ArrayList;

/**
 * Created by Pang on 4/03/17.
 */
public class RegattaTest {

    @Test
    public void testCreateMatches() {

        ArrayList<Competitor> comps = new ArrayList<>();
        ArrayList<Race> races = new ArrayList<>();

        races.add(new MatchRace());
        comps.add(new Boat("A"));
        comps.add(new Boat("B"));
        comps.add(new Boat("C"));
        Regatta regatta = new Regatta(comps, races);

        regatta.begin();
        Race race = regatta.getRaces().get(0);
        Competitor comp1 = race.getCompetitor1();
        Competitor comp2 = race.getCompetitor2();

    }
}
