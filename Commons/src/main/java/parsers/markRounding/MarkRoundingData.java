package parsers.markRounding;

/**
 * Created by psu43 on 4/05/17.
 * Mark rounding data
 */
public class MarkRoundingData {

    private Integer sourceID;
    private Integer markID;
    private String markName;
    private long roundingTime;

    /**
     * Mark rounding data
     *
     * @param sourceID     Integer source id of the mark
     * @param markID       Integer mark id
     * @param roundingTime long the time of the mark rounding
     */
    MarkRoundingData(Integer sourceID, Integer markID, long roundingTime) {
        this.sourceID = sourceID;
        this.markID = markID;
        this.roundingTime = roundingTime;
    }

    public Integer getSourceID() {
        return sourceID;
    }

    public Integer getMarkID() {
        return markID;
    }

    public String getMarkName() {
        return markName;
    }

    public void setMarkName(String markName) {
        this.markName = markName;
    }

    public long getRoundingTime() {
        return roundingTime;
    }

}
