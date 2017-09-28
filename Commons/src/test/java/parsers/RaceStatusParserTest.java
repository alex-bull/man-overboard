package parsers;

import org.junit.Assert;
import org.junit.Test;
import parsers.raceStatus.RaceStatusParser;

import static org.junit.Assert.*;

/**
 * Created by jar156 on 15/05/17.
 * Tests for race status parser
 */
public class RaceStatusParserTest {

    @Test
    public void ignoresEmptyRacePacket() {
        byte[] packet = {};

        try {
            RaceStatusParser raceStatusParser = new RaceStatusParser();
            assertNotNull(raceStatusParser);
            raceStatusParser.update(packet);
            assertNull(raceStatusParser.getRaceId());
        } catch (Exception e) {
            Assert.fail();
        }
    }

    @Test
    public void ignoresRacePacketWithMissingInfo() {
        try {
            RaceStatusParser raceStatusParser = new RaceStatusParser();
            assertNotNull(raceStatusParser);
        } catch (Exception e) {
            Assert.fail();
        }
    }

    @Test
    public void parsesValidStatus() {
        byte[] packet = new byte[25];
        packet[11] = 8;

        try {
            RaceStatusParser raceStatusParser = new RaceStatusParser();
            raceStatusParser.update(packet);
            assertEquals("TERMINATED", raceStatusParser.getRaceStatus().toString());
        } catch (Exception e) {
            Assert.fail();
        }
    }
}
