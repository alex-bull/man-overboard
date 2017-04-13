package seng302.Model;

import org.w3c.dom.Document;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
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
                boolean hasRaceStartTime = false;

                @Override
                public void startElement(String uri, String localName, String qName, Attributes attributes) {
                    if (qName.equalsIgnoreCase("RaceID")) {
                        hasRaceId = true;

                    } else if (qName.equalsIgnoreCase("RaceType")) {
                        hasRaceType = true;

                    } else if (qName.equalsIgnoreCase("CreationTimeDate")) {
                        hasCreationTimeDate = true;

                    } else if (qName.equalsIgnoreCase("RaceStartTime")) {
                        hasRaceStartTime = true;
                        System.out.println("Race Start Time : " + attributes.getValue("Time"));
                        System.out.println("Postponed? : " + attributes.getValue("Postpone"));

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

        RaceXMLParser p = new RaceXMLParser("<Race><RaceID>11080703</RaceID><RaceType>Match</RaceType><CreationTimeDate>2011-08-06T13:25:00-0000</CreationTimeDate ><RaceStartTime Time=\"2011-08-06T13:30:00-0700\" Postpone=\"false\" /></Race>");
    }

}
