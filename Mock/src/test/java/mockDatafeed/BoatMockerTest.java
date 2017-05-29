package mockDatafeed;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.lang.reflect.Method;

import static org.junit.Assert.*;
import static utilities.Utility.fileToString;

/**
 * Created by khe60 on 30/05/17.
 */
public class BoatMockerTest {
    private BoatMocker boatMocker;
    private Class<?> mockerClass;

    @Before
    public void setUp() throws Exception {

        boatMocker=new BoatMocker();
        mockerClass=boatMocker.getClass();

    }

    @Test
    public void sendRaceXMLTest() throws Exception{
        String raceTemplateString= fileToString("/raceTemplate.xml");
        Method sendRaceXML=mockerClass.getDeclaredMethod("formatRaceXML",String.class);
        sendRaceXML.setAccessible(true);
        String resultString=(String) sendRaceXML.invoke(boatMocker,raceTemplateString);
        System.out.println(resultString);
    }
}