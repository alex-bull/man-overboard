package parsers.header;

import parsers.Converter;
import parsers.markRounding.MarkRoundingData;

import java.util.Arrays;

import static parsers.Converter.hexByteArrayToInt;
import static parsers.Converter.hexByteArrayToLong;

/**
 * Created by psu43 on 20/07/17.
 * Parser for header of a message
 */
public class HeaderParser {

    /**
     * Parses the header for a message
     * @param body byte[] a byte array to be parsed
     * @return HeaderData the parsed header data
     */
    public HeaderData processMessage(byte[] body) {
        try {
            Integer messageType = hexByteArrayToInt(Arrays.copyOfRange(body, 3, 4));
            long timeStamp = hexByteArrayToLong(Arrays.copyOfRange(body, 4, 10));
            Integer sourceID = hexByteArrayToInt(Arrays.copyOfRange(body, 10, 14));
            Integer messageLength = hexByteArrayToInt(Arrays.copyOfRange(body, 14, 16));

            return new HeaderData(messageType, timeStamp, sourceID, messageLength);
        }
        catch (Exception e) {
            return null;
        }

    }
}
