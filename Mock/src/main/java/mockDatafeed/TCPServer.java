package mockDatafeed;

import utility.BinaryPackager;
import utility.ConnectionClient;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.*;

import static parsers.Converter.hexByteArrayToInt;


/**
 * Created by khe60 on 10/04/17.
 * The TCPServer class
 */
public class TCPServer extends TimerTask {

    private Selector selector;
    private ServerSocketChannel serverSocket;
    private ConnectionClient connectionClient;
    private BinaryPackager binaryPackager;
    private Map<Integer, SocketChannel> clients = new HashMap<>();

    /**
     * Constructor for TCPServer, creates port at given port
     *
     * @param connectionClient Connection Client
     * @param port             int The port number
     * @throws IOException IOException
     */
    TCPServer(int port, ConnectionClient connectionClient) throws IOException {

        this.connectionClient = connectionClient;
        binaryPackager = new BinaryPackager();
        selector = Selector.open();
        serverSocket = ServerSocketChannel.open();
        serverSocket.configureBlocking(false);
        serverSocket.socket().bind(new InetSocketAddress("0.0.0.0", port));
        serverSocket.register(selector, SelectionKey.OP_ACCEPT);
        System.out.println("Server online...");

    }


    /**
     * The establishment period of the datasender, runs for time milliseconds
     *
     * @param time the amount of time in milliseconds of the connection establishment period
     * @throws IOException IOException
     */
    public void establishConnection(long time) throws IOException {
        System.out.println("start listening for client connections");
        long finishTime = System.currentTimeMillis() + time;

//
//        while (System.currentTimeMillis() < finishTime) {
//            selector.select(time);
//            Iterator<SelectionKey> iter=selector.selectedKeys().iterator();
//            while(iter.hasNext()){
//                SelectionKey key=iter.next();
//                //accept client connection
//                if (key.isAcceptable()) {
//                    SocketChannel client = serverSocket.accept();
//                    client.configureBlocking(false);
//                    client.register(selector, SelectionKey.OP_READ|SelectionKey.OP_WRITE);
//                    connectionClient
//                    //generate and send sourceID to client
//                }
//                iter.remove();
//            }
//        }
//        serverSocket.close();
//
//        System.out.println("finish listening for client connections");
//        sendSourceID();
    }


    /**
     * `
     * Check for the first and second sync byte
     *
     * @return Boolean if Sync Byte found
     * @throws IOException IOException
     */
    private boolean checkForSyncBytes(SocketChannel client) throws IOException {

        // -125 is equivalent to 0x83 unsigned
        byte[] expected = {0x47, -125};

        byte[] actual = new byte[2];

        ByteBuffer buffer = ByteBuffer.wrap(actual);

//        client.read(ByteBuffer.wrap(actual));
        client.read(buffer);
        return Arrays.equals(actual, expected);
    }


    /**
     * Returns the first 13 bytes from a packet
     *
     * @return byte[] the header byte array
     * @throws IOException IOException
     */
    private byte[] getHeader(SocketChannel client) throws IOException {
//        ByteBuffer header=ByteBuffer.allocate(13);
//        client.read(header);
        byte[] header = new byte[13];
        ByteBuffer buffer = ByteBuffer.wrap(header);

        client.read(buffer);
        return header;
    }


    /**
     * returns the length field from a 13 byte header
     *
     * @param header byte[] the header byte array
     * @return int the message length
     */
    private int getMessageLength(byte[] header) {
        byte[] messageLengthBytes = Arrays.copyOfRange(header, 11, 13);
        return hexByteArrayToInt(messageLengthBytes);
    }


    /**
     * sends the sourceID to the selection key
     */
    private void sendSourceID() throws IOException {

        selector.select(1);
        for (SelectionKey key : new HashSet<>(selector.selectedKeys())) {
            if (key.isWritable()) {

                int sourceID = connectionClient.addCompetitor();
                byte[] packet = binaryPackager.packageSourceID(sourceID);
                ByteBuffer buffer = ByteBuffer.wrap(packet);
                SocketChannel client = (SocketChannel) key.channel();
                try {
                    while (buffer.hasRemaining()) {
                        client.write(buffer);
                    }
                } catch (IOException e) {
                    System.out.println("failed to register sourceID " + sourceID);
                    key.cancel();
                }
            }
        }
    }


    /**
     * sends the data to the output socket
     *
     * @param data byte[] byte array of the data
     * @throws IOException IOException
     */
    void sendData(byte[] data) throws IOException {

        selector.select(1);
        for (SelectionKey key : new HashSet<>(selector.selectedKeys())) {
            System.out.println("Sending to client");
            //write to channel if writable
            if (key.isWritable()) {
                ByteBuffer buffer = ByteBuffer.wrap(data);
                SocketChannel client = (SocketChannel) key.channel();
                try {
                    while (buffer.hasRemaining()) {
                        client.write(buffer);
                    }

                } catch (IOException e) {
                    System.out.println(client.getRemoteAddress() + " has disconnected, removing client");
                    key.cancel();
                }
            }
            selector.selectedKeys().remove(key);
        }
    }


//     while (System.currentTimeMillis() < finishTime) {
//            selector.select(time);
//            Iterator<SelectionKey> iter=selector.selectedKeys().iterator();
//            while(iter.hasNext()){
//                SelectionKey key=iter.next();
//                //accept client connection
//                if (key.isAcceptable()) {
//                    SocketChannel client = serverSocket.accept();
//                    client.configureBlocking(false);
//                    client.register(selector, SelectionKey.OP_READ|SelectionKey.OP_WRITE);
//                    connectionClient
//                    //generate and send sourceID to client
//                }
//                iter.remove();

    /**
     * Handle incoming messages from clients
     */
    public void run() {

        try {
            selector.select(1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (SelectionKey key : new HashSet<>(selector.selectedKeys())) {

            if(key.isAcceptable()) {
                try {
                    System.out.println("Adding client channel");
                    SocketChannel client = serverSocket.accept();
                    client.configureBlocking(false);
                    client.register(selector, SelectionKey.OP_READ, SelectionKey.OP_WRITE);
                } catch (IOException ioe) {
                    System.out.println("Failed to connect client");
                    continue;
                }
            }

            //accept client connection
            if (key.isReadable()) {

                SocketChannel client = (SocketChannel) key.channel();
                try {
                    boolean isStartOfPacket = checkForSyncBytes(client);
                    if (isStartOfPacket) {
                        byte[] header = this.getHeader(client);
                        int length = this.getMessageLength(header);
                        byte[] message = new byte[length];
                        ByteBuffer buffer = ByteBuffer.wrap(message);
                        client.read(buffer);
                        this.connectionClient.interpretPacket(header, message);
                    }
                } catch (Exception e) {
                    System.out.println("Incoming message aborted");
                }
            }
            selector.selectedKeys().remove(key);
        }
    }


}