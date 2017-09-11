package models;

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

    double getVelocity();
    double getCollisionRadius();
    void setVelocity(double velocity);
    MutablePoint getPosition17();
    void setPosition17(MutablePoint position17);
    MutablePoint getPosition();
    void setBoatSpeed(Force boatSpeed);
    Force getBoatSpeed();
     void addForce(Force externalForce);
     void removeForce(Force externalForce);

     List<Force> getExternalForces();
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

    void setTimeToNextMark(long timeToNextMark);

    void setTimeAtLastMark(long timeAtLastMark);

    String getAbbreName();

    String getLastMarkPassed();

    void setLastMarkPassed(String lastMarkPassed);

    double getCurrentHeading();

    void setCurrentHeading(double currentHeading);

    void setCurrentLegIndex(int legIndex);

    int getSourceID();

    void switchSails();

    void sailsIn();

    void sailsOut();

    double getSailValue();

    boolean hasSailsOut();

    void setSailValue(double sailValue);

    void changeHeading(boolean upwind, double angle);

    double getHealthLevel();

    void updateHealth(int delta);

    double getMaxHealth();

    void setMaxHealth(int health);

    void setHealthLevel(int health);

    double getDownWind(double windAngle);

    void startRounding();

    void finishedRounding();

    boolean isRounding();

    Line getRoundingLine1();

    void setRoundingLine1(Line roundingLine1);

    Line getRoundingLine2();

    void setRoundingLine2(Line roundingLine2);


}
