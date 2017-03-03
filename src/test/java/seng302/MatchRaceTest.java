package seng302;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static junit.framework.Assert.assertTrue;
import static junit.framework.TestCase.assertEquals;

/**
 * Created by mgo65 on 3/03/17.
 */
public class MatchRaceTest {

    @Test
    public void testGeneratePlacings() {
        Boat boat1 = new Boat("a");
        Boat boat2 = new Boat("b");
        MatchRace race = new MatchRace(boat1, boat2);
        race.start();
        assertEquals(race.getPlacings().size(), 2);
        assertTrue(race.getPlacings().contains(boat1));
        assertTrue(race.getPlacings().contains(boat2));
    }

}
