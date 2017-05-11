package utilities;

import models.Competitor;
import models.CourseFeature;
import models.MutablePoint;
import parsers.markRounding.MarkRoundingData;
import parsers.xml.race.CompoundMarkData;
import parsers.xml.race.MarkData;

import java.util.HashMap;
import java.util.List;

/**
 * Created by mgo65 on 11/05/17.
 * Data source
 */
public interface DataSource {
    boolean receive(String host, int port);
    List<CourseFeature> getCourseFeatures();
    List<MutablePoint> getCourseBoundary();
    void setCourseBoundary(List<MutablePoint> courseBoundary);
    List<CompoundMarkData> getCompoundMarks();
    String getCourseTimezone();
    List<MarkData> getStartMarks();
    List<MarkData> getFinishMarks();
    String getRaceStatus();
    long getMessageTime();
    long getExpectedStartTime();
    List<Competitor> getCompetitors();
    //Setters
    void setCompetitors(List<Competitor> competitors);
    double getWindDirection();
    HashMap<Integer, Competitor> getStoredCompetitors();
    MarkRoundingData getMarkRoundingData();
    int getNumBoats();
}
