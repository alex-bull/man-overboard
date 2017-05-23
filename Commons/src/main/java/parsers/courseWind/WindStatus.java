package parsers.courseWind;

/**
 * Created by psu43 on 23/05/17.
 * Wind status for each Wind ID
 */
public class WindStatus {

    private int windID;
    private long time;
    private int raceID;
    private double windDirection;
    private int windSpeed;
    private double bestUpwindAngle;
    private double bestDownwindAngle;
    private int flags;

    WindStatus(int windID, long time, int raceID, double windDirection, int windSpeed, double bestUpwindAngle, double bestDownwindAngle, int flags) {
        this.windID = windID;
        this.time = time;
        this.raceID = raceID;
        this.windDirection = windDirection;
        this.windSpeed = windSpeed;
        this.bestUpwindAngle = bestUpwindAngle;
        this.bestDownwindAngle = bestDownwindAngle;
        this.flags = flags;
    }

    public int getWindSpeed() {
        return windSpeed;
    }

    public double getWindDirection() {
        return windDirection;
    }




}
