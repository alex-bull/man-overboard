package seng302.Parsers;

import com.sun.org.apache.xalan.internal.utils.XMLSecurityManager;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import seng302.Model.CourseFeature;
import seng302.Model.MutablePoint;

import javax.print.Doc;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by jar156 on 13/04/17.
 * Parser for Race XML
 */
public class RaceXMLParser {

    private RaceData raceData;
    private List<MutablePoint> courseBoundary;
    private List<CourseFeature> courseFeature;
    private double scaleFactor;
    private double bufferX;
    private double bufferY;

    public RaceData getRaceData() {
        return raceData;
    }



    public RaceXMLParser(String xmlStr) throws IOException, JDOMException {
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

        System.out.println("Race ID : " + raceID);
        System.out.println("Race type: " + raceType);
        System.out.println("Creation time date: " + creationTimeDate);
        System.out.println("Race start time: " +raceStartTime);
        System.out.println("Postpone: " + raceStartTimePostponed);

        raceData.setRaceID(raceID);
        raceData.setRaceType(raceType);
        raceData.setCreationTimeDate(creationTimeDate);
        raceData.setRaceStartTime(raceStartTime);
        raceData.setRaceStartTimePostpone(raceStartTimePostponed);

        for(Element yacht: race.getChild("Participants").getChildren()) {
            int sourceID = Integer.parseInt(yacht.getAttributeValue("SourceID"));
            String entry = yacht.getAttributeValue("Entry");
            System.out.println("Yacht ID:" + sourceID);
            System.out.println("Entry: "+ entry);
            YachtData yachtData = new YachtData(sourceID, entry);
            raceData.getParticipants().add(yachtData);

        }

        List<CompoundMarkData> course = new ArrayList<>();
        for(Element compoundMark:race.getChild("Course").getChildren()){
            int compoundMarkID = Integer.parseInt(compoundMark.getAttribute("CompoundMarkID").getValue());
            String compoundMarkName = compoundMark.getAttribute("Name").getValue();
            System.out.println("Compound mark ID: " + compoundMarkID);
            System.out.println("Compound mark name: " + compoundMarkName);

            List<MarkData> marks = new ArrayList<>();
            for(Element mark: compoundMark.getChildren()) {
                int seqID = Integer.parseInt(mark.getAttributeValue("SeqID"));
                System.out.println("Seq ID: " + seqID);
                String markName = mark.getAttributeValue("Name");
                double targetLat = Double.parseDouble(mark.getAttributeValue("TargetLat"));
                double targetLng =  Double.parseDouble(mark.getAttributeValue("TargetLng"));
                int sourceID = Integer.parseInt(mark.getAttributeValue("SourceID"));

                System.out.println("Mark name: " + markName);
                System.out.println("Target lat: " +targetLat);
                System.out.println("Target Lng: " + targetLng);
                System.out.println("Source id: " + sourceID);
                MarkData markData = new MarkData(seqID, markName, targetLat, targetLng, sourceID);
                marks.add(markData);
            }

            CompoundMarkData compoundMarkData = new CompoundMarkData(compoundMarkID, compoundMarkName, marks);
            course.add(compoundMarkData);
        }
        raceData.setCourse(course);

        for(Element corner: race.getChild("CompoundMarkSequence").getChildren()) {
            int cornerSeqID = Integer.parseInt(corner.getAttributeValue("SeqID"));
            int compoundMarkID = Integer.parseInt(corner.getAttributeValue("CompoundMarkID"));
            String rounding = corner.getAttributeValue("Rounding");
            int zoneSize = Integer.parseInt(corner.getAttributeValue("ZoneSize"));

            System.out.println("Corner seq id: " + cornerSeqID);
            System.out.println("Compound mark id : " + compoundMarkID);
            System.out.println("Rounding: " + rounding);
            System.out.println("Zone size: " + zoneSize);
            CornerData cornerData = new CornerData(cornerSeqID, compoundMarkID, rounding, zoneSize);
            raceData.getCompoundMarkSequence().add(cornerData);
        }
        int count= 0;
        for(Element limit: race.getChild("CourseLimit").getChildren()) {
            int limitSeqID =  Integer.parseInt(limit.getAttributeValue("SeqID"));
            double lat = Double.parseDouble(limit.getAttributeValue("Lat"));
            double lon = Double.parseDouble(limit.getAttributeValue("Lon"));

            System.out.println("Limit seq id:" + limitSeqID);
            System.out.println("Lat:" + lat);
            System.out.println("Lon:" + lon);
            LimitData limitData  = new LimitData(limitSeqID, lat, lon);
            raceData.getCourseLimit().add(limitData);
            count++;

        }
        System.out.println("-----count---- : " + count);

        // TODO: parse in the correct parameters
        parseRace(1200.0,1000, 1, 12, 12);
    }

    public List<MutablePoint> getCourseBoundary() {
        return courseBoundary;
    }

    public List<CourseFeature> getCourseFeature() {
        return courseFeature;
    }

    private void parseRace(double width, double height, double scaleFactor, double bufferX, double bufferY) {

        parseBoundary(width, height, scaleFactor, bufferX, bufferY);
        parseCourseFeatures(width, height, bufferX, bufferY);
    }


    private void parseCourseFeatures(double width, double height, double bufferX, double bufferY) {
        int index = 0;
        bufferX=Math.max(150,width*0.6);
        bufferY=Math.max(10,height*0.1);
        // TODO: need to carry on parsing course features here and scale them

    }


    private void parseBoundary(double width, double height, double scaleFactor, double bufferX, double bufferY) {
        System.out.println("----------------PARSING BOUNDARY------------------");

        List <Double> xMercatorCoords=new ArrayList<>();
        List <Double> yMercatorCoords=new ArrayList<>();
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

        boundary.forEach(p->p.factor(scaleFactor,scaleFactor, Collections.min(xMercatorCoords),Collections.min(yMercatorCoords),bufferX/2,bufferY/2));
        System.out.println(boundary);
        System.out.println("SIZe of boundary " + boundary.size());

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



    public static void main(String[] args){

        try {
            RaceXMLParser p = new RaceXMLParser("<Race><RaceID>11080703</RaceID><RaceType>Match</RaceType>" +
                    "<CreationTimeDate>2011-08-06T13:25:00-0000</CreationTimeDate ><RaceStartTime Time=\"2011-08-06T13:30:00-0700\" Postpone=\"false\" " +
                    "/><Participants><Yacht SourceID=\"107\" Entry=\"Port\" /><Yacht SourceID=\"108\" Entry=\"Stbd\" /></Participants><Course><CompoundMark " +
                    "CompoundMarkID=\"1\" Name=\"StartLine\"><Mark SeqID=\"1\" Name=\"PRO\" TargetLat=\"-36.83\" TargetLng=\"174.83\" SourceID=\"101\" />" +
                    "<Mark SeqID=\"2\" Name=\"PIN\" TargetLat=\"-36.84\" TargetLng=\"174.81\" SourceID=\"102\" /></CompoundMark><CompoundMark CompoundMarkID=\"2\" " +
                    "Name=\"M1\"><Mark Name=\"M1\" TargetLat=\"-36.63566590\" TargetLng=\"174.88543944\" SourceID=\"103\" /></CompoundMark><CompoundMark CompoundMarkID=\"3\" " +
                    "Name=\"M2\"><Mark Name=\"M2\" TargetLat=\"-36.83\" TargetLng=\"174.80\" SourceID=\"102\" />" +
                    "</CompoundMark><CompoundMark CompoundMarkID=\"4\" Name=\"Gate\"><Mark SeqID=\"1\" Name=\"G1\" TargetLat=\"-36.63566590\" TargetLng=\"174.97205159\" " +
                    "SourceID=\"104\" /><Mark SeqID=\"2\" Name=\"G2\" TargetLat=\"-36.64566590\" TargetLng=\"174.98205159\" SourceID=\"105\" /></CompoundMark></Course>" +
                    "<CompoundMarkSequence><Corner SeqID=\"1\" CompoundMarkID=\"1\" Rounding=\"SP\" ZoneSize=\"3\" /><Corner SeqID=\"2\" CompoundMarkID=\"2\" Rounding=\"Port\" ZoneSize=\"3\" />" +
                    "<Corner SeqID=\"3\" CompoundMarkID=\"3\" Rounding=\"Stbd\" ZoneSize=\"6\" /><Corner SeqID=\"4\" CompoundMarkID=\"4\" Rounding=\"PS\" ZoneSize=\"6\" />" +
                    "<Corner SeqID=\"5\" CompoundMarkID=\"1\" Rounding=\"SP\" ZoneSize=\"3\"/></CompoundMarkSequence><CourseLimit><Limit SeqID=\"1\" Lat=\"-36.8325\" Lon=\"174.8325\"/>" +
                    "<Limit SeqID=\"2\" Lat=\"-36.82883\" Lon=\"174.81983\"/><Limit SeqID=\"3\" Lat=\"-36.82067\" Lon=\"174.81983\"/>" +
                    "<Limit SeqID=\"4\" Lat=\"-36.811\" Lon=\"174.8265\"/><Limit SeqID=\"5\" Lat=\"-36.81033\" Lon=\"174.83833\"/><Limit SeqID=\"6\" Lat=\"-36.81533\" Lon=\"174.8525\"/>" +
                    "<Limit SeqID=\"7\" Lat=\"-36.81533\" Lon=\"174.86733\"/>" +
                    "<Limit SeqID=\"8\" Lat=\"-36.81633\" Lon=\"174.88217\"/><Limit SeqID=\"9\" Lat=\"-36.83383\" Lon=\"174.87117\"/><Limit SeqID=\"10\" Lat=\"-36.83417\" Lon=\"174.84767\"/></CourseLimit></Race>");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JDOMException e) {
            e.printStackTrace();
        }
    }

}
