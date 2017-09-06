package parsers.powerUp;

import models.CrewLocation;
import models.MutablePoint;
import parsers.Converter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static parsers.Converter.hexByteArrayToInt;
import static parsers.Converter.parseCoordinate;

/**
 * Created by psu43 on 6/09/17.
 * Parse power up
 */
public class PowerUpParser {



    public PowerUp parsePowerUp(byte[] packet) {

        Integer id = hexByteArrayToInt(Arrays.copyOfRange(packet, 0, 4));
        Double latitude = parseCoordinate(Arrays.copyOfRange(packet, 4, 8));
        Double longitude = parseCoordinate(Arrays.copyOfRange(packet, 8, 12));
        Integer radius = hexByteArrayToInt(Arrays.copyOfRange(packet, 12, 14));
        Long timeout = Converter.hexByteArrayToLong(Arrays.copyOfRange(packet, 14, 20));
        int type = hexByteArrayToInt(Arrays.copyOfRange(packet, 20, 21));
        Integer duration = hexByteArrayToInt(Arrays.copyOfRange(packet, 21, 25));

        return new PowerUp(id, latitude, longitude, radius, timeout, type, duration);

    }

}
