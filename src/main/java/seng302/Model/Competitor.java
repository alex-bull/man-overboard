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

    double getCurrentHeading();

    void setCurrentHeading(double currentHeading);

    DoubleProperty getHeadingProperty();

    void setProperties(double velocity, double heading, double latitude, double longitude);
    int getSourceID();
    int getStatus();
    int getCurrentLegIndex();
    void updatePosition(double dt);
    void setStatus(int status);
    void setCurrentLegIndex(int currentLegIndex);
}
