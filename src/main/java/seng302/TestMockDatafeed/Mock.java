package seng302.TestMockDatafeed;

import org.jdom2.JDOMException;

import java.io.IOException;
import java.net.SocketException;
import java.util.Timer;

/**
 * Created by ikj11 on 4/05/17.
 */
public class Mock implements Runnable {


    public void run() {
        BoatMocker me = null;
        try {
            me = new BoatMocker();
            //find out the coordinates of the course
            me.generateCourse();

            //generate the boats
            me.generateCompetitors(6);

            //send all xml data first
            me.sendAllXML();
            //start the race, updates boat position at a rate of 10 hz
            Timer raceTimer = new Timer();
            raceTimer.schedule(me, 0, 100);
        } catch (SocketException e) {

        } catch (IOException e) {
            e.printStackTrace();

        } catch (JDOMException e) {
            e.printStackTrace();
        }
    }
}
