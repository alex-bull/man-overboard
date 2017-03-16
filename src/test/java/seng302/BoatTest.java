package seng302;

import org.junit.Test;
import seng302.Model.Boat;
import seng302.Model.MutablePoint;

import static org.junit.Assert.assertTrue;


/**
 * Created by mgo65 on 3/03/17.
 */
public class BoatTest {


    @Test
    public void testGetTeamName () {
        Boat boat = new Boat("A", 10, new MutablePoint(10.0, 29.0));
        assertTrue(boat.getTeamName().equals("A"));
    }

    @Test
    public void testGetVelocity () {
        Boat boat = new Boat("A", 10, new MutablePoint(12.0, 21.0));
        assertTrue(boat.getVelocity() == 10);
    }
}
