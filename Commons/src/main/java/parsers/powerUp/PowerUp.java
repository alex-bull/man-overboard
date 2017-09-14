package parsers.powerUp;

import models.MutablePoint;

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
    private MutablePoint location;
    private MutablePoint position;
    private MutablePoint position17;
    private MutablePoint positionOriginal;
    private boolean isTaken = false;

    public PowerUp(Integer id, Double latitude, Double longitude, Integer radius, Long timeout, int type, Integer duration) {
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
        this.radius = radius;
        this.timeout = timeout;
        this.type = type;
        this.duration = duration;
        this.location = new MutablePoint(latitude, longitude);
    }


    public void taken() {
        isTaken = true;
    }

    public boolean isTaken() {
        boolean taken = isTaken;
        return taken;
    }


    public MutablePoint getPosition() {
        return position;
    }

    public void setPosition(MutablePoint position) {
        this.position = position;
    }

    public MutablePoint getPosition17() {
        return position17;
    }

    public void setPosition17(MutablePoint position17) {
        this.position17 = position17;
    }

    public MutablePoint getPositionOriginal() {
        return positionOriginal;
    }

    public void setPositionOriginal(MutablePoint positionOriginal) {
        this.positionOriginal = positionOriginal;
    }

    public MutablePoint getLocation() {
        return location;
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
