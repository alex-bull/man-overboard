package models;

import utility.Calculator;

/**
 * Created by Izzy on 5/09/17.
 */
public class Shark {

    private int sourceId;
    private int numSharks;
    private double heading;
    private int velocity;
    private Force sharkSpeed;
    private MutablePoint position;
    private MutablePoint position17;
    private MutablePoint positionOriginal;

    public Shark(int sourceId, MutablePoint positionOriginal) {
        this.sourceId=sourceId;
        this.positionOriginal=positionOriginal;
        this.sharkSpeed = new Force(velocity, 0, false);
    }

    public Shark(int sourceId, int numSharks, MutablePoint position, int velocity, double heading) {
        this.sourceId += sourceId;
        this.numSharks = numSharks;
        this.position = position;
        this.velocity = velocity;
        this.heading = heading;
        this.sharkSpeed = new Force(velocity, 0, false);
    }

    public Shark(int sourceId, int numSharks, MutablePoint position, MutablePoint position17, MutablePoint positionOriginal, double heading, int velocity) {
        this.sourceId = sourceId;
        this.numSharks = numSharks;
        this.position = position;
        this.position17 = position17;
        this.positionOriginal = positionOriginal;
        this.heading = heading;
        this.velocity = velocity;
        this.sharkSpeed = new Force(velocity, 0, false);
    }

    public MutablePoint getPositionOriginal() {
        return positionOriginal;
    }

    public int getSourceId() {
        return sourceId;
    }

    public double getHeading() {
        return heading;
    }

    public void setHeading(double heading) {
        this.heading = heading;
    }

    public int getNumSharks() {
        return numSharks;
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

    public double getLatitude() {
        return position.getXValue();
    }

    public double getLongitude() {
        return position.getYValue();
    }

    public Force getSharkSpeed() {
        return sharkSpeed;
    }

    public double getSpeed() {
        return sharkSpeed.getMagnitude();
    }

    public void setSpeed(int velocity) {
        sharkSpeed.setMagnitude(velocity);
    }

    public int getVelocity() {
        return velocity;
    }


    /**
     * Updates the Obstacles position given the time changed
     *
     * @param elapsedTime the time elapsed in seconds
     */
    public void updatePosition(double elapsedTime) {
        //moves the boat by its speed
        MutablePoint position = Calculator.movePoint(sharkSpeed, getPosition(), elapsedTime);
        setPosition(position);
    }


}
