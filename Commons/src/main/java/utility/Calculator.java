package utility;

import models.Force;
import models.MutablePoint;
import models.Vector;

import static java.lang.Math.*;
import static parsers.Converter.hexByteArrayToInt;

/**
 * Created by psu43 on 17/07/17.
 * Calculating functions
 */
public class Calculator {


    final static int earthRadius = 6371;
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

    /**
     * moves the point from the current position by the force
     * @param force the force which the boat is affected by
     * @param point the point at the start
     * @param elapsedTime the time period of this movement
     */
    public static MutablePoint movePoint(Force force, MutablePoint point, double elapsedTime){
        double distance = force.getMagnitude() * elapsedTime / 1000; // in km
        double lat1 = point.getXValue() * Math.PI / 180; // in radians
        double lng1 = point.getYValue() * Math.PI / 180;
        double bearing = force.getDirection() * Math.PI / 180;

        //calculate new positions
        double lat2 = Math.asin(Math.sin(lat1) * Math.cos(distance / earthRadius) +
                Math.cos(lat1) * Math.sin(distance / earthRadius) * Math.cos(bearing));
        double lng2 = lng1 + Math.atan2(Math.sin(bearing) * Math.sin(distance / earthRadius) * Math.cos(lat1),
                Math.cos(distance / earthRadius) - Math.sin(lat1) * Math.sin(lat2));

        //turn the new lat and lng back to degrees
        return new MutablePoint(lat2 * 180 / Math.PI, lng2 * 180 / Math.PI);
    }


}
