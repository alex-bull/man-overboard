package parsers.xml.race;

import com.google.common.math.DoubleMath;
import models.MutablePoint;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
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

    private List<MutablePoint> courseGPSBoundary;
    private double width;
    private double height;
    private double maxLat;
    private double maxLng;
    private double minLat;
    private double minLng;
    private double zoomLevel;
    private double shiftDistance;


    /**
     * Parse XML race data
     * @param xmlStr XML String of race data
     * @param width double the width of the screen
     * @param height height the height of the screen
     * @return RaceData the parsed race data
     * @throws IOException IOException
     * @throws JDOMException JDOMException
     */
    public RaceData parseRaceData(String xmlStr, double width, double height) throws IOException, JDOMException {
        this.width = width;
        this.height = height;
        courseGPSBoundary = new ArrayList<>();
        RaceData raceData = new RaceData();
        SAXBuilder builder = new SAXBuilder();
        InputStream stream = new ByteArrayInputStream(xmlStr.getBytes("UTF-8"));
        Document root = builder.build(stream);
        Element race = root.getRootElement();

        int raceID = Integer.parseInt(race.getChild("RaceID").getValue());
        String raceType = race.getChild("RaceType").getValue();
        String creationTimeDate = race.getChild("CreationTimeDate").getValue();
        String raceStartTime = race.getChild("RaceStartTime").getAttributeValue("Time");
        boolean raceStartTimePostponed = Boolean.parseBoolean(race.getChild("RaceStartTime").getAttributeValue("Postpone"));

        raceData.setRaceID(raceID);
        raceData.setRaceType(raceType);
        raceData.setCreationTimeDate(creationTimeDate);
        raceData.setRaceStartTime(raceStartTime);
        raceData.setRaceStartTimePostpone(raceStartTimePostponed);

        Set<Integer> participantIDs = new HashSet<>();
        for (Element yacht : race.getChild("Participants").getChildren()) {
            int sourceID = Integer.parseInt(yacht.getAttributeValue("SourceID"));
            String entry = yacht.getAttributeValue("Entry");
            YachtData yachtData = new YachtData(sourceID, entry);
            participantIDs.add(sourceID);
            raceData.getParticipants().add(yachtData);
        }
        raceData.setParticipantIDs(participantIDs);

        List<CompoundMarkData> course = new ArrayList<>();
        List<MarkData> startMarks = new ArrayList<>();
        List<MarkData> finishMarks = new ArrayList<>();
        boolean startLineSet = false;

        for (Element compoundMark : race.getChild("Course").getChildren()) {
            int size = race.getChild("Course").getChildren().size();
            int compoundMarkID = Integer.parseInt(compoundMark.getAttribute("CompoundMarkID").getValue());
            String compoundMarkName = compoundMark.getAttribute("Name").getValue();
            List<MarkData> marks = new ArrayList<>();

            for (Element mark : compoundMark.getChildren()) {

                int seqID = Integer.parseInt(mark.getAttributeValue("SeqID"));
                String markName = mark.getAttributeValue("Name");
                double targetLat = Double.parseDouble(mark.getAttributeValue("TargetLat"));
                double targetLng = Double.parseDouble(mark.getAttributeValue("TargetLng"));
                int sourceID = Integer.parseInt(mark.getAttributeValue("SourceID"));
                raceData.addMarkID(sourceID);
                MarkData markData = new MarkData(seqID, markName, targetLat, targetLng, sourceID);
                marks.add(markData);
            }
            raceData.addCompoundMarkID(compoundMarkID);
            CompoundMarkData compoundMarkData = new CompoundMarkData(compoundMarkID, compoundMarkName, marks);
            course.add(compoundMarkData);

            //Start Line
            if (!startLineSet){
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

        for (Element corner : race.getChild("CompoundMarkSequence").getChildren()) {

            int cornerSeqID = Integer.parseInt(corner.getAttributeValue("SeqID"));
            int compoundMarkID = Integer.parseInt(corner.getAttributeValue("CompoundMarkID"));
            String rounding = corner.getAttributeValue("Rounding");
            int zoneSize = Integer.parseInt(corner.getAttributeValue("ZoneSize"));
            CornerData cornerData = new CornerData(cornerSeqID, compoundMarkID, rounding, zoneSize);

            raceData.getCompoundMarkSequence().add(cornerData);

        }
        for (Element limit : race.getChild("CourseLimit").getChildren()) {
            int limitSeqID = Integer.parseInt(limit.getAttributeValue("SeqID"));
            double lat = Double.parseDouble(limit.getAttributeValue("Lat"));
            double lon = Double.parseDouble(limit.getAttributeValue("Lon"));
            LimitData limitData = new LimitData(limitSeqID, lat, lon);
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
        maxLat = -180;
        maxLng = -180;
        minLat = 180;
        minLng = 180;
        this.xMercatorCoords = new ArrayList<>();
        this.yMercatorCoords = new ArrayList<>();
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
            courseGPSBoundary.add(new MutablePoint(limit.getLat(), limit.getLon()));
        }

        double xDifference = (Collections.max(xMercatorCoords) - Collections.min(xMercatorCoords));
        double yDifference = (Collections.max(yMercatorCoords) - Collections.min(yMercatorCoords));

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


        boundary.forEach(p -> p.factor(scaleFactor, scaleFactor, Collections.min(xMercatorCoords), Collections.min(yMercatorCoords), paddingX, paddingY));
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

    public List<Double> getyMercatorCoords() {
        return yMercatorCoords;
    }

    public List<Double> getGPSBounds() {
        return new ArrayList<>(Arrays.asList(minLat, minLng, maxLat, maxLng));
    }

    public double getZoomLevel() {
        return zoomLevel;
    }

    public List<MutablePoint> getCourseGPSBoundary() {
        return courseGPSBoundary;
    }

    public double getShiftDistance() {
        return shiftDistance;
    }
}
