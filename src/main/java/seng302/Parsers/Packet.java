package seng302.Parsers;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by psu43 on 10/04/17.
 * Handles parsing for received data packets
 */
public class Packet {

    /**
     * Parse the received data packet
     * @param msg byte[] byte array of the message that is being received
     */
    public void parseData(byte[] msg) {
        int count = 0;
        int bodyCount = 0;
        boolean isHeader = false;
        boolean getBoatLocationMsg = false;

        List temp = new ArrayList(); // temporary used list to store bytes
        List msgBody = new ArrayList();

        String syncByte1 = "";

        // specify expected sync byte values
        String syncByte1Value = "47";
        String syncByte2Value = "83";
        int msgSize = 1024;
        int headerSize = 15;

        for(int i=0 ; i < msgSize; i++) {

            String hexByte = String.format("%02X", msg[i]);

            // if current byte is equal to sync byte 2 and prev byte is sync byte 1 then flag is header
            if (hexByte.equals(syncByte2Value) && syncByte1.equals(syncByte1Value)) {
                temp.add(syncByte1Value);
                count++;
                isHeader = true;
            }

            if (isHeader) {
                if (count < headerSize) {
                    temp.add(hexByte);
                    count++;
                } else {
                    if (validBoatLocation(temp)) {
                        getBoatLocationMsg = true; // flag to parse boat location message
                    }
                    // get ready to parse new message
                    count = 0;
                    temp.clear();
                    isHeader = false;
                }
            }

            if (getBoatLocationMsg && bodyCount < 57) {
//                    System.out.println(hex);
                msgBody.add(hexByte);
                bodyCount++;
            }
            if (bodyCount == 56) {
                System.out.println(msgBody);
                processMsgBody(msgBody); // parse the boat location message
                getBoatLocationMsg = false;
                msgBody.clear();
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
     * Process the given list of data and parse it
     * @param body List a list of hexadecimal bytes
     */
    private void processMsgBody(List<String> body) {
        // parse source id, latitude, longitude, heading, speed

        // source id
        List sourceIDHexValues = body.subList(7, 11);
        System.out.println("sourceID " + sourceIDHexValues);
        Integer sourceID = hexListToDecimal(sourceIDHexValues);
        System.out.println("parsed source ID: " + sourceID);

        List latitudeHexValues = body.subList(16, 20);
        List longitudeHexValues = body.subList(20, 24);
        // latitude calculations
        System.out.println("lat " + latitudeHexValues);
        Double latitude = parseCoordinate(latitudeHexValues);
        System.out.println("parsed lat: " + latitude);

        // longitude calculations
        System.out.println("long " + longitudeHexValues);
        Double longitude = parseCoordinate(longitudeHexValues);
        System.out.println("parsed long: " + longitude);

        // heading
        List headingHexValues = body.subList(28, 30);
        System.out.println("head " + headingHexValues);
        Double heading = parseHeading(headingHexValues);
        System.out.println("parsed heading : " + heading);

        // speed
        List speedHexValues = body.subList(34, 36);
        System.out.println("Speed " + speedHexValues);
        Integer speed = hexListToDecimal(speedHexValues);
        System.out.println("parsed speed: " + speed);
    }


    /**
     * Convert a list of little endian hex values into an integer
     * @param hexValues List a list of hexadecimal bytes in little endian format
     * @return Integer the integer value of the hexadecimal bytes
     */
    private Integer hexListToDecimal(List hexValues) {
        String hexString = "";
        for(int i = 0; i < hexValues.size(); i++) {
            String hex = hexValues.get(i).toString();
            String reverseHex = new StringBuilder(hex).reverse().toString();
            hexString += reverseHex;
        }
        String reverseHexString = new StringBuilder(hexString).reverse().toString();
        return Integer.parseInt(reverseHexString, 16);
    }

    /**
     * Convert a list of little endian hex values into a decimal heading
     * @param hexValues List a list of (2) hexadecimal bytes in little endian format
     * @return Double the value of the heading
     */
    private Double parseHeading(List hexValues) {
        Integer integerValue = hexListToDecimal(hexValues);
        Double scaledValue = (double) integerValue * 360.0 / 65536.0;
        return scaledValue;
    }

    /**
     * Convert a list of little endian hex values into a decimal latitude or longitude
     * @param hexValues List a list of (4) hexadecimal bytes in little endian format
     * @return Double the value of the coordinate value
     */
    private Double parseCoordinate(List hexValues) {
        Integer integerValue = hexListToDecimal(hexValues);
        Double scaledValue = (double) integerValue * 180.0 /  2147483648.0;
        return scaledValue;
    }
}

