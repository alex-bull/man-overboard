package seng302;

import org.junit.Test;
import static org.junit.Assert.assertTrue;

/**
 * Created by Pang on 6/03/17.
 */
public class MarkTest {

    @Test
    public void testGetName () {

        Point point = new Point(1.0, 2.0);
        Mark mark = new Mark("A", point);

        assertTrue(mark.getName().equals("A"));
    }

    @Test
    public void testGetLocation () {
        Point point = new Point(1.0, 2.0);
        Mark mark = new Mark("A", point);

        assertTrue(mark.getLocation().equals(point));
    }

    @Test
    public void testGetExitHeading () {
        Point point = new Point(1.0, 2.0);
        Mark mark = new Mark("A", point);
        mark.setExitHeading(10.0);

        assertTrue(mark.getExitHeading() == 10.0);
    }
}
