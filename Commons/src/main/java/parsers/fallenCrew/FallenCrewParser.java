package parsers.fallenCrew;

import models.CrewLocation;
import models.MutablePoint;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static parsers.Converter.hexByteArrayToInt;
import static parsers.Converter.parseCoordinate;

/**
 * Created by khe60 on 24/08/17.
 */
public class FallenCrewParser {


    public static List<CrewLocation> parseFallenCrew(byte[] packet) {
        List<CrewLocation> crewLocations=new ArrayList<>();
        Integer n = hexByteArrayToInt(Arrays.copyOfRange(packet, 0, 1));
        int currentByte=1;
        for(int i=0;i<n;i++){
            Integer sourceId = hexByteArrayToInt(Arrays.copyOfRange(packet, currentByte, currentByte+4));
            Integer crewNumber = hexByteArrayToInt(Arrays.copyOfRange(packet, currentByte+4, currentByte+5));
            double latitude = parseCoordinate(Arrays.copyOfRange(packet, currentByte+5, currentByte+9));
            double longitude = parseCoordinate(Arrays.copyOfRange(packet, currentByte+9, currentByte+13));
            crewLocations.add(new CrewLocation(sourceId,crewNumber,new MutablePoint(latitude,longitude)));
            currentByte+=13;

        }
        return crewLocations;
    }


}
