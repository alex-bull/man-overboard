package utility;

import javafx.stage.Stage;

/**
 * Created by mgo65 on 11/05/17.
 * Interface to handle received packets
 */
public interface PacketHandler {
    void interpretPacket(byte[] header, byte[] packet);
}
