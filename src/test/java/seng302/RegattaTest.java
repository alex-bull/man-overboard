package seng302;

import org.junit.Test;

import static junit.framework.TestCase.assertFalse;;

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
        comps.add(new Boat("A", 10));
        comps.add(new Boat("B", 6));
        comps.add(new Boat("C", 4));
        Regatta regatta = new Regatta(comps, races, null);

        regatta.begin();
        Race race = regatta.getRaces().get(0);
        Competitor comp1 = race.getCompetitor1();
        Competitor comp2 = race.getCompetitor2();
        assertFalse(comp1.getTeamName().equals(comp2.getTeamName()));


    }
}
