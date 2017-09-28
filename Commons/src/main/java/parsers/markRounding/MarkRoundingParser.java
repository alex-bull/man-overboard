package parsers.markRounding;

import parsers.Converter;

import java.util.Arrays;

import static parsers.Converter.hexByteArrayToInt;

/**
 * Created by psu43 on 4/05/17.
 * Parses mark rounding message
 */
public class MarkRoundingParser {
    private long roundingTime;
    private Integer sourceID;
    private Integer markID ;
    private String markName;

    public void setMarkName(String markName) {
        this.markName = markName;
    }

    public String getMarkName() {

        return markName;
    }

    public long getRoundingTime() {
        return roundingTime;
    }

    public Integer getSourceID() {
        return sourceID;
    }

    public Integer getMarkID() {
        return markID;
    }

    /**
     * Parses the mark rounding message
     *
     * @param body byte[] a byte array to be parsed
     */
    public void update(byte[] body) {
        try {
            roundingTime = Converter.hexByteArrayToLong(Arrays.copyOfRange(body, 1, 7));
            sourceID = hexByteArrayToInt(Arrays.copyOfRange(body, 13, 17));
            markID = hexByteArrayToInt(Arrays.copyOfRange(body, 20, 21));

        } catch (Exception e) {
           // e.printStackTrace();
        }

    }


}
