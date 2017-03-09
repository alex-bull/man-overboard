package seng302;

import org.junit.Test;
import static org.junit.Assert.assertTrue;


/**
 * Created by mgo65 on 3/03/17.
 */
public class BoatTest {


    @Test
    public void testGetTeamName () {
        Boat boat = new Boat("A", 10);
        assertTrue(boat.getTeamName().equals("A"));
    }

    @Test
    public void testGetVelocity () {
        Boat boat = new Boat("A", 10);
        assertTrue(boat.getVelocity() == 10);
    }
}
