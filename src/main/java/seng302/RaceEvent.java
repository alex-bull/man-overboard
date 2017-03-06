package seng302;

/**
 * Created by mgo65 on 6/03/17.
 * Represents an event on the race timeline
 */
public class RaceEvent {

    private String teamName;
    private Integer time;
    private Double heading;
    private String pointName;
    private boolean isFinish = false;

    /**
     * Creates a single race event
     * @param teamName String the competitor
     * @param time Integer the time of the event
     * @param pointName String the course point name
     * @param heading Double the exit heading of the competitor
     */
    public RaceEvent(String teamName, Integer time, String pointName, Double heading) {
        this.teamName = teamName;
        this.time = time;
        this.pointName = pointName;
        if (heading != null) {
            this.heading = heading;
        }
        else {
            isFinish = true;
        }
    }

    /**
     * Getter for the isFinish flag
     * @return boolean isFinish
     */
    public boolean getIsFinish () {
        return this.isFinish;
    }

    /**
     * Creates a string containing the details of the event
     * @return String the event string
     */
    public String getEventString() {
        String event = "Time: " + time.toString() + "s, Event: " + this.teamName + " passed the " + this.pointName;
        if (!isFinish) {
            event += ", Heading: " + String.format("%.2f", this.heading);
        }
        return event;
    }

}
