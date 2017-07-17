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
        return atan(rangeY/rangeX) + Math.PI;
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

}
