package utilities;

import utility.QueueMessage;
import utility.WorkQueue;
import utility.PacketHandler;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.nio.channels.UnresolvedAddressException;
import java.util.Arrays;
import java.util.TimerTask;

import static parsers.Converter.hexByteArrayToInt;


/**
 * Created by khe60 on 10/04/17.
 * The Client class, currently receives messages 1 byte at a time
 * Can't connect to the test port for some reason (internet enabler)
 */
public class TCPClient extends TimerTask {

//    private SocketChannel client;
    private DataInputStream dis;
    private DataOutputStream dos;
    private WorkQueue receiveQueue;



    /**
     * Initializes port to receive binary data from
     * @param host String host of the server
     * @param port int number of port of the server
     * @param receiveQueue WorkQueue a thread safe queue to put unwrapped packets in
     * @throws IOException IOException
     */
    TCPClient(String host, int port, WorkQueue receiveQueue) throws UnresolvedAddressException, IOException {

        this.receiveQueue = receiveQueue;
        Socket receiveSock = new Socket(host, port);
        dis = new DataInputStream(receiveSock.getInputStream());
        dos = new DataOutputStream(receiveSock.getOutputStream());
        System.out.println("Start connection to server...");
    }




    /**
     * Write data to the socket
     * @param data byte[] The data to send
     * @throws IOException IOException
     */
    public void send(byte[] data) throws IOException {
        dos.write(data);
        dos.flush();
    }


    /**
     * Check for the first and second sync byte
     *
     * @return Boolean if Sync Byte found
     * @throws IOException IOException
     */
    private boolean checkForSyncBytes() throws IOException {
        // -125 is equivalent to 0x83 unsigned
        byte[] expected = {0x47,-125};
        byte[] actual = new byte[2];

        dis.readFully(actual);
        return Arrays.equals(actual, expected);
    }

    /**
     * Returns the first 13 bytes from a packet
     * @return byte[] the header byte array
     * @throws IOException IOException
     */
    private byte[] getHeader() throws IOException {
        byte[] header=new byte[13];
        dis.readFully(header);
        return header;
    }

    /**
     * returns the length field from a 13 byte header
     * @param header byte[] the header byte array
     * @return int the message length
     */
    private int getMessageLength(byte[] header) {
        byte[] messageLengthBytes = Arrays.copyOfRange(header, 11, 13);
        return hexByteArrayToInt(messageLengthBytes);
    }


    /**
     * Identify the start of a packet, determine the message type and length, then read.
     */
    public void run() throws NullPointerException {


        try {
            boolean isStartOfPacket = checkForSyncBytes();
            if (isStartOfPacket) {

                byte[] header = this.getHeader();
                int length = this.getMessageLength(header);
                byte[] message=new byte[length];
                dis.readFully(message);
                this.receiveQueue.put(null, header, message);

            }

        }catch (EOFException e){
            System.exit(0);
        }
        catch (IOException e) {
            e.printStackTrace();
        }

    }




}
