package utilities;

/**
 * Created by mgo65 on 11/05/17.
 */
public interface PacketHandler {
    void interpretPacket(byte[] header, byte[] packet);
}
