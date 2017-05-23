package parsers.xml.race;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import models.CourseFeature;
import models.MutablePoint;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * Created by jar156 on 13/04/17.
 * parsers for Race XML
 */
public class RaceXMLParser {

    private List<MutablePoint> courseBoundary;
    private List<CourseFeature> courseFeatures;
    private double scaleFactor;
    private double bufferX;
    private double bufferY;
    private double paddingX;
    private double paddingY;
    private List<Double> xMercatorCoords;
    private List<Double> yMercatorCoords;
    private double width;
    private double height;



    /**
     * Parse XML race data
     *
     * @param xmlStr XML String of race data
     * @throws IOException   IOException
     * @throws JDOMException JDOMException
     */
    public RaceData parseRaceData(String xmlStr, double width, double height) throws IOException, JDOMException {
        this.width = width;
        this.height = height;

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

        Map<Integer, List<Integer>> indexToSourceId = new HashMap<>();
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
            indexToSourceId.put(compoundMarkID, sourceIds);
            raceData.setIndexToSourceId(indexToSourceId);
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
        bufferX = 500;
        bufferY = 280;

        try {
            parseBoundary(raceData, bufferX, bufferY);
        } catch (Exception ignored) {
        }
    }


    /**
     * Parse the boundary of the course
     * @param raceData RaceData
     * @param bufferX canvas buffer width
     * @param bufferY canvas buffer height
     */
    private void parseBoundary(RaceData raceData, double bufferX, double bufferY) throws Exception {

        this.xMercatorCoords = new ArrayList<>();
        this.yMercatorCoords = new ArrayList<>();
        List<MutablePoint> boundary = new ArrayList<>();
        //loop through the parsed boundary points
        for (LimitData limit : raceData.getCourseLimit()) {
            double lat = limit.getLat();
            double lon = limit.getLon();

            ArrayList<Double> projectedPoint = mercatorProjection(lat, lon);
            double point1X = projectedPoint.get(0);
            double point1Y = projectedPoint.get(1);
            xMercatorCoords.add(point1X);
            yMercatorCoords.add(point1Y);
            MutablePoint pixel = new MutablePoint(point1X, point1Y);
            boundary.add(pixel);
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
        //set padding
        paddingX=(width-xDifference*scaleFactor)/2;
        paddingY=10;

        boundary.forEach(p -> p.factor(scaleFactor, scaleFactor, Collections.min(xMercatorCoords), Collections.min(yMercatorCoords),paddingX , paddingY));
        this.courseBoundary = boundary;

    }

    /**
     * Function to map latitude and longitude to screen coordinates
     * @param lat latitude
     * @param lon longitude
     * @return ArrayList the coordinates in metres
     */
    private ArrayList<Double> mercatorProjection(double lat, double lon) {
        ArrayList<Double> ret = new ArrayList<>();
        double x = (lon + 180) * (width / 360);
        double latRad = lat * Math.PI / 180;
        double merc = Math.log(Math.tan((Math.PI / 4) + (latRad / 2)));
        double y = (height / 2) - (width * merc / (2 * Math.PI));
        ret.add(x);
        ret.add(y);
        return ret;

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


}
