package mockDatafeed;

import com.google.common.collect.Iterables;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.*;

/**
 * Created by khe60 on 10/04/17.
 * The DataSender class, currently sends test_data.bin at a rate of 1 byte per second
 */
class DataSender {

    private OutputStream os;
    private Selector selector;
    ServerSocketChannel serverSocket;
    /**
     * Constructor for DataSender, creates port at given portnum
     *
     * @param portnum int The port number
     * @throws IOException IOException
     */
    DataSender(int portnum) throws IOException {
//        ServerSocket outputSocket = new ServerSocket(portnum);
//        Socket socket = outputSocket.accept();
//        os = socket.getOutputStream();
        selector= Selector.open();
        serverSocket=ServerSocketChannel.open();
        serverSocket.configureBlocking(false);
        serverSocket.socket().bind(new InetSocketAddress("localhost",portnum));
        serverSocket.register(selector, SelectionKey.OP_ACCEPT);
    }


    /**
     * The establishment period of the datasender, runs for time milliseconds
     * @param time the amount of time in milliseconds of the connection establishment period
     */
    public void establishConnection(long time) throws IOException {
        System.out.println("start client connection");
        long finishTime=System.currentTimeMillis()+time;
        while(System.currentTimeMillis()<finishTime){
            selector.select(time);
            for (Object key : new HashSet<>(selector.selectedKeys())) {
                SelectionKey selectionKey = (SelectionKey) key;
                //accept client connection
                if (selectionKey.isAcceptable()) {
                    SocketChannel client  = serverSocket.accept();
                    client.configureBlocking(false);
                    client.register(selector, SelectionKey.OP_WRITE);
                    System.out.println(client.getRemoteAddress());
                }
                selector.selectedKeys().remove(key);
            }
        }
        System.out.println("finish client connection");
    }

    /**
     * sends the data to the output socket
     *
     * @param data byte[] byte array of the data
     */
    public void sendData(byte[] data) throws IOException {

        selector.select(1);
        for (Object key : new HashSet<>(selector.selectedKeys())) {
            SelectionKey selectionKey = (SelectionKey) key;
            //write to channel if writable
            if (selectionKey.isWritable()) {
                ByteBuffer buffer=ByteBuffer.wrap(data);
                SocketChannel client  = (SocketChannel) selectionKey.channel();
                try {
                    client.write(buffer);
                }
                catch (IOException e){
                    System.out.println(client.getRemoteAddress()+" has disconnected, removing client");
                    ((SelectionKey) key).cancel();
                }

            }
            selector.selectedKeys().remove(key);
        }

        }
    }



