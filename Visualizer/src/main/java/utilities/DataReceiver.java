package utilities;

import models.ColourPool;
import models.Competitor;
import models.CourseFeature;
import models.MutablePoint;
import parsers.*;
import javafx.scene.paint.Color;
import org.jdom2.JDOMException;
import parsers.boatLocation.BoatData;
import parsers.boatLocation.BoatDataParser;
import parsers.markRounding.MarkRoundingData;
import parsers.markRounding.MarkRoundingParser;
import parsers.raceStatus.RaceStatusData;
import parsers.raceStatus.RaceStatusParser;
import parsers.xml.race.CompoundMarkData;
import parsers.xml.race.MarkData;
import parsers.xml.race.RaceData;
import parsers.xml.race.RaceXMLParser;
import parsers.xml.boat.BoatXMLParser;
import parsers.xml.regatta.RegattaXMLParser;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.*;


/**
 * Created by khe60 on 10/04/17.
 * The Receiver class, currently receives messages 1 byte at a time
 * Can't connect to the test port for some reason (internet enabler)
 */
public class DataReceiver extends TimerTask {


    private Socket receiveSock;
    private DataInputStream dis;
    private ByteStreamConverter byteStreamConverter;
    private List<Competitor> competitors;
    private double windDirection;
    private double canvasWidth;
    private double canvasHeight;
    private BoatData boatData;
    private RaceStatusData raceStatusData;
    private RaceData raceData;
    private MarkRoundingData markRoundingData;
    private String timezone;
    private String raceStatus;
    private long messageTime;
    private long expectedStartTime;
    private RaceXMLParser raceXMLParser;
    private HashMap<Integer, CourseFeature> storedFeatures = new HashMap<>();
    private HashMap<Integer, Competitor> storedCompetitors = new HashMap<>();
    private List<CourseFeature> courseFeatures = new ArrayList<>();
    private List<MutablePoint> courseBoundary = new ArrayList<>();
    private FileOutputStream fileOutputStream;
    private double paddingX;
    private double paddingY;
    private double scaleFactor;
    private double minXMercatorCoord;
    private double minYMercatorCoord;
    private BoatXMLParser boatXMLParser;
    private List<MarkData> startMarks = new ArrayList<>();
    private List<MarkData> finishMarks = new ArrayList<>();
    private ColourPool colourPool = new ColourPool();
    private int numBoats = 0;
    private List<CompoundMarkData> compoundMarks = new ArrayList<>();

    /**
     * Initializes port to receive binary data from
     *
     * @param host String host of the server
     * @param port int number of port of the server
     * @throws IOException IOException
     */
    public DataReceiver(String host, int port) throws IOException {
        receiveSock = new Socket(host, port);
        dis = new DataInputStream(receiveSock.getInputStream());
        byteStreamConverter = new ByteStreamConverter();
        System.out.println("Start connection to server...");
    }

    /**
     * Creates a new data receiver and runs at the period of 100ms
     *
     * @param args String[]
     * @throws InterruptedException Interrupted Exception
     */
    public static void main(String[] args) throws InterruptedException {
        DataReceiver dataReceiver = null;
        while (dataReceiver == null) {
            try {
                dataReceiver = new DataReceiver("livedata.americascup.com", 4941);
                Timer timer = new Timer();
                timer.schedule(dataReceiver, 0, 100);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //Getters
    public List<CourseFeature> getCourseFeatures() {
        return courseFeatures;
    }

    public List<MutablePoint> getCourseBoundary() {
        return courseBoundary;
    }

    public void setCourseBoundary(List<MutablePoint> courseBoundary) {
        this.courseBoundary = courseBoundary;
    }

    public List<CompoundMarkData> getCompoundMarks() {
        return compoundMarks;
    }

    public String getCourseTimezone() {
        return timezone;
    }

    public List<MarkData> getStartMarks() {
        return startMarks;
    }

    public List<MarkData> getFinishMarks() {
        return finishMarks;
    }

    public String getRaceStatus() {
        return raceStatus;
    }

    public long getMessageTime() {
        return messageTime;
    }

    public long getExpectedStartTime() {
        return expectedStartTime;
    }

    public List<Competitor> getCompetitors() {
        return competitors;
    }

    //Setters
    public void setCompetitors(List<Competitor> competitors) {
        this.competitors = competitors;
    }

    public double getWindDirection() {
        return windDirection;
    }

    public HashMap<Integer, Competitor> getStoredCompetitors() {
        return storedCompetitors;
    }

    ////////////////////////////////////////////////

    public MarkRoundingData getMarkRoundingData() {
        return markRoundingData;
    }

    public int getNumBoats() {
        return this.numBoats;
    }

    /**
     * Parse binary data into XML and create a new parser dependant on the XmlSubType
     *
     * @param message byte[] an array of bytes which includes information about the xml as well as the xml itself
     * @throws IOException   IOException
     * @throws JDOMException JDOMException
     */
    private void readXMLMessage(byte[] message) throws IOException, JDOMException {
        String xml = byteStreamConverter.parseXMLMessage(message);
        XmlSubtype subType = byteStreamConverter.getXmlSubType();
        switch (subType) {
            case REGATTA:
                RegattaXMLParser regattaParser = new RegattaXMLParser(xml.trim());
                this.timezone = regattaParser.getOffsetUTC();
                if (timezone.substring(0, 1) != "-") {
                    timezone = "+" + timezone;
                }
                break;
            case RACE:
                this.raceXMLParser = new RaceXMLParser(xml.trim(), canvasWidth, canvasHeight);
                this.raceData = raceXMLParser.getRaceData();
                this.courseBoundary = raceXMLParser.getCourseBoundary();
                this.compoundMarks = raceData.getCourse();
                setScalingFactors();
                break;
            case BOAT:
                this.boatXMLParser = new BoatXMLParser(xml.trim());
                break;
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

    /**
     * Sets the canvas width and height so that the parser can scale values to screen.
     *
     * @param width  double the width of the canvas
     * @param height double the height of the canvas
     */
    public void setCanvasDimensions(double width, double height) {
        this.canvasWidth = width;
        this.canvasHeight = height;
    }

    /**
     * Close the established streams and sockets
     *
     * @throws IOException IOException
     */
    public void close() throws IOException {
        receiveSock.close();
        dis.close();
    }

    /**
     * Read the message header (13 bytes) and parse information
     *
     * @throws IOException IOException
     */
    public void readHeader() throws IOException {
        // 13 because already read sync bytes
        byte[] header = new byte[13];
        dis.readFully(header);
        byteStreamConverter.parseHeader(header);
    }

    /**
     * Check for the first and second sync byte
     *
     * @return Boolean if Sync Byte found
     * @throws IOException IOException
     */
    public boolean checkForSyncBytes() throws IOException {
        byte firstSyncByte = 0x47;
        // -125 is equivalent to 0x83 unsigned
        byte secondSyncByte = -125;

        byte[] b1 = new byte[1];
        dis.readFully(b1);
        if (b1[0] == firstSyncByte) {
            byte[] b2 = new byte[1];

            dis.readFully(b2);
            if (b2[0] == secondSyncByte) {
                return true;
            }
        }
        return false;
    }

    /**
     * Identify the start of a packet, determine the message type and length, then read.
     */
    public void run() throws NullPointerException{
        try {
            boolean isStartOfPacket = checkForSyncBytes();

            if (isStartOfPacket) {
                readHeader();
                int XMLMessageType = 26;
                int boatLocationMessageType = 37;
                int raceStatusMessageType = 12;
                int markRoundingMessageType = 38;
                int messageType = (int) byteStreamConverter.getMessageType();
                int messageLength = (int) byteStreamConverter.getMessageLength();

                byte[] message = new byte[messageLength];
                dis.readFully(message);

                if (messageType == XMLMessageType) {
                    try {
                        readXMLMessage(message);
                    } catch (JDOMException e) {
                        e.printStackTrace();
                    }
                } else if (messageType == raceStatusMessageType) {
                    RaceStatusParser raceStatusParser = new RaceStatusParser(message);
                    this.raceStatusData = raceStatusParser.getRaceStatus();
                    this.raceStatus = raceStatusData.getRaceStatus();
                    this.messageTime = raceStatusData.getCurrentTime();
                    this.expectedStartTime = raceStatusData.getExpectedStartTime();
                    this.windDirection = raceStatusData.getWindDirection();
                    this.numBoats = raceStatusData.getNumBoatsInRace();
                    for (int id : raceStatusData.getBoatStatuses().keySet()) {
                        for (Competitor competitor : competitors) {
                            if (competitor.getSourceID() == id) {
                                competitor.setLegIndex(raceStatusData.getBoatStatuses().get(id).getLegNumber());
                            }
                        }
                    }
                } else if (messageType == markRoundingMessageType) {
                    MarkRoundingParser markRoundingParser = new MarkRoundingParser(message);
                    this.markRoundingData = markRoundingParser.getMarkRoundingData();
                    int markID = markRoundingData.getMarkID();

                    for (CompoundMarkData mark : this.compoundMarks) {
                        if (mark.getID() == markID) {
                            markRoundingData.setMarkName(mark.getName());
                        }
                    }

                    String markName = markRoundingData.getMarkName();
                    for (Competitor competitor : this.competitors) {
                        if (competitor.getSourceID() == this.markRoundingData.getSourceID()) {
                            competitor.setLastMarkPassed(markName);
                        }
                    }


                } else if (messageType == boatLocationMessageType) {
                    BoatDataParser boatDataParser = new BoatDataParser(message, canvasWidth, canvasHeight);
                    this.boatData = boatDataParser.getBoatData();
                    if (boatData.getDeviceType() == 1 && raceXMLParser.getBoatIDs().contains(boatData.getSourceID())) {
                        updateBoatProperties(boatDataParser);
                    } else if (boatData.getDeviceType() == 3 && raceXMLParser.getMarkIDs().contains(boatData.getSourceID())) {
                        updateCourseMarks(boatDataParser);

                    }
                }
            }
        } catch (EOFException e) {
            System.out.println("End of file.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Updates the course features/marks
     *
     * @param boatDataParser BoatDataParser the boat data parser
     */
    private void updateCourseMarks(BoatDataParser boatDataParser) {
        //make scaling in proportion
        startMarks = raceData.getStartMarks();
        finishMarks = raceData.getFinishMarks();
        double scaleFactor = raceXMLParser.getScaleFactor();
        List<CourseFeature> points = new ArrayList<>();
        CourseFeature courseFeature = boatDataParser.getCourseFeature();

        courseFeature.factor(scaleFactor, scaleFactor, minXMercatorCoord, minYMercatorCoord, paddingX, paddingY);
        for (MarkData mark : startMarks) {
            if (Integer.valueOf(courseFeature.getName()).equals(mark.getSourceID())) {
                mark.setTargetLat(courseFeature.getPixelLocations().get(0).getXValue());
                mark.setTargetLon(courseFeature.getPixelLocations().get(0).getYValue());
            }
        }

        for (MarkData mark : finishMarks) {
            if (Integer.valueOf(courseFeature.getName()).equals(mark.getSourceID())) {
                mark.setTargetLat(courseFeature.getPixelLocations().get(0).getXValue());
                mark.setTargetLon(courseFeature.getPixelLocations().get(0).getYValue());
            }
        }

        this.storedFeatures.put(boatData.getSourceID(), courseFeature);

        for (Integer id : this.storedFeatures.keySet()) {
            points.add(this.storedFeatures.get(id));
        }
        this.courseFeatures = points;
    }

    /**
     * Updates the boat properties as data is being received.
     *
     * @param boatDataParser BoatDataParser the boat data parser
     */
    private void updateBoatProperties(BoatDataParser boatDataParser) {
        int boatID = boatData.getSourceID();

        Competitor competitor = this.boatXMLParser.getBoats().get(boatID);
        competitor.setCurrentHeading(boatData.getHeading());

        double x = boatDataParser.getPixelPoint().getXValue();
        double y = boatDataParser.getPixelPoint().getYValue();
        MutablePoint location = new MutablePoint(x, y);
        location.factor(scaleFactor, scaleFactor, minXMercatorCoord, minYMercatorCoord, paddingX, paddingY);
        competitor.setPosition(location);
        competitor.setVelocity(boatData.getSpeed());

        // boat colour
        if (competitor.getColor() == null) {
            Color colour = this.colourPool.getColours().get(0);
            competitor.setColor(colour);
            colourPool.getColours().remove(colour);
        }

        //speed
        this.storedCompetitors.put(boatID, competitor);

        List<Competitor> comps = new ArrayList<>();
        for (Integer id : this.storedCompetitors.keySet()) {
            comps.add(this.storedCompetitors.get(id));
        }

        this.competitors = comps;
    }


}
