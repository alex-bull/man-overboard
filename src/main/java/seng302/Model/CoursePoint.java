package seng302.Model;

/**
 * Created by mgo65 on 4/03/17.
 */
public interface CoursePoint {
    String getName();
    Point getLocation();
    void setExitHeading(Double exitHeading);
    Double getExitHeading ();
    boolean getIsFinish();
}
