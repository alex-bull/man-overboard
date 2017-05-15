package parsers.xml.race;

/**
 * Created by psu43 on 26/04/17.
 * A mark from race.xml in the compound mark.
 */
public class MarkData {
    private int seqID;
    private String name;
    private Double targetLat;
    private Double targetLon;
    private int sourceID;

    public MarkData(int seqID, String name, Double targetLat, Double targetLon, int sourceID) {
        this.seqID = seqID;
        this.name = name;
        this.targetLat = targetLat;
        this.targetLon = targetLon;
        this.sourceID = sourceID;
    }

    public int getSeqID() {
        return seqID;
    }

    public void setSeqID(int seqID) {
        this.seqID = seqID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getTargetLat() {
        return targetLat;
    }

    public void setTargetLat(Double targetLat) {
        this.targetLat = targetLat;
    }

    public Double getTargetLon() {
        return targetLon;
    }

    public void setTargetLon(Double targetLon) {
        this.targetLon = targetLon;
    }

    public int getSourceID() {
        return sourceID;
    }

    public void setSourceID(int sourceID) {
        this.sourceID = sourceID;
    }


}
