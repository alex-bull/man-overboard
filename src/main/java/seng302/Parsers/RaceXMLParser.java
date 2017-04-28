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
 * Parser for Race XML
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
//            e.printStackTrace();
        }

    }

    public static void main(String[] args){

        RaceXMLParser p = new RaceXMLParser("<?xml version=\"1.0\"?>\n" +
                "<Race>\n" +
                "    <RaceID>123546789</RaceID>\n" +
                "\n" +
                "    <RaceType>Match</RaceType>\n" +
                "\n" +
                "    <CreationTimeDate>2011-08-06T13:25:00-0000</CreationTimeDate >\n" +
                "\n" +
                "    <RaceStartTime Time=\"2011-08-06T13:30:00-0700\" Postpone=\"false\" />\n" +
                "\n" +
                "    <Participants>\n" +
                "        <Yacht SourceID=\"101\" />\n" +
                "        <Yacht SourceID=\"102\" />\n" +
                "        <Yacht SourceID=\"103\" />\n" +
                "        <Yacht SourceID=\"104\" />\n" +
                "        <Yacht SourceID=\"105\" />\n" +
                "        <Yacht SourceID=\"106\" />\n" +
                "    </Participants>\n" +
                "\n" +
                "    <Course>\n" +
                "        <CompoundMark CompoundMarkID=\"1\" Name=\"Mark0\">\n" +
                "            <Mark SeqID=\"1\" Name=\"Start Line 1\" TargetLat=\"32.296577\" TargetLng=\"-64.854304\" SourceID=\"122\" />\n" +
                "            <Mark SeqID=\"2\" Name=\"Start Line 2\" TargetLat=\"32.293771\" TargetLng=\"-64.855242\" SourceID=\"123\" />\n" +
                "        </CompoundMark>\n" +
                "\n" +
                "        <CompoundMark CompoundMarkID=\"2\" Name=\"Mark1\">\n" +
                "            <Mark SeqID=\"1\" Name=\"Mark 1\" TargetLat=\"32.293039\" TargetLng=\"-64.843983\" SourceID=\"131\" />\n" +
                "        </CompoundMark>\n" +
                "\n" +
                "        <CompoundMark CompoundMarkID=\"3\" Name=\"Mark2\">\n" +
                "            <Mark SeqID=\"1\" Name=\"Lee Gate 1\" TargetLat=\"32.309693\" TargetLng=\"-64.835249\" SourceID=\"124\" />\n" +
                "            <Mark SeqID=\"2\" Name=\"Lee Gate 2\" TargetLat=\"32.308046\" TargetLng=\"-64.831785\" SourceID=\"125\" />\n" +
                "        </CompoundMark>\n" +
                "\n" +
                "        <CompoundMark CompoundMarkID=\"4\" Name=\"Mark3\">\n" +
                "            <Mark SeqID=\"1\" Name=\"Wind Gate 1\" TargetLat=\"32.284680\" TargetLng=\"-64.850045\" SourceID=\"126\" />\n" +
                "            <Mark SeqID=\"2\" Name=\"Wind Gate 2\" TargetLat=\"32.280164\" TargetLng=\"-64.847591\" SourceID=\"127\" />\n" +
                "        </CompoundMark>\n" +
                "\n" +
                "        <CompoundMark CompoundMarkID=\"5\" Name=\"Mark2\">\n" +
                "            <Mark SeqID=\"1\" Name=\"Lee Gate 1\" TargetLat=\"32.309693\" TargetLng=\"-64.835249\" SourceID=\"124\" />\n" +
                "            <Mark SeqID=\"2\" Name=\"Lee Gate 2\" TargetLat=\"32.308046\" TargetLng=\"-64.831785\" SourceID=\"125\" />\n" +
                "        </CompoundMark>\n" +
                "\n" +
                "        <CompoundMark CompoundMarkID=\"6\" Name=\"Mark4\" isfinish=\"true\">\n" +
                "            <Mark SeqID=\"1\" Name=\"Finish Line 1\" TargetLat=\"32.317379\" TargetLng=\"-64.839291\" SourceID=\"128\" />\n" +
                "            <Mark SeqID=\"2\" Name=\"Finish Line 2\" TargetLat=\"32.317257\" TargetLng=\"-64.836260\" SourceID=\"129\" />\n" +
                "        </CompoundMark>\n" +
                "\n" +
                "        <CompoundMarkSequence>\n" +
                "            <Corner SeqID=\"1\" CompoundMarkID=\"1\" Rounding=\"PS\" ZoneSize=\"3\" />\n" +
                "            <Corner SeqID=\"2\" CompoundMarkID=\"2\" Rounding=\"Port\" ZoneSize=\"3\" />\n" +
                "            <Corner SeqID=\"3\" CompoundMarkID=\"3\" Rounding=\"SP\" ZoneSize=\"3\" />\n" +
                "            <Corner SeqID=\"4\" CompoundMarkID=\"4\" Rounding=\"PS\" ZoneSize=\"3\" />\n" +
                "            <Corner SeqID=\"5\" CompoundMarkID=\"5\" Rounding=\"SP\" ZoneSize=\"3\" />\n" +
                "            <Corner SeqID=\"6\" CompoundMarkID=\"6\" Rounding=\"PS\" ZoneSize=\"3\" />\n" +
                "        </CompoundMarkSequence>\n" +
                "        <CourseLimit>\n" +
                "            <Limit SeqID=\"1\" Lat=\"32.3177476\" Lon=\"-64.8403001\" />\n" +
                "            <Limit SeqID=\"2\" Lat=\"32.3174575\" Lon=\"-64.8328543\" />\n" +
                "            <Limit SeqID=\"3\" Lat=\"32.3028767\" Lon=\"-64.8209667\" />\n" +
                "            <Limit SeqID=\"4\" Lat=\"32.2776994\" Lon=\"-64.8418236\" />\n" +
                "            <Limit SeqID=\"5\" Lat=\"32.2768286\" Lon=\"-64.8519516\" />\n" +
                "            <Limit SeqID=\"6\" Lat=\"32.2918489\" Lon=\"-64.8545266\" />\n" +
                "            <Limit SeqID=\"7\" Lat=\"32.2935902\" Lon=\"-64.8600197\" />\n" +
                "            <Limit SeqID=\"8\" Lat=\"32.299467\" Lon=\"-64.8581314\" />\n" +
                "            <Limit SeqID=\"9\" Lat=\"32.2982336\" Lon=\"-64.8480892\" />\n" +
                "            <Limit SeqID=\"10\" Lat=\"32.3122319\" Lon=\"-64.8378754\" />\n" +
                "        </CourseLimit>\n" +
                "    </Course>\n" +
                "</Race>");
    }

}
