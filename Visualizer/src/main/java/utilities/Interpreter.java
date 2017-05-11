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
    private List<Competitor> competitorsPosition;
    private double windDirection;
    private Rectangle2D primaryScreenBounds;
    private BoatData boatData;
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

public Interpreter(){
    competitorsPosition =new ArrayList<>();
}

    public List<CourseFeature> getCourseFeatures() {
        return courseFeatures;
    }

    public List<MutablePoint> getCourseBoundary() {
        return courseBoundary;
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

    public List<Competitor> getCompetitorsPosition() {
        return competitorsPosition;
    }

    public double getWindDirection() {
        return windDirection;
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
        DataReceiver dataReceiver;
        try {
            dataReceiver = new DataReceiver(host, port, this);
            this.primaryScreenBounds = Screen.getPrimary().getVisualBounds();

        } catch (IOException e) {
            System.out.println("Could not connect to: " + host + ":" + EnvironmentConfig.port);
            return false;
        }
        //start receiving data
        Timer receiverTimer = new Timer();
        receiverTimer.schedule(dataReceiver, 0, 1);

        try {
            //wait for data to come in before setting fields
            while (this.numBoats < 1 || this.competitorsPosition.size() < this.numBoats) {
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


    /**
     * Reads packet values and updates model data
     * @param header byte[] packet header
     * @param packet byte[] packet body
     */
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
            RaceStatusData raceStatusData = raceStatusParser.getRaceStatus();
            this.raceStatus = raceStatusData.getRaceStatus();
            this.messageTime = raceStatusData.getCurrentTime();
            this.expectedStartTime = raceStatusData.getExpectedStartTime();
            this.windDirection = raceStatusData.getWindDirection();
            this.numBoats = raceStatusData.getNumBoatsInRace();
            for (int id : raceStatusData.getBoatStatuses().keySet()) {
//                for (Competitor competitor : competitorsPosition) {
//                    if (competitor.getSourceID() == id) {
//                        competitor.setLegIndex(raceStatusData.getBoatStatuses().get(id).getLegNumber());
//                    }
//                }
                storedCompetitors.get(id).setLegIndex(raceStatusData.getBoatStatuses().get(id).getLegNumber());
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
            for (Competitor competitor : this.competitorsPosition) {
                if (competitor.getSourceID() == this.markRoundingData.getSourceID()) {
                    competitor.setLastMarkPassed(markName);
                }
            }


        } else if (messageType == boatLocationMessageType) {
            BoatDataParser boatDataParser = new BoatDataParser(packet, primaryScreenBounds.getWidth(), primaryScreenBounds.getHeight());
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

        //add to competitorsPosition and storedCompetitors if they are new
        if(!storedCompetitors.keySet().contains(boatID)) {
            this.storedCompetitors.put(boatID, competitor);
            competitorsPosition.add(competitor);
        }


        //order the list of competitors
        competitorsPosition.sort((o1, o2) -> (o1.getLegIndex() < o2.getLegIndex()) ? 1 : ((o1.getLegIndex() == o2.getLegIndex()) ? 0 : -1));
//        this.competitorsPosition = comps;
    }

    /**
     * Updates the course features/marks
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
                this.raceXMLParser = new RaceXMLParser(xml.trim(), primaryScreenBounds.getWidth(), primaryScreenBounds.getHeight());
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
     * @param message byte[] an array of bytes which includes information about the xml as well as the xml itself
     * @return String XML string describing Regatta, Race, or Boat
     */
    private String parseXMLMessage(byte[] message) {
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



}
