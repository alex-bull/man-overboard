package parsers.sharks;

import models.MutablePoint;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static parsers.Converter.hexByteArrayToInt;
import static parsers.Converter.parseCoordinate;

/**
 * Created by Izzy on 7/09/17.
 *
 */
public class BloodParser {

    public static List<MutablePoint> parseBlood(byte[] packet) {
        List<MutablePoint> deadCrewLocations=new ArrayList<>();
        Integer n = hexByteArrayToInt(Arrays.copyOfRange(packet, 0, 1));
        int currentByte=1;
        for(int i=0;i<n;i++){
            double latitude = parseCoordinate(Arrays.copyOfRange(packet, currentByte, currentByte+4));
            double longitude = parseCoordinate(Arrays.copyOfRange(packet, currentByte+4, currentByte+9));
            deadCrewLocations.add(new MutablePoint(latitude,longitude));
            currentByte+=9;

        }
        return deadCrewLocations;
    }
}
