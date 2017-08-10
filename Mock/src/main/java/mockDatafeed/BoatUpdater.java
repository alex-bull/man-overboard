package mockDatafeed;

import com.google.common.io.ByteStreams;
import com.google.common.io.CharStreams;
import javafx.scene.shape.Line;
import models.*;
import org.jdom2.JDOMException;
import parsers.xml.race.RaceData;
import parsers.xml.race.RaceXMLParser;
import utilities.PolarTable;
import utility.Calculator;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import static java.lang.Math.*;
import static java.lang.Math.PI;
import static java.lang.Math.sin;
import static utility.Calculator.shortToDegrees;

/**
 * Created by jar156 on 3/08/17.
 */
public class BoatUpdater {

    private PolarTable polarTable;
    private RaceData raceData;
    private Map<Integer, Competitor> competitors;
    private Course raceCourse = new RaceCourse(null, true);
    private Map<Integer, Competitor> markBoats;
    private BoatUpdateEventHandler handler;


    BoatUpdater(Map<Integer, Competitor> competitors, Map<Integer, Competitor> markBoats, RaceData raceData, BoatUpdateEventHandler handler) throws IOException, JDOMException {
        this.competitors = competitors;
        this.markBoats = markBoats;
        this.handler = handler;
        this.raceData = raceData;

        polarTable = new PolarTable("/polars/VO70_polar.txt", 12.0);
    }


    /**
     * updates the position of all the boats given the boats speed and heading
     */
    void updatePosition(WindGenerator windGenerator) throws IOException, InterruptedException {

        for (Integer sourceId : competitors.keySet()) {
            Competitor boat = competitors.get(sourceId);
            short windDirection = windGenerator.getWindDirection();
            double twa = abs(shortToDegrees(windDirection) - boat.getCurrentHeading());
            if(twa > 180) {
                twa = 180 - (twa - 180); // interpolator only goes up to 180
            }
            double speed = polarTable.getSpeed(twa);
            if (boat.hasSailsOut()) {
                boat.setVelocity(speed * 3);
                boat.updatePosition(0.1);
            } else {
                boat.setVelocity(0);
            }

            this.handleCourseCollisions(boat);
            this.handleBoatCollisions(boat);
//            boat.blownByWind(twa);
            this.handleMarkRounding(boat);
        }
    }


    /**
     * Calculates if the boat rounds the next course feature and adjusts the boats leg index and sends a rounding packet
     * @param boat Competitor the boat to check roundings for
     */
    private void handleMarkRounding(Competitor boat) throws IOException, InterruptedException {

        int nextLegIndex = boat.getCurrentLegIndex() + 1;

        List<Integer> markIds = raceData.getLegIndexToMarkSourceIds().get(nextLegIndex);
        if (markIds == null || markIds.size() < 1) return; //race is done

        if (markIds.size() == 2) { //Gate

            Competitor mark1 = markBoats.get(markIds.get(0));

            if (mark1.getTeamName().toLowerCase().contains("start") || mark1.getTeamName().toLowerCase().contains("finish")) {
                lineRounding(boat, markIds);
            } else {
                gateRounding(boat, markIds);
            }
        } else {
            //MARK - using distance based for now
            markRounding(boat, markIds.get(0));
        }
    }


    /**
     * Check for rounding of a mark
     * @param boat Competitor the boat to check
     * @param markId Integer the id of the mark
     */
    private void markRounding(Competitor boat, Integer markId) {

        int nextLegIndex = boat.getCurrentLegIndex() + 1;
        final double roundingRadius = 200;
        MutablePoint pos1 = markBoats.get(markId).getPosition();
        Double distance = raceCourse.distanceBetweenGPSPoints(pos1, boat.getPosition());
        if (distance < roundingRadius) {
            handler.markRoundingEvent(boat.getSourceID(), boat.getCurrentLegIndex());
            boat.setCurrentLegIndex(nextLegIndex);
        }
    }


    /**
     * Check for rounding of a gate -does not include finish or start line
     * @param boat Competitor the boat to check
     * @param markIds List the marks of the gate
     */
    private void gateRounding(Competitor boat, List<Integer> markIds) {

        MutablePoint pos1 = markBoats.get(markIds.get(0)).getPosition();
        MutablePoint pos2 = markBoats.get(markIds.get(1)).getPosition();

        if (boat.isRounding()) { //has already passed between the marks
            //Take lines out from either side of the gate
            Vector2D vec = new Vector2D(pos1.getXValue(), pos1. getYValue(), pos2.getXValue(), pos2.getYValue());
            vec.normalise();
            Line l2 = new Line(pos1.getXValue(), pos1. getYValue(), vec.getX() * -200, vec.getY() * -200);

            Vector2D vec2 = new Vector2D(pos2.getXValue(), pos2. getYValue(), pos1.getXValue(), pos1.getYValue());
            vec2.normalise();
            Line l3 = new Line(pos2.getXValue(), pos2. getYValue(), vec2.getX() * -200, vec2.getY() * -200);

            if (didCrossLine(boat, l2) || didCrossLine(boat, l3)) {
                boat.finishedRounding();
                handler.markRoundingEvent(boat.getSourceID(), boat.getCurrentLegIndex());
            }
        } else {
            //Check if the boat crosses the line between the marks

            Line l1 = new Line(pos1.getXValue(), pos1.getYValue(), pos2.getXValue(), pos2.getYValue());
            if (didCrossLine(boat, l1)) {
                System.out.println("Crossed line 1");
                boat.startRounding();
            }
        }
    }


    /**
     * Check for boat crossing a start or finish line
     * @param boat Competitor the boat to check
     * @param markIds List the marks of the line
     */
    private void lineRounding(Competitor boat, List<Integer> markIds) {

        int nextLegIndex = boat.getCurrentLegIndex() + 1;
        MutablePoint pos1 = markBoats.get(markIds.get(0)).getPosition();
        MutablePoint pos2 = markBoats.get(markIds.get(1)).getPosition();

        //line between marks
        Line l1 = new Line(pos1.getXValue(), pos1.getYValue(), pos2.getXValue(), pos2.getYValue());

        if (didCrossLine(boat, l1)) {
            System.out.println("Crossed line");
            handler.markRoundingEvent(boat.getSourceID(), boat.getCurrentLegIndex());
            boat.setCurrentLegIndex(nextLegIndex);
        }
    }


    /**
     * Checks if a boat is within a threshold distance of a line
     * @param boat Competitor the boat
     * @param line Line the line segment
     * @return boolean, true if in range
     */
    private boolean didCrossLine(Competitor boat, Line line) {

        final double lineCrossThreshold = 0.0002;
        double dist = Calculator.pointDistance(boat.getPosition().getXValue(), boat.getPosition().getYValue(), line.getStartX(), line.getStartY(), line.getEndX(), line.getEndY());
        return dist < lineCrossThreshold;
    }



    /**
     * Calculates if the boat collides with any course features and adjusts the boats position
     * @param boat Competitor the boat to check collisions for
     */
    private void handleCourseCollisions(Competitor boat) throws IOException, InterruptedException {

        final double collisionRadius = 55; //Large for testing

        for (Competitor mark: markBoats.values()) {

            double distance = raceCourse.distanceBetweenGPSPoints(mark.getPosition(), boat.getPosition());

            if (distance <= collisionRadius) {
//                send a collision packet
                handler.yachtEvent(boat.getSourceID(),1);
                boat.updatePosition(-10);
                break;
            }
        }
    }


    /**
     * function to calculate what happens during collision
     * @param boat1 one of the boat during collision
     * @param boat2 the other boat during collision
     */
    private void calculateCollisions(Competitor boat1, Competitor boat2){
        double x1=boat1.getPosition().getXValue();
        double x2=boat2.getPosition().getXValue();
        double y1=boat1.getPosition().getYValue();
        double y2=boat2.getPosition().getYValue();
        double contactAngle=(atan2((x1-x2),(y1-y2)));

        double v1x=calculateVx(boat2.getVelocity(),boat2.getCurrentHeading(),contactAngle,boat1.getVelocity(),boat1.getCurrentHeading());
        double v1y=calculateVy(boat2.getVelocity(),boat2.getCurrentHeading(),contactAngle,boat1.getVelocity(),boat1.getCurrentHeading());
        RepelForce force1=new RepelForce(v1x,v1y);
        boat1.setCurrentHeading(force1.angle());
        boat1.setVelocity(boat1.getVelocity()+ force1.getMagnitude()*100);



        double v2x=calculateVx(boat1.getVelocity(),boat1.getCurrentHeading(),contactAngle,boat2.getVelocity(),boat2.getCurrentHeading());
        double v2y=calculateVy(boat1.getVelocity(),boat1.getCurrentHeading(),contactAngle,boat2.getVelocity(),boat2.getCurrentHeading());
        RepelForce force2=new RepelForce(v2x,v2y);
        boat2.setCurrentHeading(force2.angle());
        boat2.setVelocity(boat2.getVelocity()+ force2.getMagnitude()*100);

    }

    private double calculateVx(double v2, double angle2, double contactAngle, double v1, double angle1){
        angle1=toRadians(angle1);
        angle2=toRadians(angle2);
        return v2*cos(angle2-contactAngle)*cos(contactAngle)+v1*sin(angle1-contactAngle)*cos(contactAngle+PI/2);
    }

    private double calculateVy(double v2, double angle2, double contactAngle, double v1, double angle1){
        angle1=toRadians(angle1);
        angle2=toRadians(angle2);
        return v2*cos(angle2-contactAngle)*sin(contactAngle)+v1*sin(angle1-contactAngle)*sin(contactAngle+PI/2);
    }

    /**
     * Calculates if the boat collides with any other boat and adjusts the position of both boats accordingly.
     * @param boat Competitor, the boat to check collisions for
     */
    private void handleBoatCollisions(Competitor boat) throws IOException, InterruptedException {

        final double collisionRadius = 35; //Large for testing

        //Can bump back a fixed amount or try to simulate a real collision.
        for (Competitor comp: this.competitors.values()) {

            if (comp.getSourceID() == boat.getSourceID()) continue; //cant collide with self

            double distance = raceCourse.distanceBetweenGPSPoints(comp.getPosition(), boat.getPosition());

            if (distance <= collisionRadius) {

//                send a collision packet
                handler.yachtEvent(comp.getSourceID(),1);

//                calculateCollisions(comp,boat);
                boat.updatePosition(-10);
                comp.updatePosition(-10);
            }
        }
    }
}
