package parsers;

import org.junit.Assert;
import org.junit.Test;
import parsers.powerUp.PowerUpParser;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * Created by psu43 on 8/09/17.
 * Power up parser test
 */
public class PowerUpParserTest {

    private PowerUpParser powerUpParser = new PowerUpParser();
    @Test
    public void ignoresEmptyPowerUpPacket() {
        byte[] packet = {};
        try {
            assertNotNull(powerUpParser);
            assertNull(powerUpParser.parsePowerUp(packet));
        } catch (Exception e) {
            Assert.fail();
        }
    }

    @Test
    public void ignoresPowerUpPacketWithMissingInfo() {
        byte[] packet = new byte[1];

        try {
            assertNotNull(powerUpParser);
            assertNull(powerUpParser.parsePowerUp(packet));
        } catch (Exception e) {
            Assert.fail();
        }
    }
}
