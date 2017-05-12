package parsers.markRounding;

import java.util.Arrays;

import static parsers.Converter.hexByteArrayToInt;

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
        try {
            Integer sourceID = hexByteArrayToInt(Arrays.copyOfRange(body, 13, 17));
            Integer markID = hexByteArrayToInt(Arrays.copyOfRange(body, 20, 21));

            return new MarkRoundingData(sourceID, markID);
        } catch (Exception e) {
            return null;
        }
    }


    public MarkRoundingData getMarkRoundingData() {
        return markRoundingData;
    }


}
