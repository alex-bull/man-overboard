package models;

import javafx.beans.property.DoubleProperty;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import parsers.BoatStatusEnum;

import java.util.List;

/**
 * Created by mgo65 on 3/03/17.
 * An interface for a competitor in a Race
 */
public interface Competitor {
    String getTeamName();
    List<RepelForce> getForces();
    void addForce(RepelForce force);
    double getVelocity();
    void blownByWind(double windAngle);
    void setVelocity(double velocity);
    MutablePoint getPosition17();
    void setPosition17(MutablePoint position17);
    MutablePoint getPosition();

    void setPosition(MutablePoint position);

    void updatePosition(double dt);

    BoatStatusEnum getStatus();

    void setStatus(BoatStatusEnum status);

    Color getColor();

    void setColor(Color color);

    int getCurrentLegIndex();

    void setLatitude(double latitude);

    void setLongitude(double longitude);

    double getLatitude();

    double getLongitude();

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

    int getSourceID();

    void switchSails();

    boolean hasSailsOut();

    void changeHeading(boolean upwind, double angle);

    int getHealthLevel();

    void updateHealth(int damage);

    int getMaxHealth();

    void setMaxHealth(int health);

    void setHealthLevel(int health);



    void startRounding();

    void finishedRounding();

    boolean isRounding();

    Line getRoundingLine1();

    void setRoundingLine1(Line roundingLine1);

    Line getRoundingLine2();

    void setRoundingLine2(Line roundingLine2);


}
