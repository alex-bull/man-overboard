package seng302.TestMockDatafeed;

import com.google.common.io.ByteStreams;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by khe60 on 10/04/17.
 * The DataSender class, currently sends test_data.bin at a rate of 1 byte per second
 */
public class DataSender {

    private Socket socket;
    private OutputStream os;

    /**
     * constructor for DataSender, creates port at given portnum
     * @param portnum
     * @throws IOException
     */
    public DataSender(int portnum) throws IOException {
        ServerSocket welcomeSocket = new ServerSocket(portnum);
        socket=welcomeSocket.accept();
        os=socket.getOutputStream();

    }

    /**
     * close the socket
     * @throws IOException
     */
    private void close() throws IOException {
        socket.close();
    }

    /**
     * sends tests data to the output stream
     * @throws IOException
     * @throws InterruptedException
     */
    private void sendTestData() throws IOException, InterruptedException {
        System.out.println("Sending test_data.bin");

        File file=new File("src/main/resources/test_data.bin");
        InputStream is=new FileInputStream(file);
        //read data from test file
        byte[] content= ByteStreams.toByteArray(is);

        for(int i=0;i<content.length;i++){
            os.write(content[i]);
            Thread.sleep(1000);
        }
    }

    public static void main (String [] args) throws IOException, InterruptedException {
        DataSender me=new DataSender(4941);
        me.sendTestData();
        me.close();

    }

}
