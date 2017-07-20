package utilities;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by khe60 on 19/07/17.
 * Test for polar table
 */
public class PolarTableTest {
    private PolarTable polarTable;

    @Before
    public void setUp() throws Exception {
        polarTable = new PolarTable("/polars/VO70_polar.txt", 12);
    }

    @Test
    public void TableTest() throws Exception{
        assertEquals(polarTable.getSpeed(0.0), 0.0, 0.01);
        assertEquals(polarTable.getSpeed(47.3), 10.3, 0.01);
        assertEquals(polarTable.getSpeed(70.4), 12.45, 0.01);
        assertEquals(polarTable.getSpeed(92.2), 13.05, 0.01);
        assertEquals(polarTable.getSpeed(107.9), 13.39, 0.01);
        assertEquals(polarTable.getSpeed(121.4), 13.48, 0.01);
        assertEquals(polarTable.getSpeed(141.1), 11.30, 0.01);
        assertEquals(polarTable.getSpeed(180.0), 7.36, 0.01);


    }

}