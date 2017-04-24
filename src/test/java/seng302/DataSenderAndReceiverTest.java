package seng302;

import org.junit.Before;
import org.junit.Test;
import seng302.Model.DataReceiver;
import seng302.Parsers.ByteStreamConverter;
import seng302.TestMockDatafeed.BinaryPackager;
import seng302.TestMockDatafeed.BoatMocker;
import seng302.TestMockDatafeed.DataSender;

import java.io.IOException;
import java.util.Timer;



/**
 * Created by khe60 on 25/04/17.
 */
public class DataSenderAndReceiverTest {
    BoatMocker boatMocker;

    DataReceiver dataReceiver;

    @Before
    public void setUp() throws Exception{
        //set up boat mocker
        boatMocker=new BoatMocker();
        //find out the coordinates of the course
        boatMocker.generateCourse();

        //generate the boats
        boatMocker.generateCompetitors(1);

        //test for local host
        dataReceiver=new DataReceiver("127.0.0.1",4941);
    }

    @Test
    public void sendAndReceiveTest() throws Exception{
        //start the race, updates boat position at a rate of 10 hz
        Timer raceTimer=new Timer();
        raceTimer.schedule(boatMocker,0,1000);

        dataReceiver.main(null);
    }
}
