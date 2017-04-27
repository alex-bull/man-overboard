package seng302.Parsers;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import seng302.Model.Boat;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jar156 on 14/04/17.
 * Parser for Boat XML
 */
public class BoatXMLParser {
    private List<Boat> boats;
    private List<Boat> markBoats;

    /**
     *
     * @return List a list of boats
     */
    public List<Boat> getBoats(){
        return boats;
    }

    public List<Boat> getMarkBoats(){
        return markBoats;
    }

    public BoatXMLParser(String xmlStr) throws IOException, JDOMException {
        boats=new ArrayList<>();
        markBoats=new ArrayList<>();
        SAXBuilder builder = new SAXBuilder();
        InputStream stream = new ByteArrayInputStream(xmlStr.getBytes("UTF-8"));
        Document root= builder.build(stream);
        Element boatConfig = root.getRootElement();


        //we only care about boat data right now
        for(Element boat: boatConfig.getChild("Boats").getChildren()){
            // only need yacht data
            if(boat.getAttributeValue("Type").equals("Yacht")){
                Boat competitor=new Boat();
                competitor.setTeamName(boat.getAttributeValue("BoatName"));
                competitor.setAbbreName(boat.getAttributeValue("ShortName"));
                competitor.setSourceID(boat.getAttributeValue("SourceID"));
                competitor.setType("Yacht");
                System.out.println("Boat name : " + competitor.getTeamName());
                System.out.println("Abbre: " + competitor.getAbbreName());
                System.out.println("Source ID: " + competitor.getSourceID());
                boats.add(competitor);
            }
            //add to mark boats if type is mark
            if(boat.getAttributeValue("Type").equals("Mark")){
                Boat mark=new Boat();
                mark.setTeamName(boat.getAttributeValue("BoatName"));
                mark.setAbbreName(boat.getAttributeValue("ShortName"));
                mark.setSourceID(boat.getAttributeValue("SourceID"));
                mark.setType("Mark");

                System.out.println("Boatname: " + mark.getTeamName());
                System.out.println("Short name : " + mark.getAbbreName());
                System.out.println("Source id: " + mark.getSourceID());
                markBoats.add(mark);
            }

        }


//        try {
//            SAXParserFactory factory = SAXParserFactory.newInstance();
//            SAXParser saxParser = factory.newSAXParser();
//
//            DefaultHandler handler = new DefaultHandler() {
//
//                boolean hasModified = false;
//                boolean hasVersion = false;
//
//                @Override
//                public void startElement(String uri, String localName, String qName, Attributes attributes) {
//                    if (qName.equalsIgnoreCase("Modified")) {
//                        hasModified = true;
//
//                    } else if (qName.equalsIgnoreCase("Version")) {
//                        hasVersion = true;
//
//                    } else if (qName.equalsIgnoreCase("RaceBoatType")) {
////                        System.out.println("Type : " + attributes.getValue("Type"));
//
//                    } else if (qName.equalsIgnoreCase("BoatDimension")) {
////                        System.out.println("Boat Length : " + attributes.getValue("BoatLength"));
////                        System.out.println("Hull Length : " + attributes.getValue("HullLength"));
//
//                    } else if (qName.equalsIgnoreCase("ZoneSize")) {
////                        System.out.println("Mark Zone Size : " + attributes.getValue("MarkZoneSize"));
////                        System.out.println("Course Zone Size : " + attributes.getValue("CourseZoneSize"));
//
//                    } else if (qName.equalsIgnoreCase("ZoneLimits")) {
////                        System.out.println("Limit 1 : " + attributes.getValue("Limit1"));
////                        System.out.println("Limit 2 : " + attributes.getValue("Limit2"));
////                        System.out.println("Limit 3 : " + attributes.getValue("Limit3"));
////                        System.out.println("Limit 4 : " + attributes.getValue("Limit4"));
////                        System.out.println("Limit 5 : " + attributes.getValue("Limit5"));
//
//                    } else if (qName.equalsIgnoreCase("BoatShape")) {
////                        System.out.println("Shape ID : " + attributes.getValue("ShapeID"));
//
//                    } else if (qName.equalsIgnoreCase("Vtx")) {
////                        System.out.println("Seq : " + attributes.getValue("Seq"));
////                        System.out.println("Y : " + attributes.getValue("Y"));
////                        System.out.println("X : " + attributes.getValue("X"));
//
//
//                    } else if (qName.equalsIgnoreCase("Boat")) {
////                        System.out.println("Type : " + attributes.getValue("Type"));
////                        System.out.println("Source ID : " + attributes.getValue("SourceID"));
////                        System.out.println("Shape ID : " + attributes.getValue("ShapeID"));
////                        System.out.println("Hull Num : " + attributes.getValue("HullNum"));
////                        System.out.println("Stowe Name : " + attributes.getValue("StoweName"));
////                        System.out.println("Short Name : " + attributes.getValue("ShortName"));
////                        System.out.println("Boat Name : " + attributes.getValue("BoatName"));
////                        System.out.println("Country : " + attributes.getValue("Country"));
////                        System.out.println("Skipper : " + attributes.getValue("Skipper"));
//
//                    } else if (qName.equalsIgnoreCase("GPSposition")) {
////                        System.out.println("Z : " + attributes.getValue("Z"));
////                        System.out.println("Y : " + attributes.getValue("Y"));
////                        System.out.println("X : " + attributes.getValue("X"));
//
//                    } else if (qName.equalsIgnoreCase("FlagPosition")) {
////                        System.out.println("Z : " + attributes.getValue("Z"));
////                        System.out.println("Y : " + attributes.getValue("Y"));
////                        System.out.println("X : " + attributes.getValue("X"));
//
//                    } else if (qName.equalsIgnoreCase("MastTop")) {
////                        System.out.println("Z : " + attributes.getValue("Z"));
////                        System.out.println("Y : " + attributes.getValue("Y"));
////                        System.out.println("X : " + attributes.getValue("X"));
//                    }
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
//                    if (hasModified) {
////                        System.out.println("Modified : " + new String(ch, start, length));
//                        hasModified = false;
//
//                    } else if (hasVersion) {
////                        System.out.println("Version : " + new String(ch, start, length));
//                        hasVersion = false;
//
//                    }
//                }
//
//
//            };
//
//            saxParser.parse(new InputSource(new StringReader(xmlStr)), handler);
//
//        } catch (Exception e) {
////            e.printStackTrace();
//        }

    }


}
