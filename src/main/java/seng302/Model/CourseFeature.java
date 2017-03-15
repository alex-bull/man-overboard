package seng302.Model;

/**
 * Created by mgo65 on 4/03/17.
 */
public interface CourseFeature {
    String getName();
    MutablePoint getLocation();
    void setExitHeading(Double exitHeading);
    Double getExitHeading ();
    boolean getIsFinish();
}
