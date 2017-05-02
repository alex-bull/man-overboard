package seng302.Model;

import javafx.beans.property.DoubleProperty;
import org.jdom2.JDOMException;
import seng302.Parsers.*;

import java.io.*;
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
    private double canvasWidth;
    private double canvasHeight;
    private BoatData boatData;
    private RaceData raceData;
    private String timezone;
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

    //Getters
    public List<CourseFeature> getCourseFeatures() { return courseFeatures; }
    public List<MutablePoint> getCourseBoundary() { return courseBoundary; }
    public String getCourseTimezone() { return timezone; }
    public List<Competitor> getCompetitors() {
        return competitors;
    }

    //Setters
    public void setCompetitors(List<Competitor> competitors){
        this.competitors=competitors;
    }
    public void setCourseBoundary(List<MutablePoint> courseBoundary) {
        this.courseBoundary = courseBoundary;
    }

    ////////////////////////////////////////////////

    /**
     * Initializes port to receive binary data from
     * @param host String host of the server
     * @param port int number of port of the server
     * @throws IOException IOException
     */
    public DataReceiver(String host, int port) throws IOException {
        receiveSock = new Socket(host, port);
        dis = new DataInputStream(receiveSock.getInputStream());
//        fileOutputStream=new FileOutputStream(new File("src/main/resources/BinaryFiles/"+host));
//        System.setOut(new PrintStream(new BufferedOutputStream(fileOutputStream)));
        byteStreamConverter = new ByteStreamConverter();
        System.out.println("Start connection to server...");

    }


    /**
     * Parse binary data into XML and create a new parser dependant on the XmlSubType
     * @param message byte[] an array of bytes which includes information about the xml as well as the xml itself
     * @throws IOException IOException
     * @throws JDOMException JDOMException
     */
    private void readXMLMessage(byte[] message) throws IOException, JDOMException {
        String xml = byteStreamConverter.parseXMLMessage(message);
        XmlSubtype subType = byteStreamConverter.getXmlSubType();
        switch (subType) {
            case REGATTA:
                RegattaXMLParser regattaParser = new RegattaXMLParser(xml.trim());
                this.timezone = regattaParser.getOffsetUTC();
                if (timezone.substring(0,1) != "-") {
                    timezone = "+" + timezone;
                }
                break;
            case RACE:
                this.raceXMLParser = new RaceXMLParser(xml.trim(), canvasWidth, canvasHeight);
                this.raceData = raceXMLParser.getRaceData();
                this.courseBoundary = raceXMLParser.getCourseBoundary();
                setScalingFactors();
                break;
            case BOAT:
                this.boatXMLParser = new BoatXMLParser(xml.trim());
//                System.out.println("mark boats" + boatParser.getBoats());

                break;
        }
    }

    /**
     * Sets the scaling values after the boundary has been received and parsed by the raceXMLParser.
     */
    private void setScalingFactors()
    {
        this.bufferX = raceXMLParser.getBufferX();
        this.bufferY = raceXMLParser.getBufferY();
        this.scaleFactor = raceXMLParser.getScaleFactor();
        this.minXMercatorCoord = Collections.min(raceXMLParser.getxMercatorCoords());
        this.minYMercatorCoord = Collections.min(raceXMLParser.getyMercatorCoords());
    }

    /**
     * Sets the canvas width and height so that the parser can scale values to screen.
     * @param width double the width of the canvas
     * @param height double the height of the canvas
     */
    public void setCanvasDimensions(double width, double height) {
        this.canvasWidth = width;
        this.canvasHeight = height;
    }


    /**
     * Close the established streams and sockets
     * @throws IOException IOException
     */
    public void close() throws IOException {
        receiveSock.close();
        dis.close();
    }

    /**
     * Read the message header (13 bytes) and parse information
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
    public void run() {
//        while(true){
            try {
                boolean isStartOfPacket = checkForSyncBytes();

                if (isStartOfPacket) {
                    readHeader();

                    int XMLMessageType = 26;
                    int boatLocationMessageType = 37;
                    int messageType = (int) byteStreamConverter.getMessageType();
                    int messageLength = (int) byteStreamConverter.getMessageLength();

                    byte[] message = new byte[messageLength];
                    dis.readFully(message);

                    if (messageType == XMLMessageType) {
                        System.out.println("XML");
                        try {
                            readXMLMessage(message);
                        } catch (JDOMException e) {
                            e.printStackTrace();
                        }
                    }
                    else if (messageType == boatLocationMessageType) {
                        BoatDataParser boatDataParser = new BoatDataParser(message, canvasWidth, canvasHeight);
                        this.boatData = boatDataParser.getBoatData();

                        //update boat with boat data
//                        if(competitors!=null){
//                            //TODO: get rid of this hack
//                            int index=boatData.getSourceID() - 101;
//                            if(index<competitors.size()) {
//                                competitors.get(index).setProperties(boatData.getSpeed(), boatData.getHeading(), boatData.getLatitude(), boatData.getLongitude());
//                            }
//
//
//                        }

//                        System.out.println("BOATSSS" + boatXMLParser.getBoats());
                        if(boatData.getDeviceType() == 1 && raceXMLParser.getBoatIDs().contains(boatData.getSourceID())) {
                            int boatID = boatData.getSourceID();

                            Competitor competitor = this.boatXMLParser.getBoats().get(boatID);
                            System.out.println("boat country " + competitor.getAbbreName());
                            double x = boatDataParser.getPixelPoint().getXValue();
                            double y = boatDataParser.getPixelPoint().getYValue();
                            System.out.println("boat lat and lon " + x +  "   " + y);
                            MutablePoint location = new MutablePoint(x, y);
                            location.factor(scaleFactor, scaleFactor, minXMercatorCoord, minYMercatorCoord, bufferX/2,bufferY/2);
                            competitor.setPosition(location);
                            System.out.println(location.getXValue() +  "   " + location.getYValue());

                            this.storedCompetitors.put(boatID, competitor);

                            List<Competitor> comps = new ArrayList<>();
                            for(Integer id: this.storedCompetitors.keySet()) {
                                comps.add(this.storedCompetitors.get(id));
                            }

                            this.competitors = comps;
//                            this.boatXMLParser.getBoats().containsKey(boatData.getSourceID());
//                            // then there are competitors
//                            List<Competitor> boats = new ArrayList<>();
//                            Competitor competitor = boatDataParser.getCompetitor();
//                            System.out.println("BOAT ID: " + boatData.getSourceID());
//

                        }
                        else if(boatData.getDeviceType() == 3 && raceXMLParser.getMarkIDs().contains(boatData.getSourceID())) {
                            //make scaling in proportion
                            ;
                            List<CourseFeature> points = new ArrayList<>();
//                            CourseFeature courseFeature = boatDataParser.getCourseFeature();
                            CourseFeature courseFeature = boatDataParser.getCourseFeature();
//                            this.storedFeatures.add(courseFeature);
                            courseFeature.factor(scaleFactor,scaleFactor,minXMercatorCoord,minYMercatorCoord,bufferX/2,bufferY/2);
                            this.storedFeatures.put(boatData.getSourceID(), courseFeature);


                            for(Integer id: this.storedFeatures.keySet()) {
                                points.add(this.storedFeatures.get(id));
                            }


                            this.courseFeatures = points;
//                            System.out.println("------STORED FEATURES------");
//                            for(CourseFeature feature: this.courseFeatures) {
//                                System.out.println("name---" + feature.getName());
//                                System.out.println(feature.getExitHeading());
//                                System.out.println(feature.getPixelLocations().get(0).getXValue());
//                                System.out.println(feature.getPixelLocations().get(0).getYValue());
//
//                                System.out.println("x and y values above");
//                            }
//                            System.out.println("---END STORED FEATURES -------");
                        }
                    }
                }
            }
            catch (EOFException e) {
                System.out.println("End of file.");
            }
            catch (IOException e) {
                e.printStackTrace();
            }
//        }
    }

    /**
     * Creates a new data receiver and runs at the period of 100ms
     * @param args
     * @throws InterruptedException
     */
    public static void main (String [] args) throws InterruptedException {
        DataReceiver dataReceiver = null;
        while(dataReceiver == null) {
            try {
//                dataReceiver = new DataReceiver("livedata.americascup.com", 4941);
                dataReceiver = new DataReceiver("csse-s302staff.canterbury.ac.nz", 4941);

                Timer timer = new Timer();
                timer.schedule(dataReceiver,0,100);

            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


}
