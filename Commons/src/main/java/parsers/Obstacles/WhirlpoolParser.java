package parsers.Obstacles;

import models.Blood;
import models.MutablePoint;
import models.Whirlpool;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static parsers.Converter.hexByteArrayToInt;
import static parsers.Converter.parseCoordinate;

/**
 * Created by msl47 on 12/09/17.
 */
public class WhirlpoolParser {

    /**
     * parse the packet that contains the data for whirlpool
     * @param packet whirlpool data
     * @return whirlpools list of whirlpools data
     */
    public static List<Whirlpool> parseWhirlpool(byte[] packet) {
        List<Whirlpool> whirlpools = new ArrayList<>();
        Integer n = hexByteArrayToInt(Arrays.copyOfRange(packet, 0, 1));
        int currentByte=1;
        for(int i=0;i<n;i++){
            Integer sourceId = hexByteArrayToInt(Arrays.copyOfRange(packet, currentByte, currentByte+4));
            Integer duration = hexByteArrayToInt(Arrays.copyOfRange(packet, currentByte+4, currentByte+8));
            double latitude = parseCoordinate(Arrays.copyOfRange(packet, currentByte+8, currentByte+12));
            double longitude = parseCoordinate(Arrays.copyOfRange(packet, currentByte+12, currentByte+16));
            Whirlpool whirlpool = new Whirlpool(sourceId, duration, new MutablePoint(latitude, longitude));
            whirlpools.add(whirlpool);
            currentByte+=16;
        }
        return whirlpools;
    }
}
