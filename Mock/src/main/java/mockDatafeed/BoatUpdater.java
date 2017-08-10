package mockDatafeed;

import com.google.common.io.ByteStreams;
import com.google.common.io.CharStreams;
import javafx.scene.shape.Line;
import models.*;
import org.jdom2.JDOMException;
import parsers.xml.race.RaceData;
import parsers.xml.race.RaceXMLParser;
import utilities.CollisionUtility;
import utilities.PolarTable;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;

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
    private List<MutablePoint> courseBoundary;
    private List<MutablePoint> courseLineEquations;
    private CollisionUtility collisionUtility;

    /**
     * Boat updater constructor
     * @param competitors Map a hashmap of competitors in the race
     * @param markBoats Map course features
     * @param raceData RaceData race data
     * @param handler BoatUpdateEventHandler boat mocker
     * @param courseBoundary List a list of course boundary points
     * @param courseLineEquations CourseLineEquations course line equations
     * @throws IOException
     * @throws JDOMException
     */
    BoatUpdater(Map<Integer, Competitor> competitors, Map<Integer, Competitor> markBoats, RaceData raceData,
                BoatUpdateEventHandler handler, List<MutablePoint> courseBoundary, List<MutablePoint> courseLineEquations, CollisionUtility collisionUtility) throws IOException, JDOMException {
        this.competitors = competitors;
        this.markBoats = markBoats;
        this.handler = handler;
        this.raceData = raceData;
        this.courseBoundary = courseBoundary;
        this.courseLineEquations = courseLineEquations;
        this.collisionUtility = collisionUtility;

        polarTable = new PolarTable("/polars/VO70_polar.txt", 12.0);
    }

    /**
     * Calculates if the boat collides with the course boundary, if so then pushes back the boat.
     * @param boat Competitor the boat to check collisions for
     */

    private void handleBoundaryCollisions(Competitor boat) throws IOException, InterruptedException {
        double collisionRadius = 50;
        for (MutablePoint point: courseBoundary) {
            double distance = raceCourse.distanceBetweenGPSPoints(boat.getPosition(), point);
            if (distance <= collisionRadius) {
                handler.yachtEvent(boat.getSourceID(), 1);
                boat.updatePosition(-10);
                break;
            }
        }
        for (MutablePoint equation: courseLineEquations ) {
            int index = courseLineEquations.indexOf(equation);
            //distance = y - (mx + c)
            double distance = boat.getPosition().getYValue() - (equation.getXValue() * boat.getPosition().getXValue() + equation.getYValue());
            if ((abs(distance) < 0.0001) && collisionUtility.isWithinBoundaryLines(boat.getPosition(), index)) {
                handler.yachtEvent(boat.getSourceID(), 1);
                boat.updatePosition(-10);
                break;
            }
        }
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
                boat.setVelocity(speed);
                boat.updatePosition(0.1);
            } else {
                boat.setVelocity(0);
            }

            this.handleCourseCollisions(boat);
            this.handleBoatCollisions(boat);
            this.handleBoundaryCollisions(boat);

//            boat.blownByWind(twa);
            this.handleMarkRounding(boat);
        }
    }


    /**
     * Calculates if the boat rounds the next course feature and adjusts the boats leg index and sends a rounding packet
     * @param boat Competitor the boat to check roundings for
     */
    private void handleMarkRounding(Competitor boat) throws IOException, InterruptedException {

        final double roundingRadius = 200;


        int nextLegIndex = boat.getCurrentLegIndex() + 1;

        List<Integer> markIds = raceData.getLegIndexToMarkSourceIds().get(nextLegIndex);
        if (markIds == null || markIds.size() < 1) return;

        //System.out.println("Rounding direction: " + raceData.getLegIndexToRoundingDirection().get(nextLegIndex));

        //String roundingSide = raceData.getLegIndexToRoundingDirection().get(nextLegIndex);

//        for (Integer markId: markIds) { //COMPARE DISTANCE TO ALL MARKS IN THE COMPOUND MARK (1 for a mark, 2 for a gate)
//            Competitor markBoat = markBoats.get(markId);
//            Double distance = raceCourse.distanceBetweenGPSPoints(markBoat.getPosition(), boat.getPosition());
//            if (distance < roundingRadius && markIsCorrectSide(boat, markBoat, true)) {
//
//                System.out.println("Rounding");
//                //handler.markRoundingEvent(boat.getSourceID(), boat.getCurrentLegIndex());
//               // boat.setCurrentLegIndex(nextLegIndex);
//            }
//        }


        //Double distance = raceCourse.distanceBetweenGPSPoints(markBoat.getPosition(), boat.getPosition());

        if (markIds.size() == 2) {
            //GATE
            Competitor mark1 = markBoats.get(markIds.get(0));
            MutablePoint pos1 = markBoats.get(markIds.get(0)).getPosition();
            MutablePoint pos2 = markBoats.get(markIds.get(1)).getPosition();

            //line between marks
            Line l1 = new Line(pos1.getXValue(), pos1.getYValue(), pos2.getXValue(), pos2.getYValue());

            if (mark1.getTeamName().toLowerCase().contains("start") || mark1.getTeamName().toLowerCase().contains("finish")) {
                //Start or finish line

                if (didCrossLine(boat, l1)) {
                    System.out.println("Crossed line");
                    handler.markRoundingEvent(boat.getSourceID(), boat.getCurrentLegIndex());
                    boat.setCurrentLegIndex(nextLegIndex);
                }
            }
            else {
               //Gate - using distance based for now
                Double distance = raceCourse.distanceBetweenGPSPoints(pos1, boat.getPosition());
                if (distance < roundingRadius) {
                    handler.markRoundingEvent(boat.getSourceID(), boat.getCurrentLegIndex());
                    boat.setCurrentLegIndex(nextLegIndex);
                }
            }
        }
        else {
            //MARK - using distance based for now
            MutablePoint pos1 = markBoats.get(markIds.get(0)).getPosition();
            Double distance = raceCourse.distanceBetweenGPSPoints(pos1, boat.getPosition());
            if (distance < roundingRadius) {
                handler.markRoundingEvent(boat.getSourceID(), boat.getCurrentLegIndex());
                boat.setCurrentLegIndex(nextLegIndex);
            }

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
        double dist = pointDistance(boat.getPosition().getXValue(), boat.getPosition().getYValue(), line.getStartX(), line.getStartY(), line.getEndX(), line.getEndY());

        if (dist < lineCrossThreshold) {
            return true;
        }
        return false;
    }


    /**
     * Calculates the distance between a point and a line segment
     * @param x Point x
     * @param y Point y
     * @param x1 lineStartx
     * @param y1 lineStarty
     * @param x2 lineEndx
     * @param y2 lineEndy
     * @return double, the shortest distance
     */
    private double pointDistance(double x, double y, double x1, double y1, double x2, double y2) {

        double A = x - x1;
        double B = y - y1;
        double C = x2 - x1;
        double D = y2 - y1;

        double dot = A * C + B * D;
        double len_sq = C * C + D * D;
        double param = -1;
        if (len_sq != 0) //in case of 0 length line
            param = dot / len_sq;

        double xx, yy;

        if (param < 0) {
            xx = x1;
            yy = y1;
        }
        else if (param > 1) {
            xx = x2;
            yy = y2;
        }
        else {
            xx = x1 + param * C;
            yy = y1 + param * D;
        }

        double dx = x - xx;
        double dy = y - yy;
        return (double) Math.sqrt(dx * dx + dy * dy);
    }





    /**
     * Calculates if the boat collides with any course features and adjusts the boats position
     * @param boat Competitor the boat to check collisions for
     */
    private void handleCourseCollisions(Competitor boat) throws IOException, InterruptedException {

        final double collisionRadius = 55; //Large for testing

        for (Integer id: markBoats.keySet()) {
            Competitor mark = markBoats.get(id);
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
