package utilities;

import models.Competitor;

/**
 * Created by psu43 on 25/05/17.
 */
public class RaceCalculator {



    /**
     * Calculates the closest mark from a start line to a boat and then calculates the estimated time to the mark and
     * compares it to expected race start time.
     * @param distanceToStart double the Distance to the a point on a start line
     * @param distanceToEnd double the Distance to the a point on a start line
     * @param boat Competitor a boat in the race
     * @return String "-" if the boat is going to cross early, "+" if late and "" if within 5 seconds.
     */
    public String calculateStartSymbol(double distanceToStart, double distanceToEnd, Competitor boat, long expectedStartTime) {
        double selectedTime;
        int timeBound = 5;

        if (distanceToStart < distanceToEnd) {
            selectedTime = (distanceToStart / boat.getVelocity());
        } else {
            selectedTime = (distanceToEnd / boat.getVelocity());
        }

        if (selectedTime < expectedStartTime) {
            return "-";
        } else if (selectedTime > (expectedStartTime + timeBound)) {
            return "+";
        } else {
            return "";
        }
    }
}
