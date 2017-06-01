package utilities;

/**
 * Created by psu43 on 25/05/17.
 * Holds all of the calculations for the race
 */
public class RaceCalculator {


    /**
     * Calculates the estimated time to the mark and compares it to expected race start time.
     * @param distanceToStart double the Distance to the a point on a start line
     * @param distanceToEnd double the Distance to the a point on a start line
     * @param velocity double the speed of the boat
     * @param expectedStartTime long the expected start time
     * @return String "-" if the boat is going to cross early, "+" if late and "" if within 5 seconds.
     */
    public String calculateStartSymbol(double distanceToStart, double distanceToEnd, double velocity, long expectedStartTime) {
        int timeBound = 5;
        double selectedTime;

        if (distanceToStart < distanceToEnd) {
            selectedTime = (distanceToStart / velocity);
        } else {
            selectedTime = (distanceToEnd / velocity);
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
