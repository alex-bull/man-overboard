package parsers.markRounding;

/**
 * Created by psu43 on 4/05/17.
 * Mark rounding data
 */
public class MarkRoundingData {

    Integer sourceID;
    Integer markID;
    String markName;

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

    public String getMarkName() {
        return markName;
    }

    public void setMarkName(String markName) {
        this.markName = markName;
    }

}
