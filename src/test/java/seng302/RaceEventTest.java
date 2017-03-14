package seng302;

import org.junit.Test;
import seng302.Model.RaceEvent;

import static junit.framework.Assert.assertTrue;

/**
 * Created by Pang on 6/03/17.
 */
public class RaceEventTest {

    @Test
    public void testGetIsFinish () {
        RaceEvent raceEvent = new RaceEvent("A", 10, 10, "Start", 10.0, false);

        assertTrue(!raceEvent.getIsFinish());

        RaceEvent raceEvent2 = new RaceEvent("B", 10, 10, "Start", null, true);

        assertTrue(raceEvent2.getIsFinish());
    }

    @Test
    public void testGetEventString () {
        RaceEvent raceEvent = new RaceEvent("A", 10, 10, "Start", 10.0, false);
        assertTrue(raceEvent.getEventString().equals("Time: 00:10, Event: A passed the Start, Heading: 10.00"));

        RaceEvent raceEvent2 = new RaceEvent("B", 10, 10, "Start", null, false);
        assertTrue(raceEvent2.getEventString().equals("Time: 00:10, Event: B passed the Start"));


    }

}
