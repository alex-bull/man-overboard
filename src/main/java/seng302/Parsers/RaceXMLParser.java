package seng302.Parsers;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.StringReader;

/**
 * Created by jar156 on 13/04/17.
 */
public class RaceXMLParser {

    public RaceXMLParser(String xmlStr) {

        try {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser saxParser = factory.newSAXParser();

            DefaultHandler handler = new DefaultHandler() {

                boolean hasRaceId = false;
                boolean hasRaceType = false;
                boolean hasCreationTimeDate = false;

                @Override
                public void startElement(String uri, String localName, String qName, Attributes attributes) {
                    if (qName.equalsIgnoreCase("RaceID")) {
                        hasRaceId = true;

                    } else if (qName.equalsIgnoreCase("RaceType")) {
                        hasRaceType = true;

                    } else if (qName.equalsIgnoreCase("CreationTimeDate")) {
                        hasCreationTimeDate = true;

                    } else if (qName.equalsIgnoreCase("RaceStartTime")) {
                        System.out.println("Race Start Time : " + attributes.getValue("Time"));
                        System.out.println("Postponed? : " + attributes.getValue("Postpone"));


                    } else if (qName.equalsIgnoreCase("Yacht")) {
                        System.out.println("Source ID : " + attributes.getValue("SourceID"));
                        System.out.println("Entry : " + attributes.getValue("Entry"));

                    } else if (qName.equalsIgnoreCase("CompoundMark")) {
                        System.out.println("Compound Mark ID : " + attributes.getValue("CompoundMarkID"));
                        System.out.println("Name : " + attributes.getValue("Name"));

                    } else if (qName.equalsIgnoreCase("Mark")) {
                        System.out.println("Seq ID : " + attributes.getValue("SeqID"));
                        System.out.println("Name : " + attributes.getValue("Name"));
                        System.out.println("Target Lat : " + attributes.getValue("TargetLat"));
                        System.out.println("Target Lon : " + attributes.getValue("TargetLng"));
                        System.out.println("Source ID : " + attributes.getValue("SourceID"));

                    } else if (qName.equalsIgnoreCase("Corner")) {
                        System.out.println("Seq ID : " + attributes.getValue("SeqID"));
                        System.out.println("Compound Mark ID : " + attributes.getValue("CompoundMarkID"));
                        System.out.println("Rounding : " + attributes.getValue("Rounding"));
                        System.out.println("Zone Size : " + attributes.getValue("ZoneSize"));

                    } else if (qName.equalsIgnoreCase("Limit")) {
                        System.out.println("Seq ID : " + attributes.getValue("SeqID"));
                        System.out.println("Lat : " + attributes.getValue("Lat"));
                        System.out.println("Lon : " + attributes.getValue("Lon"));

                    }

                }

                @Override
                public void endElement(String uri, String localName, String qName) throws SAXException {
                    // we don't care about any end elements
                }

                @Override
                public void characters(char ch[], int start, int length) throws SAXException {
                    if (hasRaceId) {
                        System.out.println("Race ID : " + new String(ch, start, length));
                        hasRaceId = false;

                    } else if (hasRaceType) {
                        System.out.println("Race Type : " + new String(ch, start, length));
                        hasRaceType = false;

                    } else if (hasCreationTimeDate) {
                        System.out.println("Creation Time Date : " + new String(ch, start, length));
                        hasCreationTimeDate = false;

                    }
                }


            };

            saxParser.parse(new InputSource(new StringReader(xmlStr)), handler);

        } catch (Exception e) {
            e.printStackTrace();
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
