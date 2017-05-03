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
 * Parser for Regatta XML
 */
public class RegattaXMLParser {

    private String offsetUTC;

    /**
     * Parse the Regatta Data
     * @param xmlStr XML String of regatta data
     */
    public RegattaXMLParser(String xmlStr) {

        try {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser saxParser = factory.newSAXParser();

            DefaultHandler handler = new DefaultHandler() {

                boolean hasRegattaId = false;
                boolean hasRegattaName = false;
                boolean hasCourseName = false;
                boolean hasCentralLatitude = false;
                boolean hasCentralLongitude = false;
                boolean hasCentralAltitude = false;
                boolean hasUtcOffset = false;
                boolean hasMagneticVariation = false;


                @Override
                public void startElement(String uri, String localName, String qName, Attributes attributes) {
                    if (qName.equalsIgnoreCase("RegattaID")) {
                        hasRegattaId = true;

                    } else if (qName.equalsIgnoreCase("RegattaName")) {
                        hasRegattaName = true;

                    } else if (qName.equalsIgnoreCase("CourseName")) {
                        hasCourseName = true;

                    } else if (qName.equalsIgnoreCase("CentralLatitude")) {
                        hasCentralLatitude = true;

                    } else if (qName.equalsIgnoreCase("CentralLongitude")) {
                        hasCentralLongitude = true;

                    } else if (qName.equalsIgnoreCase("CentralAltitude")) {
                        hasCentralAltitude = true;

                    } else if (qName.equalsIgnoreCase("UtcOffset")) {
                        hasUtcOffset = true;

                    } else if (qName.equalsIgnoreCase("MagneticVariation")) {
                        hasMagneticVariation = true;

                    }

                }

                @Override
                public void endElement(String uri, String localName, String qName) throws SAXException {
                    // we don't care about any end elements
                }

                @Override
                public void characters(char ch[], int start, int length) throws SAXException {
                    if (hasRegattaId) {
//                        System.out.println("Regatta ID : " + new String(ch, start, length));
                        hasRegattaId = false;

                    } else if (hasRegattaName) {
//                        System.out.println("Regatta Name : " + new String(ch, start, length));
                        hasRegattaName = false;

                    } else if (hasCourseName) {
//                        System.out.println("Course Name : " + new String(ch, start, length));
                        hasCourseName = false;

                    } else if (hasCentralLatitude) {
//                        System.out.println("Central Latitude : " + new String(ch, start, length));
                        hasCentralLatitude = false;

                    } else if (hasCentralLongitude) {
//                        System.out.println("Central Longitude : " + new String(ch, start, length));
                        hasCentralLongitude = false;

                    } else if (hasCentralAltitude) {
//                        System.out.println("Central Altitude : " + new String(ch, start, length));
                        hasCentralAltitude = false;

                    } else if (hasUtcOffset) {
                        offsetUTC = new String(ch, start, length);
//                        System.out.println("UTC : " + offsetUTC);
                        hasUtcOffset = false;

                    } else if (hasMagneticVariation) {
//                        System.out.println("Magnetic Variation : " + new String(ch, start, length));
                        hasMagneticVariation = false;

                    }
                }


            };

            saxParser.parse(new InputSource(new StringReader(xmlStr)), handler);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public String getOffsetUTC() {
        return this.offsetUTC;
    }

    public static void main(String[] args){

        RegattaXMLParser p = new RegattaXMLParser("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                "\n" +
                "<RegattaConfig>\n" +
                "\n" +
                "<RegattaID>3</RegattaID>\n" +
                "\n" +
                "<RegattaName>New Zealand Test</RegattaName>\n" +
                "\n" +
                "<CourseName>North Head</CourseName>\n" +
                "\n" +
                "<CentralLatitude>-36.82791529</CentralLatitude>\n" +
                "\n" +
                "<CentralLongitude>174.81218919</CentralLongitude>\n" +
                "\n" +
                "<CentralAltitude>0.00</CentralAltitude>\n" +
                "\n" +
                "<UtcOffset>12</UtcOffset>\n" +
                "\n" +
                "<MagneticVariation>14.1</MagneticVariation>\n" +
                "\n" +
                "</RegattaConfig>");
    }
}
