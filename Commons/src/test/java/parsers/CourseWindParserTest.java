package parsers;

import org.junit.Assert;
import org.junit.Test;
import parsers.courseWind.CourseWindParser;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * Created by psu43 on 23/05/17.
 * Tests for parsing course wind data.
 */
public class CourseWindParserTest {

    private CourseWindParser courseWindParser = new CourseWindParser();

    @Test
    public void ignoresEmptyWindPacket() {
        byte[] packet = {};
        try {
            assertNotNull(courseWindParser);
            assertNull(courseWindParser.processMessage(packet));
        } catch (Exception e) {
            Assert.fail();
        }
    }

    @Test
    public void ignoresPacketWithMissingInfo() {
        byte[] packet = new byte[1];

        try {
            assertNotNull(courseWindParser);
            assertNull(courseWindParser.processMessage(packet));
        } catch (Exception e) {
            Assert.fail();
        }
    }


}
