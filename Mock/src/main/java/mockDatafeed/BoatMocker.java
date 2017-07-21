package mockDatafeed;

import com.google.common.io.ByteStreams;
import com.google.common.io.CharStreams;
import models.*;
import org.jdom2.JDOMException;
import parsers.xml.CourseXMLParser;
import utilities.PolarTable;


import java.io.*;
import java.net.SocketException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import utility.*;

import static java.lang.Math.abs;
import static parsers.Converter.hexByteArrayToInt;
import static utility.Calculator.calcAngleBetweenPoints;
import static utility.Calculator.convertRadiansToShort;

import static utilities.Utility.fileToString;
import static utility.Calculator.shortToDegrees;

/**
 * Created by khe60 on 24/04/17.
 * Boat mocker
 */
public class BoatMocker extends TimerTask implements ConnectionClient {
    private List<Competitor> competitors;
    private List<Competitor> markBoats;
    private List<CourseFeature> courseFeatures;
    private ZonedDateTime expectedStartTime;
    private ZonedDateTime creationTime;
    private BinaryPackager binaryPackager;
    private TCPServer TCPServer;
    private MutablePoint prestart;
    private WindGenerator windGenerator;
    private int currentSourceID=100;
    private Random random;
    private PolarTable polarTable;

    BoatMocker() throws IOException {
        random=new Random();
        prestart = new MutablePoint(32.296577, -64.854304);
        int connectionTime = 10000;
        competitors = new ArrayList<>();
        TCPServer = new TCPServer(4941, this);
        binaryPackager = new BinaryPackager();
        //establishes the connection with Visualizer
        TCPServer.establishConnection(connectionTime);

        creationTime = ZonedDateTime.now().truncatedTo(ChronoUnit.SECONDS);
        expectedStartTime = creationTime.plusMinutes(1);

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
     * Handle control data coming in from clients
     * @param header byte[] the packet header
     * @param packet byte[] the packet body
     */
    public void interpretPacket(byte[] header, byte[] packet) {
        System.out.println("Interpreting packet");
        for (int i = 0; i < packet.length; i++) {
            System.out.println(packet[i]);
        }
    }


    /**
     * generates wind speed and direction from leeward and windward gates
     */
    private void generateWind() throws IOException {
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
        polarTable = new PolarTable("/polars/VO70_polar.txt", 12.0);
    }

    /**
     * finds the current course of the race
     */
    private void generateCourse() throws JDOMException, IOException {
        InputStream mockBoatStream = new ByteArrayInputStream(ByteStreams.toByteArray(getClass().getResourceAsStream("/raceTemplate.xml")));
        CourseXMLParser cl = new CourseXMLParser(mockBoatStream);
        //screen size is not important
        RaceCourse course = new RaceCourse(cl.parseCourse(), false);
        courseFeatures = course.getPoints();
    }

    /**
     * adds a competitor to the list of competitiors
     * @return the source Id added
     */
    private int addCompetitors(){
        Boat newCompetitor=new Boat("Boat "+currentSourceID, random.nextInt(20)+20, prestart, "B"+currentSourceID, currentSourceID, 1);
        competitors.add(newCompetitor);
        currentSourceID+=1;
        return currentSourceID-1;
    }

    public int addConnection() {
        return this.addCompetitors();
    }

    /**
     * generates the competitors list
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
     */
    private void updatePosition() {

        for (Competitor boat : competitors) {
            short windDirection = windGenerator.getWindDirection();
            double twa = abs(shortToDegrees(windDirection) - boat.getCurrentHeading());
            if(twa > 180) {
                twa = twa - 180;
            }
            double speed = polarTable.getSpeed(twa);
            boat.setVelocity(speed);
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
            TCPServer.sendData(boatinfo);
        }
        //send mark boats
        for (Competitor markBoat : markBoats) {
            byte[] boatinfo = binaryPackager.packageBoatLocation(markBoat.getSourceID(), markBoat.getPosition().getXValue(), markBoat.getPosition().getYValue(),
                    markBoat.getCurrentHeading(), markBoat.getVelocity() * 1000, 3);
            TCPServer.sendData(boatinfo);
        }
    }


    /**
     * Sends Race Status to outputport
     *
     * @throws IOException IOException
     */
    private void sendRaceStatus() throws IOException {
        short windDirection = windGenerator.getWindDirection();
        short windSpeed = windGenerator.getWindSpeed();
        int raceStatus = 3;
        byte[] raceStatusPacket = binaryPackager.raceStatusHeader(raceStatus, expectedStartTime, windDirection, windSpeed);
        byte[] eachBoatPacket = binaryPackager.packageEachBoat(competitors);
        TCPServer.sendData(binaryPackager.packageRaceStatus(raceStatusPacket, eachBoatPacket));
    }


    /**
     * formats the racexml template
     *
     * @param xmlTemplate the template for race xml
     * @return race xml with fields filled
     */
    private String formatRaceXML(String xmlTemplate) {
        DateTimeFormatter raceIDFormat = DateTimeFormatter.ofPattern("yyMMdd");
        StringBuilder participants=new StringBuilder();
        for(Competitor boat:competitors){
            participants.append(String.format("<Yacht SourceID=\"%s\"/>",boat.getSourceID()));
        }
        String raceID = creationTime.format(raceIDFormat) + "01";
        return String.format(xmlTemplate, raceID, creationTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME), expectedStartTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),participants);
    }

    /**
     * Send a race xml file to client, uses raceTemplate.xml to generate custom race xml messages
     */
    private void sendRaceXML() throws IOException {
        int messageType = 6;
        String raceTemplateString = fileToString("/raceTemplate.xml");
        String raceXML = formatRaceXML(raceTemplateString);
        TCPServer.sendData(binaryPackager.packageXML(raceXML.length(), raceXML, messageType));

    }

    /**
     * Send a xml file
     */
    private void sendXML(String xmlPath, int messageType) throws IOException {
        String xmlString = CharStreams.toString(new InputStreamReader(getClass().getResourceAsStream(xmlPath)));
        //        String mockBoatsString = Files.toString(new File(xmlPath), Charsets.UTF_8);
        TCPServer.sendData(binaryPackager.packageXML(xmlString.length(), xmlString, messageType));

    }


    private void sendBoatXML(String xmlPath, int messageType) throws IOException {
        StringBuilder stringBuilder=new StringBuilder();
        String boatTemplate="<Boat Type=\"Yacht\" SourceID=\"%s\" ShapeID=\"15\" StoweName=\"USA\" ShortName=\"%s\" ShorterName=\"USA\"\n" +
                "              BoatName=\"%s\" HullNum=\"AC4515\" Skipper=\"SPITHILL\" Helmsman=\"SPITHILL\" Country=\"USA\"\n" +
                "              PeliID=\"101\" RadioIP=\"172.20.2.101\">\n" +
                "            <GPSposition Z=\"1.78\" Y=\"-0.331\" X=\"-0.006\"/>\n" +
                "            <MastTop Z=\"21.496\" Y=\"3.7\" X=\"0\"/>\n" +
                "            <FlagPosition Z=\"0\" Y=\"6.2\" X=\"0\"/>\n" +
                "        </Boat>";
        for(Competitor boat: competitors){
            stringBuilder.append(String.format(boatTemplate, boat.getSourceID(),boat.getTeamName(),boat.getTeamName()));
        }
        String xmlString = CharStreams.toString(new InputStreamReader(getClass().getResourceAsStream(xmlPath)));
        String boatXML=String.format(xmlString,stringBuilder.toString());
        TCPServer.sendData(binaryPackager.packageXML(boatXML.length(), boatXML, messageType));
    }
    /**
     * Sends all xml files
     */
    private void sendAllXML() {

        try {
            sendBoatXML("/mock_boats.xml", 7);
            sendXML("/mock_regatta.xml", 5);
            sendRaceXML();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    /**
     * updates the boats location
     */
    @Override
    public void run() {


        try {
            TCPServer.receive();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //check if boats are at the end of the leg
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
                b.setCurrentLegIndex(b.getCurrentLegIndex() + 1);
                b.setCurrentHeading(courseFeatures.get(b.getCurrentLegIndex()).getExitHeading());
            }
        }
        //update the position of the boats given the current position, heading and velocity
        updatePosition();
        //send the boat info to receiver

        try {
            sendBoatLocation();
            sendRaceStatus();
        } catch (IOException e) {
            e.printStackTrace();

        }

    }
}
