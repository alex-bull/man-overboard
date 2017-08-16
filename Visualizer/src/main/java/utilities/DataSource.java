package utilities;

import javafx.scene.Scene;
import models.Competitor;
import models.CourseFeature;
import models.MutablePoint;
import parsers.RaceStatusEnum;

import java.io.EOFException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by mgo65 on 11/05/17.
 * Data source
 */
public interface DataSource {
    void receive(String host, int port, Scene scene, StreamDelegate delegate);
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
    Map<Integer, Competitor> getStoredCompetitors();
    Map<Integer, CourseFeature> getCourseFeatureMap();
    Map<Integer, List<Integer>> getIndexToSourceIdCourseFeatures();
    List<Double> getGPSbounds();
    int getMapZoomLevel();
    double getShiftDistance();
    int getSourceID();
    Set<Integer> getCollisions();
    void removeCollsions(int sourceID);
    void send(byte[] data);
    Competitor getCompetitor();
    List<MutablePoint> getCourseBoundary17();
    HashMap<Integer, CourseFeature> getStoredFeatures17();
}
