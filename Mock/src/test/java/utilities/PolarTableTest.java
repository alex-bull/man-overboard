package utilities;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by khe60 on 19/07/17.
 */
public class PolarTableTest {
    private PolarTable polarTable;

    @Before
    public void setUp() throws Exception {
        polarTable=new PolarTable("/polars/VO70_polar.txt");
    }

    @Test
    public void TableTest() throws Exception{
        System.out.println(polarTable.getPolar());
    }

}