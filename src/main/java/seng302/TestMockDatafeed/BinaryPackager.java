package seng302.TestMockDatafeed;

import com.google.common.io.ByteStreams;
import com.google.common.primitives.UnsignedBytes;
import edu.princeton.cs.introcs.In;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;
import java.util.zip.CRC32;
import java.util.zip.Checksum;

/**
 * Created by mattgoodson on 24/04/17.
 */
public class BinaryPackager {


    Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
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
        packetBuffer.put(this.getTimeStamp()); //timestamp
        packetBuffer.putInt(sourceId); //boat id
        packetBuffer.putInt(sequenceNumber); //sequence number
        packetBuffer.put((byte) 13); //device type: App

        latitude = latitude * 2147483648.0 / 180.0; //latitude
        packetBuffer.putFloat(latitude.intValue());

        longitude = longitude * 2147483648.0 / 180.0; //longitude
        packetBuffer.putFloat(longitude.floatValue());

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
        packetBuffer.put(getTimeStamp()); //timestamp
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
    private void writeHeader(ByteBuffer buffer, byte messageType, short messageLength) {

        buffer.put(syncByteOne);
        buffer.put(syncByteTwo);
        buffer.put(messageType);

        buffer.put(this.getTimeStamp());

        //message source id
        buffer.putInt(1); //TODO:- figure out what the message source id is
        buffer.putShort(messageLength);
    }



    /**
     * Returns the time in milliseconds since January 1, 1970
     * @return byte[], the timestamp as 6 bytes
     */
    private byte[] getTimeStamp() {

        calendar.clear();
        calendar.set(2011, Calendar.OCTOBER, 1);
        long time = calendar.getTimeInMillis();

        byte[] buffer = new byte[6];
        buffer[0] = (byte)(time >>> 40);
        buffer[1] = (byte)(time >>> 32);
        buffer[2] = (byte)(time >>> 24);
        buffer[3] = (byte)(time >>> 16);
        buffer[4] = (byte)(time >>>  8);
        buffer[5] = (byte)(time >>>  0);
        return  buffer;
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
