package seng302.Model;

import seng302.Parsers.*;

import java.io.*;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;

/**
 * Created by khe60 on 10/04/17.
 * The Receiver class, currently receives messages 1 byte at a time
 * Can't connect to the test port for some reason (internet enabler)
 */
public class DataReceiver {
    private Socket receiveSock;
    private DataInputStream dis;

    /**
     * Initializes port to receive binary data from
     * @param host String host of the server
     * @param port int number of port of the server
     * @throws IOException IOException
     */
    public DataReceiver(String host, int port) throws IOException {
        receiveSock = new Socket(host, port);
        dis = new DataInputStream(receiveSock.getInputStream());
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

    private static void readMessage(DataReceiver dataReceiver, ByteStreamConverter byteStreamConverter) throws IOException {
        // TODO shouldn't be static?
        int XMLMessageType = 26;
        int boatLocationMessageType = 37;


        int messageLength = (int) byteStreamConverter.getMessageLength();
        int messageType = (int) byteStreamConverter.getMessageType();

        byte[] message = new byte[messageLength];
        dataReceiver.dis.readFully(message);

        if (messageType == XMLMessageType) {
            String xml = byteStreamConverter.parseXMLMessage(message);
            XmlSubtype subType = byteStreamConverter.getXmlSubType();

            System.out.println(xml);

            switch (subType) {
                case REGATTA:
                    System.out.println("hi parsing regatta parsing");
                    RegattaXMLParser regattaParser = new RegattaXMLParser(xml);
                    break;
                case RACE:
                    System.out.println(" hi parsing race parsing");
                    RaceXMLParser raceParser = new RaceXMLParser(xml);
                    break;
                case BOAT:
                    System.out.println("hi parsing boat parsing");
                    BoatXMLParser boatParser = new BoatXMLParser(xml);
                    break;
            }
        }
        else if (messageType == boatLocationMessageType) {
            byteStreamConverter.parseBoatLocationMessage(message);
        }

    }

    private static void readHeader(DataReceiver dataReceiver, ByteStreamConverter byteStreamConverter) throws IOException {
        // TODO shouldn't be static?
        byte[] header = new byte[13];
        dataReceiver.dis.readFully(header);
        byteStreamConverter.parseHeader(header);
    }

    private static boolean checkForSyncBytes(DataReceiver dataReceiver) throws IOException {
        // TODO shouldn't be static?
        String firstSyncByte = "47";
        String secondSyncByte = "83";

        byte[] b1 = new byte[1];

        dataReceiver.dis.readFully(b1);
        if (String.format("%02X", b1[0]).equals(firstSyncByte)) {
            byte[] b2 = new byte[1];

            dataReceiver.dis.readFully(b2);
            if (String.format("%02X", b2[0]).equals(secondSyncByte)) {
                return true;
            }
        }
        return false;
    }


    public static void main (String [] args) throws InterruptedException {
        ByteStreamConverter byteStreamConverter = new ByteStreamConverter();
        DataReceiver dataReceiver = null;
        System.out.println("Start connection to server...");

        while(dataReceiver==null){
            try {
//                me = new DataReceiver("livedata.americascup.com",4941);
                dataReceiver = new DataReceiver("csse-s302staff.canterbury.ac.nz",4941);
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("connection failed retry in 1 sec");
                Thread.sleep(1000);
            }

        }

        while(true){
            try {
                boolean isStartOfPacket = checkForSyncBytes(dataReceiver);

                if (isStartOfPacket) {
                    readHeader(dataReceiver, byteStreamConverter);
                    readMessage(dataReceiver, byteStreamConverter);
                }

            } catch (IOException e) {
                System.out.println(e);
                break;
            }
        }
    }


}
