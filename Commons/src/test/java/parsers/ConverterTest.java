package parsers;

import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static parsers.Converter.*;

/**
 * Created by psu43 on 13/04/17.
 * Tests for the Converter Class
 */
public class ConverterTest {
    @Test
    public void hexByteArrayToIntTest() throws Exception {
        byte[] testByteArray = {0x68, 0x00, 0x00, 0x00};
        assertEquals(104, hexByteArrayToInt(testByteArray));


        byte[] testByteArray2 = {0x68, -83, -38, 0x2E};
        assertEquals(786083176, hexByteArrayToInt(testByteArray2));
    }

    @Test
    public void hexByteArrayToLongTest() throws Exception {
        byte[] testByteArray = {0x68, 83, 127, -127, 12, 12};
        assertEquals(13247851746152L, hexByteArrayToLong(testByteArray));


        byte[] testByteArray2 = {0x68, -83, -38, 0x2E, 0, 0};
        assertEquals(786083176L, hexByteArrayToLong(testByteArray2));
    }

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

    @Test
    public void testConvertRelativeTimeReturnsZeroWhenOneOfTheInputsIsZero() {
        long testRealTime = 100020000;
        long testMessageTime = 100000000;
        Assert.assertEquals(0, Converter.convertToRelativeTime(0, testMessageTime));
        Assert.assertEquals(0, Converter.convertToRelativeTime(testRealTime, 0));
    }

    @Test
    public void testConvertRelativeTimeReturnsPositiveWhenMessageTimeAfterRealTime() {
        long testRealTime = 20 * 1000;
        long testMessageTime = 30 * 1000; // milliseconds
        Assert.assertTrue(Converter.convertToRelativeTime(testRealTime, testMessageTime) > 0);
    }

    @Test
    public void testConvertRelativeTimeReturnsNegativeWhenMessageTimeBeforeRealTime() {
        long testRealTime = 30 * 1000;
        long testMessageTime = 20 * 1000; // milliseconds
        Assert.assertTrue(Converter.convertToRelativeTime(testRealTime, testMessageTime) < 0);
    }

    @Test
    public void testConvertRelativeTimeReturnsTheCorrectDifference() {
        long testRealTime = 100 * 1000;
        long testMessageTime = 200 * 1000;
        long difference = 100;
        Assert.assertEquals(difference, Converter.convertToRelativeTime(testRealTime, testMessageTime));
    }


    @Test
    public void testParseHeadingNorth() {
        byte[] testByteArray = {0x00, 0x00};
        Double heading = parseHeading(testByteArray);
        Assert.assertTrue(heading == 0);
    }

    @Test
    public void testParseHeadingSouth() {
        byte[] testByteArray = {0x7F, 0x7F};
        Double heading = parseHeading(testByteArray);
        Assert.assertEquals(heading, 180, 1);
    }

    @Test
    public void testParseHeading() {
        byte[] testByteArray = {0x68, 0x00};
        Double heading = parseHeading(testByteArray);
        Assert.assertTrue(heading == 0.5712890625);
    }

    @Test
    public void testParseHeading2() {
        byte[] testByteArray = {0x22, 0x33};
        Double heading = parseHeading(testByteArray);
        Assert.assertTrue(heading == 71.905517578125);
    }

}
