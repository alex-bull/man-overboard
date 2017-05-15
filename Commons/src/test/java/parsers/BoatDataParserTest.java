package parsers;

import org.junit.Assert;
import org.junit.Test;
import parsers.boatLocation.BoatDataParser;

import java.util.Arrays;

import static org.junit.Assert.assertTrue;
import static parsers.Converter.hexByteArrayToInt;

/**
 * Created by jar156 on 15/05/17.
 */
public class BoatDataParserTest {

    @Test
    public void ignoresEmptyBoatLocationPacket() {
        byte[] packet = {};

        try {
            BoatDataParser boatDataParser = new BoatDataParser(packet, 100, 100);
            Assert.assertNull(boatDataParser.getBoatData());
        } catch (Exception e) {
            Assert.fail();
        }
    }

    @Test
    public void ignoresBoatLocationPacketWithMissingInfo() {
        byte[] packet = new byte[12];

        try {
            BoatDataParser boatDataParser = new BoatDataParser(packet, 100, 100);
            Assert.assertNull(boatDataParser.getBoatData());
        } catch (Exception e) {
            Assert.fail();
        }
    }

    @Test
    public void convertsValidSpeedIntoMs() {
        byte[] packet = new byte[50];
        packet[38] = 2;
        packet[39] = 10;

        int expectedSpeedInMms = hexByteArrayToInt(Arrays.copyOfRange(packet, 38, 40));

        try {
            BoatDataParser boatDataParser = new BoatDataParser(packet, 100, 100);
            assertTrue(boatDataParser.getBoatData().getSpeed() == (double)expectedSpeedInMms / 1000);
        } catch (Exception e) {
            Assert.fail();
        }
    }
}
