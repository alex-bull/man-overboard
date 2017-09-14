package parsers.xml.race;

/**
 * Created by psu43 on 26/04/17.
 * Limit data from race.xml in course limit.
 */
class LimitData {
    private double lat;
    private double lon;

    /**
     * LimitData read from a data packet
     *
     * @param lat double latitude
     * @param lon double longitude
     */
    LimitData(double lat, double lon) {
        this.lat = lat;
        this.lon = lon;
    }

    double getLat() {
        return lat;
    }

    double getLon() {
        return lon;
    }


}
