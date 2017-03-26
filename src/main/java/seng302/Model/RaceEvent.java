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
    private SimpleStringProperty endPointName;
    private SimpleIntegerProperty position;
    private boolean isFinish = false;
    private CourseFeature endPoint;
    private SimpleIntegerProperty speed;


    /**EDIT LATER
     * Creates a single race event
     * @param boat Competitor the competing boat
     * @param time Integer the time of the event
     * @param displayTime Integer the time to be displayed in the event string
     * @param pointName String the course point name
     * @param heading Double the exit heading of the competitor
     */
//    public RaceEvent(Competitor boat, long time, Integer displayTime, String pointName, Double heading, boolean isFinish, CourseFeature feature) {
//        this.teamName = new SimpleStringProperty(boat.getTeamName());
//        this.time = new SimpleLongProperty(time);
//        this.pointName = pointName;
//        this.displayTime = displayTime;
//        if (heading != null) {
//            this.heading = heading;
//        }
//        this.isFinish = isFinish;
//        this.endPoint = feature;
//
//    }

    /**
     * Creates a race event
     * @param boat Competitor a competing boat
     * @param time long the time in milliseconds
     * @param feature CourseFeature the feature the competitor passed
     */
//    public RaceEvent(Competitor boat, long time, CourseFeature feature){
//        this.teamName=new SimpleStringProperty(boat.getTeamName());
//        this.time = new SimpleLongProperty(time);
//        this.color=new SimpleStringProperty(boat.getColor().toString());
//        this.endPointName=new SimpleStringProperty(feature.getName());
//        this.speed=new SimpleIntegerProperty(boat.getVelocity());
//    }

    /**
     * Creates a race event
     * @param boat Competitor a competing boat
     * @param feature CourseFeature the feature the competitor passed
     */
    public RaceEvent(Competitor boat, CourseFeature feature) {
        this.position = new SimpleIntegerProperty(0);
        this.speed = new SimpleIntegerProperty(boat.getVelocity());
        this.endPointName = new SimpleStringProperty(feature.getName());
        this.teamName = new SimpleStringProperty(boat.getTeamName());
        this.endPoint = feature;
    }


    /**
     * Compares this race event with another by comparing the index
     * @param raceEvent RaceEvent a race event
     * @return int
     */
    @Override
    public int compareTo(RaceEvent raceEvent) {
        if (this.getEndPoint().getIndex() == raceEvent.getEndPoint().getIndex()) {
            return 0;
        }
        else if (this.getEndPoint().getIndex() > raceEvent.getEndPoint().getIndex()) {
            return -1;
        }
        return 1;
    }

    /**
     * Get the speed
     * @return int the speed of the boat in m/s
     */
    public int getSpeed(){return this.speed.get();}

    /**
     * Sets the position of the boat
     * @param position the placing position of the boat
     */
    public void setPosition(int position) {
        this.position = new SimpleIntegerProperty(position);
    }

    /**
     * Gets the position of the boat
     * @return int place position of the boat
     */
    public int getPosition() {
        return this.position.get();
    }


    /**
     * Get the end point of the event
     * @return String the course feature the boat passed
     */
    public String getEndPointName(){return this.endPointName.get();}

    /**
     * Getter for the end point
     * @return CourseFeature the mark for the end point
     */
    public CourseFeature getEndPoint() {
        return endPoint;
    }


    /**
     * Getter for the isFinish flag
     * @return boolean isFinish true if the boat finished
     */
    public boolean getIsFinish () {
        return this.isFinish;
    }

    /**
     * Getter for the team name
     * @return String the team name
     */
    public String getTeamName () {return this.teamName.get();}


}
