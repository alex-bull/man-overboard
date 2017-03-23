package seng302.Model;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by khe60 on 24/03/17.
 */
public class MarkTest {
    Mark mark;

    @Before
    public void setUp() throws Exception {
        mark = new Mark("Test Mark", new MutablePoint(50.0, 50.0), 0);
        mark.setExitHeading(45.0);
    }

    @Test
    public void isLine() throws Exception {
        assertFalse(mark.isLine());
    }

    @Test
    public void isFinish() throws Exception {
        assertFalse(mark.isFinish());
    }

    @Test
    public void getName() throws Exception {
        assertEquals(mark.getName(), "Test Mark");
    }

    @Test
    public void getLocations() throws Exception {
        assertEquals(mark.getLocations().get(0), new MutablePoint(50.0, 50.0));
    }

    @Test
    public void getCentre() throws Exception {
        assertEquals(mark.getCentre(), mark.getLocations().get(0));
    }

    @Test
    public void getIndex() throws Exception {
        assertEquals(mark.getIndex(), 0);
    }


    @Test
    public void setExitHeading() throws Exception {
        mark.setExitHeading(50.0);
        assertEquals(mark.getExitHeading(), 50.0, 0.01);
    }

    @Test
    public void getExitHeading() throws Exception {
        assertEquals(mark.getExitHeading(), 45.0, 0.01);
    }

}