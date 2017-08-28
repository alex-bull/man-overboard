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
import java.util.Arrays;
import java.util.HashSet;
import java.util.TimerTask;

import static parsers.Converter.hexByteArrayToInt;

/**
 * Created by mattgoodson on 28/08/17. A TCP server
 */
public class Server extends TimerTask {

    private Selector selector;
    private ServerSocketChannel serverSocket;
    private ConnectionClient connectionClient;


    /**
     * Intialize a server instance on the given port
     * @param port int the port to expose the service on
     * @param connectionClient ConnectionClient, a handler for incoming data
     * @throws IOException If the server cannot be opened
     */
    Server(int port, ConnectionClient connectionClient) throws IOException {
        this.connectionClient = connectionClient;
        selector = Selector.open();
        serverSocket = ServerSocketChannel.open();
        serverSocket.bind(new InetSocketAddress("localhost", port));
        serverSocket.configureBlocking(false);
        serverSocket.register(selector, SelectionKey.OP_ACCEPT);
        System.out.println("Server listening on localhost " + port);
    }


    public void sendData(byte[] data) throws IOException {
        this.broadcast(data);
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
                    while(buffer.hasRemaining()) client.write(buffer);
                } catch (IOException e) {
                    System.out.println(client.getRemoteAddress() + " has disconnected, removing client");
                    key.cancel();
                }
            }
            selector.selectedKeys().remove(key);
        }
    }


    /**
     * Send a message to a single client with an identifier
     * @param data byte[] the message
     * @param channel int the id of the client channel to send to
     */
    private void unicast(byte[] data, int channel) {

    }



    /**
     * Reads data from client and puts the message in the queue.
     * @param client SocketChannel the client to read from
     * @throws IOException reading message can fail
     */
    private void processClient(SocketChannel client) throws IOException {
        // -125 is equivalent to 0x83 unsigned
        byte[] expected = {0x47,-125};
        byte[] actual = new byte[2];
        ByteBuffer buffer = ByteBuffer.wrap(actual);
        client.read(buffer);

        if (Arrays.equals(expected, actual)) {
            byte[] header = this.getHeader(client);
            int length = this.getMessageLength(header);
            byte[] message = new byte[length];
            ByteBuffer messageBuffer = ByteBuffer.wrap(message);
            client.read(messageBuffer);
            this.connectionClient.interpretPacket(header, message);
        }

    }


    /**
     * Returns the first 13 bytes from a packet
     * @return byte[] the header byte array
     * @throws IOException IOException
     */
    private byte[] getHeader(SocketChannel client) throws IOException {
//        ByteBuffer header=ByteBuffer.allocate(13);
//        client.read(header);
        byte[] header=new byte[13];
        ByteBuffer buffer  =ByteBuffer.wrap(header);

        client.read(buffer);
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




    public void run() {

//        while (true) {

            try {
                selector.select(1);
                for (SelectionKey key : new HashSet<>(selector.selectedKeys())) {

                    //handle new clients connecting
                    if (key.isAcceptable()) {
                        SocketChannel client = serverSocket.accept();
                        client.configureBlocking(false);
                        client.register(selector, SelectionKey.OP_READ|SelectionKey.OP_WRITE);
                        key.attach("MychannelID"); //assign an id to the key for unicast

                        //TODO:- for now just send back a source id
                        //In the future the source id will be generated and sent upon a req message
                        //THe source id will be attached to the key
                        int sourceID = connectionClient.addConnection();

                        byte[] packet = new BinaryPackager().packageSourceID(sourceID);
                        ByteBuffer buffer = ByteBuffer.wrap(packet);
                        try {
                            while (buffer.hasRemaining()) {
                                client.write(buffer);
                            }
                        } catch (IOException e) {
                            System.out.println("failed to register sourceID " + sourceID);
                            key.cancel();
                        }

                    }

                    //handle incoming messages
                    if (key.isReadable()) {
                        SocketChannel client = (SocketChannel) key.channel();
                        this.processClient(client);
                    }
                    selector.selectedKeys().remove(key);
                }

            } catch(IOException e) {
                e.printStackTrace();
            }
//        }
    }




}
