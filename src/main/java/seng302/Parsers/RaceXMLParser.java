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
import seng302.Model.MutablePoint;

import javax.print.Doc;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jar156 on 13/04/17.
 * Parser for Race XML
 */
public class RaceXMLParser {

    private RaceData raceData;
    private List<MutablePoint> courseBoundaryPoints;
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
            int seqID = Integer.parseInt(corner.getAttributeValue("SeqID"));
            //TODO: CONTINUTE HERE....
        }
    }


//    public RaceXMLParser(String xmlStr) {
//
//        try {
//            this.raceData = new RaceData();
//            SAXParserFactory factory = SAXParserFactory.newInstance();
//            SAXParser saxParser = factory.newSAXParser();
//
//            DefaultHandler handler = new DefaultHandler() {
//
//                boolean hasRaceId = false;
//                boolean hasRaceType = false;
//                boolean hasCreationTimeDate = false;
//                CompoundMarkData currentCompoundMark;
//
//                @Override
//                public void startElement(String uri, String localName, String qName, Attributes attributes) {
//                    if (qName.equalsIgnoreCase("RaceID")) {
//                        hasRaceId = true;
//
//                    } else if (qName.equalsIgnoreCase("RaceType")) {
//                        hasRaceType = true;
//
//                    } else if (qName.equalsIgnoreCase("CreationTimeDate")) {
//                        hasCreationTimeDate = true;
//
//                    } else if (qName.equalsIgnoreCase("RaceStartTime")) {
//                        String raceStartTime = attributes.getValue("Time");
//                        System.out.println("Race Start Time : " + attributes.getValue("Time"));
//                        System.out.println("Postponed? : " + attributes.getValue("Postpone"));
//                        raceData.setRaceStartTime(raceStartTime);
//                        raceData.setRaceStartTimePostpone(Boolean.parseBoolean(attributes.getValue("Postpone")));
//
//
//                    } else if (qName.equalsIgnoreCase("Yacht")) {
//                        int sourceID = Integer.parseInt(attributes.getValue("SourceID"));
//                        String entry = attributes.getValue("Entry");
//                        System.out.println("Source ID : " + sourceID);
//                        System.out.println("Entry : " + entry);
//                        YachtData yacht = new YachtData(sourceID, entry);
//                        raceData.getParticipants().add(yacht);
//
//                    } else if (qName.equalsIgnoreCase("CompoundMark")) {
//                        int id = Integer.parseInt(attributes.getValue("CompoundMarkID"));
//                        String name = attributes.getValue("Name");
//                        System.out.println("Compound Mark ID : " + id);
//                        System.out.println("Name : " + name);
//                        currentCompoundMark = new CompoundMarkData(id, name);
//                        raceData.getCourse().put(id, currentCompoundMark);
//
//                    } else if (qName.equalsIgnoreCase("Mark")) {
//                        int seqID = Integer.parseInt( attributes.getValue("SeqID"));
//
//                        String name = attributes.getValue("Name");
//                        double targetLat = Double.parseDouble(attributes.getValue("TargetLat"));
//                        double targetLon = Double.parseDouble(attributes.getValue("TargetLng"));
//                        int sourceID = Integer.parseInt(attributes.getValue("SourceID"));
//                        System.out.println("Seq ID : " + seqID);
//                        System.out.println("Name : " + name);
//                        System.out.println("Target Lat : " + targetLat);
//                        System.out.println("Target Lon : " + targetLon);
//                        System.out.println("Source ID : " + sourceID);
//                        MarkData mark = new MarkData(seqID, name, targetLat, targetLon, sourceID);
////                        currentCompoundMark.getMarks().add(mark);
//                        raceData.getCourse().get(currentCompoundMark.getID()).getMarks().add(mark);
//
//
//                    } else if (qName.equalsIgnoreCase("Corner")) {
//                        int seqID = Integer.parseInt( attributes.getValue("SeqID"));
//                        int compoundMarkID = Integer.parseInt(attributes.getValue("CompoundMarkID"));
//                        String rounding = attributes.getValue("Rounding");
//                        int zoneSize = Integer.parseInt(attributes.getValue("ZoneSize"));
////                        System.out.println("Seq ID : " + seqID);
////                        System.out.println("Compound Mark ID : " + compoundMarkID);
////                        System.out.println("Rounding : " + rounding);
////                        System.out.println("Zone Size : " + zoneSize);
//                        CornerData corner = new CornerData(seqID, compoundMarkID, rounding, zoneSize);
//                        raceData.getCompoundMarkSequence().add(corner);
//
//                    } else if (qName.equalsIgnoreCase("Limit")) {
//                        int seqID = Integer.parseInt( attributes.getValue("SeqID"));
//                        double lat = Double.parseDouble(attributes.getValue("Lat"));
//                        double lon = Double.parseDouble(attributes.getValue("Lon"));
////                        System.out.println("Seq ID : " + seqID);
////                        System.out.println("Lat : " + lat);
////                        System.out.println("Lon : " + lon);
//                        LimitData limit = new LimitData(seqID, lat, lon);
//                        raceData.getCourseLimit().add(limit);
//
//                    }
//
//
//
//                }
//
//                @Override
//                public void endElement(String uri, String localName, String qName) throws SAXException {
//                    // we don't care about any end elements
//                }
//
//                @Override
//                public void characters(char ch[], int start, int length) throws SAXException {
//                    if (hasRaceId) {
//                        int raceID = Integer.parseInt(new String(ch, start, length));
////                        System.out.println("Race ID : " + raceID);
//                        hasRaceId = false;
//                        raceData.setRaceID(raceID);
//
//                    } else if (hasRaceType) {
//                        String raceType =  new String(ch, start, length);
////                        System.out.println("Race Type : " + raceType);
//                        hasRaceType = false;
//                        raceData.setRaceType(raceType);
//
//
//                    } else if (hasCreationTimeDate) {
//                        String creationTimeDate = new String(ch, start, length);
////                        System.out.println("Creation Time Date : " + creationTimeDate);
//                        hasCreationTimeDate = false;
//                        raceData.setCreationTimeDate(creationTimeDate);
//
//                    }
//                }
//
//
//            };
////            parseRace();
//            saxParser.parse(new InputSource(new StringReader(xmlStr)), handler);
//
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//    }

    private void parseRace() {
        parseBoundary();
    }

    private void parseBoundary() {

        //loop through the parsed boundary points
        for(LimitData limit: this.raceData.getCourseLimit()) {
            double lat = limit.getLat();
            double lon = limit.getLon();
            System.out.println(lat);
            System.out.println(lon);
        }
        // add all the points to coures boundary points
    }


    public List<MutablePoint> getCourseBoundaryPoints() {
        return courseBoundaryPoints;
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
