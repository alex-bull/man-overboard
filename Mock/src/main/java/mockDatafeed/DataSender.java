package mockDatafeed;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.HashSet;

/**
 * Created by khe60 on 10/04/17.
 * The DataSender class
 */
class DataSender {

    private Selector selector;
    private ServerSocketChannel serverSocket;

    /**
     * Constructor for DataSender, creates port at given port
     *
     * @param port int The port number
     * @throws IOException IOException
     */
    DataSender(int port) throws IOException {
        selector = Selector.open();
        serverSocket = ServerSocketChannel.open();
        serverSocket.configureBlocking(false);
        serverSocket.socket().bind(new InetSocketAddress("0.0.0.0", port));
        serverSocket.register(selector, SelectionKey.OP_ACCEPT);

    }


    /**
     * The establishment period of the datasender, runs for time milliseconds
     *
     * @param time the amount of time in milliseconds of the connection establishment period
     */
    void establishConnection(long time) throws IOException {
        System.out.println("start client connection");
        long finishTime = System.currentTimeMillis() + time;
        while (System.currentTimeMillis() < finishTime) {
            selector.select(time);
            for (Object key : new HashSet<>(selector.selectedKeys())) {
                SelectionKey selectionKey = (SelectionKey) key;
                //accept client connection
                if (selectionKey.isAcceptable()) {
                    SocketChannel client = serverSocket.accept();
                    client.configureBlocking(false);
                    client.register(selector, SelectionKey.OP_WRITE);
                }
                selector.selectedKeys().remove(key);
            }
        }
        serverSocket.close();
        System.out.println("finish client connection");
    }

    /**
     * sends the data to the output socket
     *
     * @param data byte[] byte array of the data
     */
    void sendData(byte[] data) throws IOException {

        selector.select(1);

        for (SelectionKey key : new HashSet<>(selector.selectedKeys())) {

            //write to channel if writable
            if (key.isWritable()) {
                ByteBuffer buffer = ByteBuffer.wrap(data);
                SocketChannel client = (SocketChannel) key.channel();
                try {
                    client.write(buffer);
                } catch (IOException e) {
                    System.out.println(client.getRemoteAddress() + " has disconnected, removing client");
                    ((SelectionKey) key).cancel();
                }

            }
            selector.selectedKeys().remove(key);
        }

    }
}



