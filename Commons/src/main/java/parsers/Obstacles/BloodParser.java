package parsers.Obstacles;

import models.Blood;
import models.MutablePoint;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static parsers.Converter.hexByteArrayToInt;
import static parsers.Converter.parseCoordinate;

/**
 * Created by Izzy on 7/09/17.
 * Parse a blood packet
 */
public class BloodParser {

    public static int parseBlood(byte[] packet) {

        return hexByteArrayToInt(Arrays.copyOfRange(packet, 0, 4));
    }
}
