package seng302.Model;


import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static java.lang.Math.abs;

/**
 * Created by khe60 on 14/03/17.
 * An XML file parser for reading courses
 */
public class XMLCourseLoader {
    private File inputFile;
    private Double screenX;
    private Double screenY;
    private ArrayList<Gate> winds = new ArrayList<>();
    CoordinateMapper mapper = new CoordinateMapper();



    private ArrayList<Double> xCoords = new ArrayList<>();
    private ArrayList<Double> yCoords = new ArrayList<>();
    private ArrayList<CourseFeature> points = new ArrayList<>();
    private double distanceMetres;
    private double distancePixels;

    public XMLCourseLoader(File inputFile, Double x, Double y) {
        this.inputFile = inputFile;
        this.screenX = x;
        this.screenY = y;
    }

    /**
     *
     * @return
     */
    public double getWindDirection(){
        double x1=winds.get(0).getGPSCentre().getXValue();
        double y1=winds.get(0).getGPSCentre().getYValue();
        double x2=winds.get(1).getGPSCentre().getXValue();
        double y2=winds.get(1).getGPSCentre().getYValue();
//        System.out.println(x1);
//        System.out.println(y1);
//        System.out.println(x2);
//        System.out.println(y2);

        return Math.toDegrees(Math.atan( (x1-x2)/-(y1-y2)));
    }



    /**
     * Creates a list of course features read from an xml file
     * @param width double the width of the screen
     * @param height double the height of the screen
     * @return List the list of course features
     * @throws JDOMException
     * @throws IOException
     */
    public ArrayList<CourseFeature> parseCourse(double width, double height) throws JDOMException, IOException {
        //buffers are defined as the total buffer size, i.e. total for both sides
        double bufferX=Math.max(40,width*0.2);
        double bufferY=Math.max(40,height*0.2);
        System.out.println("bufferX: "+bufferX);
        System.out.println("bufferY: "+bufferY);

        SAXBuilder saxbuilder = new SAXBuilder();
        Document document = saxbuilder.build(inputFile);
        Element raceCourse = document.getRootElement();
        List<Element> features = raceCourse.getChildren();
        ArrayList<Double> xMercatorCoords=new ArrayList<>();
        ArrayList<Double> yMercatorCoords=new ArrayList<>();

        for (Element feature : features) {

            String type = feature.getName();

            if (type.equals("gate")) { //its a gate

                List<Element> marks = feature.getChildren();
                Element markOne = marks.get(1);
                Element markTwo = marks.get(2);

                boolean isLine = Boolean.valueOf(feature.getAttributeValue("isLine"));
                boolean isFinish = Boolean.valueOf(feature.getAttributeValue("isFinish"));
                String name = feature.getChildText("name");

                double lat1 = Double.parseDouble(markOne.getChildText("latitude"));
                double lat2 = Double.parseDouble(markTwo.getChildText("latitude"));

                double lon1= Double.parseDouble(markOne.getChildText("longtitude"));
                double lon2= Double.parseDouble(markTwo.getChildText("longtitude"));

                xCoords.add(lat1);
                xCoords.add(lat2);
                yCoords.add(lon1);
                yCoords.add(lon2);

                ArrayList<Double> point1=mapper.mercatorProjection(lat1,lon1,width,height);
                ArrayList<Double> point2=mapper.mercatorProjection(lat2,lon2,width,height);
                double point1X=point1.get(0);
                double point1Y=point1.get(1);
                double point2X=point2.get(0);
                double point2Y=point2.get(1);

                xMercatorCoords.add(point1X);
                xMercatorCoords.add(point2X);
                yMercatorCoords.add(point1Y);
                yMercatorCoords.add(point2Y);

                MutablePoint pixel1 = new MutablePoint(point1X,point1Y);
                MutablePoint pixel2 = new MutablePoint(point2X,point2Y);
                MutablePoint GPS1 = new MutablePoint(lat1, lon1);
                MutablePoint GPS2 = new MutablePoint(lat2, lon2);

                Gate gate=new Gate(name, GPS1, GPS2, pixel1, pixel2, isFinish, isLine);
                points.add(gate);

                if (feature.getAttributeValue("type")!=null) {

                        winds.add(gate);

                }
            } else { //Its a mark

                Element mark = feature.getChildren().get(1);
                String name = feature.getChildText("name");

                double lat1 =Double.parseDouble(mark.getChildText("latitude"));
                double lon1= Double.parseDouble(mark.getChildText("longtitude"));
                ArrayList<Double> point1=mapper.mercatorProjection(lat1,lon1,width,height);
                double point1X=point1.get(0);
                double point1Y=point1.get(1);
                xMercatorCoords.add(point1X);
                yMercatorCoords.add(point1Y);

                // add the original lat and lon to the array lists of lat and lons
                xCoords.add(lat1);
                yCoords.add(lon1);

                MutablePoint pixel = new MutablePoint(point1X, point1Y);
                MutablePoint GPS = new MutablePoint(lat1, lon1);
                points.add(new Mark(name, pixel, GPS,false));
            }

        }

        double xFactor= (width-bufferX/2)/(Collections.max(xMercatorCoords)-Collections.min(xMercatorCoords));
        double yFactor=(height-bufferY/2)/(Collections.max(yMercatorCoords)-Collections.min(yMercatorCoords));

        //make scaling in proportion
        double factor=Math.min(xFactor,yFactor);

        //scale points to fit screen
        points.stream().forEach(p->p.factor(factor,factor,Collections.min(xMercatorCoords),Collections.min(yMercatorCoords),bufferX/2,bufferY/2,width,height));

        return points;
    }



}
