package parsers;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import parsers.markRounding.MarkRoundingParser;

import java.util.Arrays;

import static org.junit.Assert.assertTrue;
import static parsers.Converter.hexByteArrayToInt;

/**
 * Created by jar156 on 15/05/17.
 * Tests for parsing the mark rounding data
 */
public class MarkRoundingParserTest {
    private MarkRoundingParser markRoundingParser;

    @Before
    public void setUp() {
        markRoundingParser = new MarkRoundingParser();
    }

    @Test
    public void ignoresEmptyMarkRoundingPacket() {
        try {
            Assert.assertNotNull(markRoundingParser);
        } catch (Exception e) {
            Assert.fail();
        }
    }

    @Test
    public void ignoresMarkRoundingPacketWithMissingInfo() {
        byte[] packet = new byte[12];

        try {
            Assert.assertNotNull(markRoundingParser);
            Assert.assertNull(markRoundingParser.processMessage(packet));
        } catch (Exception e) {
            Assert.fail();
        }
    }

    @Test
    public void parsesValidMarkId() {
        byte[] packet = new byte[25];
        packet[20] = 3;

        try {
            assertTrue(markRoundingParser.processMessage(packet).getMarkID() == 3);
        } catch (Exception e) {
            Assert.fail();
        }
    }

    @Test
    public void parsesValidSourceId() {
        byte[] packet = new byte[25];
        packet[13] = 3;
        packet[14] = 9;
        packet[15] = 1;
        packet[16] = 1;

        int expectedSourceId = hexByteArrayToInt(Arrays.copyOfRange(packet, 13, 17));

        try {
            assertTrue(markRoundingParser.processMessage(packet).getSourceID() == expectedSourceId);
        } catch (Exception e) {
            Assert.fail();
        }
    }
}
