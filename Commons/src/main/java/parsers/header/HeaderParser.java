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
//            Integer sync1 = hexByteArrayToInt(Arrays.copyOfRange(body, 1, 2));
//            Integer sync2 = hexByteArrayToInt(Arrays.copyOfRange(body, 2, 3));
//            System.out.println("Syncs 1" + sync1);
//            System.out.println("Syncs 2" + sync2);

            Integer messageType = hexByteArrayToInt(Arrays.copyOfRange(body, 0, 1));
            System.out.println("MEssage type " + messageType);

            long timeStamp = hexByteArrayToLong(Arrays.copyOfRange(body, 1, 7));
            System.out.println("timestamp " + timeStamp);

            Integer sourceID = hexByteArrayToInt(Arrays.copyOfRange(body, 7, 11));
            System.out.println("Source id " + sourceID);

            Integer messageLength = hexByteArrayToInt(Arrays.copyOfRange(body, 11, 13));
            System.out.println("messagelength " + messageLength);
            return new HeaderData(messageType, timeStamp, sourceID, messageLength);
        }
        catch (Exception e) {
            System.out.println("NULL");
            return null;
        }

    }
}
