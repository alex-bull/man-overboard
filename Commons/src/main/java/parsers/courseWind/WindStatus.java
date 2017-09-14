package parsers.courseWind;

/**
 * Created by psu43 on 23/05/17.
 * Wind status for each Wind ID
 */
public class WindStatus {
    private double windDirection;
    private int windSpeed;

    /**
     * Wind status
     *
     * @param windDirection double wind direction
     * @param windSpeed     int wind speed in mm/s
     */
    WindStatus(double windDirection, int windSpeed) {
        this.windDirection = windDirection;
        this.windSpeed = windSpeed;
    }

    public int getWindSpeed() {
        return windSpeed;
    }

    public double getWindDirection() {
        return windDirection;
    }


}
