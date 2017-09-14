package utilities;

import com.google.common.io.CharStreams;
import models.MutablePoint;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

/**
 * Created by khe60 on 30/05/17.
 * utility class for custom operations
 */
public class Utility {

    public static String fileToString(String filePath) throws IOException {
       return CharStreams.toString(new InputStreamReader(Utility.class.getResourceAsStream(filePath)));
    }


    /**
     * Get centroid of a polygon
     * @param points list of points of polygon
     * @return MutablePoint centroid of a polygon
     */
    public MutablePoint centroid(List<MutablePoint> points) {
        double centroidX = 0, centroidY = 0;
        for (MutablePoint point: points) {
            centroidX += point.getXValue();
            centroidY += point.getYValue();
        }
        return new MutablePoint(centroidX/ points.size(), centroidY / points.size());
    }



}
