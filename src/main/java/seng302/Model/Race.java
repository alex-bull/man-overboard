package seng302.Model;

import java.util.List;
import java.util.Map;

/**
 * Created by mgo65 on 3/03/17.
 */
public interface Race {
    void start();
    Map getRaceTimeline();
    void setCompetitors(List<Competitor> competitors);
    void setCourse(List<CourseFeature> points);
    List<Competitor> getCompetitors();
}
