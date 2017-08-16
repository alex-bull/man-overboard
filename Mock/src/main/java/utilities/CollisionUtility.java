package utilities;

import models.Force;
import models.MutablePoint;
import models.Vector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.lang.Math.max;
import static java.lang.Math.min;
import static utility.Calculator.dot;
import static utility.Calculator.multiply;
import static utility.Calculator.subtract;

/**
 * Created by msl47 on 7/08/17.
 */
public class CollisionUtility {

    /**
     * checks if the point is inside the polygon
     * @param point MutablePoint point
     * @param polygon List<MutablePoint> counter clockwise
     * @return boolean true if the point is inside the polygon
     */
    public static boolean isPointInPolygon(MutablePoint point, List<MutablePoint> polygon) {
        int j = polygon.size() - 1;
        boolean c = false;
        for (int i = 0; i < polygon.size(); j = i++)
            c ^= polygon.get(i).getYValue() > point.getYValue() ^ polygon.get(j).getYValue() > point.getYValue()
                    && point.getXValue() < (polygon.get(j).getXValue() - polygon.get(i).getXValue())
                    * (point.getYValue() - polygon.get(i).getYValue())
                    / (polygon.get(j).getYValue() - polygon.get(i).getYValue()) + polygon.get(i).getXValue();
        return c;
    }

    /**
     * calculates collision between two objects
     *
     * @param v1 velocity of object 1
     * @param v2 velocity of object 2
     * @param x1 center position of object 1
     * @param x2 center position of object 2
     * @return the final velocity of object 1
     */
    public static Force calculateFinalVelocity(Vector v1, Vector v2, Vector x1, Vector x2) {
        Vector dp = subtract(x1, x2);
        Vector dv = subtract(v1, v2);

        return (Force) subtract(v1, multiply(dot(dv, dp) / (dp.getMagnitude() * dp.getMagnitude()), dp));

    }
}
