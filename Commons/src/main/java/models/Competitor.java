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

    void setTeamName(String teamName);

    boolean isSailsOut();

    double getVelocity();

    void setVelocity(double velocity);

    double getCollisionRadius();

    MutablePoint getPosition17();

    void setPosition17(MutablePoint position17);

    MutablePoint getPosition();

    void setPosition(MutablePoint position);

    Force getBoatSpeed();

    void setBoatSpeed(Force boatSpeed);

    void addForce(Force externalForce);

    void removeForce(Force externalForce);

    List<Force> getExternalForces();

    void updatePosition(double dt);

    BoatStatusEnum getStatus();

    void setStatus(BoatStatusEnum status);

    Color getColor();

    void setColor(Color color);

    int getCurrentLegIndex();

    void setCurrentLegIndex(int legIndex);

    double getLatitude();

    void setLatitude(double latitude);

    double getLongitude();

    void setLongitude(double longitude);

    void setTimeToNextMark(long timeToNextMark);

    void setTimeAtLastMark(long timeAtLastMark);

    String getAbbreName();

    String getLastMarkPassed();

    void setLastMarkPassed(String lastMarkPassed);

    double getCurrentHeading();

    void setCurrentHeading(double currentHeading);

    int getSourceID();

    void switchSails();

    void sailsIn();

    void sailsOut();

    double getSailValue();

    void setSailValue(double sailValue);

    boolean hasSailsOut();

    void changeHeading(boolean upwind, double angle);

    double getHealthLevel();

    void setHealthLevel(int health);

    void updateHealth(int delta);

    double getMaxHealth();

    void setMaxHealth(int health);

    double getDownWind(double windAngle);

    void startRounding();

    void finishedRounding();

    boolean isRounding();

    Line getRoundingLine1();

    void setRoundingLine1(Line roundingLine1);

    Line getRoundingLine2();

    void setRoundingLine2(Line roundingLine2);

    Integer getBoatType();

    void setBoatType(Integer boatType);

    boolean hasSpeedBoost();

    void enableBoost();

    void disableBoost();

    void deactivateBoost();

    void activateBoost();

    boolean boostActivated();

    long getBoostTimeout();

    void resetBoostTimeout();

    void enablePotion();

    boolean hasPotion();

    void usePotion();

}
