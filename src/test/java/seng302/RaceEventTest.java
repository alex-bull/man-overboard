package seng302;

import javafx.scene.paint.Color;
import org.junit.Test;
import seng302.Model.*;

import static junit.framework.Assert.assertTrue;

/**
 * Created by Pang on 6/03/17.
 */
public class RaceEventTest {

    @Test
    public void testGetIsFinish () {
        Competitor boatA = new Boat("Oracle Team USA", 20, new MutablePoint(100.0, 100.0), Color.BLACK);
        Competitor boatB = new Boat("Emirates Team New Zealand", 19, new MutablePoint(200.0, 200.0), Color.YELLOW);
        CourseFeature A = new Mark("A", new MutablePoint(12.0, 2.0), false);
        CourseFeature B = new Mark("B", new MutablePoint(12.0, 2.0), true);
        RaceEvent raceEvent = new RaceEvent(boatA, 10, 10, "Start", 10.0, false, A);

        assertTrue(!raceEvent.getIsFinish());

        RaceEvent raceEvent2 = new RaceEvent(boatB, 10, 10, "Start", null, true, B);

        assertTrue(raceEvent2.getIsFinish());
    }

    @Test
    public void testGetEventString () {
        Competitor boatA = new Boat("A", 20, new MutablePoint(100.0, 100.0), Color.BLACK);
        Competitor boatB = new Boat("B", 19, new MutablePoint(200.0, 200.0), Color.YELLOW);
        CourseFeature A = new Mark("A", new MutablePoint(12.0, 2.0), false);
        CourseFeature B = new Mark("B", new MutablePoint(12.0, 2.0), true);
        RaceEvent raceEvent = new RaceEvent(boatA, 10, 10, "Start", 10.0, false, A);
        System.out.println(raceEvent.getEventString());
        assertTrue(raceEvent.getEventString().equals("Time: 00:10, Event: A passed the Start, Heading: 10.00"));

        RaceEvent raceEvent2 = new RaceEvent(boatB, 10, 10, "Start", null, true, B);
        assertTrue(raceEvent2.getEventString().equals("Time: 00:10, Event: B passed the Start"));


    }

}
