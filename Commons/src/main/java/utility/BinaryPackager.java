package utility;


import models.Competitor;
import models.CrewLocation;
import models.Shark;
import models.Whirlpool;
import parsers.BoatStatusEnum;
import parsers.MessageType;
import parsers.powerUp.PowerUpType;

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
     *
     * @param action   Integer the code for the boat action
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

        this.writeCRC(packetBuffer);

        return packet;


    }

    private int latLngToInt(double value) {
        return (int) (value * 2147483648.0 / 180.0); //latitude
    }

    /**
     * Takes boat position data and encapsulates it in a binary packet
     *
     * @param sourceId   Double, the id of the boat
     * @param latitude   Double, the current latitude of the boat
     * @param longitude  Double, the current longitude of the boat
     * @param heading    Double, the current heading of the boat
     * @param boatSpeed  Double, the current speed of the boat
     * @param deviceType integer, indicate whether its a boat or mark
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

        //latitude
        packetBuffer.putInt(latLngToInt(latitude));

        //longitude
        packetBuffer.putInt(latLngToInt(longitude));

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
        this.writeCRC(packetBuffer);
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
     * @throws IOException IO exception
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
        this.writeCRC(packetBuffer);

        return packet;

    }


    /**
     * Writes a CRC to the end of a buffer - 4 bytes
     *
     * @param buffer ByteBuffer, the buffer to write to
     */
    private void writeCRC(ByteBuffer buffer) {

        byte[] packet = buffer.array();
        Checksum crc32 = new CRC32();
        crc32.update(packet, 0, packet.length - 4);
        buffer.putInt((int) crc32.getValue());

    }


    /**
     * writes the header to a given buffer in AC35 streaming format
     * Header is 15 bytes
     *
     * @param buffer        The buffer to write to
     * @param messageType   byte, the type of message
     * @param messageLength short, the length of the message body
     * @param sourceId      int, the sourceid of a competitor
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
        buffer.putShort((short) messageLength);
    }

    /**
     * writes the header to a given buffer in AC35 streaming format
     * Header is 15 bytes
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
     * @param windDirection     the wind direction
     * @param windSpeed         the wind speed
     * @param numBoats          the number of boats in the race
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
            packetBuffer.put((byte) BoatStatusEnum.boatStatusToInt(competitor.getStatus()));//Boat Status
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
        this.writeCRC(packetBuffer);
        return packet;
    }


    /**
     * package a source id
     *
     * @param sourceID sourceId
     * @return byte[] a byte array
     */
    public byte[] packageSourceID(int sourceID) {
        byte[] packet = new byte[23];

        ByteBuffer packetBuffer = ByteBuffer.wrap(packet);
        packetBuffer.order(ByteOrder.LITTLE_ENDIAN);

        writeHeader(packetBuffer, 56, (short) 4);
        packetBuffer.putInt(sourceID);

        //CRC
        this.writeCRC(packetBuffer);

        return packet;
    }

    /**
     *  disconnect from race message, called after the race ends
     *
     * @return the packet generated
     */
    public byte[] packageDisconnect() {
        byte[] packet = new byte[19];
        ByteBuffer packetBuffer = ByteBuffer.wrap(packet);
        packetBuffer.order(ByteOrder.LITTLE_ENDIAN);

        byte type = (byte) MessageType.DISCONNECT.getValue();
        short bodyLength = 0;
        this.writeHeader(packetBuffer, type, bodyLength);
        //CRC
        this.writeCRC(packetBuffer);
        return packet;
    }

    /**
     * package yacht event
     *
     * @param sourceID the sourceID of the Boat in the event
     * @param eventID  the event happened
     *                 1-boat collision
     * @return the packet generated
     */
    public byte[] packageYachtEvent(int sourceID, int eventID) {
        byte[] packet = new byte[41];
        ByteBuffer packetBuffer = ByteBuffer.wrap(packet);
        packetBuffer.order(ByteOrder.LITTLE_ENDIAN);

        byte type = 29;
        short bodyLength = 22;
        this.writeHeader(packetBuffer, type, bodyLength);

        //MessageVersionNumber
        packetBuffer.put((byte) 1);
        //Time
        packetBuffer.put(getCurrentTimeStamp());
//        AckNumber
        packetBuffer.putShort((short) 1);
//        RaceID
        packetBuffer.putInt(123456789);
//      DestinationSourceID
        packetBuffer.putInt(sourceID);
//        IncidentID
        packetBuffer.putInt(0);
//        EventID
        packetBuffer.put((byte) eventID);

        //CRC
        this.writeCRC(packetBuffer);
        return packet;
    }


    /**
     * Packages a mark rounding message
     *
     * @param sourceID     Integer, The source Id of the boat
     * @param roundingSide Short, The side of the mark rounded: 0 is unknown, 1 is port, 2 is starboard
     * @param markID       Integer, the compoundMarkId of the mark rounded
     * @return byte[] the packet
     */
    public byte[] packageMarkRounding(Integer sourceID, byte roundingSide, Integer markID) {

        byte[] packet = new byte[40];
        ByteBuffer buffer = ByteBuffer.wrap(packet);
        buffer.order(ByteOrder.LITTLE_ENDIAN);

        byte type = 38;
        short bodyLength = 21;
        this.writeHeader(buffer, type, bodyLength);

        //MessageVersionNumber
        buffer.put((byte) 1);
        //Time
        buffer.put(getCurrentTimeStamp());
        //AckNumber
        buffer.putShort((short) 1);
        //RaceID
        buffer.putInt(123456789);
        //SourceID
        buffer.putInt(sourceID);
        //boatStatus
        buffer.put((byte) 1); //racing
        //rounding side
        buffer.put(roundingSide);
        //Mark Type
        buffer.put((byte) 0); //unknown
        //markID
        buffer.put(markID.byteValue());

        //CRC
        this.writeCRC(buffer);

        return packet;
    }


    /**
     * package boat state event
     *
     * @param sourceID Integer the sourceID of the Boat in the event
     * @param health   Integer the health as a percentage integer 0 to 100
     * @return the packet generated
     */
    public byte[] packageBoatStateEvent(Integer sourceID, Double health) {
        byte[] packet = new byte[24]; // 19 + 5
        ByteBuffer packetBuffer = ByteBuffer.wrap(packet);
        packetBuffer.order(ByteOrder.LITTLE_ENDIAN);

        byte type = 103;
        short bodyLength = 5;
        this.writeHeader(packetBuffer, type, bodyLength);
        packetBuffer.putInt(sourceID);
        packetBuffer.put(health.byteValue());

        //CRC
        this.writeCRC(packetBuffer);
        return packet;
    }


    /**
     * package connection request
     *
     * @param clientType byte the connection type
     * @return the packet generated
     */
    public byte[] packageConnectionRequest(byte clientType) {
        byte[] packet = new byte[20]; //
        ByteBuffer packetBuffer = ByteBuffer.wrap(packet);
        packetBuffer.order(ByteOrder.LITTLE_ENDIAN);

        byte type = 101;
        short bodyLength = 1;
        this.writeHeader(packetBuffer, type, bodyLength);
        packetBuffer.put(clientType);
        //CRC
        this.writeCRC(packetBuffer);
        return packet;
    }


    /**
     * package connection response
     *
     * @param status   byte the connection status
     * @param sourceID Integer the source id allocated to the client
     * @return the packet generated
     */
    public byte[] packageConnectionResponse(byte status, Integer sourceID) {
        byte[] packet = new byte[24]; //
        ByteBuffer packetBuffer = ByteBuffer.wrap(packet);
        packetBuffer.order(ByteOrder.LITTLE_ENDIAN);

        byte type = 102;
        short bodyLength = 5;
        this.writeHeader(packetBuffer, type, bodyLength);
        packetBuffer.putInt(sourceID);
        packetBuffer.put(status);
        //CRC
        this.writeCRC(packetBuffer);
        return packet;
    }


    /**
     * package player ready message
     *
     * @return the packet generated
     */
    public byte[] packagePlayerReady() {
        byte[] packet = new byte[19]; //
        ByteBuffer packetBuffer = ByteBuffer.wrap(packet);
        packetBuffer.order(ByteOrder.LITTLE_ENDIAN);

        byte type = 110;
        short bodyLength = 0;
        this.writeHeader(packetBuffer, type, bodyLength);
        //CRC
        this.writeCRC(packetBuffer);
        return packet;
    }


    /**
     * package leave lobby message
     *
     * @return the packet generated
     */
    public byte[] packageLeaveLobby() {

        byte[] packet = new byte[19]; //
        ByteBuffer packetBuffer = ByteBuffer.wrap(packet);
        packetBuffer.order(ByteOrder.LITTLE_ENDIAN);

        byte type = 111;
        short bodyLength = 0;
        this.writeHeader(packetBuffer, type, bodyLength);
        this.writeCRC(packetBuffer);
        return packet;
    }


    /**
     * packages fallen crew event
     *
     * @param locations the location of the fallen crew members
     * @return the packet for event
     */
    public byte[] packageFallenCrewEvent(List<CrewLocation> locations) {
        int n = locations.size();
        byte[] packet = new byte[20 + n * 13]; // total size of packet

        ByteBuffer packetBuffer = ByteBuffer.wrap(packet);
        packetBuffer.order(ByteOrder.LITTLE_ENDIAN);

        byte type = 107;
        short bodyLength = (short) (n * 13 + 1);
        this.writeHeader(packetBuffer, type, bodyLength);
        packetBuffer.put((byte) n);
        for (CrewLocation crewLocation : locations) {
            packetBuffer.putInt(crewLocation.getSourceId());
            packetBuffer.put((byte) crewLocation.getNumCrew());
            packetBuffer.putInt(latLngToInt(crewLocation.getLatitude()));
            packetBuffer.putInt(latLngToInt(crewLocation.getLongitude()));
        }


        //CRC
        this.writeCRC(packetBuffer);
        return packet;
    }


    /**
     * packages power up
     *
     * @param powerId   Integer ID of power up
     * @param latitude  Double Latitude of power up location
     * @param longitude Double Longitude of power up location
     * @param radius    short Radius of power up in meters
     * @param powerType int Type of power up, 0 is speed and 1 is for projectile
     * @param duration  int Time power up is active for
     * @param timeout   long the timeout of the power up
     * @return the packet for power up
     */
    public byte[] packagePowerUp(Integer powerId, Double latitude, Double longitude, short radius, PowerUpType powerType, int duration, long timeout) {
        byte[] packet = new byte[44];
        ByteBuffer packetBuffer = ByteBuffer.wrap(packet);
        packetBuffer.order(ByteOrder.LITTLE_ENDIAN);

        byte type = 112;
        short bodyLength = 25;
        this.writeHeader(packetBuffer, type, bodyLength);
        //BODY - note: chars are used as unsigned shorts
        packetBuffer.putInt(powerId); //power id

        //latitude
        packetBuffer.putInt(latLngToInt(latitude));

        //longitude
        packetBuffer.putInt(latLngToInt(longitude));

        packetBuffer.putShort(radius);

        packetBuffer.put(this.get48bitTime(timeout));
        packetBuffer.put((byte) powerType.getValue());

        packetBuffer.putInt(duration);

        this.writeCRC(packetBuffer);
        return packet;
    }


    /**
     * packages power up taken
     *
     * @param boatId   Boat ID of power up
     * @param powerId  Integer ID of power up
     * @param duration int Time power up is active for
     * @return the packet for power up taken
     */
    public byte[] packagePowerUpTaken(int boatId, int powerId, int duration) {
        byte[] packet = new byte[31];
        ByteBuffer packetBuffer = ByteBuffer.wrap(packet);
        packetBuffer.order(ByteOrder.LITTLE_ENDIAN);

        byte type = 113;
        short bodyLength = 12;
        this.writeHeader(packetBuffer, type, bodyLength);
        packetBuffer.putInt(boatId); //boat id
        packetBuffer.putInt(powerId); //power id
        packetBuffer.putInt(duration); //duration
        this.writeCRC(packetBuffer);
        return packet;
    }

    /**
     * Packages Shark event
     * only one shark currently
     *
     * @param shark the location of the Obstacles
     * @return the packet for event
     */
    public byte[] packageSharkEvent(Shark shark) {
        int n = 1;
        byte[] packet = new byte[20 + n * 22]; // total size of packet

        ByteBuffer packetBuffer = ByteBuffer.wrap(packet);
        packetBuffer.order(ByteOrder.LITTLE_ENDIAN);

        byte type = 120;
        short bodyLength = (short) (n * 22 + 1);
        this.writeHeader(packetBuffer, type, bodyLength);
        packetBuffer.put((byte) n);

            packetBuffer.putInt(shark.getSourceId());
            packetBuffer.put((byte) shark.getNumSharks());
            packetBuffer.putInt(latLngToInt(shark.getLatitude()));
            packetBuffer.putInt(latLngToInt(shark.getLongitude()));
            packetBuffer.put((byte) shark.getVelocity());
            packetBuffer.putDouble(shark.getHeading());


        //CRC
        this.writeCRC(packetBuffer);
        return packet;
    }


    /**
     * Packages Blood event
     *
     * @param sourceId the location of the blood pool
     * @return the packet for event
     */
    public byte[] packageBloodEvent(int sourceId) {

        byte[] packet = new byte[23]; // total size of packet

        ByteBuffer packetBuffer = ByteBuffer.wrap(packet);
        packetBuffer.order(ByteOrder.LITTLE_ENDIAN);

        byte type = 121;
        short bodyLength = (short) (4);
        this.writeHeader(packetBuffer, type, bodyLength);
        packetBuffer.putInt(sourceId);


        //CRC
        this.writeCRC(packetBuffer);
        return packet;
    }

    /**
     * Packages whirlpool event
     *
     * @param whirlpools the data for whirlpool
     * @return the packet for event
     */
    public byte[] packageWhirlpoolEvent(List<Whirlpool> whirlpools) {
        int n = whirlpools.size();
        byte[] packet = new byte[20 + n * 16]; // total size of packet

        ByteBuffer packetBuffer = ByteBuffer.wrap(packet);
        packetBuffer.order(ByteOrder.LITTLE_ENDIAN);

        byte type = 119;
        short bodyLength = (short) (n * 16 + 1);
        this.writeHeader(packetBuffer, type, bodyLength);
        packetBuffer.put((byte) n);
        for (Whirlpool whirlpool : whirlpools) {
            packetBuffer.putInt(whirlpool.getSourceID());
            packetBuffer.putInt(whirlpool.getCurrentLeg());
            packetBuffer.putInt(latLngToInt(whirlpool.getLatitude()));
            packetBuffer.putInt(latLngToInt(whirlpool.getLongitude()));
        }

        //CRC
        this.writeCRC(packetBuffer);
        return packet;
    }

    public byte[] packageBoatName(Integer sourceId, String boatName) {

        byte[] packet = new byte[56]; //
        ByteBuffer packetBuffer = ByteBuffer.wrap(packet);
        packetBuffer.order(ByteOrder.LITTLE_ENDIAN);

        byte type = 106;
        short bodyLength = 37;
        this.writeHeader(packetBuffer, type, bodyLength);
        packetBuffer.putInt(sourceId);
        packetBuffer.put((byte) 0);
        packetBuffer.put((byte) 0);
        packetBuffer.put((byte) 0);

        byte[] name = new byte[30];
        ByteBuffer namebuffer = ByteBuffer.wrap(name);
        namebuffer.put(boatName.getBytes());
        packetBuffer.put(name);
        this.writeCRC(packetBuffer);
        return packet;
    }


    public byte[] packageBoatModel(Integer sourceId, Integer code) {

        byte[] packet = new byte[26]; //
        ByteBuffer packetBuffer = ByteBuffer.wrap(packet);
        packetBuffer.order(ByteOrder.LITTLE_ENDIAN);

        byte type = 104;
        short bodyLength = 7;
        this.writeHeader(packetBuffer, type, bodyLength);
        packetBuffer.putInt(sourceId);
        packetBuffer.putShort((short) 2);
        packetBuffer.put(code.byteValue());
        this.writeCRC(packetBuffer);
        return packet;
    }
}
























