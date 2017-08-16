package utility;

/**
 * Created by mgo65 on 19/07/17.
 */
public interface ConnectionClient {
    int addCompetitor();
    int interpretPacket(byte[] header, byte[] packet);
}
