package models;

/**
 * Created by mgo65 on 6/03/17.
 * Used as a row for the race table
 */
public class RaceEvent{

    private Integer boatSourceId;
    private String teamName;
    private Double speed;
    private int position;
    private String feature;


    /**
     * Constructs a RaceEvent
     * @param teamName String team name of the boat
     * @param speed Double speed of the boat
     * @param feature String the course feature the boat has passed
     * @param position int the position of the boat
     */
    public RaceEvent(Integer boatSourceId, String teamName, String feature, Double speed, int position) {
        this.boatSourceId = boatSourceId;
        this.teamName = teamName;
        this.feature = feature;
        this.speed = speed;
        this.position = position;
    }

    // Actually used by FXML. DO NOT DELETE
    public String getFeature() {
        return feature;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getTeamName() {
        return teamName;
    }

    public Double getSpeed() {
        return speed;
    }

    public void setSpeed(Double speed) {
        this.speed = speed;
    }

    public Integer getBoatSourceId() {
        return this.boatSourceId;
    }

}
