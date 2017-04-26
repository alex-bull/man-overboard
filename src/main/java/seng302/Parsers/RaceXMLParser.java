package seng302.Parsers;

import com.sun.org.apache.xalan.internal.utils.XMLSecurityManager;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.StringReader;

/**
 * Created by jar156 on 13/04/17.
 * Parser for Race XML
 */
public class RaceXMLParser {

    private RaceData raceData;

    public RaceData getRaceData() {
        return raceData;
    }



    public RaceXMLParser(String xmlStr) {

        try {
            this.raceData = new RaceData();
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser saxParser = factory.newSAXParser();

            DefaultHandler handler = new DefaultHandler() {

                boolean hasRaceId = false;
                boolean hasRaceType = false;
                boolean hasCreationTimeDate = false;
                CompoundMarkData currentCompoundMark;

                @Override
                public void startElement(String uri, String localName, String qName, Attributes attributes) {
                    if (qName.equalsIgnoreCase("RaceID")) {
                        hasRaceId = true;

                    } else if (qName.equalsIgnoreCase("RaceType")) {
                        hasRaceType = true;

                    } else if (qName.equalsIgnoreCase("CreationTimeDate")) {
                        hasCreationTimeDate = true;

                    } else if (qName.equalsIgnoreCase("RaceStartTime")) {
                        String raceStartTime = attributes.getValue("Time");
//                        System.out.println("Race Start Time : " + attributes.getValue("Time"));
//                        System.out.println("Postponed? : " + attributes.getValue("Postpone"));
                        raceData.setRaceStartTime(raceStartTime);
                        raceData.setRaceStartTimePostpone(Boolean.parseBoolean(attributes.getValue("Postpone")));


                    } else if (qName.equalsIgnoreCase("Yacht")) {
                        int sourceID = Integer.parseInt(attributes.getValue("SourceID"));
                        String entry = attributes.getValue("Entry");
//                        System.out.println("Source ID : " + sourceID);
//                        System.out.println("Entry : " + entry);
                        YachtData yacht = new YachtData(sourceID, entry);
                        raceData.getParticipants().add(yacht);

                    } else if (qName.equalsIgnoreCase("CompoundMark")) {
                        int id = Integer.parseInt(attributes.getValue("CompoundMarkID"));
                        String name = attributes.getValue("Name");
//                        System.out.println("Compound Mark ID : " + id);
//                        System.out.println("Name : " + name);
                        currentCompoundMark = new CompoundMarkData(id, name);

                    } else if (qName.equalsIgnoreCase("Mark")) {
                        int seqID = Integer.parseInt( attributes.getValue("SeqID"));
                        String name = attributes.getValue("Name");
                        double targetLat = Double.parseDouble(attributes.getValue("TargetLat"));
                        double targetLon = Double.parseDouble(attributes.getValue("TargetLon"));
                        int sourceID = Integer.parseInt(attributes.getValue("SourceID"));
//                        System.out.println("Seq ID : " + seqID);
//                        System.out.println("Name : " + name);
//                        System.out.println("Target Lat : " + targetLat);
//                        System.out.println("Target Lon : " + targetLon);
//                        System.out.println("Source ID : " + sourceID);
                        MarkData mark = new MarkData(seqID, name, targetLat, targetLon, sourceID);
                        currentCompoundMark.getMarks().add(mark);

                    } else if (qName.equalsIgnoreCase("Corner")) {
                        int seqID = Integer.parseInt( attributes.getValue("SeqID"));
                        int compoundMarkID = Integer.parseInt(attributes.getValue("CompoundMarkID"));
                        String rounding = attributes.getValue("Rounding");
                        int zoneSize = Integer.parseInt(attributes.getValue("ZoneSize"));
//                        System.out.println("Seq ID : " + seqID);
//                        System.out.println("Compound Mark ID : " + compoundMarkID);
//                        System.out.println("Rounding : " + rounding);
//                        System.out.println("Zone Size : " + zoneSize);
                        CornerData corner = new CornerData(seqID, compoundMarkID, rounding, zoneSize);
                        raceData.getCompoundMarkSequence().add(corner);

                    } else if (qName.equalsIgnoreCase("Limit")) {
                        int seqID = Integer.parseInt( attributes.getValue("SeqID"));
                        double lat = Double.parseDouble(attributes.getValue("Lat"));
                        double lon = Double.parseDouble(attributes.getValue("Lon"));
//                        System.out.println("Seq ID : " + seqID);
//                        System.out.println("Lat : " + lat);
//                        System.out.println("Lon : " + lon);
                        LimitData limit = new LimitData(seqID, lat, lon);
                        raceData.getCourseLimit().add(limit);

                    }

                }

                @Override
                public void endElement(String uri, String localName, String qName) throws SAXException {
                    // we don't care about any end elements
                }

                @Override
                public void characters(char ch[], int start, int length) throws SAXException {
                    if (hasRaceId) {
                        int raceID = Integer.parseInt(new String(ch, start, length));
//                        System.out.println("Race ID : " + raceID);
                        hasRaceId = false;
                        raceData.setRaceID(raceID);

                    } else if (hasRaceType) {
                        String raceType =  new String(ch, start, length);
//                        System.out.println("Race Type : " + raceType);
                        hasRaceType = false;
                        raceData.setRaceType(raceType);


                    } else if (hasCreationTimeDate) {
                        String creationTimeDate = new String(ch, start, length);
//                        System.out.println("Creation Time Date : " + creationTimeDate);
                        hasCreationTimeDate = false;
                        raceData.setCreationTimeDate(creationTimeDate);

                    }
                }


            };

            saxParser.parse(new InputSource(new StringReader(xmlStr)), handler);

        } catch (Exception e) {
//            e.printStackTrace();
        }

    }



    public static void main(String[] args){

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
    }

}
