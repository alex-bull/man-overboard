package parsers.Obstacles;

import java.util.Arrays;

import static parsers.Converter.hexByteArrayToInt;

/**
 * Created by Izzy on 7/09/17.
 * Parse a blood packet
 */
public class BloodParser {

    public static int parseBlood(byte[] packet) {

        return hexByteArrayToInt(Arrays.copyOfRange(packet, 0, 4));
    }
}
