package seng302.Parsers;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by psu43 on 10/04/17.
 * Handles parsing for received data packets
 */
public class ByteStreamConverter {
    private BoatDataParser boatDataParser;

    public ByteStreamConverter() {
        boatDataParser = new BoatDataParser();
    }

    /**
     * Parse the received data packet
     * @param msg byte[] byte array of the message that is being received
     */
    public void parseData(byte[] msg) {
        int count = 0;
        int bodyCount = 0;
        int msgSize = 1024;
        int headerSize = 15;
        String XMLMessageLength = "";
        boolean isHeader = false;
        boolean getBoatLocationMsg = false;
        boolean getXMLMesssage = false;
        String syncByte1Value = "47";
        String syncByte2Value = "83";
        String syncByte1 = "";
        List tempBytes = new ArrayList();
        List tempMessage = new ArrayList();

        for(int i=0 ; i < msgSize; i++) {

            String hexByte = String.format("%02X", msg[i]);

            // if current byte is equal to sync byte 2 and prev byte is sync byte 1 then flag is header
            if (hexByte.equals(syncByte2Value) && syncByte1.equals(syncByte1Value)) {
                tempBytes.add(syncByte1Value);
                count++;
                isHeader = true;
            }

            if (isHeader) {
                if (count < headerSize) {
                    tempBytes.add(hexByte);
                    count++;
                } else {
                    //System.out.println("temp bytes " + tempBytes);
                    if (validBoatLocation(tempBytes)) {
                        getBoatLocationMsg = true; // flag to parse boat location message
                    }
                    else if(validXMLMessage(tempBytes)) {
                        getXMLMesssage = true;
                        XMLMessageLength = tempBytes.get(13).toString() + tempBytes.get(14).toString();
                        System.out.println(tempBytes + "\nFOUND XML" + getXMLMesssage + XMLMessageLength);
                    }
                    // get ready to parse new message
                    count = 0;
                    tempBytes.clear();
                    isHeader = false;
                }
            }


            if (getBoatLocationMsg && bodyCount < 57) {
//                    System.out.println(hex);
                tempMessage.add(hexByte);
                bodyCount++;
            }
            if (bodyCount == 56) {
                //System.out.println(tempMessage);
                boatDataParser.processMessage(tempMessage); // parse the boat location message
                getBoatLocationMsg = false;
                tempMessage.clear();
                bodyCount = 0;
            }

            syncByte1 = hexByte;
        }
    }

    /**
     * Confirm the message's message type and message length with protocol
     * @param data List a list of data in hexadecimal bytes
     * @return boolean True if the data received is a valid boat location
     */
    private boolean validBoatLocation(List data) {
        // from protocol byte 2: message type = 37 in dec and 13: length is 57 in dec
        String hexMsgType = "25";
        String hexMsgLen = "38";
        if(data.get(2).equals(hexMsgType) && data.get(13).equals(hexMsgLen)) {
            return true;
        }
        return false;
    }

    /**
     * Confirm the message's message type and message length with protocol
     * @param data List a list of data in hexadecimal bytes
     * @return boolean True if the data received is a valid XML location
     */
    private boolean validXMLMessage(List data) {
        // from protocol byte 2: message type = 26 in dec and 13: length is not fixed
        String hexMsgType = "1A";
        //String hexMsgLen = "38";
        if(data.get(2).equals(hexMsgType)) {
            return true;
        }
        return false;
    }


}

