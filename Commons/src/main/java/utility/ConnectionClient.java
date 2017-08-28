package utility;

import com.sun.tools.corba.se.idl.InterfaceGen;

/**
 * Created by mgo65 on 19/07/17.
 */
public interface ConnectionClient {
    int getNextSourceId();
    void interpretPacket(byte[] header, byte[] packet, Integer clientId);

}
