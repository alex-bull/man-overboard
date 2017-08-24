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
            Integer crewNumber = hexByteArrayToInt(Arrays.copyOfRange(packet, currentByte, currentByte+1));
            double latitude = parseCoordinate(Arrays.copyOfRange(packet, currentByte+1, currentByte+5));
            double longitude = parseCoordinate(Arrays.copyOfRange(packet, currentByte+5, currentByte+9));
            crewLocations.add(new CrewLocation(crewNumber,new MutablePoint(latitude,longitude)));
            currentByte+=9;
            System.out.println(packet.length);
        }
        return crewLocations;
    }


}
