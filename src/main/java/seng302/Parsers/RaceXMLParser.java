package seng302.Parsers;

import com.sun.org.apache.xalan.internal.utils.XMLSecurityManager;
import edu.princeton.cs.introcs.In;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import seng302.Model.CourseFeature;
import seng302.Model.Gate;
import seng302.Model.Mark;
import seng302.Model.MutablePoint;

import javax.print.Doc;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.*;
import java.util.*;

/**
 * Created by jar156 on 13/04/17.
 * Parser for Race XML
 */
public class RaceXMLParser {

    private RaceData raceData;
    private List<MutablePoint> courseBoundary;
    private List<CourseFeature> courseFeatures;
    private double scaleFactor;
    private double bufferX;
    private double bufferY;
    private List <Double> xMercatorCoords;
    private List <Double> yMercatorCoords;
    private Set<Integer> markIDs = new HashSet<>();
    private Set<Integer> boatIDs = new HashSet<>();
    private double width;
    private double height;


    public RaceData getRaceData() {
        return raceData;
    }
    public List<MutablePoint> getCourseBoundary() {
        return courseBoundary;
    }
    public List<CourseFeature> getCourseFeatures() {
        return courseFeatures;
    }
    public Set<Integer> getMarkIDs() {
        return markIDs;
    }
    public Set<Integer> getBoatIDs() {
        return boatIDs;
    }


    /**
     * Parse XML race data
     * @param xmlStr XML String of race data
     * @param width double width of the course view
     * @param height double height of the course view
     * @throws IOException IOException
     * @throws JDOMException JDOMException
     */
    public RaceXMLParser(String xmlStr, double width, double height) throws IOException, JDOMException {
        this.width = width;
        this.height = height;
        this.raceData = new RaceData();
        SAXBuilder builder = new SAXBuilder();
        InputStream stream = new ByteArrayInputStream(xmlStr.getBytes("UTF-8"));
        Document root= builder.build(stream);
        Element race = root.getRootElement();

        int raceID = Integer.parseInt(race.getChild("RaceID").getValue());
        String raceType = race.getChild("RaceType").getValue();
        String creationTimeDate = race.getChild("CreationTimeDate").getValue();
        String raceStartTime = race.getChild("RaceStartTime").getAttributeValue("Time");
        boolean raceStartTimePostponed = Boolean.parseBoolean(race.getChild("RaceStartTime").getAttributeValue("Postpone"));

//        System.out.println("Race ID : " + raceID);
//        System.out.println("Race type: " + raceType);
//        System.out.println("Creation time date: " + creationTimeDate);
//        System.out.println("Race start time: " +raceStartTime);
//        System.out.println("Postpone: " + raceStartTimePostponed);

        raceData.setRaceID(raceID);
        raceData.setRaceType(raceType);
        raceData.setCreationTimeDate(creationTimeDate);
        raceData.setRaceStartTime(raceStartTime);
        raceData.setRaceStartTimePostpone(raceStartTimePostponed);

        for(Element yacht: race.getChild("Participants").getChildren()) {
            int sourceID = Integer.parseInt(yacht.getAttributeValue("SourceID"));
            String entry = yacht.getAttributeValue("Entry");
//            System.out.println("Yacht ID:" + sourceID);
//            System.out.println("Entry: "+ entry);
            YachtData yachtData = new YachtData(sourceID, entry);
            this.boatIDs.add(sourceID);
            raceData.getParticipants().add(yachtData);

        }

        List<CompoundMarkData> course = new ArrayList<>();
        List<MarkData> startMarks = new ArrayList<>();
        List<MarkData> finishMarks = new ArrayList<>();

//        CourseParser courseParser = new CourseParser()
        for(Element compoundMark:race.getChild("Course").getChildren()){
            int compoundMarkID = Integer.parseInt(compoundMark.getAttribute("CompoundMarkID").getValue());
            String compoundMarkName = compoundMark.getAttribute("Name").getValue();
//            System.out.println("Compound mark ID: " + compoundMarkID);
//            System.out.println("Compound mark name: " + compoundMarkName);

            List<MarkData> marks = new ArrayList<>();
            for(Element mark: compoundMark.getChildren()) {
                int seqID = Integer.parseInt(mark.getAttributeValue("SeqID"));
//                System.out.println("Seq ID: " + seqID);
                String markName = mark.getAttributeValue("Name");
                double targetLat = Double.parseDouble(mark.getAttributeValue("TargetLat"));
                double targetLng =  Double.parseDouble(mark.getAttributeValue("TargetLng"));
                int sourceID = Integer.parseInt(mark.getAttributeValue("SourceID"));

//                System.out.println("Mark name: " + markName);
//                System.out.println("Target lat: " +targetLat);
//                System.out.println("Target Lng: " + targetLng);
                markIDs.add(sourceID);
                MarkData markData = new MarkData(seqID, markName, targetLat, targetLng, sourceID);
                marks.add(markData);
            }

            CompoundMarkData compoundMarkData = new CompoundMarkData(compoundMarkID, compoundMarkName, marks);
            course.add(compoundMarkData);
        }
        raceData.setCourse(course);

        for(Element corner: race.getChild("CompoundMarkSequence").getChildren()) {
            int size = race.getChild("CompoundMarkSequence").getChildren().size();

            int cornerSeqID = Integer.parseInt(corner.getAttributeValue("SeqID"));
            int compoundMarkID = Integer.parseInt(corner.getAttributeValue("CompoundMarkID"));
            String rounding = corner.getAttributeValue("Rounding");
            int zoneSize = Integer.parseInt(corner.getAttributeValue("ZoneSize"));

//            System.out.println("Corner seq id: " + cornerSeqID);
//            System.out.println("Compound mark id : " + compoundMarkID);
//            System.out.println("Rounding: " + rounding);
//            System.out.println("Zone size: " + zoneSize);
            CornerData cornerData = new CornerData(cornerSeqID, compoundMarkID, rounding, zoneSize);
            if (cornerSeqID == 1) {
                for (CompoundMarkData mark : course) {
                    if (mark.getID() == compoundMarkID) {
                        startMarks.addAll(mark.getMarks());
                    }
                }
            }
            if (cornerSeqID == size) {
                for (CompoundMarkData mark : course) {
                    if (mark.getID() == compoundMarkID) {
                        finishMarks.addAll(mark.getMarks());
                    }
                }
            }
            raceData.setStartMarks(startMarks);
            raceData.setFinishMarks(finishMarks);
            raceData.getCompoundMarkSequence().add(cornerData);

        }
        int count= 0;
        for(Element limit: race.getChild("CourseLimit").getChildren()) {
            int limitSeqID =  Integer.parseInt(limit.getAttributeValue("SeqID"));
            double lat = Double.parseDouble(limit.getAttributeValue("Lat"));
            double lon = Double.parseDouble(limit.getAttributeValue("Lon"));
//
//            System.out.println("Limit seq id:" + limitSeqID);
//            System.out.println("Lat:" + lat);
//            System.out.println("Lon:" + lon);
            LimitData limitData  = new LimitData(limitSeqID, lat, lon);
            raceData.getCourseLimit().add(limitData);
            count++;

        }
        parseRace(width, height);
    }


    /**
     * Set buffers and call course parsers
     * @param width double width of the race canvas
     * @param height double height of the race canvas
     */
    private void parseRace(double width, double height) {
        this.bufferX=Math.max(1000,width*0.6);
        this.bufferY=Math.max(10,height*0.1);

        try {
            parseBoundary(width, height, bufferX, bufferY);
        } catch (Exception e) {

        }
    }



    /**
     * Parse the boundary of the course
     * @param width double - width of the race canvas
     * @param height double - height of the race canvas
     * @param bufferX canvas buffer width
     * @param bufferY canvas buffer height
     */
    private void parseBoundary(double width, double height, double bufferX, double bufferY) throws Exception {

        this.xMercatorCoords=new ArrayList<>();
        this.yMercatorCoords=new ArrayList<>();
        List<MutablePoint> boundary = new ArrayList<>();

        //loop through the parsed boundary points
        for(LimitData limit: this.raceData.getCourseLimit()) {
            double lat = limit.getLat();
            double lon = limit.getLon();

            ArrayList<Double> projectedPoint=mercatorProjection(lat,lon,width,height);
            double point1X = projectedPoint.get(0);
            double point1Y = projectedPoint.get(1);
            xMercatorCoords.add(point1X);
            yMercatorCoords.add(point1Y);
            MutablePoint pixel = new MutablePoint(point1X, point1Y);
            boundary.add(pixel);
        }

        double xDifference = (Collections.max(xMercatorCoords)-Collections.min(xMercatorCoords));
        double yDifference = (Collections.max(yMercatorCoords)-Collections.min(yMercatorCoords));

        if (xDifference == 0 || yDifference == 0) {
            throw new Exception("Attempted to divide by zero");
        }
        double xFactor = (width-bufferX)/xDifference;
        double yFactor = (height-bufferY)/yDifference;

        //make scaling in proportion
        scaleFactor = Math.min(xFactor,yFactor);
        boundary.forEach(p->p.factor(scaleFactor,scaleFactor, Collections.min(xMercatorCoords),Collections.min(yMercatorCoords),bufferX/2,bufferY/2));
        this.courseBoundary = boundary;

    }

    /**
     * Function to map latitude and longitude to screen coordinates
     * @param lat latitude
     * @param lon longitude
     * @param width width of the screen
     * @param height height of the screen
     * @return ArrayList the coordinates in metres
     */
    private ArrayList<Double> mercatorProjection(double lat, double lon, double width, double height){
        ArrayList<Double> ret=new ArrayList<>();
        double x = (lon+180)*(width/360);
        double latRad = lat*Math.PI/180;
        double merc = Math.log(Math.tan((Math.PI/4)+(latRad/2)));
        double y = (height/2)-(width*merc/(2*Math.PI));
        ret.add(x);
        ret.add(y);
        return ret;

    }


    public double getScaleFactor() {
        return scaleFactor;
    }

    public double getBufferX() {
        return bufferX;
    }

    public double getBufferY() {
        return bufferY;
    }

    public List<Double> getxMercatorCoords() {
        return xMercatorCoords;
    }

    public List<Double> getyMercatorCoords() {
        return yMercatorCoords;
    }




}
