package seng302;

import javafx.util.Pair;
import org.junit.Test;

import static junit.framework.TestCase.assertFalse;;

import java.util.ArrayList;
import java.util.DoubleSummaryStatistics;
import java.util.List;

/**
 * Created by Pang on 4/03/17.
 */
public class RegattaTest {

    @Test
    public void testCreateMatches() {

        List<Competitor> comps = new ArrayList<>();
        List<Race> races = new ArrayList<>();

        races.add(new MatchRace());
        comps.add(new Boat("A", 10));
        comps.add(new Boat("B", 6));
        comps.add(new Boat("C", 4));

        //create the marks
        List<CoursePoint> points = new ArrayList<>();
        points.add(new Mark("PreStart", new Point(-20.0, 0.0)));
        points.add(new Mark("Start Gate", new Point(0.0, 0.0)));
        points.add(new Mark("Mark", new Point(100.0, 10.0)));
        points.add(new Mark("Leeward Gate", new Point(120.0, 180.0)));
        points.add(new Mark("Windward Gate", new Point(70.0, -70.0)));
        points.add(new Mark("Leeward Gate", new Point(120.0, 180.0)));
        points.add(new Mark("Finish", new Point(50.0, 230.0)));

        Regatta regatta = new Regatta(comps, races, points);

        regatta.begin();
        Race race = regatta.getRaces().get(0);
        Competitor comp1 = race.getCompetitors().get(0);
        Competitor comp2 = race.getCompetitors().get(1);

        //check that team names are unique
        assertFalse(comp1.getTeamName().equals(comp2.getTeamName()));

    }
}
