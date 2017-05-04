package seng302.Parsers;

/**
 * Created by psu43 on 13/04/17.
 * Common parser functions
 */
public class Converter {

    /**
     * Convert a byte array of little endian hex values into an integer
     *
     * @param hexValues byte[] a byte array of hexadecimal bytes in little endian format
     * @return int the value of the hexadecimal bytes
     */
    public static int hexByteArrayToInt(byte[] hexValues) {
        Long value = 0L;
        for (int i = 0; i < hexValues.length; i++) {
            value += ((long) hexValues[i] & 0xffL) << (8 * i);
        }
        return value.intValue();
    }

    /**
     * Convert a byte array of little endian hex values into an integer
     * @param hexValues byte[] a byte array of hexadecimal bytes in little endian format
     * @return int the value of the hexadecimal bytes
     */
    public static long hexByteArrayToLong(byte[] hexValues) {
        Long value = 0L;
        for (int i = 0; i < hexValues.length; i++)
        {
            value += ((long) hexValues[i] & 0xffL) << (8 * i);
        }
        return value;
    }

}
