package mockDatafeed;

import utility.ConnectionClient;
import utility.QueueMessage;
import utility.WorkQueue;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Arrays;
import java.util.HashSet;
import java.util.TimerTask;

import static parsers.Converter.hexByteArrayToInt;

/**
 * Created by mattgoodson on 28/08/17. A TCP server
 */
public class TCPServer extends TimerTask {

    private Selector selector;
    private ServerSocketChannel serverSocket;
    private ConnectionClient connectionClient;
    private WorkQueue sendQueue;
    private WorkQueue receiveQueue;

    /**
     * Intialize a server instance on the given port
     *
     * @param port             int the port to expose the service on
     * @param connectionClient ConnectionClient, a handler for incoming data
     * @param sendQueue
     * @param receiveQueue
     * @throws IOException If the server cannot be opened
     */
    TCPServer(int port, ConnectionClient connectionClient, WorkQueue sendQueue, WorkQueue receiveQueue) throws IOException {
        this.sendQueue = sendQueue;
        this.receiveQueue = receiveQueue;
        this.connectionClient = connectionClient;
        selector = Selector.open();
        serverSocket = ServerSocketChannel.open();
        serverSocket.bind(new InetSocketAddress("localhost", port));
        serverSocket.configureBlocking(false);
        serverSocket.register(selector, SelectionKey.OP_ACCEPT);
        System.out.println("TCPServer listening on localhost " + port);
    }


    /**
     * Broadcast a message to all clients
     *
     * @param data byte[] byte array of the data
     * @throws IOException IOException
     */
    private void broadcast(byte[] data) throws IOException {
        selector.select(1);

        for (SelectionKey key : new HashSet<>(selector.selectedKeys())) {
            //write to channel if writable
            if (key.isWritable()) {
                ByteBuffer buffer = ByteBuffer.wrap(data);
                SocketChannel client = (SocketChannel) key.channel();
                try {
                    while (buffer.hasRemaining()) client.write(buffer);
                } catch (IOException ie) {
                    System.out.println(client.getRemoteAddress() + " has disconnected, removing client");
                    key.cancel();
                }
            }
            selector.selectedKeys().remove(key);
        }
    }


    /**
     * Send a message to a single client with an identifier
     *
     * @param data     byte[] the message
     * @param clientId Integer the id of the client channel to send to
     */
    private void unicast(byte[] data, Integer clientId) throws IOException {

        selector.select(1);
        for (SelectionKey key : new HashSet<>(selector.selectedKeys())) {
            //write to the channel if writable
            if (key.attachment() == clientId && key.isWritable()) {
                ByteBuffer buffer = ByteBuffer.wrap(data);
                SocketChannel client = (SocketChannel) key.channel();
                try {
                    while (buffer.hasRemaining()) client.write(buffer);
                } catch (IOException e) {
                    System.out.println(client.getRemoteAddress() + " is unreachable, removing client");
                    key.cancel();
                }
            }
            selector.selectedKeys().remove(key);
        }
    }


    /**
     * Reads data from client and puts the message in the queue.
     *
     * @param client SocketChannel the client to read from
     * @param id     Integer the key id for the channel
     * @throws IOException reading message can fail
     */
    private void processClient(SocketChannel client, Integer id) throws IOException {
        // -125 is equivalent to 0x83 unsigned
        byte[] expected = {0x47, -125};
        byte[] actual = new byte[2];
        ByteBuffer buffer = ByteBuffer.wrap(actual);
        client.read(buffer);

        if (Arrays.equals(expected, actual)) {
            byte[] header = this.getHeader(client);
            int length = this.getMessageLength(header);
            byte[] message = new byte[length];
            ByteBuffer messageBuffer = ByteBuffer.wrap(message);
            client.read(messageBuffer);
            //this.connectionClient.interpretPacket(header, message, id);
            this.receiveQueue.put(id, header, message);
        }

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
     * Send all messages in the send queue
     */
    private void sendQueuedMessages() {

        for (QueueMessage sm : sendQueue.drain()) {
            try {
                if (sm.getClientId() == null) this.broadcast(sm.getMessage());
                else this.unicast(sm.getMessage(), sm.getClientId());
            } catch (IOException e) {
                System.out.println("Failed to send message");
            }
        }
    }


    public void run() {


        //send all messages
        this.sendQueuedMessages();

        //listen for incoming data
        try {
            selector.select(1);
            for (SelectionKey key : new HashSet<>(selector.selectedKeys())) {

                //handle new clients connecting
                if (key.isAcceptable()) {
                    if (!connectionClient.isAccepting()) {
                        key.cancel();
                    } else {

                        SocketChannel client = serverSocket.accept();
                        client.configureBlocking(false);

                        // Assign the connection a unique source id
                        // This will become the boat source id if they register as a player
                        int sourceID = connectionClient.getNextSourceId();
                        client.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE, sourceID);
                        System.out.println("Added new client: " + sourceID);
                    }
                }

                //handle incoming messages
                else if (key.isReadable()) {

                    SocketChannel client = (SocketChannel) key.channel();
                    selector.selectedKeys().remove(key); //remove key so that it can be written to for a response
                    this.processClient(client, (Integer) key.attachment());
                }
                selector.selectedKeys().remove(key);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
