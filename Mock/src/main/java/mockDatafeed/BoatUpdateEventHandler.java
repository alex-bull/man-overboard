package mockDatafeed;

/**
 * Created by jar156 on 3/08/17.
 * BoatUpdateEventHandler
 */
public interface BoatUpdateEventHandler {
    void yachtEvent(int sourceId, int eventId);
    void markRoundingEvent(int sourceId, int compoundMarkId);
    void boatStateEvent(Integer sourceId, Integer health);

}
