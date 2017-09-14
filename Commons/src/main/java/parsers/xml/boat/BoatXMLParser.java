package parsers.xml.boat;

import models.Boat;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by jar156 on 14/04/17.
 * parsers for Boat XML
 */
public class BoatXMLParser {
    private HashMap<Integer, Boat> boats;
    private List<Boat> markBoats;

    /**
     * Parse the XML string and set competitor and mark properties from given boat data
     *
     * @param xmlStr XML String of a boat data
     * @throws IOException   IOException
     * @throws JDOMException JDOMException
     */
    public BoatXMLParser(String xmlStr) throws IOException, JDOMException {
        boats = new HashMap<>();
        markBoats = new ArrayList<>();
        SAXBuilder builder = new SAXBuilder();
        InputStream stream = new ByteArrayInputStream(xmlStr.getBytes("UTF-8"));
        Document root = builder.build(stream);
        Element boatConfig = root.getRootElement();


        //we only care about boat data right now
        for (Element boat : boatConfig.getChild("Boats").getChildren()) {

            // only need yacht data
            if (boat.getAttributeValue("Type").equals("Yacht")) {
                Boat competitor = new Boat();
                int sourceID = Integer.parseInt(boat.getAttributeValue("SourceID"));
                competitor.setTeamName(boat.getAttributeValue("BoatName"));
                competitor.setAbbreName(boat.getAttributeValue("ShortName"));
                competitor.setSourceID(sourceID);
                competitor.setBoatType(Integer.parseInt(boat.getAttributeValue("Model")));
                boats.put(sourceID, competitor);
            }
            //add to mark boats if type is mark
            if (boat.getAttributeValue("Type").equals("Mark")) {
                Boat mark = new Boat();
                mark.setTeamName(boat.getAttributeValue("BoatName"));
                mark.setAbbreName(boat.getAttributeValue("ShortName"));
                mark.setSourceID(boat.getAttributeValue("SourceID"));
                markBoats.add(mark);
            }
        }
    }

    public HashMap<Integer, Boat> getBoats() {
        return boats;
    }

    public List<Boat> getMarkBoats() {
        return markBoats;
    }
}
