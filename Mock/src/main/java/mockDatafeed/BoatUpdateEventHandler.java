package mockDatafeed;

/**
 * Created by jar156 on 3/08/17.
 */
public interface BoatUpdateEventHandler {
    void yachtEvent(int sourceId, int eventId);
    void markRoundingEvent(int sourceId);
}
