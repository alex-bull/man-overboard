package utility;


import models.Competitor;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.zip.CRC32;
import java.util.zip.Checksum;

/**
 * Created by mattgoodson on 24/04/17.
 * Binary Package
 */
public class BinaryPackager {


    /**
     * Encapsulate a boat control action in a binary packet
     * @param action Integer the code for the boat action
     * @param sourceId Integer the sourceId of the boat
     * @return byte[] the packet
     */
    public byte[] packageBoatAction(Integer action, Integer sourceId) {

        byte[] packet = new byte[24];
        ByteBuffer packetBuffer = ByteBuffer.wrap(packet);
        packetBuffer.order(ByteOrder.LITTLE_ENDIAN);

        byte type = 100;
        short bodyLength = 1;
        this.writeHeader(packetBuffer, type, bodyLength, sourceId);

        packetBuffer.put(action.byteValue());

        Checksum crc32 = new CRC32();
        crc32.update(packet, 0, packet.length - 4);
        packetBuffer.putInt((int) crc32.getValue());

        return packet;


    }

    /**
     * Takes boat position data and encapsulates it in a binary packet
     *
     * @param sourceId  Double, the id of the boat
     * @param latitude  Double, the current latitude of the boat
     * @param longitude Double, the current longitude of the boat
     * @param heading   Double, the current heading of the boat
     * @param boatSpeed Double, the current speed of the boat
     * @return byte[], the binary packet
     */
    public byte[] packageBoatLocation(Integer sourceId, Double latitude, Double longitude, Double heading, Double boatSpeed, int deviceType) {

        byte[] packet = new byte[75];

        ByteBuffer packetBuffer = ByteBuffer.wrap(packet);
        packetBuffer.order(ByteOrder.LITTLE_ENDIAN);

        Integer sequenceNumber = 0; //TODO:- Figure out what the sequence number should be

        //HEADER
        byte type = 37;
        short bodyLength = 56;
        this.writeHeader(packetBuffer, type, bodyLength);

        //BODY - note: chars are used as unsigned shorts
        packetBuffer.put((byte) 1); //version number
        packetBuffer.put(this.getCurrentTimeStamp()); //timestamp
        packetBuffer.putInt(sourceId); //boat id
        packetBuffer.putInt(sequenceNumber); //sequence number
        packetBuffer.put((byte) deviceType); //device type:

        latitude = latitude * 2147483648.0 / 180.0; //latitude
        packetBuffer.putInt(latitude.intValue());

        longitude = longitude * 2147483648.0 / 180.0; //longitude
        packetBuffer.putInt(longitude.intValue());

        packetBuffer.putInt(1); //Altitude: TODO:- Figure out what value altitude should be

        double head = heading * 65536.0 / 360.0; //heading
        packetBuffer.putShort((short) head);

        packetBuffer.putShort((short) 1); // Pitch - Can probably be ignored
        packetBuffer.putShort((short) 1); // Roll- can also be ignored

        double speed = boatSpeed; //Boat speed
        packetBuffer.putShort((short) speed);

        packetBuffer.putShort((short) 0); //COG
        packetBuffer.putShort((short) speed); //SOG
        packetBuffer.putShort((short) 0); //Apparent wind speed
        packetBuffer.putShort((short) 1); // Apparent wind angle
        packetBuffer.putShort((short) 0); //True wind speed
        packetBuffer.putShort((short) 0); //True wind direction

        double trueWindAngle = 25.0; //true wind angle
        trueWindAngle = trueWindAngle * 32768.0 / 180.0;
        packetBuffer.putShort((short) trueWindAngle);

        packetBuffer.putShort((short) 0); //Current drift
        packetBuffer.putShort((short) 0); //Current set
        packetBuffer.putShort((short) 1); // Rudder angle


        //CRC
        Checksum crc32 = new CRC32();
        crc32.update(packet, 0, packet.length - 4);
        packetBuffer.putInt((int) crc32.getValue());
        return packet;

    }

    /**
     * Packages XML files into bytearray to be sent
     *
     * @param length        length of the xmlFile String
     * @param xmlFileString the xml file to be sent
     * @param messageType   the type of the xml file
     *                      5-Regatta
     *                      6-Race
     *                      7-Boat
     * @return a bytearray of packaged xml message
     */
    public byte[] packageXML(int length, String xmlFileString, int messageType) throws IOException {

        // message header is 19 bytes + 14 bytes of other xml message fields
        byte[] packet = new byte[length + 33];
        ByteBuffer packetBuffer = ByteBuffer.wrap(packet);
        packetBuffer.order(ByteOrder.LITTLE_ENDIAN);

        //HEADER
        byte type = 26;
        int bodyLength = length + 14;
        this.writeHeader(packetBuffer, type, (short) bodyLength);
        //BODY - note: chars are used as unsigned shorts
        packetBuffer.put((byte) 1); //version number
        packetBuffer.putShort((short) 1);//AckNumber, set to 1 since we only send it once for now
        packetBuffer.put(getCurrentTimeStamp()); //timestamp
        packetBuffer.put((byte) messageType); //message type
        packetBuffer.putShort((short) 1);//sequence number
        packetBuffer.putShort((short) length);//length of message

        //get the content of xmlFile
        packetBuffer.put(xmlFileString.getBytes());

        //CRC
        Checksum crc32 = new CRC32();
        crc32.update(packet, 0, packet.length - 4);
        packetBuffer.putInt((int) crc32.getValue());

        return packet;

    }

    /**
     * writes the header to a given buffer in AC35 streaming format
     *
     * @param buffer        The buffer to write to
     * @param messageType   byte, the type of message
     * @param messageLength short, the length of the message body
     * @param sourceId int, the sourceid of a competitor
     */
    private void writeHeader(ByteBuffer buffer, int messageType, int messageLength, int sourceId) {

        byte syncByteOne = 0x47;
        buffer.put(syncByteOne);
        byte syncByteTwo = -125;
        buffer.put(syncByteTwo);

        buffer.put((byte) messageType);
        buffer.put(this.getCurrentTimeStamp());

        //message source id
        buffer.putInt(sourceId);
       // System.out.println("sOURCE ID IN WRITE HEAD" + sourceId);
        buffer.putShort((short) messageLength);
    }

    /**
     * writes the header to a given buffer in AC35 streaming format
     *
     * @param buffer        The buffer to write to
     * @param messageType   byte, the type of message
     * @param messageLength short, the length of the message body
     */
    private void writeHeader(ByteBuffer buffer, int messageType, int messageLength) {

        byte syncByteOne = 0x47;
        buffer.put(syncByteOne);
        byte syncByteTwo = -125;
        buffer.put(syncByteTwo);

        buffer.put((byte) messageType);
        buffer.put(this.getCurrentTimeStamp());

        //message source id
        buffer.putInt(1);
        buffer.putShort((short) messageLength);
    }


    /**
     * returns the current time in milliseconds since January 1, 1970
     *
     * @return byte[], the timestamp of 6 bytes
     */
    private byte[] getCurrentTimeStamp() {
        long time = System.currentTimeMillis();
        return this.get48bitTime(time);
    }

    /**
     * Gets the time stamp from a LocalDateTime
     *
     * @param estimatedStartTime the LocalDateTime
     * @return byte[] the timestamp of 6 bytes
     */
    private byte[] getTimeStamp(ZonedDateTime estimatedStartTime) {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        calendar.setTimeInMillis(estimatedStartTime.toInstant().toEpochMilli());
        long time = calendar.getTimeInMillis();
        return this.get48bitTime(time);
    }


    /**
     * Takes a long time and removes the 2 lowest bytes
     *
     * @param time long the time
     * @return byte[] the remaining 6 bytes
     */
    private byte[] get48bitTime(Long time) {
        byte[] returnArray = new byte[8];
        byte[] tempArray;

        ByteBuffer buf = ByteBuffer.wrap(returnArray).order(ByteOrder.LITTLE_ENDIAN);
        buf.putLong(time);

        tempArray = Arrays.copyOfRange(returnArray, 0, 6);

        return tempArray;
    }


    /**
     * Packages a race status message, currently only takes race ID, race status and expected start time as input,
     * can add wind direction and others later on. Does not include per boat section of the message
     *
     * @param raceStatus        the race status of the race
     *                          0 – Not Active
     *                          1 – Warning (between 3:00 and 1:00 before start)
     *                          2 – Preparatory (less than 1:00 before start)
     *                          3 – Started
     *                          4 – Finished (obsolete)
     *                          5 – Retired (obsolete)
     *                          6 – Abandoned
     *                          7 – Postponed
     *                          8 – Terminated
     *                          9 – Race start time not set
     *                          10 – Prestart (more than 3:00 until start)
     * @param expectedStartTime the expected start time
     * @param windDirection the wind direction
     * @param windSpeed the wind speed
     * @return byte[], the race status message
     */
    public byte[] raceStatusHeader(int raceStatus, ZonedDateTime expectedStartTime, short windDirection, short windSpeed, int numBoats) {
        byte[] packet = new byte[24];

        ByteBuffer packetBuffer = ByteBuffer.wrap(packet);
        packetBuffer.order(ByteOrder.LITTLE_ENDIAN);

        packetBuffer.put((byte) 2); //MessageVersionNumber
        packetBuffer.put(getCurrentTimeStamp());//CurrentTime
        packetBuffer.putInt(123546789);//RaceID /TODO:- Figure out what this should be
        packetBuffer.put((byte) raceStatus); //RaceStatus
        packetBuffer.put(getTimeStamp(expectedStartTime));//ExpectedStartTime
        packetBuffer.putShort(windDirection); //WindDirection
        packetBuffer.putShort(windSpeed);//WindSpeed
        packetBuffer.put((byte) numBoats);//Number of Boats
        packetBuffer.put((byte) 1);//RaceType 1 ->MatchRace

        return packet;

    }

    /**
     * package boat's status given a list of competitors
     *
     * @param competitors HashMap the map of boats
     * @return byte[] of each boat's section in RaceStatus Message
     */
    public byte[] packageEachBoat(HashMap<Integer, Competitor> competitors) {
        byte[] packet = new byte[20 * competitors.size()];
        ByteBuffer packetBuffer = ByteBuffer.wrap(packet);
        packetBuffer.order(ByteOrder.LITTLE_ENDIAN);

        for (Integer sourceId : competitors.keySet()) {
            Competitor competitor = competitors.get(sourceId);
            packetBuffer.putInt(competitor.getSourceID()); //SourceID
            packetBuffer.put((byte) competitor.getStatus());//Boat Status
            packetBuffer.put((byte) competitor.getCurrentLegIndex()); //Leg Number
            packetBuffer.put((byte) 0);//penalties awarded, not important so far
            packetBuffer.put((byte) 0);//penalties served, not important so far
            packetBuffer.put(getCurrentTimeStamp());//Estimated time at next mark, not important so far
            packetBuffer.put(getCurrentTimeStamp());//Estimated time at finish, not important so far

        }
        return packet;
    }

    /**
     * combines the race status and each boat into one packet
     *
     * @param raceStatus the race status bytearray
     * @param eachBoat   the each boat bytearray
     * @return byte[] of the entire RaceStatus packet
     */
    public byte[] packageRaceStatus(byte[] raceStatus, byte[] eachBoat) {
        byte[] packet = new byte[19 + raceStatus.length + eachBoat.length];
        ByteBuffer packetBuffer = ByteBuffer.wrap(packet);
        packetBuffer.order(ByteOrder.LITTLE_ENDIAN);
        writeHeader(packetBuffer, 12, raceStatus.length + eachBoat.length);
        packetBuffer.put(raceStatus);
        packetBuffer.put(eachBoat);

        //CRC
        Checksum crc32 = new CRC32();
        crc32.update(packet, 0, packet.length - 4);
        packetBuffer.putInt((int) crc32.getValue());
        return packet;
    }

    public byte[] packageSourceID(int sourceID){
        byte[] packet=new byte[23];

        ByteBuffer packetBuffer=ByteBuffer.wrap(packet);
        packetBuffer.order(ByteOrder.LITTLE_ENDIAN);

        writeHeader(packetBuffer,56,(short)4);
        packetBuffer.putInt(sourceID);

        //CRC
        Checksum crc32 = new CRC32();
        crc32.update(packet, 0, packet.length - 4);
        packetBuffer.putInt((int) crc32.getValue());

        return packet;
    }

    /**
     * package yacht event
     * @param sourceID the sourceID of the Boat in the event
     * @param eventID the event happened
     *                1-boat collision
     * @return the packet generated
     */
    public byte[] packageYachtEvent(int sourceID, int eventID){
        byte[] packet=new byte[41];
        ByteBuffer packetBuffer = ByteBuffer.wrap(packet);
        packetBuffer.order(ByteOrder.LITTLE_ENDIAN);

        byte type = 29;
        short bodyLength = 22;
        this.writeHeader(packetBuffer, type, bodyLength);

        //MessageVersionNumber
        packetBuffer.put((byte)1);
        //Time
        packetBuffer.put(getCurrentTimeStamp());
//        AckNumber
        packetBuffer.putShort((short)1);
//        RaceID
        packetBuffer.putInt(123456789);
//      DestinationSourceID
        packetBuffer.putInt(sourceID);
//        IncidentID
        packetBuffer.putInt(0);
//        EventID
        packetBuffer.put((byte) eventID);

        //CRC
        Checksum crc32 = new CRC32();
        crc32.update(packet, 0, packet.length - 4);
        packetBuffer.putInt((int) crc32.getValue());
        return packet;
    }
}
