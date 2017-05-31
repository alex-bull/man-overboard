package parsers.xml;


import models.CourseFeature;
import models.Gate;
import models.Mark;
import models.MutablePoint;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static utility.Projection.mercatorProjection;

/**
 * Created by khe60 on 14/03/17.
 * An XML file parser for reading courses
 */
public class CourseXMLParser {
    private List<Double> xMercatorCoords = new ArrayList<>();
    private List<Double> yMercatorCoords = new ArrayList<>();
    private Document document;
    private Double scaleFactor;
    private Double bufferX;
    private Double bufferY;
    private ArrayList<CourseFeature> points = new ArrayList<>();

    /**
     * Constructor for loading a course with an XML input file
     * @param inputFile File a XML file with course features
     */
    public CourseXMLParser(InputStream inputFile) throws IOException, JDOMException {
        SAXBuilder saxbuilder = new SAXBuilder();
        document = saxbuilder.build(inputFile);
    }


    /**
     * Creates a list of course features read from an xml file
     * @return List the list of course features
     * @throws JDOMException JDOMException
     * @throws IOException IOException
     */
    public ArrayList<CourseFeature> parseCourse() throws JDOMException, IOException {
        double width = 1000;
        double height = 1000;
        //buffers are defined as the total buffer size, i.e. total for both sides
        bufferX = Math.max(150, width * 0.6);
        bufferY = Math.max(10, height * 0.1);

        Element raceCourse = document.getRootElement();
        List<Element> features = raceCourse.getChild("Course").getChildren();

        for (Element feature : features) {
            String type = feature.getName();
            if (type.equals("CompoundMark") && feature.getChildren().size() == 2) { //its a gate

                List<Element> marks = feature.getChildren();
                Element markOne = marks.get(0);
                Element markTwo = marks.get(1);
                boolean isFinish = Boolean.valueOf(feature.getAttributeValue("isFinish"));
                String name = feature.getAttribute("Name").getValue();

                double lat1 = Double.parseDouble(markOne.getAttribute("TargetLat").getValue());
                double lat2 = Double.parseDouble(markTwo.getAttribute("TargetLat").getValue());
                double lon1 = Double.parseDouble(markOne.getAttribute("TargetLng").getValue());
                double lon2 = Double.parseDouble(markTwo.getAttribute("TargetLng").getValue());
                int index = Integer.parseInt(feature.getAttributeValue("CompoundMarkID"));
                List<Double> point1 = mercatorProjection(lat1, lon1);
                List<Double> point2 = mercatorProjection(lat2, lon2);
                double point1X = point1.get(0);
                double point1Y = point1.get(1);
                double point2X = point2.get(0);
                double point2Y = point2.get(1);

                xMercatorCoords.add(point1X);
                xMercatorCoords.add(point2X);
                yMercatorCoords.add(point1Y);
                yMercatorCoords.add(point2Y);

                MutablePoint pixel1 = new MutablePoint(point1X, point1Y);
                MutablePoint pixel2 = new MutablePoint(point2X, point2Y);
                MutablePoint GPS1 = new MutablePoint(lat1, lon1);

                Gate gate = new Gate(name, GPS1, pixel1, pixel2, isFinish, true, index);
                points.add(gate);
            } else if (type.equals("CompoundMark") && feature.getChildren().size() == 1) { //Its a mark

                Element mark = feature.getChildren().get(0);
                String name = feature.getAttribute("Name").getValue();
                double lat1 = Double.parseDouble(mark.getAttribute("TargetLat").getValue());
                double lon1 = Double.parseDouble(mark.getAttribute("TargetLng").getValue());
                int index = Integer.parseInt(feature.getAttributeValue("CompoundMarkID"));
                List<Double> point1 = mercatorProjection(lat1, lon1);
                double point1X = point1.get(0);
                double point1Y = point1.get(1);
                xMercatorCoords.add(point1X);
                yMercatorCoords.add(point1Y);


                MutablePoint pixel = new MutablePoint(point1X, point1Y);
                MutablePoint GPS = new MutablePoint(lat1, lon1);
                Mark mark1 = new Mark(name, pixel, GPS, index);
                points.add(mark1);


            } else if (type.equals("CompoundMarkSequence")) {        //Additional information for course features
                List<Element> corners = feature.getChildren();
                for (Element corner : corners) {
                    for (CourseFeature courseFeature : points) {
                        if (corner.getAttributeValue("CompoundMarkID").equals(
                                String.valueOf(courseFeature.getIndex()))) {
                            String rounding = corner.getAttributeValue("Rounding");
                            courseFeature.setRounding(Integer.parseInt(rounding));

                        }
                    }

                }
            }
            else { //invalid course file
                throw new JDOMException();
            }
        }

        // scale to canvas size
        double xFactor = ((double) 1000 - bufferX) / (Collections.max(xMercatorCoords) - Collections.min(xMercatorCoords));
        double yFactor = ((double) 1000 - bufferY) / (Collections.max(yMercatorCoords) - Collections.min(yMercatorCoords));

        //make scaling in proportion
        scaleFactor = Math.min(xFactor, yFactor);

        //scale points to fit screen
        points.forEach(p -> p.factor(scaleFactor, scaleFactor, Collections.min(xMercatorCoords),
                Collections.min(yMercatorCoords), bufferX / 2, bufferY / 2));

        return points;

    }

}
