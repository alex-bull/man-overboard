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


    private List <Double> xMercatorCoords = new ArrayList<>();
    private List <Double> yMercatorCoords = new ArrayList<>();

    private Socket receiveSock;
    private DataInputStream dis;
    private ByteStreamConverter byteStreamConverter;
    private FileOutputStream fileOutputStream;
    private List<Competitor> competitors;
    private Race race;
    private double canvasWidth;
    private double canvasHeight;
    private Course course;
    //variables that need to be refactored
    private BoatData boatData;
    private RaceData raceData;
    private RaceXMLParser raceXMLParser;

    private List<MutablePoint> courseBoundary = new ArrayList<>();

    public List<CourseFeature> getCourseFeatures() {

        return courseFeatures;
    }
    private List<CourseFeature> storedFeatures = new ArrayList<>();
    private List<CourseFeature> courseFeatures = new ArrayList<>();
//    private List<MutablePoint> markPixelLocation = new ArrayList<>();
    public Race getRace() {
        return race;
    }
    public Course getCourse() {
        return course;
    }
    public List<MutablePoint> getCourseBoundary() {
        return courseBoundary;
    }
//    public List<MutablePoint> getMarkPixelLocation() { return markPixelLocation; }


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
//            System.out.println(xml);
        switch (subType) {
            case REGATTA:
                RegattaXMLParser regattaParser = new RegattaXMLParser(xml.trim());
                break;
            case RACE:
                this.raceXMLParser = new RaceXMLParser(xml.trim(), canvasWidth, canvasHeight);
                this.raceData = raceXMLParser.getRaceData();
                this.courseBoundary = raceXMLParser.getCourseBoundary();
                break;
            case BOAT:
                BoatXMLParser boatParser = new BoatXMLParser(xml.trim());
                break;
        }
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
     * Receives one byte from server and returns it, test server only sends one byte at a time so this is gonna get changed
     * @return the byte received
     * @throws IOException IOException
     */
    public byte[] receive() throws IOException {
        byte[] received=new byte[1];
        dis.readFully(received);
        return received;
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
//        System.out.println(String.format("First Sync: %02X",b1[0]));
        if (b1[0] == firstSyncByte) {
            byte[] b2 = new byte[1];


            dis.readFully(b2);
//            System.out.println(String.format("Second Sync: %02X",b2[0]));
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
                        try {
                            readXMLMessage(message);
                        } catch (JDOMException e) {
                            e.printStackTrace();
                        }
                    }
                    else if (messageType == boatLocationMessageType) {
                        BoatDataParser boatDataParser = new BoatDataParser(message, canvasWidth, canvasHeight);
                        this.boatData = boatDataParser.getBoatData();
//                        System.out.println(boatData);
                        //update boat with boat data
                        if(competitors!=null){
                            //TODO: get rid of this hack
                            int index=boatData.getSourceID() - 101;
                            if(index<competitors.size()) {
                                competitors.get(index).setProperties(boatData.getSpeed(), boatData.getHeading(), boatData.getLatitude(), boatData.getLongitude());
                            }
                        }
                        if(boatData.getDeviceType() == 3) {
//                            for(CourseFeature point: boatDataParser.getCourseFeatures()){
//                                System.out.println(point.getPixelLocations().size() + "PIXEL LOCATION");
//                                for(MutablePoint p : point.getPixelLocations()){
//                                    System.out.println(p.getXValue() + "       +       " + p.getYValue());
//                                }
//                            }
                            List<CourseFeature> points = new ArrayList<>();
                            CourseFeature courseFeature = boatDataParser.getCourseFeature();
                            this.storedFeatures.add(courseFeature);


                            for(CourseFeature feature: this.storedFeatures) {
                                points.add(feature);
                            }



                            //make scaling in proportion
                            double bufferX = raceXMLParser.getBufferX();
                            double bufferY = raceXMLParser.getBufferY();
                            double scaleFactor = raceXMLParser.getScaleFactor();
                            List<Double> xMercatorCoords = raceXMLParser.getxMercatorCoords();
                            List<Double> yMercatorCoords = raceXMLParser.getyMercatorCoords();


                            //scale points to fit screen
                            points.stream().forEach(p->p.factor(scaleFactor,scaleFactor,Collections.min(xMercatorCoords),Collections.min(yMercatorCoords),bufferX/2,bufferY/2));
                            this.courseFeatures = points;
                            System.out.println("------STORED FEATURES------");
                            for(CourseFeature feature: points) {
                                System.out.println("name---" + feature.getName());
                                System.out.println(feature.getPixelLocations().get(0).getXValue());
                                System.out.println(feature.getPixelLocations().get(0).getYValue());
                                System.out.println("x and y values above");
                            }
                            System.out.println("---END STORED FEATURES -------");
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
