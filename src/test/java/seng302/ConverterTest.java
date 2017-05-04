package seng302;

import org.junit.Assert;
import org.junit.Test;
import seng302.Parsers.Converter;
import seng302.Parsers.RaceStatusEnum;

import java.util.Arrays;
import java.util.List;

import static seng302.Parsers.Converter.hexByteArrayToInt;

/**
 * Created by psu43 on 13/04/17.
 */
public class ConverterTest {

    @Test
    public void testInvalidRaceStatusToString() {
        String statusString = Converter.raceStatusToString(RaceStatusEnum.NOT_VALID);
        Assert.assertNotNull(statusString);
        String expectedString = "No status found";
        Assert.assertEquals(expectedString, statusString);
    }

    @Test
    public void testPreparatoryStatusToString() {
        String status = Converter.raceStatusToString(RaceStatusEnum.PREPARATORY);
        String expected = "Preparatory";
        Assert.assertEquals(expected, status);
    }

    @Test
    public void testSingleByteArrayToLong() {
        byte[] bytes = new byte[1];
        byte b = 5;
        bytes[0] = b;
        long n = Converter.hexByteArrayToLong(bytes);
        Assert.assertTrue(n == 5);
    }
}
