package utility;

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
     * @param y2 point 2's y valie
     * @return double the angle
     */
    public static double calcAngleBetweenPoints(double x1, double y1, double x2, double y2) {
        double rangeX = x1 - x2;
        double rangeY = y1 - y2;
        return atan2(rangeY, rangeX) + Math.PI;
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
     * Checks whether the boat is turning clockwise or anticlockwise when tacking/gybing.
     * @param windDirection double wind direction in degrees
     * @param boatHeading double the heading of the boat in degrees
     * @return boolean true if the boat is supposed to turn clockwise
     */
    public static boolean isTackingClockwise(double windDirection, double boatHeading) {
        return !(boatHeading > windDirection && boatHeading < windDirection + 180);
    }

    /**
     * Converts a given angle to a positive degree between 0 and 360
     * @return double the positive angle
     */
    public static double getPositiveAngle(double angle) {
        double result = angle;
        while(result < 0) {
            result += 360;
        }
        while(result > 360) {
            result -= 360;
        }
        return result;
    }

}
