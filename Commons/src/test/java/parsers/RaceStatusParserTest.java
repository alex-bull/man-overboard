package parsers;

import org.junit.Assert;
import org.junit.Test;
import parsers.raceStatus.RaceStatusParser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * Created by jar156 on 15/05/17.
 */
public class RaceStatusParserTest {


    @Test
    public void ignoresEmptyRacePacket() {
        byte[] packet = {};

        try {
            RaceStatusParser raceStatusParser = new RaceStatusParser(packet);
            assertNotNull(raceStatusParser);
            assertNull(raceStatusParser.getRaceStatus());
        } catch (Exception e) {
            Assert.fail();
        }
    }

    @Test
    public void ignoresRacePacketWithMissingInfo() {
        byte[] packet = new byte[12];

        try {
            RaceStatusParser raceStatusParser = new RaceStatusParser(packet);
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
            RaceStatusParser raceStatusParser = new RaceStatusParser(packet);
            assertEquals("Terminated", raceStatusParser.getRaceStatus().getRaceStatus());
        } catch (Exception e) {
            Assert.fail();
        }
    }
}
