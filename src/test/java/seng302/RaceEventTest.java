package seng302;

import org.junit.Test;
import seng302.Model.*;

import static junit.framework.Assert.assertTrue;

/**
 * Created by Pang on 6/03/17.
 */
public class RaceEventTest {

    @Test
    public void testGetIsFinish () {
        Competitor boatA = new Boat("Oracle Team USA", 20, new Point(100.0, 100.0));
        Competitor boatB = new Boat("Emirates Team New Zealand", 19, new Point(200.0, 200.0));
        CourseFeature A = new Mark("A", new Point(12.0, 2.0), false);
        CourseFeature B = new Mark("B", new Point(12.0, 2.0), true);
        RaceEvent raceEvent = new RaceEvent(boatA, 10, 10, "Start", 10.0, false, A);

        assertTrue(!raceEvent.getIsFinish());

        RaceEvent raceEvent2 = new RaceEvent(boatB, 10, 10, "Start", null, true, B);

        assertTrue(raceEvent2.getIsFinish());
    }

    @Test
    public void testGetEventString () {
        Competitor boatA = new Boat("A", 20, new Point(100.0, 100.0));
        Competitor boatB = new Boat("B", 19, new Point(200.0, 200.0));
        CourseFeature A = new Mark("A", new Point(12.0, 2.0), false);
        CourseFeature B = new Mark("B", new Point(12.0, 2.0), true);
        RaceEvent raceEvent = new RaceEvent(boatA, 10, 10, "Start", 10.0, false, A);
        System.out.println(raceEvent.getEventString());
        assertTrue(raceEvent.getEventString().equals("Time: 00:10, Event: A passed the Start, Heading: 10.00"));

        RaceEvent raceEvent2 = new RaceEvent(boatB, 10, 10, "Start", null, true, B);
        assertTrue(raceEvent2.getEventString().equals("Time: 00:10, Event: B passed the Start"));


    }

}
