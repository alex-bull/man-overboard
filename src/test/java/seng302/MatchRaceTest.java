package seng302;

import org.junit.Before;
import org.junit.Test;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

/**
 * Created by mgo65 on 3/03/17.
 */
public class MatchRaceTest {

    private List<Competitor> competitors = new ArrayList<>();
    private List<CoursePoint> points = new ArrayList<>();

    @Before
    public void createDependencies () {
        //create competitors

        competitors.add(new Boat("Oracle Team USA", 9));
        competitors.add(new Boat("Emirates Team New Zealand", 8));
        competitors.add(new Boat("Ben Ainslie Racing", 7));
        competitors.add(new Boat("SoftBank Team Japan", 6));
        competitors.add(new Boat("Team France", 5));
        competitors.add(new Boat("Artemis Racing", 4));

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
