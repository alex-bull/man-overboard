package mockDatafeed;

import com.google.common.collect.Iterables;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by khe60 on 10/04/17.
 * The DataSender class, currently sends test_data.bin at a rate of 1 byte per second
 */
class DataSender {

    private OutputStream os;
    private Selector selector;
    private List<SocketChannel> outputChannels;
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
        outputChannels=new ArrayList<>();
        selector= Selector.open();
        ServerSocketChannel outputChannel=ServerSocketChannel.open();
        outputChannel.configureBlocking(false);
        outputChannel.socket().bind(new InetSocketAddress("localhost",portnum));
        outputChannel.register(selector, SelectionKey.OP_ACCEPT);
    }


    /**
     * sends the data to the output socket
     *
     * @param data byte[] byte array of the data
     */
    public void sendData(byte[] data) throws IOException {
//        os.write(data);
        System.out.println(outputChannels.size());
        if(selector.select()<1){
            return;
        }

        else{
            System.out.println(selector.select());
            Iterator readySet=selector.selectedKeys().iterator();
            while(readySet.hasNext()){
                SelectionKey key=(SelectionKey) readySet.next();
                if(key.isAcceptable()){
                    ServerSocketChannel ssChannel = (ServerSocketChannel) key.channel();
                    SocketChannel sChannel = (SocketChannel) ssChannel.accept();
                    sChannel.configureBlocking(false);
//                    sChannel.register(key.selector(), SelectionKey.OP_READ);
                    outputChannels.add(sChannel);
                }
            }
        }
    }


}
