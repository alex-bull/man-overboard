package mockDatafeed;

import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by khe60 on 10/04/17.
 * The DataSender class, currently sends test_data.bin at a rate of 1 byte per second
 */
class DataSender {

    private OutputStream os;

    /**
     * Constructor for DataSender, creates port at given portnum
     *
     * @param portnum int The port number
     * @throws IOException IOException
     */
    DataSender(int portnum) throws IOException {
        ServerSocket outputSocket = new ServerSocket(portnum);
        Socket socket = outputSocket.accept();
        os = socket.getOutputStream();

    }

    /**
     * sends the data to the output socket
     *
     * @param data byte[] byte array of the data
     */
    void sendData(byte[] data) throws IOException {
        os.write(data);
    }


}
