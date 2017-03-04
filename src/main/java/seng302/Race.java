package seng302;

import java.util.ArrayList;

/**
 * Created by mgo65 on 3/03/17.
 */
public interface Race {
    void start();
    void setCompetitors(Competitor comp1, Competitor comp2);
    void setCourse(ArrayList<CoursePoint> points);
    Competitor getCompetitor1();
    Competitor getCompetitor2();
}
