package parsers;

import org.junit.Assert;
import org.junit.Test;
import parsers.markRounding.MarkRoundingParser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by jar156 on 15/05/17.
 */
public class MarkRoundingParserTest {

    @Test
    public void ignoresEmptyMarkRoundingPacket() {
        byte[] packet = {};

        try {
            MarkRoundingParser markRoundingParser = new MarkRoundingParser();
            Assert.assertNotNull(markRoundingParser);
        } catch (Exception e) {
            Assert.fail();
        }
    }

    @Test
    public void ignoresMarkRoundingPacketWithMissingInfo() {
        byte[] header = {37,0,0,0};
        byte[] packet = new byte[12];

        try {
            MarkRoundingParser markRoundingParser = new MarkRoundingParser();
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
            MarkRoundingParser markRoundingParser = new MarkRoundingParser();
            assertTrue(markRoundingParser.processMessage(packet).getMarkID() == 3);
        } catch (Exception e) {
            Assert.fail();
        }
    }
}
