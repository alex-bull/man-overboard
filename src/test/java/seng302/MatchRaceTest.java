package seng302;

import org.junit.Before;
import org.junit.Test;
import seng302.Model.*;


import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

/**
 * Created by mgo65 on 3/03/17.
 */
public class MatchRaceTest {

    private List<Competitor> competitors = new ArrayList<>();
    private List<CourseFeature> points = new ArrayList<>();

    @Before
    public void createDependencies () {
        //create competitors

        competitors.add(new Boat("Oracle Team USA", 20, new Point(0.0, 0.0)));
        competitors.add(new Boat("Emirates Team New Zealand", 19, new Point(0.0, 0.0)));
        competitors.add(new Boat("Ben Ainslie Racing", 18,  new Point(-10.0, 0.0)));
        competitors.add(new Boat("SoftBank Team Japan", 17,  new Point(-10.0, 0.0)));
        competitors.add(new Boat("Team France", 16,  new Point(-20.0, 0.0)));
        competitors.add(new Boat("Artemis Racing", 15,  new Point(-11.0, 0.0)));

        points.add(new Mark("PreStart", new Point(-200.0, 0.0)));
        points.add(new Mark("Start Gate", new Point(0.0, 0.0)));
        points.add(new Mark("Mark", new Point(500.0, 50.0)));
        points.add(new Mark("Leeward Gate", new Point(600.0, 900.0)));
        points.add(new Mark("Windward Gate", new Point(350.0, -350.0)));
        points.add(new Mark("Leeward Gate", new Point(600.0, 900.0)));
        points.add(new Mark("Finish", new Point(250.0, 1150.0), true));

    }

    @Test
    public void testGetCompetitors () {

        MatchRace matchRace = new MatchRace(1);
        matchRace.setCompetitors(competitors);

        assertTrue(matchRace.getCompetitors().equals(competitors));
    }

    @Test
    public void testStart () {
        MatchRace matchRace = new MatchRace(1000);

        matchRace.setCompetitors(competitors);
        matchRace.setCourse(points);
        matchRace.start();

        assertTrue(matchRace.getFinishingOrder().size() == 6);


    }
}
