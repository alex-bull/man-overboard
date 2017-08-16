package mockDatafeed;

import com.google.common.io.ByteStreams;
import com.google.common.io.CharStreams;
import javafx.scene.shape.Line;
import models.*;
import org.jdom2.JDOMException;
import parsers.BoatStatusEnum;
import parsers.MessageType;
import parsers.header.HeaderData;
import parsers.header.HeaderParser;
import parsers.xml.CourseXMLParser;
import utilities.CollisionUtility;
import parsers.xml.race.CompoundMarkData;
import parsers.xml.race.MarkData;
import parsers.xml.race.RaceData;
import parsers.xml.race.RaceXMLParser;
import utilities.PolarTable;


import java.io.*;
import java.net.SocketException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import utility.*;

import static java.lang.Math.abs;
import static mockDatafeed.Keys.RIP;
import static mockDatafeed.Keys.SAILS;
import static parsers.BoatStatusEnum.DSQ;
import static parsers.BoatStatusEnum.PRESTART;
import static parsers.BoatStatusEnum.UNDEFINED;
import static parsers.MessageType.UNKNOWN;
import static utility.Calculator.calcAngleBetweenPoints;
import static utility.Calculator.convertRadiansToShort;

import static utilities.Utility.fileToString;
import static utility.Calculator.shortToDegrees;

/**
 * Created by khe60 on 24/04/17.
 * Boat mocker
 */
public class BoatMocker extends TimerTask implements ConnectionClient, BoatUpdateEventHandler {
    private HashMap<Integer, Competitor> competitors;
    private Map<Integer, Competitor> markBoats;
    //private List<Competitor> markBoats;
    private List<CourseFeature> courseFeatures;
    private List<MutablePoint> courseBoundary;
    private RaceData raceData;
    private ZonedDateTime expectedStartTime;
    private ZonedDateTime creationTime;
    private BinaryPackager binaryPackager;
    private TCPServer TCPserver;
    private MutablePoint prestart;
    private WindGenerator windGenerator;
    private int currentSourceID=100;
    private Random random;

    private boolean flag=true;
    private Timer timer;
    private BoatUpdater boatUpdater;
    private Course raceCourse = new RaceCourse(null, true);

    private CollisionUtility collisionUtility;
    private List<MutablePoint> courseLineEquations;
    private PolarTable polarTable;

    public BoatMocker() throws IOException , JDOMException {
        timer =new Timer();
        random=new Random();
        collisionUtility = new CollisionUtility();
//        prestart = new MutablePoint(32.286577, -64.864304);
        prestart = new MutablePoint(32.295842, -64.857157);
        int connectionTime = 10000;
        competitors = new HashMap<>();
        TCPserver = new TCPServer(4941,this);
        binaryPackager = new BinaryPackager();

        //establishes the connection with Visualizer
        TCPserver.establishConnection(connectionTime);

        creationTime = ZonedDateTime.now().truncatedTo(ChronoUnit.SECONDS);
        expectedStartTime = creationTime.plusMinutes(1);

        timer.schedule(TCPserver,0,1);

        //find out the coordinates of the course
        generateCourse();
        generateMarkCompetitors();
        generateWind();
        collisionUtility.setCourseInformation();
        courseLineEquations = collisionUtility.getCourseLineEquations();

        boatUpdater = new BoatUpdater(competitors, markBoats, raceData, this, courseBoundary, courseLineEquations, collisionUtility);

        //send all xml data first
        sendAllXML();
        //start the race, updates boat position at a rate of 60 hz
        timer.schedule(this,0,16);
    }

    /**
     * initializes the boats
     *
     * @param args String[]
     */
    public static void main(String[] args) {
        try {
            new BoatMocker();
        } catch (SocketException ignored) {

        } catch (IOException | JDOMException e) {
            e.printStackTrace();
        }
    }

    /**
     * Send packet for yacht event
     * @param sourceId source id of the boat
     * @param eventId event id
     */
    public void yachtEvent(int sourceId, int eventId) {
        try {
            sendYachtEvent(sourceId, eventId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Send packet for health event
     * @param sourceId source id of the boat
     * @param health health of the boat
     */
    public void healthEvent(int sourceId, int health) {
        try {
            this.TCPserver.sendData(binaryPackager.packageHealthEvent(sourceId,  health));
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Send packet for mark rounding
     * @param sourceId source id of the boat
     * @param compoundMarkId id of the mark
     */
    public void markRoundingEvent(int sourceId, int compoundMarkId) {
        try {
            this.TCPserver.sendData(binaryPackager.packageMarkRounding(sourceId, (byte)1, compoundMarkId));
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Sends the boat action data to the Visualiser
     * @param action action of the boat
     */
    private void sendBoatAction(int action, int sourceId) {
        try{
            this.TCPserver.sendData(binaryPackager.packageBoatAction(action, sourceId));
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Handle data coming in from controllers
     * @param header byte[] the packet header
     * @param packet byte[] the packet body
     */
    public void interpretPacket(byte[] header, byte[] packet) {
        MessageType messageType = UNKNOWN;
        for (MessageType messageEnum : MessageType.values()) {
            if (header[0] == messageEnum.getValue()) {
                messageType = messageEnum;
            }
        }

        switch(messageType) {
            case BOAT_ACTION:
                HeaderParser headerParser = new HeaderParser();
                HeaderData headerData = headerParser.processMessage(header);
                int sourceID = headerData.getSourceID();
                Keys action = Keys.getKeys(packet[0]);
                switch(action){
                    case SAILS:
                        sendBoatAction(SAILS.getValue(), sourceID);
                        competitors.get(sourceID).switchSails();
                        break;
                    case UP:
                        competitors.get(sourceID).changeHeading(true, shortToDegrees(windGenerator.getWindDirection()));
                        break;
                    case DOWN:
                        competitors.get(sourceID).changeHeading(false, shortToDegrees(windGenerator.getWindDirection()));
                        break;
                    case RIP:
                        competitors.get(sourceID).setStatus(DSQ);
                        sendBoatAction(RIP.getValue(), sourceID);
                        break;
                    case TACK:
                        double windAngle = shortToDegrees(windGenerator.getWindDirection());
                        Competitor boat = competitors.get(sourceID);
                        boat.setCurrentHeading(windAngle - (boat.getCurrentHeading() - windAngle));
                        break;
                }
                break;
        }
    }

    /**
     * Get the wind direction in degrees
     * @return double - wind direction in degrees
     */
    public double getWindDirection() {
        return shortToDegrees(windGenerator.getWindDirection());
    }


    /**
     * generates wind speed and direction from leeward and windward gates
     */
    private void generateWind() throws IOException {
        int windSpeed = 4600; //default wind speed
        int windDirection = 8192; // default wind direction
        List<Competitor> leewardGates = new ArrayList<>();
        List<Competitor> windwardGates = new ArrayList<>();

        for(Competitor mark: markBoats.values()) {
            if(mark.getTeamName().contains("Lee")) {
                leewardGates.add(mark);
            }
            else if(mark.getTeamName().contains("Wind")) {
                windwardGates.add(mark);
            }
        }

        if(leewardGates.size() == 2 && windwardGates.size() == 2) {
            double leewardX = (leewardGates.get(0).getPosition().getXValue() + leewardGates.get(1).getPosition().getXValue()) / 2;
            double leewardY = (leewardGates.get(0).getPosition().getYValue() + leewardGates.get(1).getPosition().getYValue()) / 2;
            double windwardX = (windwardGates.get(0).getPosition().getXValue() + windwardGates.get(1).getPosition().getXValue()) / 2;
            double windwardY = (windwardGates.get(0).getPosition().getYValue() + windwardGates.get(1).getPosition().getYValue()) / 2;
            double angle = calcAngleBetweenPoints(leewardX, leewardY, windwardX, windwardY) + Math.PI;

            if(angle > 2 * Math.PI) {
                angle = angle - 2 * Math.PI; // wind direction cannot be more than 360
            }
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
        courseBoundary = cl.parseCourseBoundary();
        collisionUtility.setCourseBoundary(courseBoundary);
    }

    /**
     * adds a competitor to the list of competitors
     * @return the source Id added
     */
    private int addCompetitors(){


        double a = 0.001 * competitors.size();
//        prestart = new MutablePoint(32.286577 + a, -64.864304);
        prestart = new MutablePoint(32.41011 + a, -64.88937);

        Boat newCompetitor=new Boat("Boat "+currentSourceID, random.nextInt(20)+20, prestart, "B"+currentSourceID, currentSourceID, PRESTART);
        newCompetitor.setCurrentHeading(0);
        competitors.put(currentSourceID, newCompetitor);
        currentSourceID+=1;
        return currentSourceID-1;
    }

    public List<Competitor> getCompetitors() {
        List<Competitor> boats = new ArrayList<>();
        for (Competitor competitor : competitors.values()) {
            boats.add(competitor);
        }
        return boats;
    }

    public int addConnection() {
        return this.addCompetitors();
    }

    /**
     * generates the competitors list from the XML race file
     */
    private void generateMarkCompetitors() throws IOException, JDOMException {

        String xml = CharStreams.toString(new InputStreamReader(new ByteArrayInputStream(ByteStreams.toByteArray(getClass().getResourceAsStream("/raceTemplate.xml")))));
        raceData = new RaceXMLParser().parseRaceData(xml);
        markBoats = new HashMap<>();

        List<CompoundMarkData> course = raceData.getCourse();

        for (CompoundMarkData compoundMark: course) {
            for (MarkData mark : compoundMark.getMarks()) {
                MutablePoint location = new MutablePoint(mark.getTargetLat(), mark.getTargetLon());
                markBoats.put(mark.getSourceID(), new Boat(mark.getName(), 0, location, "", mark.getSourceID(), UNDEFINED));
            }
        }
    }



    /**
     * generate and send a yacht event to all the clients
     * @param sourceID the boat which the event occured on
     * @param eventID the event happend
     */
    private void sendYachtEvent(int sourceID, int eventID) throws IOException, InterruptedException {
        byte[] eventPacket = binaryPackager.packageYachtEvent(sourceID, eventID);
        TCPserver.sendData(eventPacket);

        //wait for it to be send
        Thread.sleep(20);
    }


    /**
     * Sends boat info to port, including mark boats
     */
    private void sendBoatLocation() throws IOException {
        //send competitor boats
        for (Integer sourceId : competitors.keySet()) {

            Competitor boat = competitors.get(sourceId);
            byte[] boatinfo = binaryPackager.packageBoatLocation(boat.getSourceID(), boat.getPosition().getXValue(), boat.getPosition().getYValue(),
                    boat.getCurrentHeading(), boat.getVelocity() * 1000, 1);

            TCPserver.sendData(boatinfo);

        }
        //send mark boats
        if(flag) {
            for (Competitor markBoat : markBoats.values()) {
                byte[] boatinfo = binaryPackager.packageBoatLocation(markBoat.getSourceID(), markBoat.getPosition().getXValue(), markBoat.getPosition().getYValue(),
                        markBoat.getCurrentHeading(), markBoat.getVelocity() * 1000, 3);
                TCPserver.sendData(boatinfo);
            }
            flag=false;
        }
    }


    /**
     * Sends Race Status to output port
     *
     * @throws IOException IOException
     */
    private void sendRaceStatus() throws IOException {
        short windDirection = windGenerator.getWindDirection();
        short windSpeed = windGenerator.getWindSpeed();
        int raceStatus = 3;
        byte[] raceStatusPacket = binaryPackager.raceStatusHeader(raceStatus, expectedStartTime, windDirection, windSpeed,competitors.size());
        byte[] eachBoatPacket = binaryPackager.packageEachBoat(competitors);
        TCPserver.sendData(binaryPackager.packageRaceStatus(raceStatusPacket, eachBoatPacket));
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
        for(Integer sourceId:competitors.keySet()){
            Competitor boat = competitors.get(sourceId);
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
        TCPserver.sendData(binaryPackager.packageXML(raceXML.length(), raceXML, messageType));

    }

    /**
     * Send a xml file
     */
    private void sendXML(String xmlPath, int messageType) throws IOException {
        String xmlString = CharStreams.toString(new InputStreamReader(getClass().getResourceAsStream(xmlPath)));
        //        String mockBoatsString = Files.toString(new File(xmlPath), Charsets.UTF_8);
        TCPserver.sendData(binaryPackager.packageXML(xmlString.length(), xmlString, messageType));

    }

    /**
     * sends boat xml
     * @param xmlPath
     * @param messageType
     * @throws IOException
     */
    private void sendBoatXML(String xmlPath, int messageType) throws IOException {
        StringBuilder stringBuilder=new StringBuilder();
        String boatTemplate="<Boat Type=\"Yacht\" SourceID=\"%s\" ShapeID=\"15\" StoweName=\"USA\" ShortName=\"%s\" ShorterName=\"USA\"\n" +
                "              BoatName=\"%s\" HullNum=\"AC4515\" Skipper=\"SPITHILL\" Helmsman=\"SPITHILL\" Country=\"USA\"\n" +
                "              PeliID=\"101\" RadioIP=\"172.20.2.101\">\n" +
                "            <GPSposition Z=\"1.78\" Y=\"-0.331\" X=\"-0.006\"/>\n" +
                "            <MastTop Z=\"21.496\" Y=\"3.7\" X=\"0\"/>\n" +
                "            <FlagPosition Z=\"0\" Y=\"6.2\" X=\"0\"/>\n" +
                "        </Boat>";
        for(Integer sourceId: competitors.keySet()){
            Competitor boat = competitors.get(sourceId);
            stringBuilder.append(String.format(boatTemplate, boat.getSourceID(),boat.getTeamName(),boat.getTeamName()));
        }
        String xmlString = CharStreams.toString(new InputStreamReader(getClass().getResourceAsStream(xmlPath)));
        String boatXML=String.format(xmlString,stringBuilder.toString());
        TCPserver.sendData(binaryPackager.packageXML(boatXML.length(), boatXML, messageType));
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

        //send the boat info to receiver

        try {
            boatUpdater.updatePosition(windGenerator);
            sendBoatLocation();
            sendRaceStatus();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
