package seng302.Model;

import java.util.List;

/**
 * Created by mgo65 on 4/03/17.
 * An interface for a feature in a Course
 */
public interface CourseFeature {
    String getName();
    List<MutablePoint> getPixelLocations();
    void setExitHeading(Double exitHeading);
    double getExitHeading ();
    boolean isFinish();
    int getIndex();
    MutablePoint getGPSCentre();
    void factor(double xFactor,double yFactor,double minX,double minY,double xBuffer,double yBuffer);
    boolean isLine();
}
