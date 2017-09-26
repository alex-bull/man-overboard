package mockDatafeed;

import models.Competitor;
import org.junit.BeforeClass;
import org.junit.Test;

import java.lang.reflect.Method;

import static mockDatafeed.Keys.TACK;
import static mockDatafeed.Keys.UP;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static parsers.MessageType.BOAT_ACTION;
import static utilities.Utility.fileToString;

/**
 * Created by khe60 on 30/05/17.
 */
public class BoatMockerTest {
    private static BoatMocker boatMocker;
    private static Class<?> mockerClass;

    @BeforeClass
    public static void setUp() throws Exception {
        boatMocker = new BoatMocker();
        mockerClass = boatMocker.getClass();
    }


    @Test
    public void sendRaceXMLTest() throws Exception {
        String raceTemplateString = fileToString("/bermuda.xml");
        Method sendRaceXML = mockerClass.getDeclaredMethod("formatRaceXML", String.class);
        sendRaceXML.setAccessible(true);
        String resultString = (String) sendRaceXML.invoke(boatMocker, raceTemplateString);
        System.out.println(resultString);
    }

    @Test
    public void headingChangesWhenUpKeyPressed() throws Exception {
        byte[] header = new byte[15];
        byte[] packet = new byte[15];

        byte messageType = (byte) BOAT_ACTION.getValue();
        byte action = (byte) UP.getValue();
        byte sourceID = 1;

        header[0] = messageType;
        header[7] = sourceID;
        packet[0] = action;

        boatMocker.addConnection(); // generate competitors

        double initialHeading = 0;
        for (Competitor competitor : boatMocker.getCompetitors()) {
            if (competitor.getSourceID() == sourceID) {
                initialHeading = competitor.getCurrentHeading();
            }
        }

        boatMocker.interpretPacket(header, packet, 1);

        for (Competitor competitor : boatMocker.getCompetitors()) {
            if (competitor.getSourceID() == sourceID) {
                assertNotEquals(initialHeading, competitor.getCurrentHeading());
            }
        }
    }


    @Test
    public void headingChangesWhenEnterKeyPressed() throws Exception {
        byte[] header = new byte[15];
        byte[] packet = new byte[15];

        byte messageType = (byte) BOAT_ACTION.getValue();
        byte action = (byte) TACK.getValue();
        byte sourceID = 1;

        header[0] = messageType;
        header[7] = sourceID;
        packet[0] = action;

        boatMocker.addConnection(); // generate competitors


        double expectedHeading = 0;
        for (Competitor competitor : boatMocker.getCompetitors()) {
            if (competitor.getSourceID() == sourceID) {
                double windAngle = boatMocker.getWindDirection();
                expectedHeading = windAngle - (competitor.getCurrentHeading() - windAngle);
                expectedHeading = (expectedHeading % 360 + 360) % 360;
            }
        }

        boatMocker.interpretPacket(header, packet, 1);

        for (Competitor competitor : boatMocker.getCompetitors()) {
            if (competitor.getSourceID() == sourceID) {
                assertEquals(expectedHeading, competitor.getCurrentHeading(), 1);
            }
        }
    }


}