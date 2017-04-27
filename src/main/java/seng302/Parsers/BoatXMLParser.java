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
//                System.out.println("Boat name : " + competitor.getTeamName());
//                System.out.println("Abbre: " + competitor.getAbbreName());
//                System.out.println("Source ID: " + competitor.getSourceID());
                boats.add(competitor);
            }
            //add to mark boats if type is mark
            if(boat.getAttributeValue("Type").equals("Mark")){
                Boat mark=new Boat();
                mark.setTeamName(boat.getAttributeValue("BoatName"));
                mark.setAbbreName(boat.getAttributeValue("ShortName"));
                mark.setSourceID(boat.getAttributeValue("SourceID"));
                mark.setType("Mark");

//                System.out.println("Boatname: " + mark.getTeamName());
//                System.out.println("Short name : " + mark.getAbbreName());
//                System.out.println("Source id: " + mark.getSourceID());
                markBoats.add(mark);
            }

        }




    }


}
