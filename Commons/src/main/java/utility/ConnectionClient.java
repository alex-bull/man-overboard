package utility;


/**
 * Created by mgo65 on 19/07/17.
 */
public interface ConnectionClient {
    int getNextSourceId();

    boolean interpretPacket(byte[] header, byte[] packet, Integer clientId);

    boolean isAccepting();
}
