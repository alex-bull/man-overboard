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
 */
public class XMLParser {
    File inputFile;

    public XMLParser(File inputFile) {
        this.inputFile = inputFile;
    }

    public ArrayList<CourseFeature> parseCourse() throws JDOMException, IOException {
        SAXBuilder saxbuilder = new SAXBuilder();
        Document document = saxbuilder.build(inputFile);
        Element raceCourse = document.getRootElement();
        List<Element> compoundMarks = raceCourse.getChildren();
        ArrayList<CourseFeature> points = new ArrayList<CourseFeature>();
        for (Element mark : compoundMarks) {
            boolean isfinish = Boolean.valueOf(mark.getAttributeValue("isfinish"));
            String name = mark.getChildText("name");
            double x = Double.parseDouble(mark.getChildText("latitude"));
            double y = Double.parseDouble(mark.getChildText("longtitude"));

            points.add(new Mark(name, new Point(x, y), isfinish));

        }
        return points;
    }
}
