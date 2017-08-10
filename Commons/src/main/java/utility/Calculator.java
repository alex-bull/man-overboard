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

    public static double shortToDegrees(short value) {
        return toDegrees((value+32768) * 2 * Math.PI / 65536);
    }

    /**
     * Calculates the distance between a point and a line segment
     * @param x Point x
     * @param y Point y
     * @param x1 lineStartx
     * @param y1 lineStarty
     * @param x2 lineEndx
     * @param y2 lineEndy
     * @return double, the shortest distance
     */
    public static double pointDistance(double x, double y, double x1, double y1, double x2, double y2) {

        double A = x - x1;
        double B = y - y1;
        double C = x2 - x1;
        double D = y2 - y1;

        double dot = A * C + B * D;
        double len_sq = C * C + D * D;
        double param = -1;
        if (len_sq != 0) //in case of 0 length line
            param = dot / len_sq;

        double xx, yy;

        if (param < 0) {
            xx = x1;
            yy = y1;
        }
        else if (param > 1) {
            xx = x2;
            yy = y2;
        }
        else {
            xx = x1 + param * C;
            yy = y1 + param * D;
        }
        double dx = x - xx;
        double dy = y - yy;
        return (double) Math.sqrt(dx * dx + dy * dy);
    }


}
