package seng302.Model;


import javafx.beans.property.DoubleProperty;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

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
     *
     * @return List the list of course features
     * @throws JDOMException
     * @throws IOException
     */
    public ArrayList<CourseFeature> parseCourse(double width, double height) throws JDOMException, IOException {
        double bufferX=Math.min(20,width*0.1);
        double bufferY=Math.min(20,height*0.1);
        SAXBuilder saxbuilder = new SAXBuilder();
        Document document = saxbuilder.build(inputFile);
        Element raceCourse = document.getRootElement();
        List<Element> features = raceCourse.getChildren();
        ArrayList<CourseFeature> points = new ArrayList<>();

        System.out.println(height);
        System.out.println(width);

        ArrayList<Double> xCoords=new ArrayList<>();
        ArrayList<Double> yCoords=new ArrayList<>();

        for (Element feature : features) {

            String type = feature.getName();

            if (type.equals("gate")) { //its a gate

                List<Element> marks = feature.getChildren();
                Element markOne = marks.get(1);
                Element markTwo = marks.get(2);
                boolean isfinish = Boolean.valueOf(feature.getAttributeValue("isfinish"));
                String name = feature.getChildText("name");

                double point1X = Double.parseDouble(markOne.getChildText("latitude"));
                double point2X = Double.parseDouble(markTwo.getChildText("latitude"));
                double point1Y = Double.parseDouble(markOne.getChildText("longtitude"));
                double point2Y = Double.parseDouble(markTwo.getChildText("longtitude"));

                xCoords.add(point1X);
                xCoords.add(point2X);
                yCoords.add(point1Y);
                yCoords.add(point2Y);

                MutablePoint p1=new MutablePoint(point1X,point1Y);
                MutablePoint p2=new MutablePoint(point2X,point2Y);
                points.add(new Gate(name, p1, p2, isfinish));

            } else { //Its a mark

                Element mark = feature.getChildren().get(1);
                String name = feature.getChildText("name");

                double point1X = Double.parseDouble(mark.getChildText("latitude"));
                double point1Y = Double.parseDouble(mark.getChildText("longtitude"));

                xCoords.add(point1X);
                yCoords.add(point1Y);

                MutablePoint p1 = new MutablePoint(point1X,point1Y);
                points.add(new Mark(name, p1, false));
            }

        }

        double xFactor= (width-bufferX)/(Collections.max(xCoords)-Collections.min(xCoords));
        double yFactor=(height-bufferY)/(Collections.max(yCoords)-Collections.min(yCoords));


        System.out.println(width-bufferX);
        System.out.println(height-bufferY);
        points.stream().forEach(p->p.factor(xFactor,yFactor,Collections.min(xCoords),Collections.min(yCoords)));

        return points;
    }
}
