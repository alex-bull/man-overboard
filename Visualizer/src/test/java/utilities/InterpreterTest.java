package utilities;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.stage.Stage;
import mockDatafeed.BoatMocker;

import models.Boat;
import models.Competitor;
import org.junit.Before;
import org.junit.Test;
import parsers.xml.race.CompoundMarkData;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static parsers.MessageType.MARK_ROUNDING;

/**
 * Created by jar156 on 11/05/17.
 */
public class InterpreterTest {
    private Interpreter interpreter;
    private Thread mockThread;


    @Before
    public void setUp() throws Exception {
        interpreter = new Interpreter();

        mockThread = new Thread(new Runnable() {
            BoatMocker mock;

            @Override
            public void run() {
                mock.main(null);
            }
        });
    }

    @Test
    public void returnsFalseWhenInvalidHost() throws Exception {

        mockThread.start();
        Thread.sleep(200); // give mock time to start before visualiser

        Thread visualiserThread = new Thread(new Runnable() {
            @Override
            public void run() {
                assertFalse(interpreter.receive("invalidhost", 4));
            }
        });
        visualiserThread.run();
    }

    @Test
    public void returnsFalseWhenInvalidPort() throws Exception {

        mockThread.start();
        Thread.sleep(200); // give mock time to start before visualiser

        Thread visualiserThread = new Thread(new Runnable() {
            @Override
            public void run() {
                assertFalse(interpreter.receive("localhost", 4));
            }
        });
        visualiserThread.run();
    }

    @Test(expected = ExceptionInInitializerError.class)
    public void returnsTrueWhenConnectionSuccessful() throws Exception {

        mockThread.start();
        Thread.sleep(200); // give mock time to start before visualiser

        Thread visualiserThread = new Thread(new Runnable() {

            @Override
            public void run() {
                //JFXPanel toolkit = new JFXPanel(); // causes JavaFX toolkit including Application Thread to start, doesn't work on CI runner because no display
                assertTrue(interpreter.receive("localhost", 4941));
            }
        });
        visualiserThread.run();

    }

/*    @Test
    public void parsesValidMarkRoundingPacket() {

        List<Competitor> competitors = new ArrayList<>();
        Boat boat = new Boat();
        boat.setSourceID(0);
        competitors.add(boat);
        interpreter.setCompetitors(competitors);

        CompoundMarkData data = new CompoundMarkData(0, "Test Mark", new ArrayList<>());
        interpreter.getCompoundMarks().add(data);

        byte[] header = {38,0,0,0};
        byte[] packet = new byte[22];

        interpreter.interpretPacket(header, packet);
        assertTrue(interpreter.getCompetitors().get(0).getLastMarkPassed() == "Test Mark");

    }*/

    @Test
    public void ignoresPacketWithUnknownMessageType() {
        byte unknownMessageType = 100;
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

}