package parsers.xml.race;

/**
 * Created by psu43 on 26/04/17.
 * Corner data from race.xml in a compound mark sequence.
 */
public class CornerData {
    private String rounding;

    /**
     * Corner Data from the data stream (currently only reading rounding)
     * @param rounding String rounding
     */
    CornerData(String rounding) {
        this.rounding = rounding;
    }

    public String getRounding() {
        return rounding;
    }

    public void setRounding(String rounding) {
        this.rounding = rounding;
    }



}
