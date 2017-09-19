package mockDatafeed;

import models.Blood;
import models.CrewLocation;
import models.Shark;
import models.Whirlpool;

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

    void sharkEvent(List<Shark> locations) throws IOException;

    void bloodEvent(int sourceId) throws IOException;

    void whirlpoolEvent(List<Whirlpool> whirlpools) throws IOException;

    void powerUpTakenEvent(int boatId, int powerId, int duration);

}
