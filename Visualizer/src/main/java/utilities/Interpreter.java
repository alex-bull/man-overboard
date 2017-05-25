package utilities;

import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import models.ColourPool;
import models.Competitor;
import models.CourseFeature;
import models.MutablePoint;
import org.jdom2.JDOMException;
import parsers.MessageType;
import parsers.RaceStatusEnum;
import parsers.XmlSubtype;
import parsers.boatLocation.BoatData;
import parsers.boatLocation.BoatDataParser;
import parsers.courseWind.CourseWindData;
import parsers.courseWind.CourseWindParser;
import parsers.courseWind.WindStatus;
import parsers.markRounding.MarkRoundingData;
import parsers.markRounding.MarkRoundingParser;
import parsers.raceStatus.RaceStatusData;
import parsers.raceStatus.RaceStatusParser;
import parsers.xml.boat.BoatXMLParser;
import parsers.xml.race.CompoundMarkData;
import parsers.xml.race.RaceData;
import parsers.xml.race.RaceXMLParser;
import parsers.xml.regatta.RegattaXMLParser;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.*;

import static parsers.Converter.hexByteArrayToInt;
import static parsers.MessageType.UNKNOWN;

/**
 * Created by mgo65 on 11/05/17.
 * Interprets packets
 */
public class Interpreter implements DataSource, PacketHandler {

    private XmlSubtype xmlSubType;
    private List<Competitor> competitorsPosition;
    private double windDirection;
    private BoatData boatData;
    private RaceData raceData;
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

    public Interpreter() {
        competitorsPosition = new ArrayList<>();
        this.raceXMLParser = new RaceXMLParser();
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
     * Begins data receiver streaming from port.
     *
     * @param host  String the host to stream from
     * @param port  Int the port to stream from
     * @param scene the scene of the stage, for size calculations
     * @return boolean, true if the stream succeeds
     */
    public boolean receive(String host, int port, Scene scene) {
        DataReceiver dataReceiver;
        Rectangle2D primaryScreenBounds;
        try {
            dataReceiver = new DataReceiver(host, port, this);
            primaryScreenBounds = Screen.getPrimary().getVisualBounds();

        } catch (IOException e) {
            System.out.println("Could not connect to: " + host + ":" + EnvironmentConfig.port);
            return false;
        }

        //calculate the effective width and height of the screen
        width = primaryScreenBounds.getWidth() - scene.getX();
        height = primaryScreenBounds.getHeight() - scene.getY();

        //start receiving data
        Timer receiverTimer = new Timer();
        receiverTimer.schedule(dataReceiver, 0, 1);

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
                RaceStatusData raceStatusData = new RaceStatusParser().processMessage(packet);
                if (raceStatusData != null) {
                    this.raceStatus = raceStatusData.getRaceStatus();
                    this.messageTime = raceStatusData.getCurrentTime();
                    this.expectedStartTime = raceStatusData.getExpectedStartTime();
                    this.numBoats = raceStatusData.getNumBoatsInRace();
                    for (int id : storedCompetitors.keySet()) {
                        storedCompetitors.get(id).setLegIndex(raceStatusData.getBoatStatuses().get(id).getLegNumber());
                        storedCompetitors.get(id).setTimeToNextMark(raceStatusData.getBoatStatuses().get(id).getEstimatedTimeAtNextMark());
                    }
                }

                break;

            case MARK_ROUNDING:
                MarkRoundingData markRoundingData = new MarkRoundingParser().processMessage(packet);

                if (markRoundingData != null) {
                    int markID = markRoundingData.getMarkID();
                    for (CompoundMarkData mark : this.compoundMarks) {
                        if (mark.getID() == markID) {
                            markRoundingData.setMarkName(mark.getName());
                        }
                    }

                    String markName = markRoundingData.getMarkName();
                    long roundingTime = markRoundingData.getRoundingTime();

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
            case COURSE_WIND:
                CourseWindParser courseWindParser = new CourseWindParser();
                CourseWindData courseWindData = courseWindParser.processMessage(packet);
                if(courseWindData != null) {
                    if(courseWindData.getWindStatuses().containsKey(10)) {
                        WindStatus officialWindStatus = courseWindData.getWindStatuses().get(10);
                        this.windDirection = officialWindStatus.getWindDirection();
                        this.windSpeed = officialWindStatus.getWindSpeed();
                    }
                }
                break;

            default:
                break;
        }
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
        }else{
            storedCompetitors.get(boatID).setPosition(location);
            storedCompetitors.get(boatID).setVelocity(boatData.getSpeed());
            storedCompetitors.get(boatID).setCurrentHeading(boatData.getHeading());

        }

        //order the list of competitors
        competitorsPosition.sort((o1, o2) -> (o1.getCurrentLegIndex() < o2.getCurrentLegIndex()) ? 1 : ((o1.getCurrentLegIndex() == o2.getCurrentLegIndex()) ? 0 : -1));
//        this.competitorsPosition = comps;
    }

    /**
     * Updates the course features/marks
     *
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
                    this.raceData = raceXMLParser.parseRaceData(xml.trim(), width, height);
                    this.courseBoundary = raceXMLParser.getCourseBoundary();
                    this.compoundMarks = raceData.getCourse();
                    GPSbounds = raceXMLParser.getGPSBounds();
                    setScalingFactors();
                    break;

                case BOAT:
                    this.boatXMLParser = new BoatXMLParser(xml.trim());
                    break;
            }
        }

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
        this.minXMercatorCoord = Collections.min(raceXMLParser.getxMercatorCoords());
        this.minYMercatorCoord = Collections.min(raceXMLParser.getyMercatorCoords());
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
