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

    public Interpreter getInterpreter() {
        return interpreter;
    }

    private Interpreter interpreter;
    private Socket receiveSock;
    private DataInputStream dis;
    private ByteStreamConverter byteStreamConverter;
//    private List<Competitor> competitors;
//    private double windDirection;
//    private double canvasWidth;
//    private double canvasHeight;
//    private BoatData boatData;
//    private RaceStatusData raceStatusData;
//    private RaceData raceData;
//    private MarkRoundingData markRoundingData;
//    private String timezone;
//    private String raceStatus;
//    private long messageTime;
//    private long expectedStartTime;
//    private RaceXMLParser raceXMLParser;
    private HashMap<Integer, CourseFeature> storedFeatures = new HashMap<>();
    private HashMap<Integer, Competitor> storedCompetitors = new HashMap<>();
    private List<CourseFeature> courseFeatures = new ArrayList<>();
    private List<MutablePoint> courseBoundary = new ArrayList<>();
    private FileOutputStream fileOutputStream;
//    private double bufferX;
//    private double bufferY;
//    private double scaleFactor;
//    private double minXMercatorCoord;
//    private double minYMercatorCoord;
    private BoatXMLParser boatXMLParser;
    private List<MarkData> startMarks = new ArrayList<>();
    private List<MarkData> finishMarks = new ArrayList<>();
    private ColourPool colourPool = new ColourPool();
//    private int numBoats = 0;
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
        interpreter = new Interpreter();
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

//    public String getCourseTimezone() {
//        return timezone;
//    }

    public List<MarkData> getStartMarks() {
        return startMarks;
    }

    public List<MarkData> getFinishMarks() {
        return finishMarks;
    }

//    public String getRaceStatus() {
//        return raceStatus;
//    }
//
//    public long getMessageTime() {
//        return messageTime;
//    }
//
//    public long getExpectedStartTime() {
//        return expectedStartTime;
//    }

//    public List<Competitor> getCompetitors() {
//        return competitors;
//    }
//
//    //Setters
//    public void setCompetitors(List<Competitor> competitors) {
//        this.competitors = competitors;
//    }

//    public double getWindDirection() {
//        return windDirection;
//    }

//    public HashMap<Integer, Competitor> getStoredCompetitors() {
//        return storedCompetitors;
//    }

    ////////////////////////////////////////////////
//
//    public MarkRoundingData getMarkRoundingData() {
//        return markRoundingData;
//    }
//
//    public int getNumBoats() {
//        return this.numBoats;
//    }



    /**
     * Sets the canvas width and height so that the parser can scale values to screen.
     *
     * @param width  double the width of the canvas
     * @param height double the height of the canvas
     */
    public void setCanvasDimensions(double width, double height) {
        interpreter.setCanvasDimensions(width, height);
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
                interpreter.interpretPacket();

            }
        } catch (EOFException e) {
            System.out.println("End of file.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



}
