package utilities;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by khe60 on 19/07/17.
 * Test for polar table
 */
public class PolarTableTest {
    private PolarTable polarTable1;

    @Before
    public void setUp() throws Exception {
        polarTable1 = new PolarTable("/polars/VO70_polar.txt", 12);
    }

    @Test
    public void testGetSpeed() throws Exception {
        assertEquals(polarTable1.getSpeed(0.0), 0.0, 0.01);
        assertEquals(polarTable1.getSpeed(47.3), 10.3, 0.01);
        assertEquals(polarTable1.getSpeed(70.4), 12.45, 0.01);
        assertEquals(polarTable1.getSpeed(92.2), 13.05, 0.01);
        assertEquals(polarTable1.getSpeed(107.9), 13.39, 0.01);
        assertEquals(polarTable1.getSpeed(121.4), 13.48, 0.01);
        assertEquals(polarTable1.getSpeed(141.1), 11.30, 0.01);
        assertEquals(polarTable1.getSpeed(180.0), 7.36, 0.01);

    }

    @Test
    public void testGetMinimalTwa() throws Exception {
        assertEquals(polarTable1.getMinimalTwa(4.0, true), 70.0, 0.01);
        assertEquals(polarTable1.getMinimalTwa(6.0, true), 65.95, 0.01);
        assertEquals(polarTable1.getMinimalTwa(7.4, true), 55.17, 0.01);
        assertEquals(polarTable1.getMinimalTwa(8.0, true), 51.2, 0.01);
        assertEquals(polarTable1.getMinimalTwa(12.0, true), 47.3, 0.01);
        assertEquals(polarTable1.getMinimalTwa(16.0, true), 45.0, 0.01);

        assertEquals(polarTable1.getMinimalTwa(4.0, false), 120.6, 0.01);
        assertEquals(polarTable1.getMinimalTwa(6.0, false), 135.69, 0.01);
        assertEquals(polarTable1.getMinimalTwa(7.4, false), 134.85, 0.01);
        assertEquals(polarTable1.getMinimalTwa(8.0, false), 134.2, 0.01);
        assertEquals(polarTable1.getMinimalTwa(12.0, false), 141.1, 0.01);
        assertEquals(polarTable1.getMinimalTwa(16.0, false), 142.9, 0.01);


    }

}