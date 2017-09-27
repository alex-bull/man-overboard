package utilities;

import com.rits.cloning.Cloner;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;
import parsers.powerUp.PowerUp;
import parsers.powerUp.PowerUpParser;
import parsers.powerUp.PowerUpTakenParser;
import parsers.powerUp.PowerUpType;
import parsers.xml.race.Decoration;
import parsers.xml.race.ThemeEnum;
import utility.QueueMessage;
import utility.WorkQueue;
import models.ColourPool;
import models.Competitor;
import models.CourseFeature;
import models.MutablePoint;
import models.*;
import org.jdom2.JDOMException;
import parsers.MessageType;
import parsers.RaceStatusEnum;
import parsers.XmlSubtype;
import parsers.boatAction.BoatAction;
import parsers.boatAction.BoatActionParser;
import parsers.boatLocation.BoatData;
import parsers.boatLocation.BoatDataParser;
import parsers.boatState.BoatStateParser;
import parsers.connection.ConnectionParser;
import parsers.header.HeaderData;
import parsers.header.HeaderParser;
import parsers.markRounding.MarkRoundingData;
import parsers.markRounding.MarkRoundingParser;
import parsers.raceStatus.RaceStatusData;
import parsers.raceStatus.RaceStatusParser;
import parsers.xml.boat.BoatXMLParser;
import parsers.xml.race.RaceData;
import parsers.xml.race.RaceXMLParser;
import parsers.xml.regatta.RegattaXMLParser;
import parsers.yachtEvent.YachtEventParser;
import utility.*;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.channels.UnresolvedAddressException;
import java.util.*;

import static java.lang.Math.pow;
import static parsers.BoatStatusEnum.DSQ;
import static parsers.Converter.hexByteArrayToInt;
import static parsers.MessageType.UNKNOWN;
import static parsers.Obstacles.BloodParser.parseBlood;
import static parsers.Obstacles.SharkParser.parseShark;
import static parsers.Obstacles.WhirlpoolParser.parseWhirlpool;
import static parsers.fallenCrew.FallenCrewParser.parseFallenCrew;
import static parsers.powerUp.PowerUpType.BOOST;
import static parsers.powerUp.PowerUpType.POTION;
import static utility.Calculator.calculateExpectedTack;

/**
 * Created by mgo65 on 11/05/17.
 * Interprets packets.
 */
public class Interpreter implements DataSource, PacketHandler {

    private Stage primaryStage;

    private XmlSubtype xmlSubType;
    private List<Competitor> competitorsPosition;
    private double windDirection;
    private Cloner cloner = new Cloner();
    private RaceData raceData;
    private Map<Integer, Integer> collisions;
    private BoatAction boatAction;
    private String timezone;
    private double windSpeed;
    private RaceStatusEnum raceStatus;
    private long messageTime;
    private long expectedStartTime;
    private RaceXMLParser raceXMLParser;
    private HashMap<Integer, CourseFeature> originalCourseFeature = new HashMap<>();
    private HashMap<Integer, CourseFeature> storedFeatures = new HashMap<>();
    private HashMap<Integer, CourseFeature> storedFeatures17 = new HashMap<>();
    private HashMap<Integer, Competitor> storedCompetitors = new HashMap<>();
    private HashMap<String, Decoration> decorations = new HashMap<>();

    private List<MutablePoint> courseBoundary = new ArrayList<>();
    private List<MutablePoint> courseBoundaryOriginal = new ArrayList<>();
    private List<MutablePoint> courseBoundary17 = new ArrayList<>();

    private double paddingX;
    private double paddingY;
    private double scaleFactor;
    private double minXMercatorCoord;
    private double minYMercatorCoord;
    private List<Double> GPSbounds;
    private BoatXMLParser boatXMLParser;
    private double width;
    private double height;
    private ColourPool colourPool = new ColourPool();
    private int numBoats = 0;
    private boolean seenRaceXML = false;
    private int sourceID = 0;
    private boolean spectating = false;

    private ThemeEnum themeId;

    private TCPClient TCPClient;
    private Timer clientTimer;

    //zoom factor for scaling
    private int zoomLevel = 17;

    private WorkQueue receiveQueue = new WorkQueue(1000000);



    private Map<Integer,CrewLocation> crewLocations=new HashMap<>();
    private Map<Integer, Shark> sharkLocations = new HashMap<>();
    private Map<Integer, Blood> bloodLocations = new HashMap<>();
    private Map<Integer, Whirlpool> whirlpools = new HashMap<>();



    /**
     * Send control data via TCPClient
     *
     * @param data byte[] the data to send
     */
    public void send(byte[] data) {
        try {
            this.TCPClient.send(data);
        } catch (Exception e) {
            System.out.println("Failed to send data");
        }
    }

    /**
     * Disconnect client from server
     */
    public void disconnect() {
        this.clientTimer.cancel();
        try {
            this.TCPClient.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Begins data receiver streaming from port.
     *
     * @param host  String the host to stream from
     * @param port  Int the port to stream from
     * @param scene the scene of the stage, for size calculations
     * @return boolean, false if connetion failed, true otherwise
     */
    public boolean receive(String host, int port, Scene scene) throws NullPointerException {


        Rectangle2D primaryScreenBounds;
        try {
            TCPClient = new TCPClient(host, port, receiveQueue);
            primaryScreenBounds = Screen.getPrimary().getVisualBounds();
        } catch (UnresolvedAddressException e) {
            // System.out.println("Address is not found");
            return false;
        } catch (IOException e) {
            //  System.out.println("Could not connect to: " + host + ":" + EnvironmentConfig.port);
            return false;
        }

        //calculate the effective width and height of the screen
        width = primaryScreenBounds.getWidth() - scene.getX();
        height = primaryScreenBounds.getHeight() - scene.getY();

        System.out.println("Starting client");
        //start receiving data
        this.clientTimer = new Timer();
        clientTimer.schedule(TCPClient, 0, 1);


        //request game join
        System.out.println("Sending connection request...");
        this.send(new BinaryPackager().packageConnectionRequest((byte) 1));
        return true;

    }

    /**
     * handle server updates
     */
    public void update() {
        for (QueueMessage m : this.receiveQueue.drain()) {
            this.interpretPacket(m.getHeader(), m.getBody());
        }
    }

    /**
     * Reads packet values and updates model data
     *
     * @param header byte[] packet header
     * @param packet byte[] packet body
     */
    public void interpretPacket(byte[] header, byte[] packet) {

        MessageType messageType = UNKNOWN;
        for (MessageType messageEnum : MessageType.values()) {
            if (header[0] == messageEnum.getValue()) {
                messageType = messageEnum;
            }
        }

        switch (messageType) {
            case XML:
                try {
                    readXMLMessage(packet);
                } catch (JDOMException | IOException e) {
                    e.printStackTrace();
                }
                break;
            case RACE_STATUS:
                System.out.println("Race status recieved");
                RaceStatusData raceStatusData = new RaceStatusParser().processMessage(packet);
                if (raceStatusData != null) {
                    this.raceStatus = raceStatusData.getRaceStatus();
                    System.out.println(raceStatus);
                   // this.messageTime = raceStatusData.getCurrentTime();
                    this.expectedStartTime = raceStatusData.getExpectedStartTime();
                    this.numBoats = raceStatusData.getNumBoatsInRace();
                    this.windDirection = raceStatusData.getWindDirection() + 180;
                    this.windSpeed = raceStatusData.getWindSpeed();
                    for (int id : storedCompetitors.keySet()) {

                        int newLegNumber = raceStatusData.getBoatStatuses().get(id).getLegNumber();
                        storedCompetitors.get(id).setCurrentLegIndex(newLegNumber);
                        storedCompetitors.get(id).setStatus(raceStatusData.getBoatStatuses().get(id).getBoatStatus());
                        storedCompetitors.get(id).setTimeToNextMark(raceStatusData.getBoatStatuses().get(id).getEstimatedTimeAtNextMark());
                    }
                }

                break;
            case MARK_ROUNDING:
                MarkRoundingData markRoundingData = new MarkRoundingParser().processMessage(packet);
                if (markRoundingData != null) {
                    int markID = markRoundingData.getMarkID();
                    String markName = "Start Line";

                    switch (markID) {
                        case 100:
                            markName = "Entry Limit Line";
                            break;
                        case 101:
                            markName = "Entry Line";
                            break;
                        case 102:
                            markName = "Start Line";
                            break;
                        case 103:
                            markName = "Finish Line";
                            break;
                        case 104:
                            markName = "Speed test start";
                            break;
                        case 105:
                            markName = "Speed test finish";
                            break;
                        case 106:
                            markName = "ClearStart";
                            break;
                        default:
                            markName = raceData.getCourse().get(markID - 1).getName();
                            break;

                    }
                    markRoundingData.setMarkName(markName);
                    long roundingTime = markRoundingData.getRoundingTime();

                    Competitor markRoundingBoat = storedCompetitors.get(markRoundingData.getSourceID());
                    markRoundingBoat.setLastMarkPassed(markName);
                    markRoundingBoat.setTimeAtLastMark(roundingTime);


                }
                break;
            case BOAT_LOCATION:
                BoatDataParser boatDataParser = new BoatDataParser();
                BoatData boatData = boatDataParser.processMessage(packet);

                if (boatData != null && this.raceData!= null) {

                    if (boatData.getDeviceType() == 1 && this.raceData.getParticipantIDs().contains(boatData.getSourceID())) {
                        updateBoatProperties(boatData);
                    } else if (boatData.getDeviceType() == 3 && raceData.getMarkSourceIDs().contains(boatData.getSourceID())) {
                        CourseFeature courseFeature = boatDataParser.getCourseFeature();
                        updateCourseMarks(courseFeature, boatData);
                    }
                }
                break;
            case BOAT_ACTION:
                HeaderParser headerParser = new HeaderParser();
                BoatActionParser boatActionParser = new BoatActionParser();

                HeaderData headerData = headerParser.processMessage(header);
                this.boatAction = boatActionParser.processMessage(packet);
                if (boatAction != null && headerData != null) {
                    if (headerData.getSourceID() == this.sourceID) {
                        Competitor boat = this.storedCompetitors.get(this.sourceID);
                        switch (boatAction) {
                            case SAILS_IN:
                                boat.sailsIn();
                                break;
                            case SAILS_OUT:
                                boat.sailsOut();
                                break;
                            case SWITCH_SAILS:
                                boat.switchSails();
                                break;
                            case TACK_GYBE:
                                double boatHeading = boat.getCurrentHeading();
                                boat.setCurrentHeading(calculateExpectedTack(this.windDirection, boatHeading));
                                break;
                            case RIP:
                                boat.setStatus(DSQ);
                                break;
                            default:
                                break;
                        }
                    }
                }
                break;

            case YACHT_ACTION:
                YachtEventParser parser = new YachtEventParser(packet);
                switch (parser.getEventID()) {
                    case 1: // collision
                        collisions.put(parser.getSourceID(), parser.getEventID());
                        break;
                    case 2: // whirlpool
                        collisions.put(parser.getSourceID(), parser.getEventID());
                    default:
                        break;
                }
                break;
            case BOAT_STATE:
                BoatStateParser boatStateParser = new BoatStateParser(packet);
                Competitor stateBoat = this.storedCompetitors.get(boatStateParser.getSourceId());
                if(stateBoat!= null) {
                    stateBoat.setHealthLevel(boatStateParser.getHealth());
                }
                break;
            case CONNECTION_RES:
                ConnectionParser connectionParser = new ConnectionParser(packet);
                this.sourceID = connectionParser.getSourceId();
                if (connectionParser.getStatus() == 0) this.spectating = true;
                System.out.println("Connection accepted, my source ID: " + sourceID);
                break;
            case FALLEN_CREW:
                addCrewLocation(parseFallenCrew(packet));
                break;
            case POWER_UP:
                PowerUpParser powerUpParser = new PowerUpParser();
                PowerUp powerUp = powerUpParser.parsePowerUp(packet);
                if (powerUp.getType()==BOOST|| powerUp.getType()==POTION) {
                    MutablePoint location = powerUp.getLocation();
                    MutablePoint positionOriginal = cloner.deepClone(Projection.mercatorProjection(location));

                    MutablePoint position = cloner.deepClone(Projection.mercatorProjection(location));
                    position.factor(scaleFactor, scaleFactor, minXMercatorCoord, minYMercatorCoord, paddingX, paddingY);

                    MutablePoint position17 = cloner.deepClone(Projection.mercatorProjection(location));
                    position17.factor(pow(2, zoomLevel), pow(2, zoomLevel), minXMercatorCoord, minYMercatorCoord, paddingX, paddingY);

                    powerUp.setPosition(position);
                    powerUp.setPosition17(position17);
                    powerUp.setPositionOriginal(positionOriginal);

                    this.powerUps.put(powerUp.getId(), powerUp);
                }
                break;
            case POWER_UP_TAKEN:
                PowerUpTakenParser powerUpTakenParser = new PowerUpTakenParser(packet);
                int id = powerUpTakenParser.getPowerId();
                int boatId = powerUpTakenParser.getBoatId();
                if (powerUps.containsKey(id)) {
                    PowerUp power = powerUps.get(id);
                    power.taken();
                    PowerUpType type= power.getType();
                    if (getCompetitor().getSourceID() == boatId) {
                        switch(type){
                            case BOOST:
                                getCompetitor().enableBoost();
                                break;
                            case POTION:
                                getCompetitor().enablePotion();
                                break;
                            default:
                                break;
                        }
                    }
                }
                break;
            case SHARK:
                addShark(parseShark(packet));
                break;
            case BLOOD:
                crewLocations.get((parseBlood(packet))).setDied();
                break;
            case WHIRLPOOL:
                addWhirlPool(parseWhirlpool(packet));
            default:
                break;
        }
    }

    /**
     * add whirlpools with location converted
     *
     * @param locations location of whirlpools
     */
    private void addWhirlPool(List<Whirlpool> locations) {
        for (Whirlpool whirlpool : locations) {
            MutablePoint location = cloner.deepClone(Projection.mercatorProjection(whirlpool.getPosition()));
            MutablePoint locationOriginal = cloner.deepClone(Projection.mercatorProjection(whirlpool.getPosition()));
            location.factor(scaleFactor, scaleFactor, minXMercatorCoord, minYMercatorCoord, paddingX, paddingY);
            MutablePoint location17 = cloner.deepClone(Projection.mercatorProjection(whirlpool.getPosition()));
            location17.factor(pow(2, zoomLevel), pow(2, zoomLevel), minXMercatorCoord, minYMercatorCoord, paddingX, paddingY);
            whirlpools.put(whirlpool.getSourceID(), new Whirlpool(whirlpool.getSourceID(), whirlpool.getCurrentLeg(), location, location17, locationOriginal));
        }
    }

    /**
     * updates whirlpools when scaling level changes
     */
    private void updateWhirlpools() {
        for (Whirlpool whirlpool : whirlpools.values()) {
            MutablePoint point = cloner.deepClone(whirlpool.getPositionOriginal());
            point.factor(pow(2, zoomLevel), pow(2, zoomLevel), minXMercatorCoord, minYMercatorCoord, paddingX, paddingY);
            whirlpool.setPosition17(point);
        }
    }

    /**
     * adds crew locations with location converted
     *
     * @param locations List locations
     */
    public void addCrewLocation(List<CrewLocation> locations) {
        crewLocations.clear();
        for (CrewLocation crewLocation : locations) {
            MutablePoint location = cloner.deepClone(Projection.mercatorProjection(crewLocation.getPosition()));
            MutablePoint locationOriginal = cloner.deepClone(Projection.mercatorProjection(crewLocation.getPosition()));
            location.factor(scaleFactor, scaleFactor, minXMercatorCoord, minYMercatorCoord, paddingX, paddingY);
            MutablePoint location17 = cloner.deepClone(Projection.mercatorProjection(crewLocation.getPosition()));
            location17.factor(pow(2, zoomLevel), pow(2, zoomLevel), minXMercatorCoord, minYMercatorCoord, paddingX, paddingY);
            crewLocations.put(crewLocation.getSourceId(), new CrewLocation(crewLocation.getSourceId(), crewLocation.getNumCrew(), location, location17, locationOriginal));
        }
    }

    /**
     * updates crew location when scaling level changes
     */
    private void updateCrewLocation() {
        for (CrewLocation crewLocation : crewLocations.values()) {
            MutablePoint point = cloner.deepClone(crewLocation.getPositionOriginal());
            point.factor(pow(2, zoomLevel), pow(2, zoomLevel), minXMercatorCoord, minYMercatorCoord, paddingX, paddingY);
            crewLocation.setPosition17(point);
        }
    }

    /**
     * updates power up location when scaling level changes
     */
    private void updatePowerUpLocation() {
        for (PowerUp powerUp : powerUps.values()) {
            MutablePoint point = cloner.deepClone(powerUp.getPositionOriginal());
            point.factor(pow(2, zoomLevel), pow(2, zoomLevel), minXMercatorCoord, minYMercatorCoord, paddingX, paddingY);
            powerUp.setPosition17(point);
        }
    }

    /**
     * updates decoration item location when scaling level changes
     */
    private void updateDecorationLocation(){
        for(Decoration decoration: decorations.values()){
            MutablePoint point=cloner.deepClone(decoration.getPositionOriginal());
            point.factor(pow(2,zoomLevel), pow(2,zoomLevel), minXMercatorCoord, minYMercatorCoord, paddingX, paddingY);
            decoration.setPosition17(point);
        }
    }

    /**
     * adds blood locations with location converted
     *
     * @param locations list of the blood locations
     */
    public void addBloodLocation(List<Blood> locations) {
        for (Blood blood : locations) {
            if (!bloodLocations.containsKey(blood.getSourceID())) {
                MutablePoint location = cloner.deepClone(Projection.mercatorProjection(blood.getPosition()));
                MutablePoint locationOriginal = cloner.deepClone(Projection.mercatorProjection(blood.getPosition()));
                location.factor(scaleFactor, scaleFactor, minXMercatorCoord, minYMercatorCoord, paddingX, paddingY);
                MutablePoint location17 = cloner.deepClone(Projection.mercatorProjection(blood.getPosition()));
                location17.factor(pow(2, zoomLevel), pow(2, zoomLevel), minXMercatorCoord, minYMercatorCoord, paddingX, paddingY);
                bloodLocations.put(blood.getSourceID(), new Blood(blood.getSourceID(), location, location17, locationOriginal));
            }
        }
    }

    /**
     * updates blood location when scaling level changes
     */
    private void updateBloodLocation() {
        for (Blood blood : bloodLocations.values()) {
            MutablePoint point = cloner.deepClone(blood.getPositionOriginal());
            point.factor(pow(2, zoomLevel), pow(2, zoomLevel), minXMercatorCoord, minYMercatorCoord, paddingX, paddingY);
            blood.setPosition17(point);
        }
    }

    /**
     * adds shark locations with location converted
     *
     * @param locations list of shark locations
     */
    public void addShark(List<Shark> locations) {

        sharkLocations.clear();
        for (Shark shark : locations) {
            MutablePoint location = cloner.deepClone(Projection.mercatorProjection(shark.getPosition()));
            MutablePoint locationOriginal = cloner.deepClone(Projection.mercatorProjection(shark.getPosition()));
            location.factor(scaleFactor, scaleFactor, minXMercatorCoord, minYMercatorCoord, paddingX, paddingY);
            MutablePoint location17 = cloner.deepClone(Projection.mercatorProjection(shark.getPosition()));
            location17.factor(pow(2, zoomLevel), pow(2, zoomLevel), minXMercatorCoord, minYMercatorCoord, paddingX, paddingY);
            double heading = cloner.deepClone(shark.getHeading());
            int velocity = cloner.deepClone(shark.getVelocity());
            sharkLocations.put(shark.getSourceId(), new Shark(shark.getSourceId(), shark.getNumSharks(), location, location17, locationOriginal, heading, velocity));
            shark.setSpeed(shark.getVelocity());
        }
    }

    /**
     * updates shark location when scaling level changes
     */
    private void updateSharkLocation() {
        for (Shark shark : sharkLocations.values()) {
            MutablePoint point = cloner.deepClone(shark.getPositionOriginal());
            point.factor(pow(2, zoomLevel), pow(2, zoomLevel), minXMercatorCoord, minYMercatorCoord, paddingX, paddingY);
            shark.setPosition17(point);
        }
    }

    public Map<Integer, CrewLocation> getCrewLocations() {
        return crewLocations;
    }

    public Map<Integer, Shark> getSharkLocations() {
        return sharkLocations;
    }

    public Map<Integer, Blood> getBloodLocations() {
        return bloodLocations;
    }

    public Map<Integer, Whirlpool> getWhirlpools() {
        return whirlpools;
    }

    /**
     * returns the sourceID of the clients boat
     *
     * @return the sourceID of the clients boat
     */
    public int getSourceID() {
        return sourceID;
    }

    /**
     * removes sourceID from collisions list
     *
     * @param sourceID the sourceID to be removed
     */
    @Override
    public void removeCollsions(int sourceID) {
        collisions.remove(sourceID);
    }

    /**
     * Updates the boat properties as data is being received.
     */
    private void updateBoatProperties(BoatData boatData) {
        int boatID = boatData.getSourceID();

        MutablePoint location = cloner.deepClone(boatData.getMercatorPoint());
        MutablePoint location17 = cloner.deepClone(boatData.getMercatorPoint());
        location.factor(scaleFactor, scaleFactor, minXMercatorCoord, minYMercatorCoord, paddingX, paddingY);
        location17.factor(pow(2, zoomLevel), pow(2, zoomLevel), minXMercatorCoord, minYMercatorCoord, paddingX, paddingY);

        //add to competitorsPosition and storedCompetitors if they are new

        if (storedCompetitors.get(boatID) == null) {
            Competitor competitor = this.boatXMLParser.getBoats().get(boatID);
            this.storedCompetitors.put(boatID, competitor);
        } else {
            //update its properties
            Competitor updatingBoat = storedCompetitors.get(boatID);
            // boat colour
            if (updatingBoat.getColor() == null) {
                Color colour = this.colourPool.getColours().get(0);
                updatingBoat.setColor(colour);
                colourPool.getColours().remove(colour);
            }


            updatingBoat.setPosition(location);
            updatingBoat.setPosition17(location17);
            updatingBoat.setVelocity(boatData.getSpeed());
            updatingBoat.setCurrentHeading(boatData.getHeading());
            updatingBoat.setLatitude(boatData.getLatitude());
            updatingBoat.setLongitude(boatData.getLongitude());
        }

        //order the list of competitors
        competitorsPosition.sort((o1, o2) -> (o1.getCurrentLegIndex() < o2.getCurrentLegIndex()) ? 1 : ((o1.getCurrentLegIndex() == o2.getCurrentLegIndex()) ? 0 : -1));
    }

    /**
     * Updates the course features/marks
     *
     * @param courseFeature CourseFeature
     */
    private void updateCourseMarks(CourseFeature courseFeature, BoatData boatData) {
        CourseFeature courseFeature17 = cloner.deepClone(courseFeature);
        CourseFeature courseFeatureOriginal = cloner.deepClone(courseFeature);
        courseFeature.factor(scaleFactor, scaleFactor, minXMercatorCoord, minYMercatorCoord, paddingX, paddingY);
        courseFeature17.factor(pow(2, zoomLevel), pow(2, zoomLevel), minXMercatorCoord, minYMercatorCoord, paddingX, paddingY);

        if (!storedFeatures.containsKey(boatData.getSourceID())) {
            this.storedFeatures.put(boatData.getSourceID(), courseFeature);
            this.storedFeatures17.put(boatData.getSourceID(), courseFeature17);
            this.originalCourseFeature.put(boatData.getSourceID(), courseFeatureOriginal);
        }
    }

    /**
     * updates the scaling of course marks when scaling level changes
     */
    private void updateCourseMarksScaling() {
        HashMap<Integer, CourseFeature> clone = cloner.deepClone(originalCourseFeature);
        for (int sourceId : clone.keySet()) {
            MutablePoint newLocation = clone.get(sourceId).factor(pow(2, zoomLevel), pow(2, zoomLevel), minXMercatorCoord, minYMercatorCoord, paddingX, paddingY);
            this.storedFeatures17.get(sourceId).setPixelLocation(newLocation);
        }
    }

    /**
     * Parse binary data into XML and create a new parser dependant on the XmlSubType
     *
     * @param message byte[] an array of bytes which includes information about the xml as well as the xml itself
     * @throws IOException   IOException
     * @throws JDOMException JDOMException
     */
    private void readXMLMessage(byte[] message) throws IOException, JDOMException {
        String xml = parseXMLMessage(message);
        if (xml != null) {
            switch (this.xmlSubType) {
                case REGATTA:
                    RegattaXMLParser regattaParser = new RegattaXMLParser(xml.trim());
                    this.timezone = regattaParser.getOffsetUTC();
                    if (!Objects.equals(timezone.substring(0, 1), "-")) {
                        timezone = "+" + timezone;
                    }
                    break;
                case RACE:
                    if(!seenRaceXML) {
                        raceXMLParser.setScreenSize(width, height);
                        this.raceData = raceXMLParser.parseRaceData(xml.trim());
                        this.themeId = raceXMLParser.getThemeId();
                        this.decorations = this.raceData.getDecorations();

                        setScalingFactors();
                        setCourseBoundary(raceXMLParser.getCourseBoundary());
                        GPSbounds = raceXMLParser.getGPSBounds();
//                        this.seenRaceXML = true;
                        this.messageTime = raceData.getGameStartTime();
                    }

                    break;
                case BOAT:
                    this.boatXMLParser = new BoatXMLParser(xml.trim());
                    competitorsPosition.clear();
                    competitorsPosition.addAll(boatXMLParser.getBoats().values());
                    if (this.sourceID != 0 && boatXMLParser.getBoats().get(this.sourceID) != null) {
                        this.storedCompetitors.put(this.sourceID, boatXMLParser.getBoats().get(this.sourceID));
                    }
                    break;
            }
        }

    }

    /**
     * updates course boundary when zoom level changes
     */
    private void updateCourseBoundary() {
        courseBoundary17.removeAll(courseBoundary17);
        for (MutablePoint p : cloner.deepClone(courseBoundaryOriginal)) {
            courseBoundary17.add(p.factor(pow(2, zoomLevel), pow(2, zoomLevel), minXMercatorCoord, minYMercatorCoord, paddingX, paddingY));
        }
    }

    @Override
    public int getZoomLevel() {
        return zoomLevel;
    }


    /**
     * Parse binary data into XML
     *
     * @param message byte[] an array of bytes which includes information about the xml as well as the xml itself
     * @return String XML string describing Regatta, Race, or Boat
     */
    private String parseXMLMessage(byte[] message) {

        try {
            int regattaType = 5;
            int raceType = 6;
            int boatType = 7;
            // can get version number, ack number, timestamp if needed

            int subType = message[9];
            if (subType == regattaType) {
                xmlSubType = XmlSubtype.REGATTA;
            }
            if (subType == raceType) {
                xmlSubType = XmlSubtype.RACE;
            }
            if (subType == boatType) {
                xmlSubType = XmlSubtype.BOAT;
            }

            // can get sequence number if needed
            byte[] xmlLengthBytes = Arrays.copyOfRange(message, 12, 14);

            int xmlLength = hexByteArrayToInt(xmlLengthBytes);
            int start = 14;
            int end = start + xmlLength;
            byte[] xmlBytes = Arrays.copyOfRange(message, start, end);
            String charset = "UTF-8";
            String xmlString = "";

            try {
                xmlString = new String(xmlBytes, charset);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            return xmlString;
        } catch (Exception e) {
            return null;
        }
    }


    /**
     * Sets the scaling values after the boundary has been received and parsed by the raceXMLParser.
     */
    private void setScalingFactors() {
        this.paddingX = raceXMLParser.getPaddingX();
        this.paddingY = raceXMLParser.getPaddingY();
        this.scaleFactor = raceXMLParser.getScaleFactor();
        this.minXMercatorCoord = raceXMLParser.getxMin();
        this.minYMercatorCoord = raceXMLParser.getyMin();
    }

    /**
     * Evaluates position17 given a position
     * @param position MutablePoint the position to factor
     * @return MutablePoint the factored position
     */
    public MutablePoint evaluatePosition17(MutablePoint position) {
        MutablePoint position17=cloner.deepClone(Projection.mercatorProjection(position));
        position17.factor(pow(2,zoomLevel), pow(2,zoomLevel), minXMercatorCoord, minYMercatorCoord, paddingX, paddingY);
        return position17;
    }

    public void setScalingFactor(double scaleFactor) {
        this.scaleFactor = scaleFactor;
    }

    /**
     * Evaluates position given a location
     * @param location MutablePoint the location to factor
     * @return MutablePoint the factored position
     */
    public MutablePoint evaluatePosition(MutablePoint location) {
        MutablePoint position = cloner.deepClone(Projection.mercatorProjection(location));
        position.factor(scaleFactor, scaleFactor, minXMercatorCoord, minYMercatorCoord, paddingX, paddingY);
        return position;
    }

    /**
     * Evaluates original position given a position
     * @param location MutablePoint the location to factor
     * @return MutablePoint the factored position
     */
    public MutablePoint evaluateOriginalPosition(MutablePoint location) {
        return cloner.deepClone(Projection.mercatorProjection(location));
    }


    /**
     * changes the scaling when zoomed in
     * @param deltaLevel level of zoom
     */
    public void changeScaling(double deltaLevel) {
        this.zoomLevel += deltaLevel;
        updateCourseMarksScaling();
        updateCourseBoundary();
        updateCrewLocation();
        updatePowerUpLocation();
        updateDecorationLocation();
        updateSharkLocation();
        updateBloodLocation();
        updateWhirlpools();
    }

    public Map<Integer, Integer> getCollisions() {
        return collisions;
    }

    public HashMap<Integer, CourseFeature> getStoredFeatures() {
        return storedFeatures;
    }

    public List<Double> getGPSbounds() {
        return GPSbounds;
    }

    public int getMapZoomLevel() {
        return (int) raceXMLParser.getZoomLevel();
    }

    public double getShiftDistance() {
        return raceXMLParser.getShiftDistance();
    }

    public boolean isSpectating() {
        return spectating;
    }

    public HashMap<String, Decoration> getDecorations() {
        return decorations;
    }

    public void setPrimaryStage(Stage primaryStage){
        this.primaryStage=primaryStage;
    }
    private Map<Integer, PowerUp> powerUps = new HashMap<>();

    public Interpreter() {
        competitorsPosition = new ArrayList<>();
        collisions = new HashMap<>();
        this.raceXMLParser = new RaceXMLParser();

    }

    public Map<Integer, CourseFeature> getCourseFeatureMap() {
        return this.storedFeatures;
    }

    public List<MutablePoint> getCourseBoundary() {
        return courseBoundary;
    }

    private void setCourseBoundary(List<MutablePoint> courseBoundary) {
        courseBoundaryOriginal = cloner.deepClone(courseBoundary);

        for (MutablePoint p : cloner.deepClone(courseBoundary)) {
            this.courseBoundary.add(p.factor(scaleFactor, scaleFactor, minXMercatorCoord, minYMercatorCoord, paddingX, paddingY));
        }

        updateCourseBoundary();

    }

    public List<MutablePoint> getCourseBoundary17() {
        return courseBoundary17;
    }

    public HashMap<Integer, CourseFeature> getStoredFeatures17() {
        return storedFeatures17;
    }

    public String getCourseTimezone() {
        return timezone;
    }

    public List<Integer> getStartMarks() {
        return raceData.getStartMarksID();
    }

    public List<Integer> getFinishMarks() {
        return raceData.getFinishMarksID();
    }

    public RaceStatusEnum getRaceStatus() {
        return raceStatus;
    }

    public long getMessageTime() {
        return messageTime;
    }

    public long getExpectedStartTime() {
        return expectedStartTime;
    }

    public List<Competitor> getCompetitorsPosition() {
        return new ArrayList<>(competitorsPosition); //return a shallow copy for thread safety
    }

    public Map<Integer, Competitor> getStoredCompetitors() {
        return this.storedCompetitors;
    }

    public double getWindDirection() {
        return windDirection;
    }

    public double getWindSpeed() {
        return windSpeed / 1000.0;
    }

    public Map<Integer, List<Integer>> getIndexToSourceIdCourseFeatures() {
        return this.raceData.getLegIndexToMarkSourceIds();
    }

    /**
     * @return the boat which the visualizer controls
     */
    public Competitor getCompetitor() {
        return storedCompetitors.get(sourceID);
    }

    public ThemeEnum getThemeId() {
        return themeId;
    }

    public Map<Integer, PowerUp> getPowerUps() {
        return powerUps;
    }


}
