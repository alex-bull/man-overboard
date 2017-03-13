package seng302.Model;

import java.util.List;

/**
 * Created by mgo65 on 3/03/17.
 */
public interface Race {
    void start();
    void setCompetitors(List<Competitor> competitors);
    void setCourse(List<CourseFeature> points);
    List<Competitor> getCompetitors();
}
