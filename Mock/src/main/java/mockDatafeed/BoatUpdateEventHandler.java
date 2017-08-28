package mockDatafeed;

import models.CrewLocation;

import java.io.IOException;
import java.util.List;

/**
 * Created by jar156 on 3/08/17.
 * BoatUpdateEventHandler
 */
public interface BoatUpdateEventHandler {
    void yachtEvent(int sourceId, int eventId);
    void markRoundingEvent(int sourceId, int compoundMarkId);
    void boatStateEvent(Integer sourceId, double health);
    void fallenCrewEvent(List<CrewLocation> locations) throws IOException;

}
