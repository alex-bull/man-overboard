package seng302;

import javafx.scene.paint.Color;
import org.junit.Before;
import org.junit.Test;
import seng302.Model.*;


import java.util.ArrayList;
import java.util.List;

import static com.sun.xml.internal.ws.dump.LoggingDumpTube.Position.Before;
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

        competitors.add(new Boat("Oracle Team USA", 20, new MutablePoint(0.0, 0.0), Color.BLACK,"USA"));
        competitors.add(new Boat("Emirates Team New Zealand", 19, new MutablePoint(30.0, 0.0), Color.RED,"NZL"));
        competitors.add(new Boat("Ben Ainslie Racing", 18, new MutablePoint(60.0, 0.0), Color.PURPLE, "GBR"));
        competitors.add(new Boat("SoftBank Team Japan", 17, new MutablePoint(90.0, 0.0), Color.YELLOW,"JPN"));
        competitors.add(new Boat("Team France", 16, new MutablePoint(120.0, 0.0), Color.BROWN,"FRA"));
        competitors.add(new Boat("Artemis Racing", 15, new MutablePoint(150.0, 0.0), Color.GREEN,"SWE"));

        points.add(new Mark("PreStart", new MutablePoint(-200.0, 0.0)));
        points.add(new Mark("Start Gate", new MutablePoint(0.0, 0.0)));
        points.add(new Mark("Mark", new MutablePoint(500.0, 50.0)));
        points.add(new Mark("Leeward Gate", new MutablePoint(600.0, 900.0)));
        points.add(new Mark("Windward Gate", new MutablePoint(350.0, -350.0)));
        points.add(new Mark("Leeward Gate", new MutablePoint(600.0, 900.0)));
        points.add(new Mark("Finish", new MutablePoint(250.0, 1150.0), true));

    }

//    @Test
//    public void testGetCompetitors () {
//
//        MatchRace matchRace = new MatchRace(1, null, new RaceCourse(points), competitors);
//        matchRace.setCompetitors(competitors);
//
//        assertTrue(matchRace.getCompetitors().equals(competitors));
//    }
//
//    @Test
//    public void testStart () {
//        MatchRace matchRace = new MatchRace(1000, null, new RaceCourse(points), competitors);
//
//        matchRace.setCompetitors(competitors);
//        matchRace.start();
//
//        assertTrue(matchRace.getFinishingOrder().size() == 6);
//
//
//    }
}
