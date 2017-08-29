package utilities;

import javafx.scene.Scene;
import mockDatafeed.BoatMocker;

import org.junit.Before;
import org.junit.Test;
import parsers.RaceStatusEnum;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

/**
 * Created by jar156 on 11/05/17.
 */
public class InterpreterTest {
    private Interpreter interpreter;
    private Thread mockThread;
    private Scene mockScene;

    private boolean streamStarted = false;




    @Before
    public void setUp() throws Exception {
        interpreter = new Interpreter();
        mockScene=mock(Scene.class);

        mockThread = new Thread(() -> BoatMocker.main(null));
        streamStarted = false;

    }

    @Test
    public void returnsFalseWhenInvalidHost() throws Exception {

        mockThread.start();
        Thread.sleep(200); // give mock time to start before visualiser
        boolean running = interpreter.receive("invalidhost", 4,mockScene);

        Thread visualiserThread = new Thread(() -> assertFalse(running));
        visualiserThread.run();
    }

    @Test
    public void returnsFalseWhenInvalidPort() throws Exception {

        mockThread.start();
        Thread.sleep(200); // give mock time to start before visualiser

        boolean connected = interpreter.receive("localhost", 4,mockScene);
        Thread visualiserThread = new Thread(() -> assertFalse(connected));
        visualiserThread.run();
    }

    @Test(expected = ExceptionInInitializerError.class)
    public void returnsTrueWhenConnectionSuccessful() throws Exception {

        mockThread.start();
        Thread.sleep(200); // give mock time to start before visualiser

        Thread visualiserThread = new Thread(() -> {
            //JFXPanel toolkit = new JFXPanel(); // causes JavaFX toolkit including Application Thread to start, doesn't work on CI runner because no display
            boolean connected = interpreter.receive("localhost", 4941,mockScene);
            assertTrue(connected);
        });
        visualiserThread.run();

    }

    @Test
    public void ignoresPacketWithUnknownMessageType() {
        byte unknownMessageType = 18;
        byte[] header = {unknownMessageType,0,0,0};
        byte[] packet = {0};

        interpreter.interpretPacket(header, packet);
    }

    @Test
    public void ignoresEmptyPacket() {
        byte[] header = {12,0,0,0};
        byte[] packet = {};

        interpreter.interpretPacket(header, packet);
    }

    @Test
    public void ignoresPacketWithMissingInfo() {
        byte[] header = {12,0,0,0};
        byte[] packet = {0,0,0,0,0,0,0,0,0,0,0,0};

        interpreter.interpretPacket(header, packet);
    }

    @Test
    public void ignoresXMLPacketWithMissingInfo() {
        byte[] header = {26,0,0,0};
        byte[] packet = {0,0,0,0,0,0,0,0,0,0,0,0};

        interpreter.interpretPacket(header, packet);
    }

    @Test
    public void ignoresBoatActionPacketWithMissingInfo() {
        byte[] header = {100,0,0,0};
        byte[] packet = new byte[20];

        interpreter.interpretPacket(header, packet);
    }

}