package seng302.Model;

import seng302.Parsers.*;

import java.io.*;
import java.net.Socket;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by khe60 on 10/04/17.
 * The Receiver class, currently receives messages 1 byte at a time
 * Can't connect to the test port for some reason (internet enabler)
 */
public class DataReceiver extends TimerTask {
    private Socket receiveSock;
    private DataInputStream dis;
    private ByteStreamConverter byteStreamConverter;
    private FileOutputStream fileOutputStream;
    private List<Competitor> competitors;
    private List<MutablePoint> courseBoundary;
    private Race race;
    //variables that need to be refactored
    private BoatData boatData;
    private RaceData raceData;

    /**
     * Initializes port to receive binary data from
     * @param host String host of the server
     * @param port int number of port of the server
     * @throws IOException IOException
     */
    public DataReceiver(String host, int port) throws IOException {
        receiveSock = new Socket(host, port);
        dis = new DataInputStream(receiveSock.getInputStream());
        fileOutputStream=new FileOutputStream(new File("src/main/resources/BinaryFiles/"+host));
//        System.setOut(new PrintStream(new BufferedOutputStream(fileOutputStream)));
        byteStreamConverter = new ByteStreamConverter();
        System.out.println("Start connection to server...");



    }


    /**
     * Close the established streams and sockets
     * @throws IOException IOException
     */
    public void close() throws IOException {
        receiveSock.close();
        dis.close();
    }

    /**
     * Receives one byte from server and returns it, test server only sends one byte at a time so this is gonna get changed
     * @return the byte received
     * @throws IOException
     */
    public byte[] receive() throws IOException {
        byte[] received=new byte[1];
        dis.readFully(received);
        return received;

    }

    private void readXMLMessage(byte[] message) {
        String xml = byteStreamConverter.parseXMLMessage(message);
        XmlSubtype subType = byteStreamConverter.getXmlSubType();

//            System.out.println(xml);

        switch (subType) {
            case REGATTA:
                RegattaXMLParser regattaParser = new RegattaXMLParser(xml);
                break;
            case RACE:
                RaceXMLParser raceParser = new RaceXMLParser(xml);
                raceData = raceParser.getRaceData();
                updateCourseBoundary(raceData);
                break;
            case BOAT:
                BoatXMLParser boatParser = new BoatXMLParser(xml);
                break;
        }
    }

    private void updateCourseBoundary(RaceData raceData) {
        if(courseBoundary!=null) {

        }
    }


    public void readHeader() throws IOException {
        // 13 because already read sync bytes
        byte[] header = new byte[13];
        dis.readFully(header);
        byteStreamConverter.parseHeader(header);
    }


    public boolean checkForSyncBytes() throws IOException {
        byte firstSyncByte = 0x47;
        // -125 is equivalent to 0x83 unsigned
        byte secondSyncByte = -125;

        byte[] b1 = new byte[1];
        dis.readFully(b1);
//        System.out.println(String.format("First Sync: %02X",b1[0]));
        if (b1[0] == firstSyncByte) {
            byte[] b2 = new byte[1];


            dis.readFully(b2);
//            System.out.println(String.format("Second Sync: %02X",b2[0]));
            if (b2[0] == secondSyncByte) {
                return true;
            }
        }
        return false;
    }

    public void setCompetitors(List<Competitor> competitors){
        this.competitors=competitors;
    }


    public void setCourseBoundary(List<MutablePoint> courseBoundary) {
        this.courseBoundary = courseBoundary;
    }

    public void run() {
//        while(true){
            try {
                boolean isStartOfPacket = checkForSyncBytes();

                if (isStartOfPacket) {
                    readHeader();

                    int XMLMessageType = 26;
                    int boatLocationMessageType = 37;
                    int messageType = (int) byteStreamConverter.getMessageType();
                    int messageLength = (int) byteStreamConverter.getMessageLength();

                    byte[] message = new byte[messageLength];
                    dis.readFully(message);

                    if (messageType == XMLMessageType) {
                        readXMLMessage(message);
                    }
                    else if (messageType == boatLocationMessageType) {
                        boatData = byteStreamConverter.parseBoatLocationMessage(message);
//                        System.out.println(boatData);
                        //update boat with boat data
                        if(competitors!=null){
                            //TODO: get rid of this hack
                            int index=boatData.getSourceID() - 101;
                            if(index<competitors.size()) {
                                competitors.get(index).setProperties(boatData.getSpeed(), boatData.getHeading(), boatData.getLatitude(), boatData.getLongitude());
                            }
                        }

                    }
                }

            }
            catch (EOFException e) {
                System.out.println("End of file.");

//                break;
            }
            catch (IOException e) {
                e.printStackTrace();
//                break;
            }
//        }
    }

    public static void main (String [] args) throws InterruptedException {
        DataReceiver dataReceiver = null;
        while(dataReceiver == null) {
            try {
//                dataReceiver = new DataReceiver("livedata.americascup.com", 4941);
                dataReceiver = new DataReceiver("csse-s302staff.canterbury.ac.nz", 4941);

                Timer timer=new Timer();
                timer.schedule(dataReceiver,0,100);

            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }


    }

    public Race getRace() {
        return race;
    }
}
