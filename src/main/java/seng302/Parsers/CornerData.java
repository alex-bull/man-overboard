package seng302.Parsers;

/**
 * Created by psu43 on 26/04/17.
 * Corner data from race.xml in a compound mark sequence.
 */
public class CornerData {
    private int seqID;
    private int compoundMarkID;
    private String rounding;
    private int zoneSize;

    public CornerData(int seqID, int compoundMarkID, String rounding, int zoneSize) {
        this.seqID = seqID;
        this.compoundMarkID = compoundMarkID;
        this.rounding = rounding;
        this.zoneSize = zoneSize;
    }

    public int getSeqID() {
        return seqID;
    }

    public void setSeqID(int seqID) {
        this.seqID = seqID;
    }

    public int getCompoundMarkID() {
        return compoundMarkID;
    }

    public void setCompoundMarkID(int compoundMarkID) {
        this.compoundMarkID = compoundMarkID;
    }

    public String getRounding() {
        return rounding;
    }

    public void setRounding(String rounding) {
        this.rounding = rounding;
    }

    public int getZoneSize() {
        return zoneSize;
    }

    public void setZoneSize(int zoneSize) {
        this.zoneSize = zoneSize;
    }


}
