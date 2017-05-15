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
            RaceStatusParser raceStatusParser = new RaceStatusParser();
            assertNotNull(raceStatusParser);
            assertNull(raceStatusParser.processMessage(packet));
        } catch (Exception e) {
            Assert.fail();
        }
    }

    @Test
    public void ignoresRacePacketWithMissingInfo() {
        byte[] packet = new byte[12];

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
            assertEquals("TERMINATED", raceStatusParser.processMessage(packet).getRaceStatus());
        } catch (Exception e) {
            Assert.fail();
        }
    }
}
