package parsers.xml.regatta;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;

/**
 * Created by jar156 on 14/04/17.
 * parsers for Regatta XML
 */
public class RegattaXMLParser {

    private String offsetUTC;
    private double centralLatitude;
    private double centralLongitude;


    /**
     * Parse the Regatta Data
     * @param xmlStr XML String of regatta data
     * @throws JDOMException JDOMException
     * @throws IOException IOException
     */
    public RegattaXMLParser(String xmlStr) throws JDOMException, IOException {

        SAXBuilder builder = new SAXBuilder();
        InputStream stream = new ByteArrayInputStream(xmlStr.getBytes("UTF-8"));
        Document root = builder.build(stream);
        Element regattaConfig = root.getRootElement();

//        int regattaID = Integer.parseInt(regattaConfig.getChild("RegattaID").getValue());
//        String regattaName = regattaConfig.getChild("RegattaName").getValue();
//        String courseName = regattaConfig.getChild("CourseName").getValue();
        this.centralLatitude = Double.parseDouble(regattaConfig.getChild("CentralLatitude").getValue());
        this.centralLongitude = Double.parseDouble(regattaConfig.getChild("CentralLongitude").getValue());
//        double centralAltitude = Double.parseDouble(regattaConfig.getChild("CentralAltitude").getValue());
        this.offsetUTC = regattaConfig.getChild("UtcOffset").getValue();
//        double magneticVariation = Double.parseDouble(regattaConfig.getChild("MagneticVariation").getValue());

    }

    public String getOffsetUTC() {
        return this.offsetUTC;
    }

    public double getCentralLatitude() {
        return centralLatitude;
    }

    public double getCentralLongitude() {
        return centralLongitude;
    }
}
