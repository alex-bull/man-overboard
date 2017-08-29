package utilities;

import parsers.RaceStatusEnum;

/**
 * Created by mgo65 on 26/07/17.
 */
public interface StreamObserver {

    void raceStatusUpdated(RaceStatusEnum status);
    void streamFailed();
}
