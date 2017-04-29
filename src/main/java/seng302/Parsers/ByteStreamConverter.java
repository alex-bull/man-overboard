package seng302.Parsers;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by psu43 on 10/04/17.
 * Handles parsing for received data packets
 */
public class ByteStreamConverter extends Converter {
    private BoatDataParser boatDataParser;
    private int messageType;
    private int messageLength;
    private XmlSubtype xmlSubType;



    public long getMessageType() {
        return this.messageType;
    }

    public long getMessageLength() {
        return this.messageLength;
    }

    public XmlSubtype getXmlSubType() {
        return xmlSubType;
    }

    ///////////////////////////////////////////


    /**
     * Find information about the message
     * @param header byte[] an array of 15 bytes corresponding to the message header
     */
    public void parseHeader(byte[] header) {
        messageType = header[0];

        // can get timestamp and source id from here if needed

        byte[] messageLengthBytes=Arrays.copyOfRange(header, 11,13);
        messageLength = hexByteArrayToInt(messageLengthBytes);
    }

    /**
     * Parse binary data into XML
     * @param message byte[] an array of bytes which includes information about the xml as well as the xml itself
     * @return String XML string describing Regatta, Race, or Boat
     */
    public String parseXMLMessage(byte[] message) {
        int regattaType = 5;
        int raceType = 6;
        int boatType = 7;
        // can get version number, ack number, timestamp if needed

        int subType = message[9];
        if (subType == regattaType) {
            xmlSubType = XmlSubtype.REGATTA;
        }
        if (subType == raceType) {
            xmlSubType = XmlSubtype.RACE;
        }
        if (subType == boatType) {
            xmlSubType = XmlSubtype.BOAT;
        }

        // can get sequence number if needed

        byte[] xmlLengthBytes = Arrays.copyOfRange(message, 12,14);
        int xmlLength = hexByteArrayToInt(xmlLengthBytes);

        int start = 14;
        int end = start + xmlLength;

        byte[] xmlBytes = Arrays.copyOfRange(message, start, end);
        String charset = "UTF-8";
        String xmlString = "";

        try {
            xmlString = new String(xmlBytes, charset);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return xmlString;
    }




}

