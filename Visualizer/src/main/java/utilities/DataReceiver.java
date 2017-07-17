package utilities;

import javafx.application.Platform;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.channels.UnresolvedAddressException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.TimerTask;

import static parsers.Converter.hexByteArrayToInt;


/**
 * Created by khe60 on 10/04/17.
 * The Receiver class, currently receives messages 1 byte at a time
 * Can't connect to the test port for some reason (internet enabler)
 */
public class DataReceiver extends TimerTask {

//    private SocketChannel client;
    private PacketHandler handler;
    private DataInputStream dis;
    private int sourceID;


    /**
     * Initializes port to receive binary data from
     * @param host String host of the server
     * @param port int number of port of the server
     * @param handler PacketHandler handler for incoming packets
     * @throws IOException IOException
     */
    DataReceiver(String host, int port, PacketHandler handler) throws UnresolvedAddressException, IOException {
        Socket receiveSock = new Socket(host, port);
        this.handler = handler;
        dis = new DataInputStream(receiveSock.getInputStream());
        System.out.println("Start connection to server...");

        //find sourceID
        byte[] sourceIDByte=new byte[4];
        ByteBuffer sourceIDBuffer=ByteBuffer.wrap(sourceIDByte);
        sourceIDBuffer.order(ByteOrder.LITTLE_ENDIAN);
        dis.readFully(sourceIDByte);
        System.out.println(sourceIDBuffer.array());
        this.sourceID=sourceIDBuffer.getInt();
        System.out.println(sourceID);
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
//        DataReceiver dataReceiver = null;
//        while (dataReceiver == null) {
//            try {
//                dataReceiver = new DataReceiver("livedata.americascup.com", 4941);
//                Timer timer = new Timer();
//                timer.schedule(dataReceiver, 0, 100);
//
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//    }


}
