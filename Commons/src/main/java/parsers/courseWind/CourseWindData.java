package parsers.courseWind;

/**
 * Created by psu43 on 19/05/17.
 * Course wind data from the AC35 streaming data interface specification
 */
public class CourseWindData {
    private int messageVersionNumber;
    private int selectedWindID;

    private int windID;
    private long time;
    private int raceID;
    private double windDirection;
    private int windSpeed;
    private double bestUpwindAngle;
    private double bestDownwindAngle;
    private int flags;

    public CourseWindData(int messageVersionNumber, int selectedWindID, int windID, long time, int raceID,
                          double windDirection, int windSpeed, double bestUpwindAngle, double bestDownwindAngle, int flags) {
        this.messageVersionNumber = messageVersionNumber;
        this.selectedWindID = selectedWindID;
        this.windID = windID;
        this.time = time;
        this.raceID = raceID;
        this.windDirection = windDirection;
        this.windSpeed = windSpeed;
        this.bestUpwindAngle = bestUpwindAngle;
        this.bestDownwindAngle = bestDownwindAngle;
        this.flags = flags;
    }


    public int getMessageVersionNumber() {
        return messageVersionNumber;
    }

    public void setMessageVersionNumber(int messageVersionNumber) {
        this.messageVersionNumber = messageVersionNumber;
    }

    public int getSelectedWindID() {
        return selectedWindID;
    }

    public void setSelectedWindID(int selectedWindID) {
        this.selectedWindID = selectedWindID;
    }

    public int getWindID() {
        return windID;
    }

    public void setWindID(int windID) {
        this.windID = windID;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public int getRaceID() {
        return raceID;
    }

    public void setRaceID(int raceID) {
        this.raceID = raceID;
    }

    public double getWindDirection() {
        return windDirection;
    }

    public void setWindDirection(double windDirection) {
        this.windDirection = windDirection;
    }

    public double getWindSpeed() {
        return windSpeed;
    }

    public void setWindSpeed(int windSpeed) {
        this.windSpeed = windSpeed;
    }

    public double getBestUpwindAngle() {
        return bestUpwindAngle;
    }

    public void setBestUpwindAngle(double bestUpwindAngle) {
        this.bestUpwindAngle = bestUpwindAngle;
    }

    public double getBestDownwindAngle() {
        return bestDownwindAngle;
    }

    public void setBestDownwindAngle(double bestDownwindAngle) {
        this.bestDownwindAngle = bestDownwindAngle;
    }

    public int getFlags() {
        return flags;
    }

    public void setFlags(int flags) {
        this.flags = flags;
    }








}
