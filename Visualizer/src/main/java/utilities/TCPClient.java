package utilities;

import utility.PacketHandler;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.nio.channels.UnresolvedAddressException;
import java.util.Arrays;
import java.util.TimerTask;

import static parsers.Converter.hexByteArrayToInt;


/**
 * Created by khe60 on 10/04/17.
 * The Client class, currently receives messages 1 byte at a time
 * Can't connect to the test port for some reason (internet enabler)
 */
public class TCPClient extends TimerTask {

//    private SocketChannel client;
    private PacketHandler handler;
    private DataInputStream dis;
    private DataOutputStream dos;
    private int sourceID;


    /**
     * Initializes port to receive binary data from
     * @param host String host of the server
     * @param port int number of port of the server
     * @param handler PacketHandler handler for incoming packets
     * @throws IOException IOException
     */
    TCPClient(String host, int port, PacketHandler handler) throws UnresolvedAddressException, IOException {
        Socket receiveSock = new Socket(host, port);
        this.handler = handler;
        dis = new DataInputStream(receiveSock.getInputStream());
        dos = new DataOutputStream(receiveSock.getOutputStream());
        System.out.println("Start connection to server...");

    }

    /**
     * Write data to the socket
     * @param data byte[] The data to send
     * @throws IOException
     */
    public void send(byte[] data) throws IOException {
        System.out.println("Sending message...");
        dos.write(data);
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

//        client.read(ByteBuffer.wrap(actual));
        dis.read(actual);
        return Arrays.equals(actual, expected);
    }

    /**
     * Returns the first 13 bytes from a packet
     * @return byte[] the header byte array
     * @throws IOException IOException
     */
    private byte[] getHeader() throws IOException {
//        ByteBuffer header=ByteBuffer.allocate(13);
//        client.read(header);
        byte[] header=new byte[13];
        dis.readFully(header);
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
    public void run() throws NullPointerException {
        try {
            boolean isStartOfPacket = checkForSyncBytes();
            if (isStartOfPacket) {
                byte[] header = this.getHeader();
                int length = this.getMessageLength(header);
                byte[] message=new byte[length];
                dis.readFully(message);
                this.handler.interpretPacket(header, message);
            }

        }catch (EOFException e){
//            try {
//                Runtime.getRuntime().exec("java -jar Visualizer/target/Visualizer-0.0.jar");
//            } catch (IOException e1) {
//                e1.printStackTrace();
//            }
            System.exit(0);
//            Platform.runLater(()->{
//                primaryStage.fireEvent(new WindowEvent(primaryStage,WindowEvent.WINDOW_CLOSE_REQUEST));
//                Platform.exit();
//
//            });

        }
        catch (IOException e) {
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
//        TCPClient dataReceiver = null;
//        while (dataReceiver == null) {
//            try {
//                dataReceiver = new TCPClient("livedata.americascup.com", 4941);
//                Timer timer = new Timer();
//                timer.schedule(dataReceiver, 0, 100);
//
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//    }


}
