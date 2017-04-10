package seng302.Model;

import seng302.Parsers.Packet;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by khe60 on 10/04/17.
 * The Receiver class, currently receives messages 1 byte at a time
 * Can't connect to the test port for some reason
 */
public class DataReceiver {
    private Socket receiveSock;
    private DataInputStream dis;

    /**
     * initializes port to receive binary data from
     * @param host host of the server
     * @param port port of the server
     * @throws IOException
     */
    public DataReceiver(String host, int port) throws IOException {
        receiveSock = new Socket(host, port);
        dis = new DataInputStream(receiveSock.getInputStream());
    }


    /**
     * close the established streams and sockets
     * @throws IOException
     */
    public void close() throws IOException {
        receiveSock.close();
        dis.close();
    }

    /**
     * receives one byte from server and returns it, test server only sends one byte at a time so this is gonna get changed
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
        DataReceiver me= null;
        System.out.println("start connection to server");
        while(me==null){
            try {
                me=new DataReceiver("livedata.americascup.com",4941);
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("connection failed retry in 1 sec");
                Thread.sleep(1000);
            }

        }


        while(true){
            byte[] msg= new byte[1024];
            try {
                msg = me.receive();

            } catch (IOException e) {
                e.printStackTrace();
            }

            int count = 0;
            boolean isHeader = false;
            boolean getBoatLocationMsg = false;
            ArrayList arrayList = new ArrayList();
            ArrayList msgBody = new ArrayList();
            int bodyCount = 0;
            String syncByte1 = "";
            for(int i=0;i<1024;i++){

                String hex = String.format("%02X", msg[i]);
                if(hex.equals("83") && syncByte1.equals("47")) {
                    arrayList.add("47");
                    count++;
                    isHeader = true;
                }

                if(isHeader) {
                    if(count < 15) {
                        arrayList.add(hex);
                        count++;
                    }
                    else{
                        boolean valid  = packet.validBoatLocation(arrayList);
                        if(valid) {
                            getBoatLocationMsg = true;
                        }
                        count = 0;
                        arrayList.clear();
                        isHeader = false;

                    }
                }

                if(getBoatLocationMsg && bodyCount < 57) {
//                    System.out.println(hex);
                    msgBody.add(hex);
                    bodyCount++;
                }
                if (bodyCount == 56) {
                    System.out.println(msgBody);
                    packet.processMsgBody(msgBody);
                    getBoatLocationMsg = false;
                    msgBody.clear();
                    bodyCount = 0;
                }

                syncByte1 = hex;

//                System.out.print(String.format("%02X ", msg[i]));

            }

        }
    }


}
