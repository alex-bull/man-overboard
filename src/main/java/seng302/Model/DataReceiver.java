package seng302.Model;

import seng302.Parsers.*;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;

/**
 * Created by khe60 on 10/04/17.
 * The Receiver class, currently receives messages 1 byte at a time
 * Turn on internet enabler to connect to live data
 */
public class DataReceiver implements Callable {
    private Socket receiveSock;
    private DataInputStream dis;
    private ByteStreamConverter byteStreamConverter;
    private FileOutputStream fileOutputStream;

    private BoatData boatData;

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
     * @throws IOException IOException
     */
    public byte[] receive() throws IOException {
        byte[] received=new byte[1];
        dis.readFully(received);
        return received;

    }

    /**
     * Reads the message by finding the message type and length.
     * @throws IOException IOException
     */
    public void readMessage() throws IOException {
        int XMLMessageType = 26;
        int boatLocationMessageType = 37;

        int messageLength = (int) byteStreamConverter.getMessageLength();
        int messageType = (int) byteStreamConverter.getMessageType();

        byte[] message = new byte[messageLength];
        dis.readFully(message);

        if (messageType == XMLMessageType) {
            String xml = byteStreamConverter.parseXMLMessage(message);
            XmlSubtype subType = byteStreamConverter.getXmlSubType();

//            System.out.println(xml);
//
//            switch (subType) {
//                case REGATTA:
//                    RegattaXMLParser regattaParser = new RegattaXMLParser(xml);
//                    break;
//                case RACE:
//                    RaceXMLParser raceParser = new RaceXMLParser(xml);
//                    break;
//                case BOAT:
//                    BoatXMLParser boatParser = new BoatXMLParser(xml);
//                    break;
//            }
        }
        else if (messageType == boatLocationMessageType) {
            this.boatData = byteStreamConverter.parseBoatLocationMessage(message);
            System.out.println("hi");
            System.out.println(boatData.getSourceID());
            System.out.println("byte");
        }

    }


    /**
     * Reads the header of the binary message. This is called after sync bytes found.
     * @throws IOException IOException
     */
    public void readHeader() throws IOException {
        // 13 because already read sync bytes
        byte[] header = new byte[13];
        dis.readFully(header);
        byteStreamConverter.parseHeader(header);
    }


    /**
     * Checks for sync bytes in the binary message.
     * @return boolean True if sync bytes are found
     * @throws IOException IOException
     */
    public boolean checkForSyncBytes() throws IOException {
        byte firstSyncByte = 0x47;
        // -125 is equivalent to 0x83 unsigned
        byte secondSyncByte = -125;

        byte[] b1 = new byte[1];
        dis.readFully(b1);
        if (b1[0] == firstSyncByte) {
            byte[] b2 = new byte[1];

            dis.readFully(b2);
            if (b2[0] == secondSyncByte) {
                return true;
            }
        }
        return false;
    }


    /**
     * Receives the binary message
     */
    @Override
    public BoatData call() {
        while(true){
            try {

                boolean isStartOfPacket = checkForSyncBytes();

                if (isStartOfPacket) {
                    readHeader();
                    readMessage();
                }
                System.out.println(boatData.getSourceID());
                return this.boatData;

            }
            catch (EOFException e) {
                System.out.println("End of file.");
                break;
            }
            catch (IOException e) {
                e.printStackTrace();
                break;
            }
        }
        return null;
    }


    public BoatData getBoatData() {
        return boatData;
    }


    public static void main (String [] args) throws InterruptedException {
        DataReceiver dataReceiver = null;
        while(dataReceiver == null) {
            try {
//                dataReceiver = new DataReceiver("livedata.americascup.com", 4941);
                dataReceiver = new DataReceiver("csse-s302staff.canterbury.ac.nz", 4941);

                // not finished yet still need work
//                (new Thread(dataReceiver)).start();
//                ExecutorService service =  Executors.newSingleThreadExecutor();
//                Future<BoatData> future = service.submit(dataReceiver);
//                try {
//                    BoatData boatData = future.get();
//                    System.out.println(boatData.getSourceID());
//                } catch (ExecutionException |InterruptedException e) {
//                    e.printStackTrace();
//                }

            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }


    }


}
