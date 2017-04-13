package seng302.Model;

import seng302.Parsers.Packet;

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
        byte[] received=new byte[1024];
        dis.readFully(received);
        return received;

    }


    public static void main (String [] args) throws InterruptedException {
        Packet packet = new Packet();
        DataReceiver me = null;
        System.out.println("Start connection to server...");

        while(me==null){
            try {
                me=new DataReceiver("livedata.americascup.com",4941);
//                me=new DataReceiver("csse-s302staff.canterbury.ac.nz",4941);
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("connection failed retry in 1 sec");
                Thread.sleep(1000);
            }

        }

        while(true){
            byte[] msg = new byte[1024];
            try {
                msg = me.receive();

            } catch (IOException e) {
                e.printStackTrace();
            }

            packet.parseData(msg);

        }
    }


}
