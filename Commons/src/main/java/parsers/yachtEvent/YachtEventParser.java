package parsers.yachtEvent;

import parsers.Converter;

import java.util.Arrays;

import static parsers.Converter.hexByteArrayToInt;

/**
 * Created by khe60 on 26/07/17.
 * Class to parse YachtEvent
 */
public class YachtEventParser {

    private long currentTime;
    private int ackNumber;
    private int raceID;
    private int sourceID;
    private int incidentID;
    private int eventID;


    /**
     * Process the given data and get SourceID and EventID
     *
     * @param data the data received
     */
    public YachtEventParser(byte[] data) {
        currentTime = Converter.hexByteArrayToLong(Arrays.copyOfRange(data, 1, 7));
        ackNumber = hexByteArrayToInt(Arrays.copyOfRange(data, 7, 9));
        raceID = hexByteArrayToInt(Arrays.copyOfRange(data, 9, 13));
        sourceID = Converter.hexByteArrayToInt(Arrays.copyOfRange(data, 13, 17));
        incidentID = hexByteArrayToInt(Arrays.copyOfRange(data, 17, 21));
        eventID = hexByteArrayToInt(Arrays.copyOfRange(data, 21, 22));
    }

    public long getCurrentTime() {
        return currentTime;
    }

    public int getAckNumber() {
        return ackNumber;
    }

    public int getRaceID() {
        return raceID;
    }

    public int getSourceID() {
        return sourceID;
    }

    public int getIncidentID() {
        return incidentID;
    }

    public int getEventID() {
        return eventID;
    }
}
