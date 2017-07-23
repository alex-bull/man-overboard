package parsers.header;

/**
 * Created by psu43 on 20/07/17.
 * Header data
 */
public class HeaderData {
    private long timeStamp;
    private int messageType;
    private int sourceID;
    private int messageLength;

    /**
     * Header data
     * @param timeStamp long the timestamp in milliseconds
     * @param messageType message type
     * @param sourceID source id of message
     * @param messageLength length of message does not include this header or CRC
     */
    public HeaderData(int messageType, long timeStamp, int sourceID, int messageLength) {
        this.timeStamp = timeStamp;
        this.messageType = messageType;
        this.sourceID = sourceID;
        this.messageLength = messageLength;

    }


    public int getSourceID() {
        return sourceID;
    }

}
