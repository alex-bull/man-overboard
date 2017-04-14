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
    public static Long hexListToDecimal(List hexValues) {
        String hexString = "";
        for(Object hexValue: hexValues) {
            String hex = hexValue.toString();
            String reverseHex = new StringBuilder(hex).reverse().toString();
            hexString += reverseHex;
        }
        String reverseHexString = new StringBuilder(hexString).reverse().toString();
        return Long.parseLong(reverseHexString, 16);
        //return Integer.parseInt(reverseHexString, 16);
    }

}
