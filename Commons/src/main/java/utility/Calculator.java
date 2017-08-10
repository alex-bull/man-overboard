package utility;

import models.Force;
import models.MutablePoint;
import models.Vector;

import static java.lang.Math.*;

/**
 * Created by psu43 on 17/07/17.
 * Calculating functions
 */
public class Calculator {

    /**
     * Calculates the angle between two points
     * @param x1 point 1's x value
     * @param y1 point 1's y value
     * @param x2 point 2's x value
     * @param y2 point 2's y value
     * @return double the angle
     */
    public static double calcAngleBetweenPoints(double x1, double y1, double x2, double y2) {
        double rangeX = x1 - x2;
        double rangeY = y1 - y2;
        return atan2(rangeY, rangeX) + Math.PI;
    }

    /**
     * Calculates the angle between y intercept and the line made by the two point p1 and p2
     * @param p1 point 1
     * @param p2 point 2
     * @return the angle between two points in degrees
     */
    public static double calculateContactAngle(MutablePoint p1, MutablePoint p2) {
        double rangeX = p1.getXValue()-p2.getXValue();
        double rangeY = p1.getYValue()-p2.getYValue();
        return toDegrees(atan2(rangeY, rangeX));
    }

    /**
     * Converts radians to short (range between -32,768 and a maximum value of 32,767)
     * @param radians double angle in radians
     * @return short the result of the conversion
     */
    public static short convertRadiansToShort(double radians) {
        double ratio = radians / (2 * Math.PI); // radians is between 0 and 2 PI
        double result = ratio * 65536 - 32768;
        return (short) result;
    }

    /**
     * Converts a short value to degrees
     * @param value short
     * @return double degrees
     */
    public static double shortToDegrees(short value) {
        return toDegrees((value+32768) * 2 * Math.PI / 65536);
    }

    /**
     * Calculates the expected tack angle
     * @param windDirection double wind direction in degrees
     * @param boatHeading double boat heading in degrees
     * @return double the expected heading of the tack in degrees
     */
    public static double calculateExpectedTack(double windDirection, double boatHeading) {
        return windDirection - (boatHeading - windDirection);
    }


    /**
     * calculates the dot product between v1 and v2
     * @param v1 vector 1
     * @param v2 vector 2
     * @return the dot product
     */
    public static double dot(Vector v1, Vector v2){
        return v1.getXValue()*v2.getXValue()+v1.getYValue()*v2.getYValue();
    }


    public static Vector subtract(Vector v1, Vector v2){
        return new Force(v1.getXValue()-v2.getXValue(),v1.getYValue()-v2.getYValue(),true);
    }

    public static Vector multiply(double s, Vector v){
        return new Force(s*v.getXValue(),s*v.getYValue(),true);
    }

}
