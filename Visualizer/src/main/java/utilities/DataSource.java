package utilities;

import javafx.scene.Scene;
import models.*;
import parsers.RaceStatusEnum;
import parsers.powerUp.PowerUp;
import parsers.xml.race.Decoration;
import parsers.xml.race.ThemeEnum;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by mgo65 on 11/05/17.
 * Data source
 */
public interface DataSource {
    void changeScaling(double Multiplier);
    long getLatency();

    int getZoomLevel();

    void disconnect();

    Map<Integer, CrewLocation> getCrewLocations();

    Map<Integer, Shark> getSharkLocations();

    Map<Integer, Blood> getBloodLocations();

    Map<Integer, Whirlpool> getWhirlpools();

    boolean receive(String host, int port, Scene scene);

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

    Map<Integer, Integer> getCollisions();

    void removeCollsions(int sourceID);

    void send(byte[] data);

    Competitor getCompetitor();

    List<MutablePoint> getCourseBoundary17();

    HashMap<Integer, CourseFeature> getStoredFeatures17();

    void update();

    Map<Integer, PowerUp> getPowerUps();

    boolean isSpectating();
    ThemeEnum getThemeId();
    MutablePoint evaluatePosition17(MutablePoint position);
    MutablePoint evaluateOriginalPosition(MutablePoint location);
    MutablePoint evaluatePosition(MutablePoint location);
    HashMap<String, Decoration> getDecorations();
//    HashMap<Integer, Mark> getMarks();
    Map<Integer, String> getMarkSourceIdToRoundingDirection();
}
