package parsers.xml.race;

import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;
import com.google.common.math.DoubleMath;
import models.MutablePoint;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

import static utility.Projection.mercatorProjection;

/**
 * Created by jar156 on 13/04/17.
 * parsers for Race XML
 */
public class RaceXMLParser {

    private List<MutablePoint> courseBoundary;
    private double scaleFactor;
    private double paddingX;
    private double paddingY;
    private List<Double> xMercatorCoords;
    private List<Double> yMercatorCoords;


    private double width;
    private double height;
    private double maxLat;
    private double maxLng;
    private double minLat;
    private double minLng;
    private double zoomLevel;
    private double shiftDistance;
    private double xMin;
    private double yMin;
    /**
     * initializer to initialize variables
     */
    public RaceXMLParser() {
        xMercatorCoords = new ArrayList<>();
        yMercatorCoords = new ArrayList<>();
        maxLat = -180;
        maxLng = -180;
        minLat = 180;
        minLng = 180;
    }

    /** Set width and height of the screen
     * @param width  double the width of the screen
     * @param height height the height of the screen
     */
    public void setScreenSize(double width, double height) {
        this.width = width;
        this.height = height;
    }

    /**
     * Parse XML race data
     *
     * @param xmlStr XML String of race data
     * @return RaceData the parsed race data
     * @throws IOException   IOException
     * @throws JDOMException JDOMException
     */
    public RaceData parseRaceData(String xmlStr) throws IOException, JDOMException {

        RaceData raceData = new RaceData();
        SAXBuilder builder = new SAXBuilder();
        InputStream stream = new ByteArrayInputStream(xmlStr.getBytes("UTF-8"));

        Document root = builder.build(stream);
        Element race = root.getRootElement();
        Set<Integer> participantIDs = new HashSet<>();
        for (Element yacht : race.getChild("Participants").getChildren()) {
            int sourceID = Integer.parseInt(yacht.getAttributeValue("SourceID"));
            YachtData yachtData = new YachtData(sourceID);
            participantIDs.add(sourceID);
            raceData.getParticipants().add(yachtData);
        }
        raceData.setParticipantIDs(participantIDs);

        List<CompoundMarkData> course = new ArrayList<>();
        List<MarkData> startMarks = new ArrayList<>();
        List<MarkData> finishMarks = new ArrayList<>();
        boolean startLineSet = false;

        Map<Integer, List<Integer>> compoundMarkIdToSourceId = new HashMap<>();

        for (Element compoundMark : race.getChild("Course").getChildren()) {
            int size = race.getChild("Course").getChildren().size();
            int compoundMarkID = Integer.parseInt(compoundMark.getAttribute("CompoundMarkID").getValue());
            String compoundMarkName = compoundMark.getAttribute("Name").getValue();
            List<MarkData> marks = new ArrayList<>();
            List<Integer> sourceIds = new ArrayList<>();

            for (Element mark : compoundMark.getChildren()) {

                int seqID = Integer.parseInt(mark.getAttributeValue("SeqID"));
                String markName = mark.getAttributeValue("Name");
                double targetLat = Double.parseDouble(mark.getAttributeValue("TargetLat"));
                double targetLng = Double.parseDouble(mark.getAttributeValue("TargetLng"));
                int sourceID = Integer.parseInt(mark.getAttributeValue("SourceID"));
                raceData.addMarkID(sourceID);
                sourceIds.add(sourceID);
                MarkData markData = new MarkData(seqID, markName, targetLat, targetLng, sourceID);
                marks.add(markData);
            }
            compoundMarkIdToSourceId.put(compoundMarkID, sourceIds);
            raceData.addCompoundMarkID(compoundMarkID);
            CompoundMarkData compoundMarkData = new CompoundMarkData(compoundMarkID, compoundMarkName, marks);
            course.add(compoundMarkData);

            if (!startLineSet) {
                for (CompoundMarkData mark : course) {
                    if (mark.getName().equals(compoundMarkName) && mark.getMarks().size() == 2) {
                        startMarks.addAll(mark.getMarks());
                        startLineSet = true;
                    }
                }
            }
            //Finish Line
            if (compoundMarkID == size) {
                for (CompoundMarkData mark : course) {
                    if (mark.getID() == compoundMarkID) {
                        finishMarks.addAll(mark.getMarks());
                    }
                }
            }
            raceData.setStartMarks(startMarks);
            raceData.setFinishMarks(finishMarks);
        }
        raceData.setCourse(course);

        Map<Integer, List<Integer>> legIndexToSourceId = new HashMap<>();

        for (Element corner : race.getChild("CompoundMarkSequence").getChildren()) {

            int size = race.getChild("CompoundMarkSequence").getChildren().size();
            int cornerSeqID = Integer.parseInt(corner.getAttributeValue("SeqID"));
            int compoundMarkID = Integer.parseInt(corner.getAttributeValue("CompoundMarkID"));
            String rounding = corner.getAttributeValue("Rounding");
            int zoneSize = Integer.parseInt(corner.getAttributeValue("ZoneSize"));

            legIndexToSourceId.put(cornerSeqID, compoundMarkIdToSourceId.get(compoundMarkID));
            CornerData cornerData = new CornerData(rounding);

            raceData.getCompoundMarkSequence().add(cornerData);


        }
        raceData.setLegIndexToSourceId(legIndexToSourceId);

        for (Element limit : race.getChild("CourseLimit").getChildren()) {
            double lat = Double.parseDouble(limit.getAttributeValue("Lat"));
            double lon = Double.parseDouble(limit.getAttributeValue("Lon"));
            LimitData limitData = new LimitData(lat, lon);
            raceData.addCourseLimit(limitData);

        }

        parseRace(raceData);
        return raceData;
    }


    public List<MutablePoint> getCourseBoundary() {
        return courseBoundary;
    }

    /**
     * Set buffers and call course parsers
     * buffers are calculated by the size of widgets surrounding the course
     */
    private void parseRace(RaceData raceData) {
        double bufferX = 400;
        double bufferY = 200;
        try {
            parseBoundary(raceData, bufferX, bufferY);
        } catch (Exception ignored) {
        }
    }


    /**
     * Parse the boundary of the course
     *
     * @param raceData RaceData
     * @param bufferX  canvas buffer width
     * @param bufferY  canvas buffer height
     */
    private void parseBoundary(RaceData raceData, double bufferX, double bufferY) throws Exception {

        List<MutablePoint> boundary = new ArrayList<>();
        //loop through the parsed boundary points
        for (LimitData limit : raceData.getCourseLimit()) {
            double lat = limit.getLat();
            double lon = limit.getLon();

            //find course boundary
            if (lat < minLat) {
                minLat = lat;
            }
            if (lon < minLng) {
                minLng = lon;
            }

            if (lat > maxLat) {
                maxLat = lat;
            }
            if (lon > maxLng) {
                maxLng = lon;
            }

            List<Double> projectedPoint = mercatorProjection(lat, lon);
            double point1X = projectedPoint.get(0);
            double point1Y = projectedPoint.get(1);
            xMercatorCoords.add(point1X);
            yMercatorCoords.add(point1Y);
            MutablePoint pixel = new MutablePoint(point1X, point1Y);
            boundary.add(pixel);
        }

        //add course feature to zoom level calculation
        for(CompoundMarkData compoundMarkData:raceData.getCourse()) {
            for (MarkData markData : compoundMarkData.getMarks()) {
                double lat=markData.getTargetLat();
                double lon=markData.getTargetLon();

                //find course boundary
                if (lat < minLat) {
                    minLat = lat;
                }
                if (lon < minLng) {
                    minLng = lon;
                }

                if (lat > maxLat) {
                    maxLat = lat;
                }
                if (lon > maxLng) {
                    maxLng = lon;
                }

                List<Double> projectedPoint = mercatorProjection(lat, lon);
                double point1X = projectedPoint.get(0);
                double point1Y = projectedPoint.get(1);
                xMercatorCoords.add(point1X);
                yMercatorCoords.add(point1Y);
            }
        }

        if(scaleFactor==0.0) {
            xMin=Collections.min(xMercatorCoords);
            yMin=Collections.min(yMercatorCoords);
            double xDifference = (Collections.max(xMercatorCoords) - xMin);
            double yDifference = (Collections.max(yMercatorCoords) - yMin);

            if (xDifference == 0 || yDifference == 0) {
                throw new Exception("Attempted to divide by zero");
            }
            double xFactor = (width - bufferX) / xDifference;
            double yFactor = (height - bufferY) / yDifference;


            //make scaling in proportion
            scaleFactor = Math.min(xFactor, yFactor);

            //set scale factor to the largest power of 2 thats smaller than current value
            this.zoomLevel = Math.floor(DoubleMath.log2(scaleFactor));
            scaleFactor = Math.pow(2, zoomLevel);

            //set padding
            paddingY = (height - bufferY - yDifference * scaleFactor) / 2;
            paddingX = (width - xDifference * scaleFactor) / 2;
            //calculate shift distance in pixels
            shiftDistance = bufferY / 2;
        }
        boundary.forEach(p -> p.factor(scaleFactor, scaleFactor, xMin, yMin, paddingX, paddingY));

        this.courseBoundary = boundary;

    }


    public double getScaleFactor() {
        return scaleFactor;
    }

    public double getPaddingX() {
        return paddingX;
    }

    public double getPaddingY() {
        return paddingY;
    }

    public List<Double> getxMercatorCoords() {
        return xMercatorCoords;
    }

    public double getxMin() {
        return xMin;
    }

    public double getyMin() {
        return yMin;
    }

    public List<Double> getyMercatorCoords() {
        return yMercatorCoords;
    }

    public List<Double> getGPSBounds() {
        return new ArrayList<>(Arrays.asList(minLat, minLng, maxLat, maxLng));
    }

    public double getZoomLevel() {
        return zoomLevel;
    }


    public double getShiftDistance() {
        return shiftDistance;
    }
}
