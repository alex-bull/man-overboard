package seng302;

import javafx.scene.paint.Color;
import org.junit.Before;
import org.junit.Test;
import seng302.Model.*;

import static junit.framework.Assert.assertTrue;

/**
 * Created by Pang on 6/03/17.
 */
public class RaceEventTest {
    Competitor boatA;
    Competitor boatB;

//    @Before
//    public void setUp(){
//        boatA = new Boat("Oracle Team USA", 20, new MutablePoint(100.0, 100.0), Color.BLACK,"USA");
//        boatB = new Boat("Emirates Team New Zealand", 19, new MutablePoint(200.0, 200.0), Color.YELLOW,"NZL");
//    }
//
//    @Test
//    public void testGetIsFinish () {
//        CourseFeature A = new Mark("A", new MutablePoint(12.0, 2.0), false);
//        CourseFeature B = new Mark("B", new MutablePoint(12.0, 2.0), true);
//        RaceEvent raceEvent = new RaceEvent(boatA, 10, 10, "Start", 10.0, false, A);
//
//        assertTrue(!raceEvent.getIsFinish());
//
//        RaceEvent raceEvent2 = new RaceEvent(boatB, 10, 10, "Start", null, true, B);
//
//        assertTrue(raceEvent2.getIsFinish());
//    }
//
//    @Test
//    public void testGetEventString () {
//        CourseFeature A = new Mark("A", new MutablePoint(12.0, 2.0), false);
//        CourseFeature B = new Mark("B", new MutablePoint(12.0, 2.0), true);
//        RaceEvent raceEvent = new RaceEvent(boatA, 10, 10, "Start", 10.0, false, A);
//        System.out.println(raceEvent.getEventString());
//        assertTrue(raceEvent.getEventString().equals("Time: 00:10, Event: Orace Team USA passed the Start, Heading: 10.00"));
//
//        RaceEvent raceEvent2 = new RaceEvent(boatB, 10, 10, "Start", null, true, B);
//        assertTrue(raceEvent2.getEventString().equals("Time: 00:10, Event: Emirates Team New Zealand passed the Start"));
//
//
//    }

}
