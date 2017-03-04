package seng302;

import javafx.util.Pair;
import org.junit.Test;

import static junit.framework.TestCase.assertFalse;;

import java.util.ArrayList;
import java.util.DoubleSummaryStatistics;

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

        //create the marks
        ArrayList<CoursePoint> points = new ArrayList<>();
        points.add(new Mark("PreStart", new Pair<>(-20.0, 0.0)));
        points.add(new Mark("Start Gate", new Pair<>(0.0, 0.0)));
        points.add(new Mark("Mark", new Pair<>(100.0, 10.0)));
        points.add(new Mark("Leeward Gate", new Pair<>(120.0, 180.0)));
        points.add(new Mark("Windward Gate", new Pair<>(70.0, -70.0)));
        points.add(new Mark("Leeward Gate", new Pair<>(120.0, 180.0)));
        points.add(new Mark("Finish", new Pair<>(50.0, 230.0)));

        Regatta regatta = new Regatta(comps, races, points);

        regatta.begin();
        Race race = regatta.getRaces().get(0);
        Competitor comp1 = race.getCompetitors().get(0);
        Competitor comp2 = race.getCompetitors().get(1);

        //check that team names are unique
        assertFalse(comp1.getTeamName().equals(comp2.getTeamName()));

    }
}
