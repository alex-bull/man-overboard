package seng302.TestMockDatafeed;

import com.google.common.io.ByteStreams;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by khe60 on 10/04/17.
 * The DataSender class, currently sends test_data.bin at a rate of 1 byte per second
 */
public class DataSender {

    private Socket socket;
    private OutputStream os;

    /**
     * Constructor for DataSender, creates port at given portnum
     *
     * @param portnum int The port number
     * @throws IOException
     */
    public DataSender(int portnum) throws IOException {
        ServerSocket outputSocket = new ServerSocket(portnum);
        socket = outputSocket.accept();
        os = socket.getOutputStream();

    }

    /**
     * close the socket
     *
     * @throws IOException
     */
    private void close() throws IOException {
        socket.close();
    }

    /**
     * sends tests data to the output stream
     *
     * @throws IOException
     * @throws InterruptedException
     */
    private void sendTestData() throws IOException, InterruptedException {
        File file = new File("src/main/resources/test_data.bin");
        InputStream is = new FileInputStream(file);
        //read data from test file
        byte[] content = ByteStreams.toByteArray(is);

        for (int i = 0; i < content.length; i++) {
            os.write(content[i]);
            Thread.sleep(1000);
        }
    }

    /**
     * sends the data to the output socket
     *
     * @param data
     */
    public void sendData(byte[] data) throws IOException {
        os.write(data);
    }


}
