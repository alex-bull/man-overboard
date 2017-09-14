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

    public static List<Blood> parseBlood(byte[] packet) {
        List<Blood> deadCrewLocations = new ArrayList<>();
        Integer n = hexByteArrayToInt(Arrays.copyOfRange(packet, 0, 1));
        int currentByte=1;
        for(int i=0;i<n;i++){
            Integer sourceId = hexByteArrayToInt(Arrays.copyOfRange(packet, currentByte, currentByte+4));
            double latitude = parseCoordinate(Arrays.copyOfRange(packet, currentByte+4, currentByte+8));
            double longitude = parseCoordinate(Arrays.copyOfRange(packet, currentByte+8, currentByte+12));
            deadCrewLocations.add(new Blood(sourceId, new MutablePoint(latitude,longitude)));
            currentByte+=12;

        }
        return deadCrewLocations;
    }
}
