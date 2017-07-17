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

    /**
     * MarkData from the data stream
     * @param seqID int sequence id of mark
     * @param name String name of mark
     * @param targetLat Double target latitude
     * @param targetLon Double target longitude
     * @param sourceID int source ID
     */
    MarkData(int seqID, String name, Double targetLat, Double targetLon, int sourceID) {
        this.seqID = seqID;
        this.name = name;
        this.targetLat = targetLat;
        this.targetLon = targetLon;
        this.sourceID = sourceID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSourceID() {
        return sourceID;
    }

    public void setSourceID(int sourceID) {
        this.sourceID = sourceID;
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

    @Override
    public String toString() {
        return "MarkData{" +
                "seqID=" + seqID +
                ", name='" + name + '\'' +
                ", targetLat=" + targetLat +
                ", targetLon=" + targetLon +
                ", sourceID=" + sourceID +
                '}';
    }
}
