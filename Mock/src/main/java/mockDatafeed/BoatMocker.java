package mockDatafeed;

import com.google.common.io.ByteStreams;
import com.google.common.io.CharStreams;
import models.*;
import org.jdom2.JDOMException;
import parsers.MessageType;
import parsers.header.HeaderData;
import parsers.header.HeaderParser;
import parsers.xml.CourseXMLParser;
import utilities.CollisionUtility;
import utilities.PolarTable;


import java.io.*;
import java.net.SocketException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import utility.*;

import static java.lang.Math.*;
import static java.lang.Math.abs;
import static mockDatafeed.Keys.SAILS;
import static parsers.MessageType.UNKNOWN;
import static utility.Calculator.calcAngleBetweenPoints;
import static utility.Calculator.convertRadiansToShort;

import static utilities.Utility.fileToString;
import static utility.Calculator.shortToDegrees;

/**
 * Created by khe60 on 24/04/17.
 * Boat mocker
 */
public class BoatMocker extends TimerTask implements ConnectionClient {
    private HashMap<Integer, Competitor> competitors;
    private List<Competitor> markBoats;
    private List<CourseFeature> courseFeatures;
    private List<MutablePoint> courseBoundary;

    private ZonedDateTime expectedStartTime;
    private ZonedDateTime creationTime;
    private BinaryPackager binaryPackager;
    private TCPServer TCPserver;
    private MutablePoint prestart;
    private WindGenerator windGenerator;
    private int currentSourceID=100;
    private Random random;
    private PolarTable polarTable;
    private Course raceCourse = new RaceCourse(null, true);
    private boolean flag=true;
    private Timer timer;
    private CollisionUtility collisionUtility;
    private List<MutablePoint> courseLineEquations;


    public BoatMocker() throws IOException , JDOMException {
        timer =new Timer();
        random=new Random();
        collisionUtility = new CollisionUtility();
//        prestart = new MutablePoint(32.286577, -64.864304);
        prestart = new MutablePoint(32.295842, -64.857157);
        int connectionTime = 10000;
        competitors = new HashMap<>();
        TCPserver = new TCPServer(4941,this);
        binaryPackager = new BinaryPackager();
        //establishes the connection with Visualizer
        TCPserver.establishConnection(connectionTime);


        creationTime = ZonedDateTime.now().truncatedTo(ChronoUnit.SECONDS);
        expectedStartTime = creationTime.plusMinutes(1);

        timer.schedule(TCPserver,0,1);

        //find out the coordinates of the course
        generateCourse();
        generateCompetitors();
        generateWind();
        collisionUtility.setCourseInformation();
        courseLineEquations = collisionUtility.getCourseLineEquations();

        //send all xml data first
        sendAllXML();
        //start the race, updates boat position at a rate of 60 hz
        timer.schedule(this,0,16);

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

    /**
     * Sends the boat action data to the Visualiser
     * @param action action of the boat
     */
    private void sendBoatAction(int action, int sourceId) {
        try{
            this.TCPserver.sendData(binaryPackager.packageBoatAction(action, sourceId));
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Handle data coming in from controllers
     * @param header byte[] the packet header
     * @param packet byte[] the packet body
     */
    public void interpretPacket(byte[] header, byte[] packet) {
        MessageType messageType = UNKNOWN;
        for (MessageType messageEnum : MessageType.values()) {
            if (header[0] == messageEnum.getValue()) {
                messageType = messageEnum;
            }
        }

        switch(messageType) {
            case BOAT_ACTION:
                HeaderParser headerParser = new HeaderParser();
                HeaderData headerData = headerParser.processMessage(header);
                int sourceID = headerData.getSourceID();
                Keys action = Keys.getKeys(packet[0]);
                switch(action){
                    case SAILS:
                        sendBoatAction(SAILS.getValue(), sourceID);
                        competitors.get(sourceID).switchSails();
                        break;
                    case UP:
                        competitors.get(sourceID).changeHeading(true, shortToDegrees(windGenerator.getWindDirection()));
                        break;
                    case DOWN:
                        competitors.get(sourceID).changeHeading(false, shortToDegrees(windGenerator.getWindDirection()));
                        break;
                    case TACK:
                        double windAngle = shortToDegrees(windGenerator.getWindDirection());
                        Competitor boat = competitors.get(sourceID);
                        boat.setCurrentHeading(windAngle - (boat.getCurrentHeading() - windAngle));
                        break;
                }
                break;
        }
    }

    /**
     * Get the wind direction in degrees
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

        for(Competitor mark: markBoats) {
            if(mark.getAbbreName().contains("LG")) {
                leewardGates.add(mark);
            }
            else if(mark.getAbbreName().contains("WG")) {
                windwardGates.add(mark);
            }
        }

        if(leewardGates.size() == 2 && windwardGates.size() == 2) {
            double leewardX = (leewardGates.get(0).getPosition().getXValue() + leewardGates.get(1).getPosition().getXValue()) / 2;
            double leewardY =  (leewardGates.get(0).getPosition().getYValue() + leewardGates.get(1).getPosition().getYValue()) / 2;
            double windwardX = (windwardGates.get(0).getPosition().getXValue() + windwardGates.get(1).getPosition().getXValue()) / 2;
            double windwardY = (windwardGates.get(0).getPosition().getYValue() + windwardGates.get(1).getPosition().getYValue()) / 2;
            double angle = calcAngleBetweenPoints(leewardX, leewardY, windwardX, windwardY) + Math.PI;

            if(angle > 2 * Math.PI) {
                angle = angle - 2 * Math.PI; // wind direction cannot be more than 360
            }
            windDirection = convertRadiansToShort(angle);
        }
        windGenerator = new WindGenerator(windSpeed, windDirection);
        polarTable = new PolarTable("/polars/VO70_polar.txt", 12.0);
    }

    /**
     * finds the current course of the race
     */
    private void generateCourse() throws JDOMException, IOException {
        InputStream mockBoatStream = new ByteArrayInputStream(ByteStreams.toByteArray(getClass().getResourceAsStream("/raceTemplate.xml")));
        CourseXMLParser cl = new CourseXMLParser(mockBoatStream);
        //screen size is not important
        RaceCourse course = new RaceCourse(cl.parseCourse(), false);
        courseFeatures = course.getPoints();
        courseBoundary = cl.parseCourseBoundary();
        collisionUtility.setCourseBoundary(courseBoundary);
    }

    /**
     * adds a competitor to the list of competitors
     * @return the source Id added
     */
    private int addCompetitors(){


        double a = 0.001 * competitors.size();
//        prestart = new MutablePoint(32.286577 + a, -64.864304);
        prestart = new MutablePoint(32.296117 + a, -64.858834);

        Boat newCompetitor=new Boat("Boat "+currentSourceID, random.nextInt(20)+20, prestart, "B"+currentSourceID, currentSourceID, 1);
        competitors.put(currentSourceID, newCompetitor);
        currentSourceID+=1;
        return currentSourceID-1;
    }

    public List<Competitor> getCompetitors() {
        List<Competitor> boats = new ArrayList<>();
        for (Competitor competitor : competitors.values()) {
            boats.add(competitor);
        }
        return boats;
    }

    public int addConnection() {
        return this.addCompetitors();
    }

    /**
     * generates the competitors list
     */
    private void generateCompetitors() {

        //generate mark boats
        markBoats = new ArrayList<>();
        markBoats.add(new Boat("Start Line 1", 0, new MutablePoint(32.296577, -64.854304), "SL1", 122, 0));
        markBoats.add(new Boat("Start Line 2", 0, new MutablePoint(32.293771, -64.855242), "SL2", 123, 0));
        markBoats.add(new Boat("Mark1", 0, new MutablePoint(32.293039, -64.843983), "M1", 131, 0));
        markBoats.add(new Boat("Lee Gate 1", 0, new MutablePoint(32.309693, -64.835249), "LG1", 124, 0));
        markBoats.add(new Boat("Lee Gate 2", 0, new MutablePoint(32.308046, -64.831785), "LG2", 125, 0));

        markBoats.add(new Boat("Wind Gate 1", 0, new MutablePoint(32.284680, -64.850045), "WG1", 126, 0));
        markBoats.add(new Boat("Wind Gate 2", 0, new MutablePoint(32.280164, -64.847591), "WG2", 127, 0));

        markBoats.add(new Boat("Finish Line 1", 0, new MutablePoint(32.317379, -64.839291), "FL1", 128, 0));
        markBoats.add(new Boat("Finish Line 2", 0, new MutablePoint(32.317257, -64.836260), "FL2", 129, 0));

        //set initial heading
        for (Integer sourceId : competitors.keySet()) {
            Competitor b = competitors.get(sourceId);
            b.setCurrentHeading(courseFeatures.get(0).getExitHeading());
        }


    }

    /**
     * updates the position of all the boats given the boats speed and heading
     */
    private void updatePosition() throws IOException, InterruptedException {

        for (Integer sourceId : competitors.keySet()) {
            Competitor boat = competitors.get(sourceId);
            short windDirection = windGenerator.getWindDirection();
            double twa = abs(shortToDegrees(windDirection) - boat.getCurrentHeading());
            if(twa > 180) {
                twa = 180 - (twa - 180); // interpolator only goes up to 180
            }
            double speed = polarTable.getSpeed(twa);
            if (boat.hasSailsOut()) {
                boat.setVelocity(speed);
                boat.updatePosition(0.1);
            } else {
                boat.setVelocity(0);
            }

            this.handleCourseCollisions(boat);
            this.handleBoatCollisions(boat);
            this.handleBoundaryCollisions(boat);
//            boat.blownByWind(twa);

        }
    }


    /**
     * Calculates if the boat collides with any course features and adjusts the boats position
     * @param boat Competitor the boat to check collisions for
     */
    private void handleCourseCollisions(Competitor boat) throws IOException, InterruptedException {

        for (Competitor mark: markBoats) {
            double collisionRadius=mark.getCollisionRadius()+boat.getCollisionRadius();

            double distance = raceCourse.distanceBetweenGPSPoints(mark.getPosition(), boat.getPosition());

            if (distance <= collisionRadius) {
//              send a collision packet
                sendYachtEvent(boat.getSourceID(),1);
                boat.updatePosition(-10);
                break;
            }
        }
    }

    /**
     * Calculates if the boat collides with the course boundary, if so then pushes back the boat.
     * @param boat Competitor the boat to check collisions for
     */

    private void handleBoundaryCollisions(Competitor boat) throws IOException, InterruptedException {
        double collisionRadius = boat.getCollisionRadius();
        for (MutablePoint point: courseBoundary) {
            double distance = raceCourse.distanceBetweenGPSPoints(boat.getPosition(), point);
            if (distance <= collisionRadius) {
                sendYachtEvent(boat.getSourceID(), 1);
                boat.updatePosition(-10);
                break;
            }
        }
        for (MutablePoint equation: courseLineEquations ) {
            int index = courseLineEquations.indexOf(equation);
            //distance = y - (mx + c)
            double distance = boat.getPosition().getYValue() - (equation.getXValue() * boat.getPosition().getXValue() + equation.getYValue());
            if ((abs(distance) < 0.0001) && collisionUtility.isWithinBoundaryLines(boat.getPosition(), index)) {
                //TODO: Add health reduction here later
                sendYachtEvent(boat.getSourceID(), 1);
                boat.updatePosition(-10);
                break;
            }
        }
    }



//    /**
//     * function to calculate what happens during collision
//     * @param boat1 one of the boat during collision
//     * @param boat2 the other boat during collision
//     */
//    private void calculateCollisions(Competitor boat1, Competitor boat2){
//        double x1=boat1.getPosition().getXValue();
//        double x2=boat2.getPosition().getXValue();
//        double y1=boat1.getPosition().getYValue();
//        double y2=boat2.getPosition().getYValue();
//        double contactAngle=(atan2((x1-x2),(y1-y2)));
//
//        double v1x=calculateVx(boat2.getVelocity(),boat2.getCurrentHeading(),contactAngle,boat1.getVelocity(),boat1.getCurrentHeading());
//        double v1y=calculateVy(boat2.getVelocity(),boat2.getCurrentHeading(),contactAngle,boat1.getVelocity(),boat1.getCurrentHeading());
//        Force force1=new Force(v1x,v1y,true);
//        boat1.setCurrentHeading(force1.angle());
//        boat1.setVelocity(boat1.getVelocity()+ force1.getMagnitude()*100);
//
//
//
//        double v2x=calculateVx(boat1.getVelocity(),boat1.getCurrentHeading(),contactAngle,boat2.getVelocity(),boat2.getCurrentHeading());
//        double v2y=calculateVy(boat1.getVelocity(),boat1.getCurrentHeading(),contactAngle,boat2.getVelocity(),boat2.getCurrentHeading());
//        Force force2=new Force(v2x,v2y,true);
//        boat2.setCurrentHeading(force2.angle());
//        boat2.setVelocity(boat2.getVelocity()+ force2.getMagnitude()*100);
//
//    }

    private double calculateVx(double v2, double angle2, double contactAngle, double v1, double angle1){
        angle1=toRadians(angle1);
        angle2=toRadians(angle2);
        return v2*cos(angle2-contactAngle)*cos(contactAngle)+v1*sin(angle1-contactAngle)*cos(contactAngle+PI/2);
    }

    private double calculateVy(double v2, double angle2, double contactAngle, double v1, double angle1){
        angle1=toRadians(angle1);
        angle2=toRadians(angle2);
        return v2*cos(angle2-contactAngle)*sin(contactAngle)+v1*sin(angle1-contactAngle)*sin(contactAngle+PI/2);
    }

    /**
     * Calculates if the boat collides with any other boat and adjusts the position of both boats accordingly.
     * @param boat Competitor, the boat to check collisions for
     */
    private void handleBoatCollisions(Competitor boat) throws IOException, InterruptedException {



        //Can bump back a fixed amount or try to simulate a real collision.
        for (Competitor comp: this.competitors.values()) {
            double collisionRadius=comp.getCollisionRadius()+boat.getCollisionRadius();

            if (comp.getSourceID() == boat.getSourceID()) continue; //cant collide with self

            double distance = raceCourse.distanceBetweenGPSPoints(comp.getPosition(), boat.getPosition());

            if (distance <= collisionRadius) {

//                send a collision packet
                sendYachtEvent(comp.getSourceID(),1);

//                calculateCollisions(comp,boat);
                boat.updatePosition(-10);
                comp.updatePosition(-10);
            }
        }
    }


    /**
     * generate and send a yacht event to all the clients
     * @param sourceID the boat which the event occured on
     * @param eventID the event happend
     */
    private void sendYachtEvent(int sourceID, int eventID) throws IOException, InterruptedException {
        byte[] eventPacket=binaryPackager.packageYachtEvent(sourceID,eventID);
        TCPserver.sendData(eventPacket);

        //wait for it to be send
        Thread.sleep(20);

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

            TCPserver.sendData(boatinfo);

        }
        //send mark boats
        if(flag) {
            for (Competitor markBoat : markBoats) {
                byte[] boatinfo = binaryPackager.packageBoatLocation(markBoat.getSourceID(), markBoat.getPosition().getXValue(), markBoat.getPosition().getYValue(),
                        markBoat.getCurrentHeading(), markBoat.getVelocity() * 1000, 3);
                TCPserver.sendData(boatinfo);
            }
            flag=false;
        }
    }


    /**
     * Sends Race Status to outputport
     *
     * @throws IOException IOException
     */
    private void sendRaceStatus() throws IOException {
        short windDirection = windGenerator.getWindDirection();
        short windSpeed = windGenerator.getWindSpeed();
        int raceStatus = 3;
        byte[] raceStatusPacket = binaryPackager.raceStatusHeader(raceStatus, expectedStartTime, windDirection, windSpeed,competitors.size());
        byte[] eachBoatPacket = binaryPackager.packageEachBoat(competitors);
        TCPserver.sendData(binaryPackager.packageRaceStatus(raceStatusPacket, eachBoatPacket));
    }


    /**
     * formats the racexml template
     *
     * @param xmlTemplate the template for race xml
     * @return race xml with fields filled
     */
    private String formatRaceXML(String xmlTemplate) {
        DateTimeFormatter raceIDFormat = DateTimeFormatter.ofPattern("yyMMdd");
        StringBuilder participants=new StringBuilder();
        for(Integer sourceId:competitors.keySet()){
            Competitor boat = competitors.get(sourceId);
            participants.append(String.format("<Yacht SourceID=\"%s\"/>",boat.getSourceID()));
        }
        String raceID = creationTime.format(raceIDFormat) + "01";
        return String.format(xmlTemplate, raceID, creationTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME), expectedStartTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),participants);
    }

    /**
     * Send a race xml file to client, uses raceTemplate.xml to generate custom race xml messages
     */

    private void sendRaceXML() throws IOException {
        int messageType = 6;
        String raceTemplateString = fileToString("/raceTemplate.xml");
        String raceXML = formatRaceXML(raceTemplateString);
        TCPserver.sendData(binaryPackager.packageXML(raceXML.length(), raceXML, messageType));

    }

    /**
     * Send a xml file
     */
    private void sendXML(String xmlPath, int messageType) throws IOException {
        String xmlString = CharStreams.toString(new InputStreamReader(getClass().getResourceAsStream(xmlPath)));
        //        String mockBoatsString = Files.toString(new File(xmlPath), Charsets.UTF_8);
        TCPserver.sendData(binaryPackager.packageXML(xmlString.length(), xmlString, messageType));

    }


    private void sendBoatXML(String xmlPath, int messageType) throws IOException {
        StringBuilder stringBuilder=new StringBuilder();
        String boatTemplate="<Boat Type=\"Yacht\" SourceID=\"%s\" ShapeID=\"15\" StoweName=\"USA\" ShortName=\"%s\" ShorterName=\"USA\"\n" +
                "              BoatName=\"%s\" HullNum=\"AC4515\" Skipper=\"SPITHILL\" Helmsman=\"SPITHILL\" Country=\"USA\"\n" +
                "              PeliID=\"101\" RadioIP=\"172.20.2.101\">\n" +
                "            <GPSposition Z=\"1.78\" Y=\"-0.331\" X=\"-0.006\"/>\n" +
                "            <MastTop Z=\"21.496\" Y=\"3.7\" X=\"0\"/>\n" +
                "            <FlagPosition Z=\"0\" Y=\"6.2\" X=\"0\"/>\n" +
                "        </Boat>";
        for(Integer sourceId: competitors.keySet()){
            Competitor boat = competitors.get(sourceId);
            stringBuilder.append(String.format(boatTemplate, boat.getSourceID(),boat.getTeamName(),boat.getTeamName()));
        }
        String xmlString = CharStreams.toString(new InputStreamReader(getClass().getResourceAsStream(xmlPath)));
        String boatXML=String.format(xmlString,stringBuilder.toString());
        TCPserver.sendData(binaryPackager.packageXML(boatXML.length(), boatXML, messageType));
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
     * updates the boats location
     */
    @Override
    public void run() {

        //send the boat info to receiver

        try {
            updatePosition();
            sendBoatLocation();
            sendRaceStatus();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();

        }

    }
}
