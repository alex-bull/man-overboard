package models;

/**
 * Created by mgo65 on 6/03/17.
 * Represents an event on the race timeline
 */
public class RaceEvent{

    private String teamName;
    private String featureName;
    private Double speed;
    private int position;

    public RaceEvent(String teamName, Double speed, String featureName, int position) {
        this.teamName = teamName;
        this.speed = speed;
        this.featureName = featureName;
        this.position = position;
    }


    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getFeatureName() {
        return featureName;
    }

    public void setFeatureName(String featureName) {
        this.featureName = featureName;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public Double getSpeed() {
        return speed;
    }

    public void setSpeed(Double speed) {
        this.speed = speed;
    }

}
