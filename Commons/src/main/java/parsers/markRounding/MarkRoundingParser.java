package parsers.markRounding;

import parsers.Converter;

import java.util.Arrays;

import static parsers.Converter.hexByteArrayToInt;
import static parsers.Converter.hexByteArrayToLong;

/**
 * Created by psu43 on 4/05/17.
 * Parses mark rounding message
 */
public class MarkRoundingParser {


    public MarkRoundingData processMessage(byte[] body) {

        long roundingTime = Converter.hexByteArrayToLong(Arrays.copyOfRange(body, 1, 7));
        Integer sourceID = hexByteArrayToInt(Arrays.copyOfRange(body, 13, 17));
        Integer markID = hexByteArrayToInt(Arrays.copyOfRange(body, 20, 21));
        return new MarkRoundingData(sourceID, markID, roundingTime);
    }



}
