package utilities;

import javafx.geometry.Rectangle2D;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import models.ColourPool;
import models.Competitor;
import models.CourseFeature;
import models.MutablePoint;
import org.jdom2.JDOMException;
import parsers.XmlSubtype;
import parsers.boatLocation.BoatData;
import parsers.boatLocation.BoatDataParser;
import parsers.markRounding.MarkRoundingData;
import parsers.markRounding.MarkRoundingParser;
import parsers.raceStatus.RaceStatusData;
import parsers.raceStatus.RaceStatusParser;
import parsers.xml.boat.BoatXMLParser;
import parsers.xml.race.CompoundMarkData;
import parsers.xml.race.MarkData;
import parsers.xml.race.RaceData;
import parsers.xml.race.RaceXMLParser;
import parsers.xml.regatta.RegattaXMLParser;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.*;

import static parsers.Converter.hexByteArrayToInt;

/**
 * Created by mgo65 on 11/05/17.
 * Interprets packets
 */
public class Interpreter implements DataSource, PacketHandler {

    private XmlSubtype xmlSubType;
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
    private double bufferX;
    private double bufferY;
    private double scaleFactor;
    private double minXMercatorCoord;
    private double minYMercatorCoord;
    private BoatXMLParser boatXMLParser;
    private List<MarkData> startMarks = new ArrayList<>();
    private List<MarkData> finishMarks = new ArrayList<>();
    private ColourPool colourPool = new ColourPool();
    private int numBoats = 0;
    private List<CompoundMarkData> compoundMarks = new ArrayList<>();
    private DataReceiver dataReceiver;

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

    public MarkRoundingData getMarkRoundingData() {
        return markRoundingData;
    }

    public int getNumBoats() {
        return this.numBoats;
    }




    /**
     * Begins data receiver streaming from port.
     * @param host String the host to stream from
     * @param port Int the port to stream from
     * @return boolean, true if the stream succeeds
     */
    public boolean receive(String host, int port) {

        //create a data receiver
        try {
            dataReceiver = new DataReceiver(host, port, this);
            Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();
            this.setCanvasDimensions(primaryScreenBounds.getWidth(), primaryScreenBounds.getHeight());

        } catch (IOException e) {
            System.out.println("Could not connect to: " + host + ":" + EnvironmentConfig.port);
            dataReceiver = null;
            return false;
        }
        //start receiving data
        Timer receiverTimer = new Timer();
        receiverTimer.schedule(dataReceiver, 0, 1);

        try {
            //wait for data to come in before setting fields
            while (this.numBoats < 1 || this.competitors.size() < this.numBoats) {
                try {
                    Thread.sleep(1000);
                } catch (Exception e) {
                    System.out.println("Thread sleep error");
                }
            }

        }
        catch(NullPointerException e) {
            System.out.println("Live stream is down");
            return false;
        }
        return true;
    }


    public void interpretPacket(byte[] header, byte[] packet) {

        int XMLMessageType = 26;
        int boatLocationMessageType = 37;
        int raceStatusMessageType = 12;
        int markRoundingMessageType = 38;
        int messageType = header[0];

        if (messageType == XMLMessageType) {
            try {
                readXMLMessage(packet);
            } catch (JDOMException | IOException e) {
                e.printStackTrace();
            }
        } else if (messageType == raceStatusMessageType) {
            RaceStatusParser raceStatusParser = new RaceStatusParser(packet);
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
            MarkRoundingParser markRoundingParser = new MarkRoundingParser(packet);
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
            BoatDataParser boatDataParser = new BoatDataParser(packet, canvasWidth, canvasHeight);
            this.boatData = boatDataParser.getBoatData();
            if (boatData.getDeviceType() == 1 && raceXMLParser.getBoatIDs().contains(boatData.getSourceID())) {
                updateBoatProperties(boatDataParser);
            } else if (boatData.getDeviceType() == 3 && raceXMLParser.getMarkIDs().contains(boatData.getSourceID())) {
                updateCourseMarks(boatDataParser);

            }
        }
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
        location.factor(scaleFactor, scaleFactor, minXMercatorCoord, minYMercatorCoord, bufferX / 2, bufferY / 2);
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

    /**
     * Updates the course features/marks
     *
     * @param boatDataParser BoatDataParser the boat data parser
     */
    private void updateCourseMarks(BoatDataParser boatDataParser) {
        //make scaling in proportion
        startMarks = raceData.getStartMarks();
        finishMarks = raceData.getFinishMarks();
        double bufferX = raceXMLParser.getBufferX();
        double bufferY = raceXMLParser.getBufferY();
        double scaleFactor = raceXMLParser.getScaleFactor();
        List<CourseFeature> points = new ArrayList<>();
        CourseFeature courseFeature = boatDataParser.getCourseFeature();

        courseFeature.factor(scaleFactor, scaleFactor, minXMercatorCoord, minYMercatorCoord, bufferX / 2, bufferY / 2);
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
     * Parse binary data into XML and create a new parser dependant on the XmlSubType
     *
     * @param message byte[] an array of bytes which includes information about the xml as well as the xml itself
     * @throws IOException   IOException
     * @throws JDOMException JDOMException
     */
    private void readXMLMessage(byte[] message) throws IOException, JDOMException {
        String xml = parseXMLMessage(message);
        switch (this.xmlSubType) {
            case REGATTA:
                RegattaXMLParser regattaParser = new RegattaXMLParser(xml.trim());
                this.timezone = regattaParser.getOffsetUTC();
                if (!Objects.equals(timezone.substring(0, 1), "-")) {
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
     * Parse binary data into XML
     *
     * @param message byte[] an array of bytes which includes information about the xml as well as the xml itself
     * @return String XML string describing Regatta, Race, or Boat
     */
    public String parseXMLMessage(byte[] message) {
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
    }


    /**
     * Sets the scaling values after the boundary has been received and parsed by the raceXMLParser.
     */
    private void setScalingFactors() {
        this.bufferX = raceXMLParser.getBufferX();
        this.bufferY = raceXMLParser.getBufferY();
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
    private void setCanvasDimensions(double width, double height) {
        this.canvasWidth = width;
        this.canvasHeight = height;
    }



}
