package seng302.Model;


import javafx.beans.property.DoubleProperty;
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
public class XMLCourseLoader {
    File inputFile;
    Double screenX;
    Double screenY;

    public XMLCourseLoader(File inputFile, Double x, Double y) {
        this.inputFile = inputFile;
        this.screenX = x;
        this.screenY = y;
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
        ArrayList<CourseFeature> points = new ArrayList<>();

        for (Element feature : features) {

            String type = feature.getName();

            if (type.equals("gate")) { //its a gate

                List<Element> marks = feature.getChildren();
                Element markOne = marks.get(1);
                Element markTwo = marks.get(2);
                boolean isfinish = Boolean.valueOf(feature.getAttributeValue("isfinish"));
                String name = feature.getChildText("name");

                MutablePoint p1 = new MutablePoint((Double.parseDouble(markOne.getChildText("latitude")) -32) * 1000, (Double.parseDouble(markOne.getChildText("longtitude")) + 64) * 1000 + 1000);
                MutablePoint p2 = new MutablePoint((Double.parseDouble(markTwo.getChildText("latitude")) -32) * 1000, (Double.parseDouble(markTwo.getChildText("longtitude")) + 64) * 1000 + 1000);
                points.add(new Gate(name, p1, p2, isfinish));
                System.out.println(p1.getXValue());
                System.out.println(p1.getYValue());
            }
            else { //Its a mark

                Element mark = feature.getChildren().get(1);
                String name = feature.getChildText("name");
                MutablePoint p1 = new MutablePoint(Double.parseDouble(mark.getChildText("latitude")), Double.parseDouble(mark.getChildText("longtitude")));
                points.add(new Mark(name, p1, false));
            }

        }
        return points;
    }
}
