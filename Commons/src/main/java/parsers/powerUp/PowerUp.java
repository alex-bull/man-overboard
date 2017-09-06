package parsers.powerUp;

/**
 * Created by psu43 on 6/09/17.
 * Power up
 */
public class PowerUp {

    private Integer id;
    private Double latitude;
    private Double longitude;
    private Integer radius;
    private Long timeout;
    private int type;
    private Integer duration;

    public PowerUp(Integer id, Double latitude, Double longitude, Integer radius, Long timeout, int type, Integer duration) {
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
        this.radius = radius;
        this.timeout = timeout;
        this.type = type;
        this.duration = duration;
    }

    public Integer getId() {
        return id;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public Integer getRadius() {
        return radius;
    }

    public Long getTimeout() {
        return timeout;
    }

    public int getType() {
        return type;
    }

    public Integer getDuration() {
        return duration;
    }

}
