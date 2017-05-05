package seng302.Parsers;

import java.util.Arrays;

import static seng302.Parsers.Converter.hexByteArrayToInt;

/**
 * Created by psu43 on 4/05/17.
 * Parses mark rounding message
 */
public class MarkRoundingParser {

    private MarkRoundingData markRoundingData;

    public MarkRoundingParser(byte[] messsage) {
        this.markRoundingData = processMessage(messsage);
    }

    private MarkRoundingData processMessage(byte[] body) {
        Integer sourceID = hexByteArrayToInt(Arrays.copyOfRange(body, 13, 17));
        Integer markID = hexByteArrayToInt(Arrays.copyOfRange(body, 20, 21));

//        System.out.println(sourceID);
//        System.out.println(markID);

        return new MarkRoundingData(sourceID, markID);
    }


    public MarkRoundingData getMarkRoundingData() {
        return markRoundingData;
    }


}
