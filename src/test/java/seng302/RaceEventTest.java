package seng302;

import org.junit.Test;
import static junit.framework.Assert.assertTrue;

/**
 * Created by Pang on 6/03/17.
 */
public class RaceEventTest {

    @Test
    public void testGetIsFinish () {
        RaceEvent raceEvent = new RaceEvent("A", 10, "Start", 10.0);

        assertTrue(!raceEvent.getIsFinish());

        RaceEvent raceEvent2 = new RaceEvent("B", 10, "Start", null);

        assertTrue(raceEvent2.getIsFinish());
    }

    @Test
    public void testGetEventString () {
        RaceEvent raceEvent = new RaceEvent("A", 10, "Start", 10.0);
        assertTrue(raceEvent.getEventString().equals("Time: 10s, Event: A passed the Start, Heading: 10.00"));

        RaceEvent raceEvent2 = new RaceEvent("B", 10, "Start", null);
        assertTrue(raceEvent2.getEventString().equals("Time: 10s, Event: B passed the Start"));


    }

}
