package seng302.TestClasses;

import org.junit.Test;
import seng302.TestMockDatafeed.BinaryPackager;

import static org.junit.Assert.*;

/**
 * Created by mgo65 on 1/05/17.
 */
public class BinaryPackagerTest {

    @Test
    public void testPackageBoatLocation() {
        BinaryPackager a = new BinaryPackager();
        byte[] b = a.packageBoatLocation(12, 123.444, 234.434, 65535.0, 20.3);

        assertEquals(75, b.length);
        assertEquals(71, b[0]);
    }

}