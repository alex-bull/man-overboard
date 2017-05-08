package parsers;

/**
 * Created by psu43 on 26/04/17.
 * Limit data from race.xml in course limit.
 */
public class LimitData {
    private int seqID;
    private double lat;
    private double lon;

    public LimitData(int seqID, double lat, double lon) {
        this.seqID = seqID;
        this.lat = lat;
        this.lon = lon;
    }

    public int getSeqID() {
        return seqID;
    }

    public void setSeqID(int seqID) {
        this.seqID = seqID;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }


}
