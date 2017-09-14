package utilities;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static utilities.Utility.fileToString;

/**
 * Created by khe60 on 30/05/17.
 * Tests for Utility methods
 */
public class UtilityTest {
    @Test
    public void fileToStringTest() throws Exception {
        String string = fileToString("/test/fileToStringTest.txt");
        assertEquals("This is the test file\n", string);
    }

}