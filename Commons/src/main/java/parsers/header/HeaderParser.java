package parsers.header;

import java.util.Arrays;

import static parsers.Converter.hexByteArrayToInt;
import static parsers.Converter.hexByteArrayToLong;

/**
 * Created by psu43 on 20/07/17.
 * Parser for header of a message
 */
public class HeaderParser {

    private Integer messageType ;
    private long timeStamp ;
    private Integer sourceID;
    private Integer messageLength;

    public Integer getMessageType() {
        return messageType;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public Integer getSourceID() {
        return sourceID;
    }

    public Integer getMessageLength() {
        return messageLength;
    }

    /**
     * Parses the header for a message
     *
     * @param body byte[] a byte array to be parsed
     */
    public void update(byte[] body) {
        try {
//            Integer sync1 = hexByteArrayToInt(Arrays.copyOfRange(body, 1, 2));
//            Integer sync2 = hexByteArrayToInt(Arrays.copyOfRange(body, 2, 3));
            messageType = hexByteArrayToInt(Arrays.copyOfRange(body, 0, 1));
            timeStamp = hexByteArrayToLong(Arrays.copyOfRange(body, 1, 7));
            sourceID = hexByteArrayToInt(Arrays.copyOfRange(body, 7, 11));
            messageLength = hexByteArrayToInt(Arrays.copyOfRange(body, 11, 13));

        } catch (Exception e) {
            //e.printStackTrace();
        }

    }
}
