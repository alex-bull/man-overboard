package seng302.Model;


import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by khe60 on 14/03/17.
 * An XML file parser for reading courses
 */
public class XMLParser {
    File inputFile;

    public XMLParser(File inputFile) {
        this.inputFile = inputFile;
    }

    /**
     * Creates a list of course features read from an xml file
     * @return List the list of course features
     * @throws JDOMException
     * @throws IOException
     */
    public ArrayList<CourseFeature> parseCourse() throws JDOMException, IOException {

        SAXBuilder saxbuilder = new SAXBuilder();
        Document document = saxbuilder.build(inputFile);
        Element raceCourse = document.getRootElement();
        List<Element> features = raceCourse.getChildren();
        ArrayList<CourseFeature> points = new ArrayList<CourseFeature>();

        for (Element feature : features) {

            String type = feature.getName();

            if (type.equals("gate")) { //its a gate

                List<Element> marks = feature.getChildren();
                Element markOne = marks.get(1);
                Element markTwo = marks.get(2);
                boolean isfinish = Boolean.valueOf(feature.getAttributeValue("isfinish"));
                String name = feature.getChildText("name");
                Point p1 = new Point(Double.parseDouble(markOne.getChildText("latitude")), Double.parseDouble(markOne.getChildText("longtitude")));
                Point p2 = new Point(Double.parseDouble(markTwo.getChildText("latitude")), Double.parseDouble(markTwo.getChildText("longtitude")));
                points.add(new Gate(name, p1, p2, isfinish));
            }
            else { //Its a mark

                Element mark = feature.getChildren().get(1);
                String name = feature.getChildText("name");
                Point p1 = new Point(Double.parseDouble(mark.getChildText("latitude")), Double.parseDouble(mark.getChildText("longtitude")));
                points.add(new Mark(name, p1, false));
            }

        }
        return points;
    }
}
