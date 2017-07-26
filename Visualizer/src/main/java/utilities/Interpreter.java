package utilities;

import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;
import models.ColourPool;
import models.Competitor;
import models.CourseFeature;
import models.MutablePoint;
import org.jdom2.JDOMException;
import parsers.MessageType;
import parsers.RaceStatusEnum;
import parsers.XmlSubtype;
import parsers.boatAction.BoatAction;
import parsers.boatAction.BoatActionParser;
import parsers.boatLocation.BoatData;
import parsers.boatLocation.BoatDataParser;
import parsers.markRounding.MarkRoundingData;
import parsers.markRounding.MarkRoundingParser;
import parsers.raceStatus.RaceStatusData;
import parsers.raceStatus.RaceStatusParser;
import parsers.xml.boat.BoatXMLParser;
import parsers.xml.race.CompoundMarkData;
import parsers.xml.race.RaceData;
import parsers.xml.race.RaceXMLParser;
import parsers.xml.regatta.RegattaXMLParser;
import parsers.yachtEvent.YachtEventParser;
import utility.PacketHandler;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.UnresolvedAddressException;
import java.util.*;

import static parsers.Converter.hexByteArrayToInt;
import static parsers.MessageType.BOAT_ACTION;
import static parsers.MessageType.UNKNOWN;

/**
 * Created by mgo65 on 11/05/17.
 * Interprets packets
 */
public class Interpreter implements DataSource, PacketHandler {

    private Stage primaryStage;

    private XmlSubtype xmlSubType;
    private List<Competitor> competitorsPosition;
    private double windDirection;
    private BoatData boatData;
    private RaceData raceData;
    private Set<Integer> collisions;
    private BoatAction boatAction;
    private String timezone;
    private double windSpeed;
    private RaceStatusEnum raceStatus;
    private long messageTime;
    private long expectedStartTime;
    private RaceXMLParser raceXMLParser;
    private HashMap<Integer, CourseFeature> storedFeatures = new HashMap<>();
    private HashMap<Integer, Competitor> storedCompetitors = new HashMap<>();
    private List<CourseFeature> courseFeatures = new ArrayList<>();
    private List<MutablePoint> courseBoundary = new ArrayList<>();
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
    private List<CompoundMarkData> compoundMarks = new ArrayList<>();
    private boolean seenRaceXML = false;
    private int sourceID;
    private TCPClient TCPClient;

    public Interpreter() {
        competitorsPosition = new ArrayList<>();
        collisions=new HashSet<>();
        this.raceXMLParser = new RaceXMLParser();

    }

    public void setPrimaryStage(Stage primaryStage){
        this.primaryStage=primaryStage;
    }

    public Stage getPrimaryStage(){
        return primaryStage;
    }
    public List<CourseFeature> getCourseFeatures() {
        return courseFeatures;
    }
    public Map<Integer, CourseFeature> getCourseFeatureMap() {return this.storedFeatures;}

    public List<MutablePoint> getCourseBoundary() {
        return courseBoundary;
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

    public Map<Integer, Competitor> getStoredCompetitors() {return this.storedCompetitors;}

    public double getWindDirection() {
        return windDirection;
    }

    public double getWindSpeed() {
        return windSpeed/1000.0;
    }

    public Map<Integer, List<Integer>> getIndexToSourceIdCourseFeatures() {
        return this.raceData.getLegIndexToSourceId();
    }


    /**
     * Send control data via TCPClient
     * @param data byte[] the data to send
     */
    public void send(byte[] data) {
        try {
            TCPClient.send(data);
        }
        catch (IOException e) {
            System.out.println("Could not send data");
        }
    }


    /**
     * Begins data receiver streaming from port.
     * @param host  String the host to stream from
     * @param port  Int the port to stream from
     * @param scene the scene of the stage, for size calculations
     * @return boolean, true if the stream succeeds
     */
    public boolean receive(String host, int port, Scene scene) throws NullPointerException{

        Rectangle2D primaryScreenBounds;
        try {
            TCPClient = new TCPClient(host, port, this);
            primaryScreenBounds = Screen.getPrimary().getVisualBounds();
        }
        catch (UnresolvedAddressException e){
            System.out.println("Address is not found");
            return false;
        }
        catch (IOException e) {
            System.out.println("Could not connect to: " + host + ":" + EnvironmentConfig.port);
            return false;
        }

        //calculate the effective width and height of the screen
        width = primaryScreenBounds.getWidth() - scene.getX();
        height = primaryScreenBounds.getHeight() - scene.getY();

        //start receiving data
        Timer receiverTimer = new Timer();

        receiverTimer.schedule(TCPClient, 0, 1);

        try {
            //wait for data to come in before setting fields
            while (this.numBoats < 1 || storedCompetitors.size() < this.numBoats) {
                try {
                    Thread.sleep(1000);
                } catch (Exception e) {
                    System.out.println("Thread sleep error");
                }
            }

        } catch (NullPointerException e) {
            System.out.println("Live stream is down");
            return false;

        }
        return true;
    }


    /**
     * Reads packet values and updates model data
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
                RaceStatusData raceStatusData = new RaceStatusParser().processMessage(packet);
                if (raceStatusData != null) {
                    this.raceStatus = raceStatusData.getRaceStatus();
                    this.messageTime = raceStatusData.getCurrentTime();
                    this.expectedStartTime = raceStatusData.getExpectedStartTime();
                    this.numBoats = raceStatusData.getNumBoatsInRace();
                    this.windDirection = raceStatusData.getWindDirection() + 180;
                    this.windSpeed = raceStatusData.getWindSpeed();
                    for (int id : storedCompetitors.keySet()) {
                        storedCompetitors.get(id).setCurrentLegIndex(raceStatusData.getBoatStatuses().get(id).getLegNumber());
                        storedCompetitors.get(id).setTimeToNextMark(raceStatusData.getBoatStatuses().get(id).getEstimatedTimeAtNextMark());
                    }
                }

                break;
            case MARK_ROUNDING:
                MarkRoundingData markRoundingData = new MarkRoundingParser().processMessage(packet);

                if (markRoundingData != null) {
                    int markID = markRoundingData.getMarkID();
                    String markName;
//                    if(storedFeatures.keySet().contains(markID)) {
//                        markName = storedFeatures.get(markID).getName();
//                    }
                    switch(markID){
                        case 100:
                            markName="Entry Limit Line";
                            break;
                        case 101:
                            markName="Entry Line";
                            break;
                        case 102:
                            markName="Start Line";
                            break;
                        case 103:
                            markName="Finish Line";
                            break;
                        case 104:
                            markName="Speed test start";
                            break;
                        case 105:
                            markName="Speed test finish";
                            break;
                        case 106:
                            markName="ClearStart";
                            break;
                        default:
                            markName=raceData.getCourse().get(markID+1).getName();
                            break;

                    }
                    long roundingTime = markRoundingData.getRoundingTime();

//                    System.out.println(markRoundingData.getSourceID());
//                    System.out.println(markID);
//                    System.out.println(markName);
//                    System.out.println("-----------------------------------------");

                    storedCompetitors.get(markRoundingData.getSourceID()).setLastMarkPassed(markName);
                    storedCompetitors.get(markRoundingData.getSourceID()).setTimeAtLastMark(roundingTime);
                }
                break;
            case BOAT_LOCATION:
                BoatDataParser boatDataParser = new BoatDataParser();
                this.boatData = boatDataParser.processMessage(packet);

                if (boatData != null) {
                    if (boatData.getDeviceType() == 1 && this.raceData.getParticipantIDs().contains(boatData.getSourceID())) {
                        updateBoatProperties();
                    } else if (boatData.getDeviceType() == 3 && raceData.getMarkIDs().contains(boatData.getSourceID())) {
                        CourseFeature courseFeature = boatDataParser.getCourseFeature();
                        updateCourseMarks(courseFeature);
                    }
                }
                break;
            case BOAT_ACTION:
                BoatActionParser boatActionParser = new BoatActionParser();
                this.boatAction = boatActionParser.processMessage(packet);
                if (boatAction != null) {
                    if (boatAction.equals(BoatAction.SAILS_IN)) {
                        Competitor boat = this.storedCompetitors.get(getSourceID());
                        boat.switchSails();
                    }
                }
                break;
            case SOURCE_ID:

                ByteBuffer byteBuffer=ByteBuffer.wrap(packet);
                byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
                sourceID=byteBuffer.get();
                break;
            case YACHT_ACTION:
                YachtEventParser parser=new YachtEventParser(packet);
                switch (parser.getEventID()){
                    case 1:
//                  collision
                        collisions.add(sourceID);
                        break;
                    default:
                        break;
                }
                break;

            default:
                break;
        }
    }

    /**
     * returns the sourceID of the clients boat
     * @return the sourceID of the clients boat
     */
    public int getSourceID() {
        return sourceID;
    }

    /**
     * removes sourceID from collisions list
     * @param sourceID the sourceID to be removed
     */
    @Override
    public void removeCollsions(int sourceID) {
        collisions.remove(sourceID);
    }

    /**
     * Updates the boat properties as data is being received.
     */
    private void updateBoatProperties() {
        int boatID = boatData.getSourceID();

        Competitor competitor = this.boatXMLParser.getBoats().get(boatID);

        double x = this.boatData.getPixelPoint().getXValue();
        double y = this.boatData.getPixelPoint().getYValue();
        MutablePoint location = new MutablePoint(x, y);
        location.factor(scaleFactor, scaleFactor, minXMercatorCoord, minYMercatorCoord, paddingX, paddingY);

        // boat colour
        if (competitor.getColor() == null) {
            Color colour = this.colourPool.getColours().get(0);
            competitor.setColor(colour);
            colourPool.getColours().remove(colour);
        }
        //add to competitorsPosition and storedCompetitors if they are new

        if (!storedCompetitors.keySet().contains(boatID)) {
            this.storedCompetitors.put(boatID, competitor);
            competitorsPosition.add(competitor);
        } else {
            storedCompetitors.get(boatID).setPosition(location);
            storedCompetitors.get(boatID).setVelocity(boatData.getSpeed());
            storedCompetitors.get(boatID).setCurrentHeading(boatData.getHeading());
            storedCompetitors.get(boatID).setLatitude(boatData.getLatitude());
            storedCompetitors.get(boatID).setLongitude(boatData.getLongitude());
        }

        //order the list of competitors
        competitorsPosition.sort((o1, o2) -> (o1.getCurrentLegIndex() < o2.getCurrentLegIndex()) ? 1 : ((o1.getCurrentLegIndex() == o2.getCurrentLegIndex()) ? 0 : -1));
//        this.competitorsPosition = comps;
    }

    /**
     * Updates the course features/marks
     * @param courseFeature CourseFeature
     */
    private void updateCourseMarks(CourseFeature courseFeature) {
        //make scaling in proportion
        double scaleFactor = raceXMLParser.getScaleFactor();
        List<CourseFeature> points = new ArrayList<>();

        courseFeature.factor(scaleFactor, scaleFactor, minXMercatorCoord, minYMercatorCoord, paddingX, paddingY);

        this.storedFeatures.put(boatData.getSourceID(), courseFeature);

        for (Integer id : this.storedFeatures.keySet()) {
            points.add(this.storedFeatures.get(id));
        }
        this.courseFeatures = points;
    }

    /**
     * Parse binary data into XML and create a new parser dependant on the XmlSubType
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
                        this.raceData = raceXMLParser.parseRaceData(xml.trim(), width, height);
                        this.courseBoundary = raceXMLParser.getCourseBoundary();
                        this.compoundMarks = raceData.getCourse();
                        GPSbounds = raceXMLParser.getGPSBounds();
                        setScalingFactors();
//                        this.seenRaceXML = true;
                    }

                    break;
                case BOAT:
                    this.boatXMLParser = new BoatXMLParser(xml.trim());
                    break;
            }
        }

    }


    /**
     * Parse binary data into XML
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
    public Set<Integer> getCollisions(){
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

}
