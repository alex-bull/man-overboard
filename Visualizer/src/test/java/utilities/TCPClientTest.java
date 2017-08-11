package utilities;

import mockDatafeed.BoatMocker;
import org.junit.Test;

/**
 * Created by jar156 on 11/05/17.
 */
public class TCPClientTest {
    TCPClient TCPClient;

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

        TCPClient = new TCPClient(host, port, interpreter);

    }

}