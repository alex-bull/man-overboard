package seng302.Parsers;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.StringReader;

/**
 * Created by jar156 on 14/04/17.
 * Parser for Boat XML
 */
public class BoatXMLParser {

    public BoatXMLParser(String xmlStr) {

        try {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser saxParser = factory.newSAXParser();

            DefaultHandler handler = new DefaultHandler() {

                boolean hasModified = false;
                boolean hasVersion = false;

                @Override
                public void startElement(String uri, String localName, String qName, Attributes attributes) {
                    if (qName.equalsIgnoreCase("Modified")) {
                        hasModified = true;

                    } else if (qName.equalsIgnoreCase("Version")) {
                        hasVersion = true;

                    } else if (qName.equalsIgnoreCase("RaceBoatType")) {
//                        System.out.println("Type : " + attributes.getValue("Type"));

                    } else if (qName.equalsIgnoreCase("BoatDimension")) {
//                        System.out.println("Boat Length : " + attributes.getValue("BoatLength"));
//                        System.out.println("Hull Length : " + attributes.getValue("HullLength"));

                    } else if (qName.equalsIgnoreCase("ZoneSize")) {
//                        System.out.println("Mark Zone Size : " + attributes.getValue("MarkZoneSize"));
//                        System.out.println("Course Zone Size : " + attributes.getValue("CourseZoneSize"));

                    } else if (qName.equalsIgnoreCase("ZoneLimits")) {
//                        System.out.println("Limit 1 : " + attributes.getValue("Limit1"));
//                        System.out.println("Limit 2 : " + attributes.getValue("Limit2"));
//                        System.out.println("Limit 3 : " + attributes.getValue("Limit3"));
//                        System.out.println("Limit 4 : " + attributes.getValue("Limit4"));
//                        System.out.println("Limit 5 : " + attributes.getValue("Limit5"));

                    } else if (qName.equalsIgnoreCase("BoatShape")) {
//                        System.out.println("Shape ID : " + attributes.getValue("ShapeID"));

                    } else if (qName.equalsIgnoreCase("Vtx")) {
//                        System.out.println("Seq : " + attributes.getValue("Seq"));
//                        System.out.println("Y : " + attributes.getValue("Y"));
//                        System.out.println("X : " + attributes.getValue("X"));


                    } else if (qName.equalsIgnoreCase("Boat")) {
//                        System.out.println("Type : " + attributes.getValue("Type"));
//                        System.out.println("Source ID : " + attributes.getValue("SourceID"));
//                        System.out.println("Shape ID : " + attributes.getValue("ShapeID"));
//                        System.out.println("Hull Num : " + attributes.getValue("HullNum"));
//                        System.out.println("Stowe Name : " + attributes.getValue("StoweName"));
//                        System.out.println("Short Name : " + attributes.getValue("ShortName"));
//                        System.out.println("Boat Name : " + attributes.getValue("BoatName"));
//                        System.out.println("Country : " + attributes.getValue("Country"));
//                        System.out.println("Skipper : " + attributes.getValue("Skipper"));

                    } else if (qName.equalsIgnoreCase("GPSposition")) {
//                        System.out.println("Z : " + attributes.getValue("Z"));
//                        System.out.println("Y : " + attributes.getValue("Y"));
//                        System.out.println("X : " + attributes.getValue("X"));

                    } else if (qName.equalsIgnoreCase("FlagPosition")) {
//                        System.out.println("Z : " + attributes.getValue("Z"));
//                        System.out.println("Y : " + attributes.getValue("Y"));
//                        System.out.println("X : " + attributes.getValue("X"));

                    } else if (qName.equalsIgnoreCase("MastTop")) {
//                        System.out.println("Z : " + attributes.getValue("Z"));
//                        System.out.println("Y : " + attributes.getValue("Y"));
//                        System.out.println("X : " + attributes.getValue("X"));
                    }

                }

                @Override
                public void endElement(String uri, String localName, String qName) throws SAXException {
                    // we don't care about any end elements
                }

                @Override
                public void characters(char ch[], int start, int length) throws SAXException {
                    if (hasModified) {
//                        System.out.println("Modified : " + new String(ch, start, length));
                        hasModified = false;

                    } else if (hasVersion) {
//                        System.out.println("Version : " + new String(ch, start, length));
                        hasVersion = false;

                    }
                }


            };

            saxParser.parse(new InputSource(new StringReader(xmlStr)), handler);

        } catch (Exception e) {
//            e.printStackTrace();
        }

    }




    public static void main(String[] args){

        BoatXMLParser p = new BoatXMLParser("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                "\n" +
                "<BoatConfig>\n" +
                "\n" +
                "<Modified>2012-05-17T07:49:40+0200</Modified>\n" +
                "\n" +
                "<Version>12</Version>\n" +
                "\n" +
                "<Settings>\n" +
                "\n" +
                "<RaceBoatType Type=\"AC45\" />\n" +
                "\n" +
                "<BoatDimension BoatLength=\"14.019\" HullLength=\"13.449\" />\n" +
                "\n" +
                "<ZoneSize MarkZoneSize=\"40.347\" CourseZoneSize=\"40.347\" />\n" +
                "\n" +
                "<ZoneLimits Limit1=\"200\" Limit2=\"100\" Limit3=\"40.347\" Limit4=\"0\" Limit5=\"-100\" />\n" +
                "\n" +
                "</Settings>\n" +
                "\n" +
                "<BoatShapes>\n" +
                "\n" +
                "<BoatShape ShapeID=\"0\">\n" +
                "\n" +
                "<Vertices>\n" +
                "\n" +
                "<Vtx Seq=\"1\" Y=\"0\" X=\"-2.659\" />\n" +
                "\n" +
                "<Vtx Seq=\"2\" Y=\"18.359\" X=\"-2.659\" />\n" +
                "\n" +
                "<Vtx Seq=\"3\" Y=\"18.359\" X=\"2.659\" />\n" +
                "\n" +
                "<Vtx Seq=\"4\" Y=\"0\" X=\"2.659\" />\n" +
                "\n" +
                "</Vertices>\n" +
                "\n" +
                "</BoatShape>\n" +
                "\n" +
                "<BoatShape ShapeID=\"1\">\n" +
                "\n" +
                "<Vertices>\n" +
                "\n" +
                "<Vtx Seq=\"1\" Y=\"0\" X=\"-1.278\" />\n" +
                "\n" +
                "<Vtx Seq=\"2\" Y=\"8.876\" X=\"-1.278\" />\n" +
                "\n" +
                "<Vtx Seq=\"3\" Y=\"8.876\" X=\"1.278\" />\n" +
                "\n" +
                "<Vtx Seq=\"4\" Y=\"0\" X=\"1.278\" />\n" +
                "\n" +
                "</Vertices>\n" +
                "\n" +
                "</BoatShape>\n" +
                "\n" +
                "<BoatShape ShapeID=\"2\">\n" +
                "\n" +
                "<Vertices>\n" +
                "\n" +
                "<Vtx Seq=\"1\" Y=\"0\" X=\"-1.1\" />\n" +
                "\n" +
                "<Vtx Seq=\"2\" Y=\"8.3\" X=\"-1.1\" />\n" +
                "\n" +
                "<Vtx Seq=\"3\" Y=\"8.3\" X=\"1.1\" />\n" +
                "\n" +
                "<Vtx Seq=\"4\" Y=\"0\" X=\"1.1\" />\n" +
                "\n" +
                "</Vertices>\n" +
                "\n" +
                "</BoatShape>\n" +
                "\n" +
                "<BoatShape ShapeID=\"3\">\n" +
                "\n" +
                "<Vertices>\n" +
                "\n" +
                "<Vtx Seq=\"1\" Y=\"0\" X=\"-0.75\" />\n" +
                "\n" +
                "<Vtx Seq=\"2\" Y=\"3\" X=\"-0.75\" />\n" +
                "\n" +
                "<Vtx Seq=\"3\" Y=\"3\" X=\"0.75\" />\n" +
                "\n" +
                "<Vtx Seq=\"4\" Y=\"0\" X=\"0.75\" />\n" +
                "\n" +
                "</Vertices>\n" +
                "\n" +
                "</BoatShape>\n" +
                "\n" +
                "<BoatShape ShapeID=\"4\">\n" +
                "\n" +
                "<Vertices>\n" +
                "\n" +
                "<Vtx Seq=\"1\" Y=\"0\" X=\"-3.46\" />\n" +
                "\n" +
                "<Vtx Seq=\"2\" Y=\"13.449\" X=\"-3.46\" />\n" +
                "\n" +
                "<Vtx Seq=\"3\" Y=\"14.019\" X=\"0\" />\n" +
                "\n" +
                "<Vtx Seq=\"4\" Y=\"13.449\" X=\"3.46\" />\n" +
                "\n" +
                "<Vtx Seq=\"5\" Y=\"0\" X=\"3.46\" />\n" +
                "\n" +
                "</Vertices>\n" +
                "\n" +
                "<Catamaran>\n" +
                "\n" +
                "<Vtx Seq=\"1\" Y=\"1.769\" X=\"-2.752\" /><Vtx Seq=\"2\" Y=\"0\" X=\"-2.813\" />\n" +
                "\n" +
                "<Vtx Seq=\"3\" Y=\"0\" X=\"-3.34\" />\n" +
                "\n" +
                "<Vtx Seq=\"4\" Y=\"5.351\" X=\"-3.46\" />\n" +
                "\n" +
                "<Vtx Seq=\"5\" Y=\"10.544\" X=\"-3.387\" />\n" +
                "\n" +
                "<Vtx Seq=\"6\" Y=\"13.449\" X=\"-3.075\" />\n" +
                "\n" +
                "<Vtx Seq=\"7\" Y=\"10.851\" X=\"-2.793\" />\n" +
                "\n" +
                "<Vtx Seq=\"8\" Y=\"6.669\" X=\"-2.699\" />\n" +
                "\n" +
                "<Vtx Seq=\"9\" Y=\"6.669\" X=\"2.699\" />\n" +
                "\n" +
                "<Vtx Seq=\"10\" Y=\"10.851\" X=\"2.793\" />\n" +
                "\n" +
                "<Vtx Seq=\"11\" Y=\"13.449\" X=\"3.075\" />\n" +
                "\n" +
                "<Vtx Seq=\"12\" Y=\"10.544\" X=\"3.387\" />\n" +
                "\n" +
                "<Vtx Seq=\"13\" Y=\"5.351\" X=\"3.46\" />\n" +
                "\n" +
                "<Vtx Seq=\"14\" Y=\"0\" X=\"3.34\" />\n" +
                "\n" +
                "<Vtx Seq=\"15\" Y=\"0\" X=\"2.813\" />\n" +
                "\n" +
                "<Vtx Seq=\"16\" Y=\"1.769\" X=\"2.752\" />\n" +
                "\n" +
                "</Catamaran>\n" +
                "\n" +
                "<Bowsprit>\n" +
                "\n" +
                "<Vtx Seq=\"1\" Y=\"6.669\" X=\"-0.2\" />\n" +
                "\n" +
                "<Vtx Seq=\"2\" Y=\"11.377\" X=\"-0.2\" />\n" +
                "\n" +
                "<Vtx Seq=\"3\" Y=\"14.019\" X=\"0\" />\n" +
                "\n" +
                "<Vtx Seq=\"4\" Y=\"11.377\" X=\"0.2\" />\n" +
                "\n" +
                "<Vtx Seq=\"5\" Y=\"6.669\" X=\"0.2\" />\n" +
                "\n" +
                "</Bowsprit>\n" +
                "\n" +
                "<Trampoline>\n" +
                "\n" +
                "<Vtx Seq=\"1\" Y=\"2\" X=\"-2.699\" />\n" +
                "\n" +
                "<Vtx Seq=\"2\" Y=\"6.438\" X=\"-2.699\" />\n" +
                "\n" +
                "<Vtx Seq=\"3\" Y=\"6.438\" X=\"2.699\" />\n" +
                "\n" +
                "<Vtx Seq=\"4\" Y=\"2\" X=\"2.699\" />\n" +
                "\n" +
                "</Trampoline>\n" +
                "\n" +
                "</BoatShape>\n" +
                "\n" +
                "<BoatShape ShapeID=\"5\" />\n" +
                "\n" +
                "</BoatShapes>\n" +
                "\n" +
                "<Boats>\n" +
                "\n" +
                "<Boat Type=\"RC\" SourceID=\"121\" ShapeID=\"0\" HullNum=\"RG01\" StoweName=\"PRO\" ShortName=\"PRO\"\n" +
                "\n" +
                "BoatName=\"Regardless\">\n" +
                "\n" +
                "<GPSposition Z=\"6.840\" Y=\"7.800\" X=\"0.000\" />\n" +
                "\n" +
                "<FlagPosition Z=\"0.000\" Y=\"7.800\" X=\"0.000\" />\n" +
                "\n" +
                "</Boat>\n" +
                "\n" +
                "<Boat Type=\"Mark\" SourceID=\"122\" ShapeID=\"1\" HullNum=\"LC05\" StoweName=\"CON\" ShortName=\"Constellation\"\n" +
                "\n" +
                "BoatName=\"Constellation\">\n" +
                "\n" +
                "<GPSposition Z=\"5.334\" Y=\"3.804\" X=\"0.000\" />\n" +
                "\n" +
                "<FlagPosition Z=\"0.000\" Y=\"3.426\" X=\"0.000\" />\n" +
                "\n" +
                "</Boat>\n" +
                "\n" +
                "<Boat Type=\"Mark\" SourceID=\"123\" ShapeID=\"1\" HullNum=\"LC04\" StoweName=\"MIS\" ShortName=\"Mischief\"\n" +
                "\n" +
                "BoatName=\"Mischief\">\n" +
                "\n" +
                "<GPSposition Z=\"5.334\" Y=\"3.804\" X=\"0.000\" />\n" +
                "\n" +
                "<FlagPosition Z=\"0.000\" Y=\"3.426\" X=\"0.000\" />\n" +
                "\n" +
                "</Boat>\n" +
                "\n" +
                "<Boat Type=\"Mark\" SourceID=\"124\" ShapeID=\"1\" HullNum=\"LC03\" ShortName=\"Atalanta\" BoatName=\"Atalanta\">\n" +
                "\n" +
                "<GPSposition Z=\"5.334\" Y=\"3.804\" X=\"0.000\" />\n" +
                "\n" +
                "<FlagPosition Z=\"0.000\" Y=\"3.426\" X=\"0.000\" />\n" +
                "\n" +
                "</Boat>\n" +
                "\n" +
                "<Boat SourceID=\"125\" ShapeID=\"1\" StoweName=\"VOL\" HullNum=\"LC01\" ShortName=\"Volunteer\"\n" +
                "\n" +
                "BoatName=\"Volunteer\">\n" +
                "\n" +
                "<GPSposition Z=\"5.334\" Y=\"3.804\" X=\"0.000\" />\n" +
                "\n" +
                "<FlagPosition Z=\"0.000\" Y=\"3.426\" X=\"0.000\" />\n" +
                "\n" +
                "</Boat>\n" +
                "\n" +
                "<Boat Type=\"Mark\" SourceID=\"126\" ShapeID=\"1\" HullNum=\"LC13\" StoweName=\"MS2\" ShortName=\"Defender\"\n" +
                "\n" +
                "BoatName=\"Defender\">\n" +
                "\n" +
                "<GPSposition Z=\"5.334\" Y=\"3.804\" X=\"0.000\" />\n" +
                "\n" +
                "<FlagPosition Z=\"0.000\" Y=\"3.426\" X=\"0.000\" />\n" +
                "\n" +
                "</Boat>\n" +
                "\n" +
                "<Boat Type=\"Mark\" SourceID=\"128\" ShapeID=\"1\" HullNum=\"LC01\" ShortName=\"Shamrock\" BoatName=\"Shamrock\">\n" +
                "\n" +
                "<GPSposition Z=\"5.334\" Y=\"3.804\" X=\"0.000\" />\n" +
                "\n" +
                "<FlagPosition Z=\"0.000\" Y=\"3.426\" X=\"0.000\" />\n" +
                "\n" +
                "</Boat>\n" +
                "\n" +
                "<Boat Type=\"Yacht\" SourceID=\"101\" ShapeID=\"4\" HullNum=\"AC4501\" StoweName=\"KOR\" ShortName=\"TEAM KOREA\"\n" +
                "\n" +
                "BoatName=\"TEAM KOREA\" Country=\"KOR\">\n" +
                "\n" +
                "<GPSposition Z=\"1.738\" Y=\"0.625\" X=\"0.001\" />\n" +
                "\n" +
                "<MastTop Z=\"21.496\" Y=\"4.233\" X=\"0.000\" /></Boat>\n" +
                "\n" +
                "</Boats>\n" +
                "\n" +
                "</BoatConfig>");
    }

}
