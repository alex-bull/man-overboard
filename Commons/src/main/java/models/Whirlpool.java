package models;

/**
 * Created by msl47 on 12/09/17.
 */
public class Whirlpool {

    private int sourceID;
    private int currentLeg;
    private double collisionRadius=500;
    private MutablePoint position;
    private MutablePoint position17;
    private MutablePoint positionOriginal;
    private double collisionMagnitude = 5;

    public Whirlpool(int sourceID, int currentLeg, MutablePoint position) {
        this.sourceID = sourceID;
        this.position = position;
        this.currentLeg = currentLeg;
    }

    public Whirlpool(int sourceID, int currentLeg, MutablePoint position, MutablePoint position17, MutablePoint positionOriginal) {
        this.sourceID = sourceID;
        this.currentLeg = currentLeg;
        this.position = position;
        this.position17=position17;
        this.positionOriginal=positionOriginal;
    }

    public int getSourceID() {
        return sourceID;
    }

    public void setSourceID(int sourceID) {
        this.sourceID = sourceID;
    }

    public int getCurrentLeg() {
        return currentLeg;
    }

    public void setCurrentLeg(int currentLeg) {
        this.currentLeg = currentLeg;
    }

    public MutablePoint getPosition() {
        return position;
    }

    public void setPosition(MutablePoint position) {
        this.position = position;
    }

    public double getLatitude(){
        return position.getXValue();
    }

    public double getLongitude(){
        return position.getYValue();
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

    public double getCollisionRadius() {
        return collisionRadius;
    }

    public double getCollisionMagnitude() {
        return collisionMagnitude;
    }


}
