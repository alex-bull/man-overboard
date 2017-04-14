package seng302.Parsers;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by psu43 on 10/04/17.
 * Handles parsing for received data packets
 */
public class ByteStreamConverter extends Converter {
    private BoatDataParser boatDataParser;

    public ByteStreamConverter() {
        boatDataParser = new BoatDataParser();
    }


    private long messageType;
    private long messageLength;

    public long getMessageType() {
        return messageType;
    }

    public long getMessageLength() {
        return this.messageLength;
    }

    private String byteToHex(byte b) {
        return String.format("%02X", b);
    }

    private long hexToLong(String hex) {
        return Long.parseLong(hex, 16);
    }

    public void parseHeader(byte[] header) {
        messageType = hexToLong(byteToHex(header[0]));

        // can get timestamp and source id from here if needed

        List tempBytes = new ArrayList();
        tempBytes.add(byteToHex(header[11]));
        tempBytes.add(byteToHex(header[12]));
        messageLength = hexListToDecimal(tempBytes);
    }

    /**
     * Parse the received data packet
     * @param msg byte[] byte array of the message that is being received
     */
    public void parseData(byte[] msg) {
        int count = 0;
        long bodyCount = 0;
        int msgSize = 1024;
        int headerSize = 15;
        long messageLength = 0;
        boolean isHeader = false;
        boolean getBoatLocationMsg = false;
        boolean getXMLMesssage = false;
        String syncByte1Value = "47";
        String syncByte2Value = "83";
        String syncByte1 = "";
        List tempBytes = new ArrayList();
        List tempMessage = new ArrayList();


        for(int j = 0 ; j < 15; j++) {
            String hexByte = String.format("%02X", msg[0]);
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
                }
                if(count == headerSize) {
                    System.out.println(tempBytes);

                }
//                else {
//                    System.out.println("temp bytes " + tempBytes);
//
//                    messageLength = hexListToDecimal(tempBytes.subList(13, 15));
//                    if (validBoatLocation(tempBytes)) {
//                        getBoatLocationMsg = true; // flag to parse boat location message
//                    }
//                    else if(validXMLMessage(tempBytes)) {
//                        getXMLMesssage = true;
//                        System.out.println(tempBytes + "\nFOUND XML " + messageLength);
//
//                    }
//
//                    // get ready to parse new message
//                    count = 0;
//                    tempBytes.clear();
//                    isHeader = false;
//                }
            }
            syncByte1 = hexByte;
        }


//        for(int i=15 ; i < messageLength + 15; i++) {
//            String hexByte = String.format("%02X", msg[i]);
////            // if current byte is equal to sync byte 2 and prev byte is sync byte 1 then flag is header
////            if (hexByte.equals(syncByte2Value) && syncByte1.equals(syncByte1Value)) {
////                tempBytes.add(syncByte1Value);
////                count++;
////                isHeader = true;
////            }
////
////            if (isHeader) {
////                if (count < headerSize) {
////                    tempBytes.add(hexByte);
////                    count++;
////                } else {
////                    //System.out.println("temp bytes " + tempBytes);ddd
////                    if (validBoatLocation(tempBytes)) {
////                        getBoatLocationMsg = true; // flag to parse boat location message
////                    }
////                    else if(validXMLMessage(tempBytes)) {
////                        getXMLMesssage = true;
////                        XMLMessageLength = hexListToDecimal(tempBytes.subList(13, 15));
////                        System.out.println(tempBytes + "\nFOUND XML " + XMLMessageLength);
////
////                    }
////                    // get ready to parse new message
////                    count = 0;
////                    tempBytes.clear();
////                    isHeader = false;
////                }
////            }
//
//            if(getXMLMesssage && bodyCount < messageLength + 1) {
//                if(bodyCount == 1024) {
//                    System.out.println("body count is " + bodyCount);
//                }
//                tempMessage.add(hexByte);
//                bodyCount++;
//                if(bodyCount == messageLength) {
//                    System.out.println("body count is " + bodyCount);
//                }
//            }
////            if(getXMLMesssage && bodyCount == XMLMessageLength) {
////
////                System.out.println("Body count is " + bodyCount);
////                System.out.println(tempMessage);
////                getXMLMesssage = false;
////                tempMessage.clear();
////                bodyCount = 0;
////            }
//
//            if (getBoatLocationMsg && bodyCount < 57) {
////                    System.out.println(hex);
//                tempMessage.add(hexByte);
//                bodyCount++;
//            }
//            if (getBoatLocationMsg && bodyCount == 56) {
//                //System.out.println(tempMessage);
//                boatDataParser.processMessage(tempMessage); // parse the boat location message
//                getBoatLocationMsg = false;
//                tempMessage.clear();
//                bodyCount = 0;
//            }
//
////            syncByte1 = hexByte;
//        }
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
        // from protocol byte 2: message type = 26 in dec and length is not fixed
        String hexMsgType = "1A";
        if(data.get(2).equals(hexMsgType)) {
            return true;
        }
        return false;
    }


}

