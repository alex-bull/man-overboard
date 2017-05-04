package seng302.Parsers;

/**
 * Created by psu43 on 4/05/17.
 * Mark rounding data
 */
public class MarkRoundingData {

    Integer sourceID;
    Integer markID;

    public MarkRoundingData(Integer sourceID, Integer markID) {
        this.sourceID = sourceID;
        this.markID = markID;
    }

    public Integer getSourceID() {
        return sourceID;
    }

    public Integer getMarkID() {
        return markID;
    }

}
