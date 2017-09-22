package parsers.powerUp;

import java.util.Arrays;

import static parsers.Converter.hexByteArrayToInt;

/**
 * Created by psu43 on 6/09/17.
 * parse power up taken
 */
public class PowerUpTakenParser {

    private int boatId;
    private int powerId;
    private int duration;

    /**
     * Parses power up taken packet
     *
     * @param packet byte[] the packet of the power up taken
     */
    public PowerUpTakenParser(byte[] packet) {
        this.boatId = hexByteArrayToInt(Arrays.copyOfRange(packet, 0, 4));
        this.powerId = hexByteArrayToInt(Arrays.copyOfRange(packet, 4, 8));
        this.duration = hexByteArrayToInt(Arrays.copyOfRange(packet, 8, 12));
    }

    public int getBoatId() {
        return boatId;
    }

    public int getPowerId() {
        return powerId;
    }

    public int getDuration() {
        return duration;
    }

}
