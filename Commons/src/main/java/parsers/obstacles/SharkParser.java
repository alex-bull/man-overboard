package parsers.obstacles;

import models.MutablePoint;
import models.Shark;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static parsers.Converter.hexByteArrayToInt;
import static parsers.Converter.hexByteArrayToLong;
import static parsers.Converter.parseCoordinate;

/**
 * Created by Izzy on 5/09/17.
 *
 */
public class SharkParser {

    public static List<Shark> parseShark(byte[] packet) {
        List<Shark> sharkLocations=new ArrayList<>();
        Integer n = hexByteArrayToInt(Arrays.copyOfRange(packet, 0, 1));
        int currentByte=1;
        for(int i=0;i<n;i++){
            //TODO: ADD HEADING
            Integer sourceId = hexByteArrayToInt(Arrays.copyOfRange(packet, currentByte, currentByte+4));
            Integer sharkNumber = hexByteArrayToInt(Arrays.copyOfRange(packet, currentByte+4, currentByte+5));
            double latitude = parseCoordinate(Arrays.copyOfRange(packet, currentByte+5, currentByte+9));
            double longitude = parseCoordinate(Arrays.copyOfRange(packet, currentByte+9, currentByte+13));
            Integer velocity = hexByteArrayToInt(Arrays.copyOfRange(packet, currentByte+13, currentByte+14));
            double heading = hexByteArrayToLong(Arrays.copyOfRange(packet, currentByte+14, currentByte+18));
            sharkLocations.add(new Shark(sourceId,sharkNumber,new MutablePoint(latitude,longitude), velocity, heading));
            currentByte+=18;

        }
        return sharkLocations;
    }

}
