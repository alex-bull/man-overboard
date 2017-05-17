package utilities;

import mockDatafeed.BoatMocker;
import org.junit.Before;
import org.junit.Test;

import java.io.DataInputStream;
import java.util.Timer;

import static org.junit.Assert.*;

/**
 * Created by jar156 on 11/05/17.
 */
public class DataReceiverTest {
    DataReceiver dataReceiver;

    @Test
    public void connectsToMockServer() throws Exception {

        Interpreter interpreter = new Interpreter();

        Thread mockThread = new Thread(new Runnable() {
            BoatMocker mock;

            @Override
            public void run() {
                mock.main(null);
            }
        });

        mockThread.start();
        Thread.sleep(200);

        String host = "localhost";
        int port = 4941;

        dataReceiver = new DataReceiver(host, port, interpreter);

    }

}