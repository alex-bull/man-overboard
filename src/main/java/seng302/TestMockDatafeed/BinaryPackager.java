package seng302.TestMockDatafeed;

import com.google.common.io.ByteStreams;
import com.google.common.primitives.UnsignedBytes;
import edu.princeton.cs.introcs.In;
import seng302.Model.Boat;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;
import java.util.zip.CRC32;
import java.util.zip.Checksum;

/**
 * Created by mattgoodson on 24/04/17.
 */
public class BinaryPackager {


    private Calendar calendar;
    private byte syncByteOne = 0x47;
    private byte syncByteTwo = -125;



    /**
     * Takes boat position data and encapsulates it in a binary packet
     * @param sourceId Double, the id of the boat
     * @param latitude Double, the current latitude of the boat
     * @param longitude Double, the current longitude of the boat
     * @param heading Double, the current heading of the boat
     * @param boatSpeed Double, the current speed of the boat
     * @return byte[], the binary packet
     */
    public byte[] packageBoatLocation(Integer sourceId, Double latitude, Double longitude, Double heading, Double boatSpeed) {

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
        packetBuffer.put((byte) 13); //device type: App

        latitude = latitude * 2147483648.0 / 180.0; //latitude
        packetBuffer.putInt(latitude.intValue());

        longitude = longitude * 2147483648.0 / 180.0; //longitude
        packetBuffer.putInt(longitude.intValue());

        packetBuffer.putInt(1); //Altitude: TODO:- Figure out what value altitude should be

        double head = heading * 65536.0 / 360.0; //heading
        packetBuffer.putChar((char) head);

        packetBuffer.putShort((short) 1); // Pitch - Can probably be ignored
        packetBuffer.putShort((short) 1); // Roll- can also be ignored

        double speed = boatSpeed; //Boat speed
        packetBuffer.putChar((char) speed);

        packetBuffer.putChar('0'); //COG
        packetBuffer.putChar('0'); //SOG
        packetBuffer.putChar('0'); //Apparent wind speed
        packetBuffer.putShort((short) 1); // Apparent wind angle
        packetBuffer.putChar('0'); //True wind speed
        packetBuffer.putChar('0'); //True wind direction

        double trueWindAngle = 25.0; //true wind angle
        trueWindAngle = trueWindAngle * 32768.0 / 180.0;
        packetBuffer.putShort((short) trueWindAngle);

        packetBuffer.putChar('0'); //Current drift
        packetBuffer.putChar('0'); //Current set
        packetBuffer.putShort((short) 1); // Rudder angle


        //CRC
        Checksum crc32=new CRC32();
        crc32.update(packet,0,packet.length);
        packetBuffer.putInt((int) crc32.getValue());

        System.out.println();
        return packet;

    }

    /**
     * Packages XML files into bytearray to be sent
     * @param length length of the xmlFile
     * @param xmlFileString the xml file to be sent
     * @param messageType the type of the xml file
     *                    5-Regatta
     *                    6-Race
     *                    7-Boat
     * @return a bytearray of packaged xml message
     */
    public byte[] packageXML(int length, String xmlFileString, int messageType) throws IOException {

        // message header is 19 bytes + 14 bytes of other xml message fields
        byte[] packet = new byte[length+33];
        ByteBuffer packetBuffer = ByteBuffer.wrap(packet);
        packetBuffer.order(ByteOrder.LITTLE_ENDIAN);

        //HEADER
        byte type = 26;
        int bodyLength = length+14;
        this.writeHeader(packetBuffer, type, (short) bodyLength);
        //BODY - note: chars are used as unsigned shorts
        packetBuffer.put((byte) 1); //version number
        packetBuffer.putShort((short) 1);//AckNumber, set to 1 since we only send it once for now
        packetBuffer.put(getCurrentTimeStamp()); //timestamp
        packetBuffer.put((byte)messageType); //message type
        packetBuffer.putShort((short) 1);//sequence number
        packetBuffer.putShort((short)length);//length of message

        //get the content of xmlFile
        packetBuffer.put(xmlFileString.getBytes());

        //CRC
        Checksum crc32=new CRC32();
        crc32.update(packet,0,packet.length);
        packetBuffer.putInt((int) crc32.getValue());

        return packet;

    }




    /**
     * writes the header to a given buffer in AC35 streaming format
     * @param buffer The buffer to write to
     * @param messageType byte, the type of message
     * @param messageLength short, the length of the message body
     */
    private void writeHeader(ByteBuffer buffer, int messageType, int messageLength) {

        buffer.put(syncByteOne);
        buffer.put(syncByteTwo);

        buffer.put((byte)messageType);
        buffer.put(this.getCurrentTimeStamp());

        //message source id
        buffer.putInt(1); //TODO:- figure out what the message source id is
        buffer.putShort((short)messageLength);
    }



    /**
     * returns the current time in milliseconds since January 1, 1970
     * @return byte[], the timestamp of 6 bytes
     */
    private byte[] getCurrentTimeStamp(){
        long time = System.currentTimeMillis();
        return this.get48bitTime(time);
    }

    /**
     * Gets the time stamp from a LocalDateTime
     * @param estimatedStartTime the LocalDateTime
     * @return byte[] the timestamp of 6 bytes
     */
    private byte[] getTimeStamp(ZonedDateTime estimatedStartTime){
        this.calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        calendar.setTimeInMillis(estimatedStartTime.toInstant().toEpochMilli());
        long time = calendar.getTimeInMillis();
        return this.get48bitTime(time);
    }


    /**
     * Takes a long time and removes the 2 lowest bytes
     * @param time long the time
     * @return byte[] the remaining 6 bytes
     */
    private byte[] get48bitTime(long time) {
        byte[] buffer = new byte[6];
        buffer[0] = (byte)(time >>> 40);
        buffer[1] = (byte)(time >>> 32);
        buffer[2] = (byte)(time >>> 24);
        buffer[3] = (byte)(time >>> 16);
        buffer[4] = (byte)(time >>>  8);
        buffer[5] = (byte)(time >>>  0);

        return  buffer;
    }



    /**
     * Packages a race status message, currently only takes race ID, race status and expected start time as input,
     * can add wind direction and others later on. Does not include per boat section of the message
     * @param raceID the race id of the race defined in race.xml
     * @param raceStatus the race status of the race
     *                   0 – Not Active
     *                   1 – Warning (between 3:00 and 1:00 before start)
     *                   2 – Preparatory (less than 1:00 before start)
     *                   3 – Started
     *                   4 – Finished (obsolete)
     *                   5 – Retired (obsolete)
     *                   6 – Abandoned
     *                   7 – Postponed
     *                   8 – Terminated
     *                   9 – Race start time not set
     *                   10 – Prestart (more than 3:00 until start)
     * @param expectedStartTime the expected start time
     * @return byte[], the race status message
     */
    public byte[] packageRaceStatus(int raceID, int raceStatus, ZonedDateTime expectedStartTime){
        byte[] packet = new byte[24];
        short windDirection = -32768;// 0x8000 in signed short


        ByteBuffer packetBuffer = ByteBuffer.wrap(packet);
        packetBuffer.order(ByteOrder.LITTLE_ENDIAN);

        packetBuffer.put((byte) 2); //MessageVersionNumber
        packetBuffer.put(getCurrentTimeStamp());//CurrentTime
        packetBuffer.putInt(raceID);//RaceID
        packetBuffer.put((byte) raceStatus); //RaceStatus
        packetBuffer.put(getTimeStamp(expectedStartTime));//ExpectedStartTime
        packetBuffer.putShort(windDirection); //WindDirection
        packetBuffer.putShort((short) 0);//WindSpeed
        packetBuffer.put((byte) 6);//Number of Boats
        packetBuffer.put((byte) 1);//RaceType 1 ->MatchRace

        return packet;

    }

    /**
     * package boat's status given a list of competitors
     * @param competitors the list of boats
     * @return byte[] of each boat's section in RaceStatus Message
     */
    public byte[] packageEachBoat(List<Boat> competitors){
        byte[] packet=new byte[20*competitors.size()];
        ByteBuffer packetBuffer = ByteBuffer.wrap(packet);
        packetBuffer.order(ByteOrder.LITTLE_ENDIAN);

        for(Boat competitor: competitors){
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
     * @param raceStatus the race status bytearray
     * @param eachBoat the each boat bytearray
     * @return byte[] of the entire RaceStatus packet
     */
    public byte[] packetRaceStatus(byte[] raceStatus, byte[] eachBoat){
        byte[] packet=new byte[19+raceStatus.length+eachBoat.length];
        ByteBuffer packetBuffer = ByteBuffer.wrap(packet);
        packetBuffer.order(ByteOrder.LITTLE_ENDIAN);
        writeHeader(packetBuffer,12,raceStatus.length+eachBoat.length);
        packetBuffer.put(raceStatus);
        packetBuffer.put(eachBoat);

        //CRC
        Checksum crc32=new CRC32();
        crc32.update(packet,0,packet.length);
        packetBuffer.putInt((int) crc32.getValue());
        return packet;
    }


    public static void main(String[] args) {
        BinaryPackager a = new BinaryPackager();
        byte[] b = a.packageBoatLocation(12, 123.444, 234.434, 65535.0, 20.3);
        for (byte c: b) {
            System.out.println(c);
        }
        System.out.println(b.length);
    }

}
