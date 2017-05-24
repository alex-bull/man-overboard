package utility;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ke on 19/05/17.
 * Projection utility methods
 */
public class Projection {
    /**
     * Calculates the mercator projection onto a 256x256 size map, just like google maps
     * @param lat latitude of point
     * @param lng longitude of point
     * @return a list of calculated x and y values on the map
     */
    public static List<Double> mercatorProjection(double lat, double lng){
        double size=256;
        List<Double> ret = new ArrayList<>();
        double siny=Math.sin(lat*Math.PI/180);
        siny=Math.min(Math.max(siny,-0.9999),0.9999);
        ret.add(size*(0.5+lng/360));
        ret.add(size*(0.5-Math.log((1+siny)/(1-siny))/(4*Math.PI)));
        return ret;
    }
}
