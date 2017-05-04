package seng302.Model;

import javafx.beans.property.DoubleProperty;
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

    Color getColor();

    void setColor(Color color);

    String getAbbreName();
    String getLastMarkPassed();
    double getCurrentHeading();
    int getLegIndex();
    DoubleProperty getHeadingProperty();
    void setCurrentHeading(double currentHeading);
    int getSourceID();
    void setLastMarkPassed(String lastMarkPassed);
    void setColor(Color color);
    void setVelocity(double velocity);
    void setPosition(MutablePoint position);
    void setLegIndex(int legIndex);
    void setProperties(double velocity, double heading, double latitude, double longitude);


}
