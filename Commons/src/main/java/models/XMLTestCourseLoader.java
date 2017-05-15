package models;


import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by khe60 on 14/03/17.
 * An XML file parser for reading courses
 */
public class XMLTestCourseLoader {
    List<Double> xMercatorCoords = new ArrayList<>();
    List<Double> yMercatorCoords = new ArrayList<>();
    private Document document;
    private ArrayList<Gate> winds = new ArrayList<>();
    private Double scaleFactor;
    private Double bufferX;
    private Double bufferY;
    private ArrayList<CourseFeature> points = new ArrayList<>();

    /**
     * Constructor for loading a course with an XML input file
     *
     * @param inputFile File a XML file with course features
     */
    public XMLTestCourseLoader(InputStream inputFile) throws IOException, JDOMException {
        SAXBuilder saxbuilder = new SAXBuilder();

        document = saxbuilder.build(inputFile);
    }

    /**
     * Gets the wind direction
     *
     * @return double the angle of the wind direction
     */
    public double getWindDirection() {
        double x1 = winds.get(0).getPixelLocations().get(0).getXValue();
        double y1 = winds.get(0).getPixelLocations().get(0).getYValue();
        double x2 = winds.get(1).getPixelLocations().get(0).getXValue();
        double y2 = winds.get(1).getPixelLocations().get(0).getYValue();
        return Math.toDegrees(Math.atan((x1 - x2) / -(y1 - y2)));
    }

    /**
     * Function to map latitude and longitude to screen coordinates
     *
     * @param lat    latitude
     * @param lon    longitude
     * @param width  width of the screen
     * @param height height of the screen
     * @return ArrayList the coordinates in metres
     */
    private ArrayList<Double> mercatorProjection(double lat, double lon, double width, double height) {
        ArrayList<Double> ret = new ArrayList<>();
        double x = (lon + 180) * (width / 360);
        double latRad = lat * Math.PI / 180;
        double merc = Math.log(Math.tan((Math.PI / 4) + (latRad / 2)));
        double y = (height / 2) - (width * merc / (2 * Math.PI));
        ret.add(x);
        ret.add(y);
        return ret;

    }

    public List<MutablePoint> parseCourseBoundary(double width, double height) throws JDOMException, IOException {



        Element raceCourse = document.getRootElement();
        List<Element> features = raceCourse.getChild("Course").getChildren();

        List<MutablePoint> boundary = new ArrayList<>();

        for (Element feature : features) {

            String type = feature.getName();
            if (type.equals("CourseLimit")) {
                List<Element> points = feature.getChildren();

                for (Element point : points) {
                    double lat = Double.parseDouble(point.getAttribute("Lat").getValue());
                    double lon = Double.parseDouble(point.getAttribute("Lon").getValue());

                    ArrayList<Double> point1 = mercatorProjection(lat, lon, width, height);
                    double point1X = point1.get(0);
                    double point1Y = point1.get(1);
                    xMercatorCoords.add(point1X);
                    yMercatorCoords.add(point1Y);

                    MutablePoint pixel = new MutablePoint(point1X, point1Y);
                    boundary.add(pixel);

                }
            }
        }
        boundary.stream().forEach(p -> p.factor(scaleFactor, scaleFactor, Collections.min(xMercatorCoords), Collections.min(yMercatorCoords), bufferX / 2, bufferY / 2));
        return boundary;
    }

    /**
     * Creates a list of course features read from an xml file
     *
     * @param width  double the width of the screen
     * @param height double the height of the screen
     * @return List the list of course features
     * @throws JDOMException
     * @throws IOException
     */
    public ArrayList<CourseFeature> parseCourse(double width, double height) throws JDOMException, IOException {
        //buffers are defined as the total buffer size, i.e. total for both sides
        bufferX = Math.max(150, width * 0.6);
        bufferY = Math.max(10, height * 0.1);


        Element raceCourse = document.getRootElement();
//        System.out.println(raceCourse);
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


                ArrayList<Double> point1 = mercatorProjection(lat1, lon1, width, height);
                ArrayList<Double> point2 = mercatorProjection(lat2, lon2, width, height);
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
                MutablePoint GPS2 = new MutablePoint(lat2, lon2);


                Gate gate = new Gate(name, GPS1, GPS2, pixel1, pixel2, isFinish, true, index);
                points.add(gate);


                if (markOne.getAttribute("Name").getValue().contains("Lee") ||
                        markOne.getAttribute("Name").getValue().contains("Wind")) {
                    winds.add(gate);

                } else if (markTwo.getAttribute("Name").getValue().contains("Lee") ||
                        markTwo.getAttribute("Name").getValue().contains("Wind")) {
                    winds.add(gate);

                }
            } else if (type.equals("CompoundMark") && feature.getChildren().size() == 1) { //Its a mark

                Element mark = feature.getChildren().get(0);
                String name = feature.getAttribute("Name").getValue();
                double lat1 = Double.parseDouble(mark.getAttribute("TargetLat").getValue());
                double lon1 = Double.parseDouble(mark.getAttribute("TargetLng").getValue());
                int index = Integer.parseInt(feature.getAttributeValue("CompoundMarkID"));
                ArrayList<Double> point1 = mercatorProjection(lat1, lon1, width, height);
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
                            String zoneSize = corner.getAttributeValue("ZoneSize");
                            courseFeature.setZoneSize(zoneSize);
                        }
                    }

                }
            } else if (type.equals("CourseLimit")) {
            } else { //invalid course file
                throw new JDOMException();
            }
        }

        // scale to canvas size
        double xFactor = (width - bufferX) / (Collections.max(xMercatorCoords) - Collections.min(xMercatorCoords));
        double yFactor = (height - bufferY) / (Collections.max(yMercatorCoords) - Collections.min(yMercatorCoords));

        //make scaling in proportion
        scaleFactor = Math.min(xFactor, yFactor);

        //scale points to fit screen
        points.stream().forEach(p -> p.factor(scaleFactor, scaleFactor, Collections.min(xMercatorCoords), Collections.min(yMercatorCoords), bufferX / 2, bufferY / 2));

        return points;

    }

}
