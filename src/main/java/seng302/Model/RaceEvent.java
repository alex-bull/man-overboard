package seng302.Model;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;

/**
 * Created by mgo65 on 6/03/17.
 * Represents an event on the race timeline
 */
public class RaceEvent implements Comparable<RaceEvent>{

    private SimpleStringProperty teamName;
    private SimpleLongProperty time;
    private SimpleStringProperty color;
    private SimpleStringProperty endPointName;
    private SimpleIntegerProperty position;
    private Double heading;
    private String pointName;
    private Integer displayTime;
    private boolean isFinish = false;
    private Boat boat;
    private CourseFeature endPoint;
    private SimpleIntegerProperty speed;

    /**
     * Creates a single race event
     * @param boat Competitor the competing boat
     * @param time Integer the time of the event
     * @param displayTime Integer the time to be displayed in the event string
     * @param pointName String the course point name
     * @param heading Double the exit heading of the competitor
     */
    public RaceEvent(Competitor boat, long time, Integer displayTime, String pointName, Double heading, boolean isFinish, CourseFeature endPoint) {
        this.teamName = new SimpleStringProperty(boat.getTeamName());
        this.time = new SimpleLongProperty(time);
        this.pointName = pointName;
        this.displayTime = displayTime;
        if (heading != null) {
            this.heading = heading;
        }
        this.isFinish = isFinish;
        this.endPoint = endPoint;

    }

    public RaceEvent(Competitor boat, long time, CourseFeature endPoint){
        this.teamName=new SimpleStringProperty(boat.getTeamName());
        this.time = new SimpleLongProperty(time);
        this.color=new SimpleStringProperty(boat.getColor().toString());
        this.endPointName=new SimpleStringProperty(endPoint.getName());
        this.speed=new SimpleIntegerProperty(boat.getVelocity());
    }

    /**
     * get the speed
     * @return
     */
    public int getSpeed(){return this.speed.get();}

    /**
     * get the color of the boat in the event
     * @return
     */
    public String getColor(){return this.color.get();}

    /**
     * get the time of the event
     * @return
     */
    public double getTime(){return this.time.get();}

    /**
     * get the end point of the event
     * @return
     */
    public String getEndPointName(){return this.endPointName.get();}
    /**
     * Getter for the competitor boat
     * @return Boat a competitor
     */
    public Boat getBoat() {
        return boat;
    }

    /**
     * Getter for the end point
     * @return CourseFeature the mark for the end point
     */
    public CourseFeature getEndPoint() {
        return endPoint;
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
    public String getTeamName () {return this.teamName.get();}
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

    /**
     * compares this event with another by comparing the time
     * @param o
     * @return
     */
    @Override
    public int compareTo(RaceEvent o) {
        if (this.getSpeed()==o.getSpeed()) return 0;
        else if (this.getSpeed()>o.getSpeed())return -1;
        return 1;
    }
}
