package utility;

import models.MutablePoint;

/**
 * Created by ke on 19/05/17.
 * Projection utility methods
 */
public class Projection {
    /**
     * Calculates the mercator projection onto a 256x256 size map, just like google maps
     *
     * @param lat latitude of point
     * @param lng longitude of point
     * @return a list of calculated x and y values on the map
     */
    public static MutablePoint mercatorProjection(double lat, double lng) {
        double size = 256;
        double siny = Math.sin(lat * Math.PI / 180);
        siny = Math.min(Math.max(siny, -0.9999), 0.9999);
        return new MutablePoint(size * (0.5 + lng / 360), size * (0.5 - Math.log((1 + siny) / (1 - siny)) / (4 * Math.PI)));
    }

    /**
     * Calculates the mercator projection onto a 256x256 size map, just like google maps
     *
     * @param point the point which contains lat and lng information
     * @return a list of calculated x and y values on the map
     */
    public static MutablePoint mercatorProjection(MutablePoint point) {
        double lat = point.getXValue();
        double lng = point.getYValue();
        double size = 256;
        double siny = Math.sin(lat * Math.PI / 180);
        siny = Math.min(Math.max(siny, -0.9999), 0.9999);
        return new MutablePoint(size * (0.5 + lng / 360), size * (0.5 - Math.log((1 + siny) / (1 - siny)) / (4 * Math.PI)));
    }
}
