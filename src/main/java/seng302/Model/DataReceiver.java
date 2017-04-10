package seng302.Model;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
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
    public byte receive() throws IOException {
        byte[] received=new byte[1];
            dis.readFully(received);
            return received[0];

    }
    public static void main (String [] args) throws InterruptedException {

        DataReceiver me= null;
        System.out.println("start connection to server");
        while(me==null){
            try {
                me=new DataReceiver("livedata.americascup.com",4941);
            } catch (IOException e) {
                System.out.println(e.getMessage());
                System.out.println("connection failed retry in 1 sec");
                Thread.sleep(1000);
            }

        }


        while(true){
            byte msg= 0;
            try {
                msg = me.receive();
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println(String.format("%02X ", msg));
        }
    }
}
