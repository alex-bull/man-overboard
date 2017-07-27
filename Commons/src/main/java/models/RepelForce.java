package models;

import static java.lang.Math.atan2;
import static java.lang.Math.sqrt;
import static java.lang.Math.toDegrees;

/**
 * Created by khe60 on 26/07/17.
 * Class to simulate real collision
 */
public class RepelForce {
    private double x;
    private double y;

    public RepelForce(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getMagnitude(){
        return sqrt(x*x+y*y);
    }

    public double angle(){
        return toDegrees(atan2(y,x));
    }

    /**
     * decreases the force by some amount
     */
    public void decrease(){
        x=x*0.9;
        y=y*0.9;
    }
}
