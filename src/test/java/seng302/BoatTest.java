package seng302;

import javafx.scene.paint.Color;
import org.junit.Before;
import org.junit.Test;
import seng302.Model.Boat;
import seng302.Model.MutablePoint;

import static org.junit.Assert.*;

/**
 * Created by khe60 on 24/03/17.
 */
public class BoatTest {
    Boat boat;
    @Before
    public void setUp(){
        boat= new Boat("A", 10, new MutablePoint(10.0, 29.0), Color.BLACK,"ABC");
    }

    @Test
    public void getAbbreName() throws Exception {
        assertEquals(boat.getAbbreName(),"ABC");
        assertNotEquals(boat.getAbbreName(),"123");
    }

    @Test
    public void getColor() throws Exception {
        assertEquals(boat.getColor(),Color.BLACK);
    }

    @Test
    public void getTeamName() throws Exception {
        assertEquals(boat.getTeamName(),"A");
    }

    @Test
    public void getVelocity() throws Exception {
        assertEquals(boat.getVelocity(),10);
    }

    @Test
    public void getPosition() throws Exception {
        MutablePoint p=new MutablePoint(10.0,29.0);
        assertEquals(boat.getPosition(),p);
    }

}