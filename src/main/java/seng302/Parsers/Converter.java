package seng302.Parsers;

import java.util.List;

/**
 * Created by psu43 on 13/04/17.
 * Common parser functions
 */
public class Converter {

    /**
     * Convert a list of little endian hex values into an integer
     * @param hexValues List a list of hexadecimal bytes in little endian format
     * @return Long the value of the hexadecimal bytes
     */
    public static int hexByteArrayToInt(byte[] hexValues) {
        Long value = 0l;
        for (int i = 0; i < hexValues.length; i++)
        {
            value += ((long) hexValues[i] & 0xffL) << (8 * i);
        }
        return value.intValue();
    }

}
