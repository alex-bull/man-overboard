package utilities;

import java.io.EOFException;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.channels.UnresolvedAddressException;
import java.util.Arrays;
import java.util.TimerTask;

import static parsers.Converter.hexByteArrayToInt;


/**
 * Created by khe60 on 10/04/17.
 * The Receiver class, currently receives messages 1 byte at a time
 * Can't connect to the test port for some reason (internet enabler)
 */
public class DataReceiver extends TimerTask {

    private SocketChannel client;
    private PacketHandler handler;

    /**
     * Initializes port to receive binary data from
     * @param host String host of the server
     * @param port int number of port of the server
     * @param handler PacketHandler handler for incoming packets
     * @throws IOException IOException
     */
    DataReceiver(String host, int port, PacketHandler handler) throws UnresolvedAddressException, IOException {
//        Socket receiveSock = new Socket(host, port);
        this.handler = handler;
//        dis = new DataInputStream(receiveSock.getInputStream());
//        System.out.println("Start connection to server...");

        client = SocketChannel.open(new InetSocketAddress(host,port));
        client.configureBlocking(false);
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

        client.read(ByteBuffer.wrap(actual));

        return Arrays.equals(actual, expected);
    }

    /**
     * Returns the first 13 bytes from a packet
     * @return byte[] the header byte array
     * @throws IOException IOException
     */
    private byte[] getHeader() throws IOException {
        byte[] header = new byte[13];
        client.read(ByteBuffer.wrap(header));
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
    public void run() throws NullPointerException{
        try {
            boolean isStartOfPacket = checkForSyncBytes();

            if (isStartOfPacket) {
                byte[] header = this.getHeader();
                int length = this.getMessageLength(header);
                byte[] message = new byte[length];
                client.read(ByteBuffer.wrap(message));
                this.handler.interpretPacket(header, message);
            }
        } catch (EOFException e) {
            System.out.println("End of file.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //    /**
//     * Creates a new data receiver and runs at the period of 100ms
//     *
//     * @param args String[]
//     * @throws InterruptedException Interrupted Exception
//     */
//    public static void main(String[] args) throws InterruptedException {
//        DataReceiver dataReceiver = null;
//        while (dataReceiver == null) {
//            try {
//                dataReceiver = new DataReceiver("livedata.americascup.com", 4941);
//                Timer timer = new Timer();
//                timer.schedule(dataReceiver, 0, 100);
//
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//    }


}
