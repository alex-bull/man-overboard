package mockDatafeed;

import javafx.scene.shape.Line;
import models.*;
import org.jdom2.JDOMException;
import parsers.xml.race.RaceData;
import parsers.xml.race.RaceXMLParser;
import utilities.CollisionUtility;
import utilities.PolarTable;
import utility.Calculator;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static java.lang.Math.*;
import static java.lang.Math.PI;
import static java.lang.Math.sin;
import static parsers.BoatStatusEnum.DSQ;
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
        this.buildRoundingLines();
    }





    /**
     * updates the position of all the boats given the boats speed and heading
     * @param windGenerator WindGenerator
     * @throws IOException IOException
     * @throws InterruptedException InterruptedException
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
            if(boat.getStatus() != DSQ) {
                if (boat.hasSailsOut()) {
                    boat.setVelocity(speed * 3);
                    boat.updatePosition(0.1);
                } else {
                    boat.setVelocity(0);
                }
            } else boat.setVelocity(0);

            boolean courseCollision = this.handleCourseCollisions(boat);
            boolean boatCollision = this.handleBoatCollisions(boat);
            boolean boundaryCollision =this.handleBoundaryCollisions(boat);

            if(courseCollision || boatCollision || boundaryCollision) {
                boat.updateHealth(-15);
                handler.boatStateEvent(boat.getSourceID(), boat.getHealthLevel());
            }

//            boat.blownByWind(twa);
            this.handleRounding(boat);
        }
    }


    /**
     * Calculates if the boat rounds the next course feature and adjusts the boats leg index and sends a rounding packet
     * @param boat Competitor the boat to check roundings for
     */
    private void handleRounding(Competitor boat) throws IOException, InterruptedException {

        int nextLegIndex = boat.getCurrentLegIndex() + 1;

        List<Integer> markIds = raceData.getLegIndexToMarkSourceIds().get(nextLegIndex);
        if (markIds == null || markIds.size() < 1) return; //race is done

        if (markIds.size() == 2) { //Gate

            Competitor mark1 = markBoats.get(markIds.get(0));

            if (mark1.getTeamName().toLowerCase().contains("start") || mark1.getTeamName().toLowerCase().contains("finish")) {
                lineRounding(boat, markIds);
            } else if (nextLegIndex == 5) { //TODO:- A bit of a hack. The last time through the lee gate it is treated as a line
                lineRounding(boat, markIds);
            } else {
                gateRounding(boat, markIds);
            }
        } else {
            //MARK
            markRounding(boat);
        }
    }


    /**
     * Check for rounding of a mark
     * @param boat Competitor the boat to check
     */
    private void markRounding(Competitor boat) {

        int targetLegIndex = boat.getCurrentLegIndex() + 1;
        Competitor mark = markBoats.get(raceData.getLegIndexToMarkSourceIds().get(targetLegIndex).get(0));

        if (boat.isRounding()) {
            //have crossed first line - Check for crossing line to the side of mark
            if (didCrossLine(boat, mark.getRoundingLine2())) {
                boat.finishedRounding();
                handler.markRoundingEvent(boat.getSourceID(), boat.getCurrentLegIndex());
                boat.updateHealth(15);
                handler.boatStateEvent(boat.getSourceID(), boat.getHealthLevel());

            }
        } else {
            //Check for rounding a line down from the mark
            if (didCrossLine(boat, mark.getRoundingLine1())) {
                System.out.println("Crossed line 1");
                boat.startRounding();
            }
        }
    }


    /**
     * Check for rounding of a gate -does not include finish or start line
     * @param boat Competitor the boat to check
     * @param markIds List the marks of the gate
     */
    private void gateRounding(Competitor boat, List<Integer> markIds) {

        Competitor mark1 = markBoats.get(markIds.get(0));
        Competitor mark2 = markBoats.get(markIds.get(1));
        if (boat.isRounding()) { //has already passed between the marks

            if (didCrossLine(boat, mark1.getRoundingLine2()) || didCrossLine(boat, mark2.getRoundingLine2())) {
                boat.finishedRounding();
                handler.markRoundingEvent(boat.getSourceID(), boat.getCurrentLegIndex());
                boat.updateHealth(15);
                handler.boatStateEvent(boat.getSourceID(), boat.getHealthLevel());

            }
        } else {

            if (didCrossLine(boat, mark1.getRoundingLine1())) {
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
        Competitor mark = markBoats.get(markIds.get(0));

        if (didCrossLine(boat, mark.getRoundingLine1())) {
            System.out.println("Crossed line");
            handler.markRoundingEvent(boat.getSourceID(), boat.getCurrentLegIndex());
            boat.setCurrentLegIndex(nextLegIndex);
            boat.updateHealth(15);
            handler.boatStateEvent(boat.getSourceID(), boat.getHealthLevel());

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
     * Calculates the rounding lines of each mark and stores them in the mark.
     * These lines are used to determine whether or not a boat has rounded a mark
     */
    private void buildRoundingLines() {

        int index = 0;
        List<Integer> markIds = raceData.getLegIndexToMarkSourceIds().get(index + 1);

        while (markIds != null && markIds.size() > 0) {

            if (markIds.size() == 2) { //GATE

                Competitor mark1 = markBoats.get(markIds.get(0));
                Competitor mark2 = markBoats.get(markIds.get(1));

                MutablePoint pos1 = markBoats.get(markIds.get(0)).getPosition();
                MutablePoint pos2 = markBoats.get(markIds.get(1)).getPosition();

                Line l1 = new Line(pos1.getXValue(), pos1.getYValue(), pos2.getXValue(), pos2.getYValue());

                Vector2D vec = new Vector2D(pos1.getXValue(), pos1. getYValue(), pos2.getXValue(), pos2.getYValue());
                vec.normalise();
                Line l2 = new Line(pos1.getXValue(), pos1. getYValue(), vec.getX() * -200, vec.getY() * -200);

                Vector2D vec2 = new Vector2D(pos2.getXValue(), pos2. getYValue(), pos1.getXValue(), pos1.getYValue());
                vec2.normalise();
                Line l3 = new Line(pos2.getXValue(), pos2. getYValue(), vec2.getX() * -200, vec2.getY() * -200);

                mark1.setRoundingLine1(l1);
                mark1.setRoundingLine2(l2);
                mark2.setRoundingLine1(l1);
                mark2.setRoundingLine2(l3);

            } else { //MARK

                int targetLegIndex = index + 1;
                int nextLegIndex = index + 2;
                int previousIndex = index;

                Competitor mark = markBoats.get(raceData.getLegIndexToMarkSourceIds().get(targetLegIndex).get(0));

                MutablePoint targetPos = markBoats.get(raceData.getLegIndexToMarkSourceIds().get(targetLegIndex).get(0)).getPosition();
                MutablePoint nextPos = markBoats.get(raceData.getLegIndexToMarkSourceIds().get(nextLegIndex).get(0)).getPosition();
                MutablePoint prevPos = markBoats.get(raceData.getLegIndexToMarkSourceIds().get(previousIndex).get(0)).getPosition();

                Vector2D vec = new Vector2D(targetPos.getXValue(), targetPos.getYValue(), nextPos.getXValue(), nextPos.getYValue());
                vec.normalise();
                Line l1 = new Line(targetPos.getXValue(), targetPos. getYValue(), vec.getX() * -200, vec.getY() * -200);

                Vector2D vec2 = new Vector2D(targetPos.getXValue(), targetPos. getYValue(), prevPos.getXValue(), prevPos.getYValue());
                vec.normalise();
                Line l2 = new Line(targetPos.getXValue(), targetPos. getYValue(), vec2.getX() * -200, vec2.getY() * -200);

                mark.setRoundingLine1(l1);
                mark.setRoundingLine2(l2);
            }

            index += 1;
            markIds = raceData.getLegIndexToMarkSourceIds().get(index + 1);
        }
    }


    /**
     * Calculates if the boat collides with the course boundary, if so then pushes back the boat.
     * @param boat Competitor the boat to check collisions for
     * @return boolean if the boat has collided
     */

    private boolean handleBoundaryCollisions(Competitor boat) throws IOException, InterruptedException {
        double collisionRadius = 50;
        boolean collision = false;
        for (MutablePoint point: courseBoundary) {
            double distance = raceCourse.distanceBetweenGPSPoints(boat.getPosition(), point);
            if (distance <= collisionRadius) {
                handler.yachtEvent(boat.getSourceID(), 1);
                boat.updatePosition(-10);
                collision = true;
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
                collision = true;
                break;
            }
        }
        return collision;
    }


    /**
     * Calculates if the boat collides with any course features and adjusts the boats position
     * @param boat Competitor the boat to check collisions for
     * @return boolean true if the boat has collided
     */
    private boolean handleCourseCollisions(Competitor boat) throws IOException, InterruptedException {

        final double collisionRadius = 55; //Large for testing

        for (Integer id: markBoats.keySet()) {
            Competitor mark = markBoats.get(id);
            double distance = raceCourse.distanceBetweenGPSPoints(mark.getPosition(), boat.getPosition());

            if (distance <= collisionRadius) {
//                send a collision packet
                handler.yachtEvent(boat.getSourceID(),1);
                boat.updatePosition(-10);
                return true;
            }
        }
        return false;
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
     * @return boolean true if the boat has collided
     */
    private boolean handleBoatCollisions(Competitor boat) throws IOException, InterruptedException {

        final double collisionRadius = 35; //Large for testing
        boolean collided = false;
        //Can bump back a fixed amount or try to simulate a real collision.
        for (Competitor comp: this.competitors.values()) {

            if (comp.getSourceID() == boat.getSourceID()) continue; //cant collide with self

            double distance = raceCourse.distanceBetweenGPSPoints(comp.getPosition(), boat.getPosition());

            if (distance <= collisionRadius) {

//                send a collision packet
                handler.yachtEvent(comp.getSourceID(),1);
                collided = true;
//                calculateCollisions(comp,boat);
                boat.updatePosition(-10);
                comp.updatePosition(-10);
            }
        }
        return collided;
    }
}
