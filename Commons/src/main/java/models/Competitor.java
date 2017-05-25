package models;

import javafx.scene.paint.Color;

/**
 * Created by mgo65 on 3/03/17.
 * An interface for a competitor in a Race
 */
public interface Competitor {
    String getTeamName();

    double getVelocity();

    void setVelocity(double velocity);

    MutablePoint getPosition();

    void setPosition(MutablePoint position);

    void updatePosition(double dt);

    int getStatus();

    void setStatus(int status);

    Color getColor();

    void setColor(Color color);

    int getCurrentLegIndex();

    long getTimeToNextMark();

    void setTimeToNextMark(long timeToNextMark);

    long getTimeAtLastMark();

    void setTimeAtLastMark(long timeAtLastMark);

    String getAbbreName();

    String getLastMarkPassed();

    void setLastMarkPassed(String lastMarkPassed);

    double getCurrentHeading();

    void setCurrentHeading(double currentHeading);

    void setCurrentLegIndex(int legIndex);

    DoubleProperty getHeadingProperty();

    int getSourceID();

}
