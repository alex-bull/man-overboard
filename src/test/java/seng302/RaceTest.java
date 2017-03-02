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
public class RaceTest {
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();

    @Before
    public void setUpStreams() {
        System.setOut(new PrintStream(outContent));
    }

    @After
    public void cleanUpStreams() {
        System.setOut(null);
    }


    @Test
    public void testGeneratePlacings() {
        Boat boat1 = new Boat("a");
        Boat boat2 = new Boat("b");
        Race race = new Race(boat1, boat2);
        race.start();
        assertEquals(race.getPlacings().size(), 2);
        assertTrue(race.getPlacings().contains(boat1));
        assertTrue(race.getPlacings().contains(boat2));
    }

}
