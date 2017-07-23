package mockDatafeed;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by khe60 on 2/06/17.
 */
public class TCPServerTest {
    private TCPServer TCPServer;

    @Before
    public void setUp() throws Exception{

        TCPServer =new TCPServer(4941, Mockito.mock(BoatMocker.class));
        Thread dataSenderThread = new Thread(() -> {
            try {
                TCPServer.establishConnection(1000);

            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        dataSenderThread.start();
    }

    /**
     * test for connection establishment period, the server should connect to as many client in establishment time, then
     * it should refuse any other connection past that time
     * @throws Exception
     */
    @Test(expected = ConnectException.class)
    public void establishConnection() throws Exception {


        SocketChannel connectableclient = SocketChannel.open(new InetSocketAddress("localhost",4941));
        assertTrue(connectableclient.isConnected());

        Thread.sleep(2000);
        //client should not be abel to connect to server
        SocketChannel unconnectableclient = SocketChannel.open(new InetSocketAddress("localhost",4941));
        assertFalse(unconnectableclient.isConnected());
    }

    /**
     * test that the data sender is able to send data to multiple clients
     * @throws Exception
     */
    @Test
    public void sendData() throws Exception {


        byte[] data={0,1,2,3,4,5,6,7,8,9};
        List<SocketChannel> clients=new ArrayList<>();
        //add 3 clients
        for(int i=0; i<3;i++) {
            clients.add(SocketChannel.open(new InetSocketAddress("localhost", 4941)));
        }

        //wait for connections to establish
        Thread.sleep(1000);
        TCPServer.sendData(data);

        for(SocketChannel client:clients){
            ByteBuffer readBuffer=ByteBuffer.allocate(10);
            client.read(readBuffer);
            for(int i=0;i<readBuffer.array().length;i++){
                assertEquals(data[i],readBuffer.array()[i]);
            }

        }

    }

}