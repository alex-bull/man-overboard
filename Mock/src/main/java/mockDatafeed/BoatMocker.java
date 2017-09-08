package mockDatafeed;

import com.google.common.io.ByteStreams;
import com.google.common.io.CharStreams;
import models.*;
import org.jdom2.JDOMException;
import parsers.MessageType;
import parsers.header.HeaderData;
import parsers.header.HeaderParser;
import parsers.powerUp.PowerUp;
import parsers.xml.CourseXMLParser;
import parsers.xml.race.CompoundMarkData;
import parsers.xml.race.MarkData;
import parsers.xml.race.RaceData;
import parsers.xml.race.RaceXMLParser;
import utility.BinaryPackager;
import utility.ConnectionClient;
import utility.QueueMessage;
import utility.WorkQueue;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.SocketException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

import static mockDatafeed.Keys.RIP;
import static mockDatafeed.Keys.SAILS;
import static parsers.BoatStatusEnum.*;
import static parsers.MessageType.UNKNOWN;
import static utilities.CollisionUtility.isPointInPolygon;
import static utilities.Utility.fileToString;
import static utility.Calculator.*;

/**
 * Created by khe60 on 24/04/17.
 * Boat mocker
 */
public class BoatMocker extends TimerTask implements ConnectionClient, BoatUpdateEventHandler {

    private HashMap<Integer, Competitor> competitors = new HashMap<>();
    private Map<Integer, Competitor> markBoats;
    private List<MutablePoint> courseBoundary;
    private RaceData raceData;
    private ZonedDateTime expectedStartTime;
    private ZonedDateTime creationTime;
    private BinaryPackager binaryPackager = new BinaryPackager();
    private MutablePoint prestart = new MutablePoint(32.295842, -64.857157);
    private WindGenerator windGenerator;
    private int currentSourceID = 99;
    private Random random = new Random();

    private boolean flag = true;
    private BoatUpdater boatUpdater;
    private long startTime = System.currentTimeMillis() / 1000;//time in seconds

    private TCPServer TCPserver;
    private boolean raceInProgress = false;
    private Map<Integer, Boolean> clientStates = new HashMap<>();

    private WorkQueue sendQueue = new WorkQueue(1000000);
    private WorkQueue receiveQueue = new WorkQueue(1000000);
    private long previousPotionTime = System.currentTimeMillis();
    private long previousBoostTime = System.currentTimeMillis();
    private int powerUpId = 0;



    BoatMocker() throws IOException, JDOMException {

        creationTime = ZonedDateTime.now().truncatedTo(ChronoUnit.SECONDS);
        expectedStartTime = creationTime.plusMinutes(1);

        //find out the coordinates of the course
        generateCourse();
        generateMarkCompetitors();
        generateWind();
        boatUpdater = new BoatUpdater(competitors, markBoats, raceData, this, courseBoundary, windGenerator);


        //Start the server

        TCPserver = new TCPServer(4942, this, sendQueue, receiveQueue);
        Timer serverTimer = new Timer();
        serverTimer.schedule(TCPserver, 0, 1);


        //start the race, updates boat position at a rate of 60 hz
        Timer timer = new Timer();
        timer.schedule(this, 0, 16);
    }



    /**
     * Handle data coming in from controllers
     *
     * @param header byte[] the packet header
     * @param packet byte[] the packet body
     * @param clientId Integer the id of the client who sent the message
     */
    public void interpretPacket(byte[] header, byte[] packet, Integer clientId) {
        MessageType messageType = UNKNOWN;
        for (MessageType messageEnum : MessageType.values()) {
            if (header[0] == messageEnum.getValue()) {
                messageType = messageEnum;
            }
        }

        switch (messageType) {
            case BOAT_ACTION:
                HeaderParser headerParser = new HeaderParser();
                HeaderData headerData = headerParser.processMessage(header);
                int sourceID = headerData.getSourceID();
                Competitor boat = competitors.get(sourceID);
                Keys action = Keys.getKeys(packet[0]);
                switch (action) {
                    case SAILS:
                        sendBoatAction(SAILS.getValue(), sourceID);
                        boat.switchSails();
                        break;
                    case UP:
                        boat.changeHeading(true, shortToDegrees(windGenerator.getWindDirection()));
                        break;
                    case DOWN:
                        boat.changeHeading(false, shortToDegrees(windGenerator.getWindDirection()));
                        break;
                    case RIP:
                        boat.setStatus(DSQ);
                        sendBoatAction(RIP.getValue(), sourceID);
                        if (!boatUpdater.finisherList.contains(competitors.get(sourceID))){
                            boatUpdater.finisherList.add(competitors.get(sourceID));
                        }
                        break;
                    case TACK:
                        double windAngle = shortToDegrees(windGenerator.getWindDirection());
                        boat.setCurrentHeading(windAngle - (boat.getCurrentHeading() - windAngle));
                        break;
                    case BOOST:
                        boat.activateBoost();
                        break;
                    case POTION:
                        boat.updateHealth((int)  boat.getMaxHealth() / 2);
                        boatStateEvent(sourceID, boat.getHealthLevel());
                        break;
                }
                break;
            case CONNECTION_REQ:
                this.addCompetitor(clientId);
                break;
            case PLAYER_READY:
                this.updateReady(clientId);
                break;
            case LEAVE_LOBBY:
                this.removePlayer(clientId);
        }
    }


    /**
     * Adds a dummy connection for testing
     */
    void addConnection() {
        Boat b = new Boat("Boat", random.nextInt(20) + 20, prestart, "B" , 1, PRESTART);
        b.setCurrentHeading(0);
        this.competitors.put(1, b);
    }


    /**
     * adds a competitor to the list of competitors
     * Sends a response with a source id for the client
     * Stores the client in the clientState map
     * @param clientId the channel id of the client, this is used as the source id of the new competitor
     */
    private void addCompetitor(Integer clientId) {

        double a = 0.005 * competitors.size(); //shift competitors so they aren't colliding at the start
//        prestart = new MutablePoint(32.41011 + a, -64.88937);
        prestart = new MutablePoint(32.350797 + a, -64.799214);

        Boat newCompetitor = new Boat("Boat " + clientId, random.nextInt(20) + 20, prestart, "B" + clientId, clientId, PRESTART);
        newCompetitor.setCurrentHeading(0);
        competitors.put(clientId, newCompetitor);

        byte[] res = binaryPackager.packageConnectionResponse((byte)1, clientId);
        //send connection response and broadcast XML so update lobbies
        sendQueue.put(clientId, res);
        this.clientStates.put(clientId, false);
        this.sendAllXML();
    }


    /**
     * gets the next available source id
     * @return int the source id
     */
    public int getNextSourceId() {
        currentSourceID += 1;
        return currentSourceID;
    }


    /**
     * Returns true if new connections are still being accepted
     * @return boolean
     */
    public boolean isAccepting() {
        return !raceInProgress;
    }


    /**
     * Sets the client to ready if they are a registered player
     * @param clientId Integer the id of the client to mark as ready
     */
    private void updateReady(Integer clientId) {
        if (this.competitors.get(clientId) == null) return; //not a registered player
        clientStates.put(clientId, true);
    }


    /**
     * returns true if the game if the game should start
     * There must be at least one player
     * All players must be ready or the timer must have reached a minute
     */
    private boolean shouldStartGame() {
        if (raceInProgress) return false; //game already in progress
        if (competitors.size() < 1) return false; //no competitors

        //all players are ready or the timer has reached a minute
        return !clientStates.values().contains(false) || ((System.currentTimeMillis() / 1000) - startTime > 60);
    }


    /**
     * Removes the player from the race
     * the server will automatically remove selector key when socket disconnects
     * Send xml to update other clients
     * @param clientId Integer, the client to remove
     */
    private void removePlayer(Integer clientId) {
        clientStates.remove(clientId);
        this.competitors.remove(clientId);
        this.sendAllXML();
    }


    /**
     *get a list of competitors
     * @return List the competitors
     */
    public List<Competitor> getCompetitors() {
        List<Competitor> boats = new ArrayList<>();
        for (Competitor competitor : competitors.values()) {
            boats.add(competitor);
        }
        return boats;
    }


    /**
     * Get the wind direction in degrees
     *
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

        for (Competitor mark : markBoats.values()) {
            if (mark.getTeamName().contains("Lee")) {
                leewardGates.add(mark);
            } else if (mark.getTeamName().contains("Wind")) {
                windwardGates.add(mark);
            }
        }

        if (leewardGates.size() == 2 && windwardGates.size() == 2) {
            double leewardX = (leewardGates.get(0).getPosition().getXValue() + leewardGates.get(1).getPosition().getXValue()) / 2;
            double leewardY = (leewardGates.get(0).getPosition().getYValue() + leewardGates.get(1).getPosition().getYValue()) / 2;
            double windwardX = (windwardGates.get(0).getPosition().getXValue() + windwardGates.get(1).getPosition().getXValue()) / 2;
            double windwardY = (windwardGates.get(0).getPosition().getYValue() + windwardGates.get(1).getPosition().getYValue()) / 2;
            double angle = calcAngleBetweenPoints(leewardX, leewardY, windwardX, windwardY) + Math.PI;

            if (angle > 2 * Math.PI) {
                angle = angle - 2 * Math.PI; // wind direction cannot be more than 360
            }
            windDirection = convertRadiansToShort(angle);
        }
        windGenerator = new WindGenerator(windSpeed, windDirection);
//        polarTable = new PolarTable("/polars/VO70_polar.txt", 12.0);
    }


    /**
     * finds the current course of the race
     */
    private void generateCourse() throws JDOMException, IOException {
        InputStream mockBoatStream = new ByteArrayInputStream(ByteStreams.toByteArray(getClass().getResourceAsStream("/raceTemplate.xml")));
        CourseXMLParser cl = new CourseXMLParser(mockBoatStream);
        //screen size is not important
        RaceCourse course = new RaceCourse(cl.parseCourse(), false);

        courseBoundary = cl.parseCourseBoundary();
//        collisionUtility.setCourseBoundary(courseBoundary);
    }





    /**
     * generates the competitors list from the XML race file
     */
    private void generateMarkCompetitors() throws IOException, JDOMException {

        String xml = CharStreams.toString(new InputStreamReader(new ByteArrayInputStream(ByteStreams.toByteArray(getClass().getResourceAsStream("/raceTemplate.xml")))));
        raceData = new RaceXMLParser().parseRaceData(xml);
        markBoats = new HashMap<>();

        List<CompoundMarkData> course = raceData.getCourse();

        for (CompoundMarkData compoundMark : course) {
            for (MarkData mark : compoundMark.getMarks()) {
                MutablePoint location = new MutablePoint(mark.getTargetLat(), mark.getTargetLon());
                markBoats.put(mark.getSourceID(), new Boat(mark.getName(), 0, location, "", mark.getSourceID(), UNDEFINED));
            }
        }
    }




    /**
     * Send packet for yacht event
     *
     * @param sourceId source id of the boat
     * @param eventId  event id
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
     *
     * @param sourceId Integer source id of the boat
     * @param health   Integer health of the boat
     */
    public void boatStateEvent(Integer sourceId, double health) {

        Competitor boat = competitors.get(sourceId);
        if (health >= boat.getMaxHealth()) {
            health = boat.getMaxHealth();
        }
        else if (health < 0) {
            health = 0; // make sure that negative health is not sent
        }
        // System.out.println("boat health sending " + health);
//            this.TCPserver.broadcast(binaryPackager.packageBoatStateEvent(sourceId, health));
        this.sendQueue.put(null, binaryPackager.packageBoatStateEvent(sourceId, health));
    }

    /**
     * Send packet for mark rounding
     *
     * @param sourceId       source id of the boat
     * @param compoundMarkId id of the mark
     */
    public void markRoundingEvent(int sourceId, int compoundMarkId) {
        //this.TCPserver.broadcast(binaryPackager.packageMarkRounding(sourceId, (byte) 1, compoundMarkId));
        this.sendQueue.put(null, binaryPackager.packageMarkRounding(sourceId, (byte) 1, compoundMarkId));

    }


    /**
     * Sends the boat action data to the Visualiser
     *
     * @param action action of the boat
     */
    private void sendBoatAction(int action, int sourceId) {
//        try {
//            this.TCPserver.broadcast(binaryPackager.packageBoatAction(action, sourceId));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        this.sendQueue.put(null, binaryPackager.packageBoatAction(action, sourceId));
    }


    /**
     * generate and send a yacht event to all the clients
     *
     * @param sourceID the boat which the event occured on
     * @param eventID  the event happend
     */
    private void sendYachtEvent(int sourceID, int eventID) throws IOException, InterruptedException {
        byte[] eventPacket = binaryPackager.packageYachtEvent(sourceID, eventID);
//        TCPserver.broadcast(eventPacket);
        this.sendQueue.put(null, eventPacket);
        //wait for it to be send
//        Thread.sleep(20);
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

            this.sendQueue.put(null, boatinfo);

        }
        //send mark boats only once
        if (flag) {
            for (Competitor markBoat : markBoats.values()) {
                byte[] boatinfo = binaryPackager.packageBoatLocation(markBoat.getSourceID(), markBoat.getPosition().getXValue(), markBoat.getPosition().getYValue(),
                        markBoat.getCurrentHeading(), markBoat.getVelocity() * 1000, 3);
                this.sendQueue.put(null, boatinfo);
            }
            flag = false;
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
        int raceStatus;
        if(boatUpdater.checkAllFinished()){
            raceStatus = 4;
        } else {
            raceStatus = 3;
        }
        byte[] raceStatusPacket = binaryPackager.raceStatusHeader(raceStatus, expectedStartTime, windDirection, windSpeed,competitors.size());
        byte[] eachBoatPacket = binaryPackager.packageEachBoat(competitors);
        this.sendQueue.put(null, binaryPackager.packageRaceStatus(raceStatusPacket, eachBoatPacket));
    }


    /**
     * Sends power up to output port
     */
    public void powerUpEvent() {
        long currentTime = System.currentTimeMillis();

        // potion
        if(currentTime > previousPotionTime + 40000 && raceInProgress) {
            long timeout = currentTime + 70000;
            MutablePoint generatedLocation = getRandomLocation();
            int radius = 10; // we dont use this but other teams do
            int powerType = 3;
            int duration = 20000; // we dont use this but other teams do

            byte[] eventPacket = binaryPackager.packagePowerUp(this.powerUpId, generatedLocation.getXValue(), generatedLocation.getYValue(), (short) radius, powerType, duration, timeout);
            this.sendQueue.put(null, eventPacket);
            PowerUp powerUp = new PowerUp(this.powerUpId, generatedLocation.getXValue(), generatedLocation.getYValue(), radius, timeout, powerType, duration);
            boatUpdater.updatePowerUps(powerUp);
            previousPotionTime = currentTime;
            this.powerUpId++;
        }

        // speed boost
        if(currentTime > previousBoostTime + 30000 && raceInProgress) {
            long timeout = currentTime + 60000;
            MutablePoint generatedLocation = getRandomLocation();
            int radius = 10; // we dont use this but other teams do
            int powerType = 0;
            int duration = 20000; // we dont use this but other teams do

            byte[] eventPacket = binaryPackager.packagePowerUp(this.powerUpId, generatedLocation.getXValue(), generatedLocation.getYValue(), (short) radius, powerType, duration, timeout);
            this.sendQueue.put(null, eventPacket);

            PowerUp powerUp = new PowerUp(this.powerUpId, generatedLocation.getXValue(), generatedLocation.getYValue(), radius, timeout, powerType, duration);
            boatUpdater.updatePowerUps(powerUp);
            previousBoostTime = currentTime;
            this.powerUpId++;
        }

    }

    /**
     * Sends power up taken to output port
     */
    public void powerUpTakenEvent(int boatId, int powerId, int duration) {
        byte[] eventPacket = binaryPackager.packagePowerUpTaken(boatId, powerId, duration);
        this.sendQueue.put(null, eventPacket);
    }

    /**
     * Generate a random point in the course near a random boat
     * @return MutablePoint a mutable point of the location
     */
    private MutablePoint getRandomLocation() {
        Random random = new Random();
        List<Integer> keys = new ArrayList<>(competitors.keySet());
        Integer randomKey = keys.get(random.nextInt(keys.size()));
        Competitor randomBoat = competitors.get(randomKey);

        double randomLat = randomBoat.getPosition().getXValue() + getRandomRadius();
        double randomLon = randomBoat.getPosition().getYValue() + getRandomRadius();

        MutablePoint generatedLocation = new MutablePoint(randomLat, randomLon);

        while (!isPointInPolygon(generatedLocation, courseBoundary)) {
            randomLat = randomBoat.getPosition().getXValue() + getRandomRadius();
            randomLon = randomBoat.getPosition().getYValue() + getRandomRadius();
            generatedLocation=new MutablePoint(randomLat,randomLon);

        }
        return generatedLocation;

    }

    /**
     * Generates a random number for the radius of power up spawning
     * @return double radius
     */
    private double getRandomRadius() {
        return (ThreadLocalRandom.current().nextInt(-10, 10) / 1000.0);
    }




    /**
     * formats the racexml template
     *
     * @param xmlTemplate the template for race xml
     * @return race xml with fields filled
     */
    private String formatRaceXML(String xmlTemplate) {
        DateTimeFormatter raceIDFormat = DateTimeFormatter.ofPattern("yyMMdd");
        StringBuilder participants = new StringBuilder();
        for (Integer sourceId : competitors.keySet()) {
            Competitor boat = competitors.get(sourceId);
            participants.append(String.format("<Yacht SourceID=\"%s\"/>", boat.getSourceID()));
        }
        String raceID = creationTime.format(raceIDFormat) + "01";
        return String.format(xmlTemplate, raceID, creationTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME), expectedStartTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME), participants);
    }


    /**
     * Send a race xml file to client, uses raceTemplate.xml to generate custom race xml messages
     */
    private void sendRaceXML() throws IOException {
        int messageType = 6;
        String raceTemplateString = fileToString("/raceTemplate.xml");
        String raceXML = formatRaceXML(raceTemplateString);
        this.sendQueue.put(null, binaryPackager.packageXML(raceXML.length(), raceXML, messageType));

    }


    /**
     * Send a xml file
     */
    private void sendXML(String xmlPath, int messageType) throws IOException {
        String xmlString = CharStreams.toString(new InputStreamReader(getClass().getResourceAsStream(xmlPath)));
        this.sendQueue.put(null, binaryPackager.packageXML(xmlString.length(), xmlString, messageType));
    }

    /**
     * sends boat xml
     *
     * @param xmlPath     xmlPath
     * @param messageType message type
     * @throws IOException IO exception
     */
    private void sendBoatXML(String xmlPath, int messageType) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        String boatTemplate = "<Boat Type=\"Yacht\" SourceID=\"%s\" ShapeID=\"15\" StoweName=\"USA\" ShortName=\"%s\" ShorterName=\"USA\"\n" +
                "              BoatName=\"%s\" HullNum=\"AC4515\" Skipper=\"SPITHILL\" Helmsman=\"SPITHILL\" Country=\"USA\"\n" +
                "              PeliID=\"101\" RadioIP=\"172.20.2.101\">\n" +
                "            <GPSposition Z=\"1.78\" Y=\"-0.331\" X=\"-0.006\"/>\n" +
                "            <MastTop Z=\"21.496\" Y=\"3.7\" X=\"0\"/>\n" +
                "            <FlagPosition Z=\"0\" Y=\"6.2\" X=\"0\"/>\n" +
                "        </Boat>";
        for (Integer sourceId : competitors.keySet()) {
            Competitor boat = competitors.get(sourceId);
            stringBuilder.append(String.format(boatTemplate, boat.getSourceID(), boat.getTeamName(), boat.getTeamName()));
        }
        String xmlString = CharStreams.toString(new InputStreamReader(getClass().getResourceAsStream(xmlPath)));
        String boatXML = String.format(xmlString, stringBuilder.toString());
        this.sendQueue.put(null, binaryPackager.packageXML(boatXML.length(), boatXML, messageType));
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
     * Get all messages from receive queue and pass them to interpreter
     */
    private void readAllMessages() {
        for (QueueMessage m: receiveQueue.drain()) {
            this.interpretPacket(m.getHeader(), m.getBody(), m.getClientId());
        }
    }

    /**
     * packages and sends fallen crew event
     * @param locations data for the event
     * @throws IOException if send fails
     */
    public void fallenCrewEvent(List<CrewLocation> locations) throws IOException {

        byte[] eventPacket = binaryPackager.packageFallenCrewEvent(locations);
        this.sendQueue.put(null, eventPacket);
    }



    /**
     * updates the boats location and handles outgoing and incoming messages
     */
    @Override
    public void run() {

        this.readAllMessages();

        if (shouldStartGame()) raceInProgress = true;

        if (!raceInProgress) return;

        try {
            boatUpdater.updatePosition();
            sendBoatLocation();
            sendRaceStatus();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
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
}

