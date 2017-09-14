package models;

import static java.lang.Math.*;

/**
 * Created by khe60 on 26/07/17.
 * Class for a force vector
 */
public class Force implements Vector{
    private double x;
    private double y;
    private double magnitude;
    private double direction;
    private double factor;

    /**
     * constructor for the force vector
     * @param x either horizontal velocity or magnitude depending on inputType
     * @param y either vertical velocity or direction depending on inputType
     *          direction in degrees
     * @param inputType specifies what x and y are
     *                  true - x and y are horizontal and vertical velocity
     *                  false - x and y are magnitude and direction
     */
    public Force(double x, double y, boolean inputType) {
        factor=1;
        if(inputType){
            this.x = x;
            this.y = y;
            this.magnitude=sqrt(x*x+y*y);
            this.direction=capDirection(toDegrees(atan2(x,y)));


        }
        else{
            this.magnitude=x;
            this.direction=y;
            this.x=magnitude*sin(toRadians(y));
            this.y=magnitude*cos(toRadians(y));
        }
    }

    /**
     * makes direction range from 0 to 360
     * @param direction the direction to be modified
     * @return the modified direction ranging from 0 to 360
     */
    private double capDirection(double direction){
        double retDirection=direction;
        //make direction always positive
        if(retDirection<0){
            retDirection=360+retDirection;
        }

        return retDirection%360;
    }

    public void setX(double x) {
        this.x = x;
        this.magnitude=sqrt(x*x+y*y);
        this.direction=capDirection(toDegrees(atan2(x,y)));
    }

    public void setY(double y) {
        this.y = y;
        this.magnitude=sqrt(x*x+y*y);
        this.direction=capDirection(toDegrees(atan2(x,y)));
    }

    public void setMagnitude(double magnitude) {
        this.magnitude = magnitude;
        this.x=magnitude*sin(toRadians(direction));
        this.y=magnitude*cos(toRadians(direction));
    }

    public void setDirection(double direction) {
        this.direction = capDirection(direction);
        this.x=magnitude*sin(toRadians(this.direction));
        this.y=magnitude*cos(toRadians(this.direction));
    }


    public double getXValue() {
        return x*factor;
    }

    public double getYValue() {
        return y*factor;
    }

    public double getMagnitude() {
        return magnitude*factor;
    }

    public double getDirection() {
        return direction;
    }

    @Override
    public String toString() {
        return "Force{" +
                "x=" + x +
                ", y=" + y +
                ", magnitude=" + magnitude +
                ", direction=" + direction +
                '}';
    }

    /**
     * reduces itself by a certain amount
     * @param amount the amount left
     */
    public void reduce(double amount){
        factor*=amount;
    }


    /**
     * increases itself by a certain amount, capped at maximum force
     * @param amount the amount to increase
     */
    public void increase(double amount){
        factor+=(1-factor)*amount;
    }


    /**
     * rounds the force down to 0 if its small
     */
    public void round(){
        if(getMagnitude()<0.01){
            setX(0);
            setY(0);
        }
    }
}
