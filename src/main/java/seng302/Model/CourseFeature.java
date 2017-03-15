package seng302.Model;

import java.util.List;

/**
 * Created by mgo65 on 4/03/17.
 */
public interface CourseFeature {
    String getName();
    List<Point> getLocations();
    void setExitHeading(Double exitHeading);
    Double getExitHeading ();
    boolean getIsFinish();
    boolean isGate();
}
