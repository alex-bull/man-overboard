package seng302.Model;

import seng302.Parsers.ByteStreamConverter;

import java.io.*;
import java.net.Socket;

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

            byte[] b = new byte[1];
            try {
                dataReceiver.dis.readFully(b);
                if (String.format("%02X", b[0]).equals("47")) {
                    byte[] b2 = new byte[1];
                    try {
                        dataReceiver.dis.readFully(b2);
                        if (String.format("%02X", b2[0]).equals("83")) {
                            byte[] header = new byte[13];
                            try {
                                dataReceiver.dis.readFully(header);
                                byteStreamConverter.parseHeader(header);


                            } catch (IOException e) {
                                break;
                            }
                        }
                    } catch (IOException e) {
                        break;
                    }
                }
            } catch (IOException e) {
                break;
            }


//            byte[] header;
//            try {
//                header = new byte[15];
//                dataReceiver.dis.readFully(header);
//                boolean isHeader = byteStreamConverter.parseHeader(header);
//                System.out.println(byteStreamConverter.getMessageLength());
//            } catch (IOException e) {
//                break;
//            }

//            byte[] message;
//            try {
//                message = dataReceiver.receive();
//                byteStreamConverter.parseMessage(message);
//            } catch (IOException e) {
//                break;
//            }
//            byte[] crc;
//            try {
//                msg = dataReceiver.receive();
//                byteStreamConverter.checkCRC(msg);
//            } catch (IOException e) {
//                break;
//            }
        }
    }


}
