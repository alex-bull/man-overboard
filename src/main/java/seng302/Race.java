package seng302;

import java.util.ArrayList;

/**
 * Created by mgo65 on 3/03/17.
 */
public interface Race {
    void start();
    void setCompetitors(ArrayList<Competitor> competitors);
    void setCourse(ArrayList<CoursePoint> points);
    ArrayList<Competitor> getCompetitors();
}
