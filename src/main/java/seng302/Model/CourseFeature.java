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
    Double getExitHeading ();
    boolean isFinish();
    int getIndex();
    void setIndex(int index);
    MutablePoint getGPSCentre();
    void factor(double xFactor,double yFactor,double minX,double minY,double xBuffer,double yBuffer,double width,double height);
    boolean isLine();
}
