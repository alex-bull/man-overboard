package mockDatafeed;

import com.google.common.io.ByteStreams;
import com.google.common.io.CharStreams;
import models.*;
import org.jdom2.JDOMException;
import parsers.xml.CourseXMLParser;

import java.io.*;
import java.net.SocketException;
import java.time.ZonedDateTime;
import java.util.*;

import static utility.Calculator.calcAngleBetweenPoints;
import static utility.Calculator.convertRadiansToShort;

/**
 * Created by khe60 on 24/04/17.
 * Boat mocker
 */
public class BoatMocker extends TimerTask {
    private List<Competitor> competitors;
    private List<Competitor> markBoats;
    private List<CourseFeature> courseFeatures;
    private int raceStatus;
    private ZonedDateTime expectedStartTime;
    private BinaryPackager binaryPackager;
    private DataSender dataSender;
    private MutablePoint prestart;
    private WindGenerator windGenerator;

    private BoatMocker() throws IOException {
        binaryPackager = new BinaryPackager();
        dataSender = new DataSender(4941);
        prestart = new MutablePoint(32.296577, -64.854304);
        raceStatus = 3;
        expectedStartTime = ZonedDateTime.now();
    }

    /**
     * initializes the boats
     *
     * @param args String[]
     */
    public static void main(String[] args) {
        BoatMocker me;
        try {
            me = new BoatMocker();
            //find out the coordinates of the course
            me.generateCourse();
            me.generateCompetitors();
            me.generateWind();

            //send all xml data first
            me.sendAllXML();
            //start the race, updates boat position at a rate of 10 hz
            Timer raceTimer = new Timer();
            raceTimer.schedule(me, 0, 100);
        } catch (SocketException ignored) {

        } catch (IOException | JDOMException e) {
            e.printStackTrace();

        }


    }

    /**
     * generates wind speed and direction from leeward and windward gates
     */
    private void generateWind() {
        int windSpeed = 4600; //default wind speed
        int windDirection = 8192; // default wind direction
        List<Competitor> leewardGates = new ArrayList<>();
        List<Competitor> windwardGates = new ArrayList<>();
        
        for(Competitor mark: markBoats) {
            if(mark.getAbbreName().contains("LG")) {
                leewardGates.add(mark);
            }
            else if(mark.getAbbreName().contains("WG")) {
                windwardGates.add(mark);
            }
        }

        if(leewardGates.size() == 2 && windwardGates.size() == 2) {
            double leewardX = (leewardGates.get(0).getPosition().getXValue() + leewardGates.get(1).getPosition().getXValue()) / 2;
            double leewardY =  (leewardGates.get(0).getPosition().getYValue() + leewardGates.get(1).getPosition().getYValue()) / 2;
            double windwardX = (windwardGates.get(0).getPosition().getXValue() + windwardGates.get(1).getPosition().getXValue()) / 2;
            double windwardY = (windwardGates.get(0).getPosition().getYValue() + windwardGates.get(1).getPosition().getYValue()) / 2;
            double angle = calcAngleBetweenPoints(leewardX, leewardY, windwardX, windwardY);
            windDirection = convertRadiansToShort(angle);
        }
        windGenerator = new WindGenerator(windSpeed, windDirection);
    }

    /**
     * finds the current course of the race
     */
    private void generateCourse() throws JDOMException, IOException {
        InputStream mockBoatStream= new ByteArrayInputStream(ByteStreams.toByteArray(getClass().getResourceAsStream("/mock_race.xml")));
        CourseXMLParser cl = new CourseXMLParser(mockBoatStream);
        //screen size is not important
        RaceCourse course = new RaceCourse(cl.parseCourse(), false);
        courseFeatures = course.getPoints();
    }

    /**
     * generates the competitors list given numBoats
     *
     */
    private void generateCompetitors() {
        competitors = new ArrayList<>();
        //generate all boats
        competitors.add(new Boat("Oracle Team USA", 42, prestart, "USA", 101, 1));
        competitors.add(new Boat("Emirates Team New Zealand", 40, prestart, "NZL", 103, 1));
        competitors.add(new Boat("Ben Ainslie Racing", 36, prestart, "GBR", 106, 1));
        competitors.add(new Boat("SoftBank Team Japan", 32, prestart, "JPN", 104, 1));
        competitors.add(new Boat("Team France", 30, prestart, "FRA", 105, 1));
        competitors.add(new Boat("Artemis Racing", 38, prestart, "SWE", 102, 1));

        //generate mark boats
        markBoats = new ArrayList<>();
        markBoats.add(new Boat("Start Line 1", 0, new MutablePoint(32.296577, -64.854304), "SL1", 122, 0));
        markBoats.add(new Boat("Start Line 2", 0, new MutablePoint(32.293771, -64.855242), "SL2", 123, 0));
        markBoats.add(new Boat("Mark1", 0, new MutablePoint(32.293039, -64.843983), "M1", 131, 0));
        markBoats.add(new Boat("Lee Gate 1", 0, new MutablePoint(32.309693, -64.835249), "LG1", 124, 0));
        markBoats.add(new Boat("Lee Gate 2", 0, new MutablePoint(32.308046, -64.831785), "LG2", 125, 0));

        markBoats.add(new Boat("Wind Gate 1", 0, new MutablePoint(32.284680, -64.850045), "WG1", 126, 0));
        markBoats.add(new Boat("Wind Gate 2", 0, new MutablePoint(32.280164, -64.847591), "WG2", 127, 0));

        markBoats.add(new Boat("Finish Line 1", 0, new MutablePoint(32.317379, -64.839291), "FL1", 128, 0));
        markBoats.add(new Boat("Finish Line 2", 0, new MutablePoint(32.317257, -64.836260), "FL2", 129, 0));

        //set initial heading
        for (Competitor b : competitors) {
            b.setCurrentHeading(courseFeatures.get(0).getExitHeading());
        }

        //randomly select competitors
        Collections.shuffle(competitors);
        competitors = competitors.subList(0, 6);
    }

    /**
     * updates the position of all the boats given the boats speed and heading
     *
     */
    private void updatePosition() {

        for (Competitor boat : competitors) {
            boat.updatePosition(0.1);
        }
    }

    /**
     * Sends boat info to port, including mark boats
     */
    private void sendBoatLocation() throws IOException {
        //send competitor boats
        for (Competitor boat : competitors) {
            byte[] boatinfo = binaryPackager.packageBoatLocation(boat.getSourceID(), boat.getPosition().getXValue(), boat.getPosition().getYValue(),
                    boat.getCurrentHeading(), boat.getVelocity() * 1000, 1);
            dataSender.sendData(boatinfo);
        }
        //send mark boats
        for (Competitor markBoat : markBoats) {
            byte[] boatinfo = binaryPackager.packageBoatLocation(markBoat.getSourceID(), markBoat.getPosition().getXValue(), markBoat.getPosition().getYValue(),
                    markBoat.getCurrentHeading(), markBoat.getVelocity() * 1000, 3);
            dataSender.sendData(boatinfo);
        }
    }

    /**
     * Sends Race Status to outputport
     *
     * @throws IOException IOException
     */
    private void sendRaceStatus() throws IOException {
        //TODO: make race status message
        short windDirection = windGenerator.getWindDirection();
        short windSpeed = windGenerator.getWindSpeed();
        byte[] raceStatusPacket = binaryPackager.raceStatusHeader(raceStatus, expectedStartTime, windDirection, windSpeed);
        byte[] eachBoatPacket = binaryPackager.packageEachBoat(competitors);
        dataSender.sendData(binaryPackager.packageRaceStatus(raceStatusPacket, eachBoatPacket));
    }


    /**
     * Send a xml file
     */
    private void sendXml(String xmlPath, int messageType) throws IOException {
        String mockBoatString= CharStreams.toString(new InputStreamReader(getClass().getResourceAsStream(xmlPath)));
        //        String mockBoatsString = Files.toString(new File(xmlPath), Charsets.UTF_8);
        dataSender.sendData(binaryPackager.packageXML(mockBoatString.length(), mockBoatString, messageType));

    }

    /**
     * Sends all xml files
     */
    private void sendAllXML() {

        try {
            sendXml("/mock_boats.xml", 7);
            sendXml("/mock_regatta.xml", 5);
            sendXml("/mock_race.xml", 6);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * sends different courses for testing
     */
    private void sendCourse() {
        int index = competitors.get(0).getCurrentLegIndex();
        if (index % 2 == 1) {
            try {
                sendXml("/mock_race2.xml", 6);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (index % 2 == 0) {
            try {
                sendXml("/mock_race.xml", 6);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * updates the boats location
     */
    @Override
    public void run() {

//        try {
//            BufferedReader br=new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/test_data.bin")));
//
//            String inputLine;
//            while ((inputLine = br.readLine()) != null) {
//                dataSender.sendData(inputLine.getBytes());
//                Thread.sleep(1);
//                System.out.println(inputLine);
//            }
//            br.close();
//
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }

        //check if boats are at the end of the leg
        boolean finished=true;
        for (Competitor b : competitors) {
            //if at the end stop
            if (b.getCurrentLegIndex() == courseFeatures.size() - 1) {
                b.setVelocity(0);
                b.setStatus(3);
                continue;
            }

            //set status to started
            if (b.getCurrentLegIndex() == 0) {
                b.setStatus(1);
            }
            //update direction if they are close enough
            if (b.getPosition().isWithin(courseFeatures.get(b.getCurrentLegIndex() + 1).getGPSPoint())) {
                sendCourse();
                b.setCurrentLegIndex(b.getCurrentLegIndex() + 1);
                b.setCurrentHeading(courseFeatures.get(b.getCurrentLegIndex()).getExitHeading());
            }
            finished = finished&&(b.getVelocity()==0);
        }
        //update the position of the boats given the current position, heading and velocity
        updatePosition();
        //send the boat info to receiver
        if (finished){
            try {
                sendEOF();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            sendBoatLocation();
            sendRaceStatus();
        } catch (IOException e) {
            e.printStackTrace();

        }


    }

    private void sendEOF() throws IOException {
        String EOFString= CharStreams.toString(new InputStreamReader(getClass().getResourceAsStream("/EOF")));
        dataSender.sendData(EOFString.getBytes());
    }
}
