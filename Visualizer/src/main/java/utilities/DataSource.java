package utilities;

import javafx.scene.Scene;
import models.Competitor;
import models.CourseFeature;
import models.MutablePoint;
import parsers.RaceStatusEnum;

import java.util.HashMap;
import java.util.List;

/**
 * Created by mgo65 on 11/05/17.
 * Data source
 */
public interface DataSource {
    boolean receive(String host, int port, Scene scene);
    List<CourseFeature> getCourseFeatures();
    List<MutablePoint> getCourseBoundary();
    String getCourseTimezone();
    List<Integer> getStartMarks();
    List<Integer> getFinishMarks();
    RaceStatusEnum getRaceStatus();
    long getMessageTime();
    long getExpectedStartTime();
    List<Competitor> getCompetitorsPosition();
    double getWindDirection();
    double getWindSpeed();
    HashMap<Integer, CourseFeature> getStoredFeatures();
    List<Double> getGPSbounds();
    int getMapZoomLevel();
    double getShiftDistance();
}
