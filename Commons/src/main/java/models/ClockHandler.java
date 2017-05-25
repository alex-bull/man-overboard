package models;

/**
 * Created by mgo65 on 30/03/17.
 * Clock Handler interface
 */
public interface ClockHandler {
    void clockTicked(String newTime, Clock clock);
}
