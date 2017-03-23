package seng302;

import javafx.scene.paint.Color;
import org.junit.Before;
import org.junit.Test;
import seng302.Model.Boat;
import seng302.Model.MutablePoint;

import static org.junit.Assert.assertTrue;


/**
 * Created by mgo65 on 3/03/17.
 */
public class BoatTest {

    Boat boat;
    @Before
    public void setUp(){
        boat= new Boat("A", 10, new MutablePoint(10.0, 29.0), Color.BLACK,"ABC");
    }

    @Test
    public void testGetTeamName () {
        assertTrue(boat.getTeamName().equals("A"));
    }

    @Test
    public void testGetVelocity () {
        assertTrue(boat.getVelocity() == 10);
    }

    @Test
    public void testGetAbbrev(){
        assertTrue(boat.getAbbreName()=="ABC");
    }
}
