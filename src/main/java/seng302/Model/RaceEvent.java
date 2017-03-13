package seng302.Model;

/**
 * Created by mgo65 on 6/03/17.
 * Represents an event on the race timeline
 */
public class RaceEvent {

    private String teamName;
    private Integer time;
    private Double heading;
    private String pointName;
    private Integer displayTime;
    private boolean isFinish = false;

    /**
     * Creates a single race event
     * @param teamName String the competitor
     * @param time Integer the time of the event
     * @param displayTime Integer the time to be displayed in the event string
     * @param pointName String the course point name
     * @param heading Double the exit heading of the competitor
     */
    public RaceEvent(String teamName, Integer time, Integer displayTime, String pointName, Double heading, boolean isFinish) {
        this.teamName = teamName;
        this.time = time;
        this.pointName = pointName;
        this.displayTime = displayTime;
        if (heading != null) {
            this.heading = heading;
        }
        this.isFinish = isFinish;

    }

    /**
     * Getter for the isFinish flag
     * @return boolean isFinish
     */
    public boolean getIsFinish () {
        return this.isFinish;
    }

    /**
     * Getter for the team name
     * @return String the team name
     */
    public String getTeamName () {return this.teamName;}
    /**
     * Creates a formatted display time string in mm:ss
     * @return String the time string
     */
    private String formatDisplayTime () {
        int minutes = this.displayTime / 60;
        int seconds = this.displayTime - (60 * minutes);

        String formattedTime = "";
        if (minutes > 9) {
            formattedTime += minutes + ":";
        }
        else {
            formattedTime += "0" + minutes + ":";
        }
        if (seconds > 9) {
            formattedTime += seconds;
        }
        else {
            formattedTime += "0" + seconds;
        }
        return formattedTime;

    }


    /**
     * Creates a string containing the details of the event
     * @return String the event string
     */
    public String getEventString() {
        String event = "Time: " + this.formatDisplayTime() + ", Event: " + this.teamName + " passed the " + this.pointName;
        if (this.heading != null) {
            event += ", Heading: " + String.format("%.2f", this.heading);
        }
        return event;
    }

}
