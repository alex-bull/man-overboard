package mockDatafeed;

import javafx.scene.shape.Line;
import models.*;
import org.jdom2.JDOMException;
import parsers.xml.race.RaceData;
import utilities.PolarTable;
import utilities.Utility;
import utility.Calculator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static java.lang.Math.PI;
import static java.lang.Math.abs;
import static java.lang.Math.atan2;
import static parsers.BoatStatusEnum.DSQ;
import static utilities.CollisionUtility.calculateFinalVelocity;
import static utilities.CollisionUtility.isPointInPolygon;
import static utility.Calculator.*;
import static utility.Projection.mercatorProjection;

/**
 * Created by jar156 on 3/08/17.
 * Boat Updater
 */
public class BoatUpdater {

    List<Competitor> finisherList = new ArrayList<>();
    private PolarTable polarTable;
    private Utility utility;
    private RaceData raceData;
    private Map<Integer, Competitor> competitors;
    private Course raceCourse = new RaceCourse(null, true);
    private Map<Integer, Competitor> markBoats;
    private BoatUpdateEventHandler handler;
    private List<MutablePoint> courseBoundary;
    private WindGenerator windGenerator;
    private int crewLocationSourceID = 0;
    private int bloodlocationSourceID = 0;
    private int sharkSourceID = 0;
    private int sharkRoamIndex = 0;
    private int leadLeg = 0;
    private int whirlpoolSourceID = 0;
    private MutablePoint sharkRoamPos;
    private List<CrewLocation> crewMembers = new ArrayList<>();
    private List<Shark> sharks = new ArrayList<>();
    private List<Blood> bloodList = new ArrayList<>();
    private List<Whirlpool> whirlpools = new ArrayList<>();


    /**
     * Boat updater constructor
     *
     * @param competitors    Map a hashmap of competitors in the race
     * @param markBoats      Map course features
     * @param raceData       RaceData race data
     * @param handler        BoatUpdateEventHandler boat mocker
     * @param courseBoundary List a list of course boundary points
     * @throws IOException   IOException
     * @throws JDOMException JDOMException
     */
    BoatUpdater(Map<Integer, Competitor> competitors, Map<Integer, Competitor> markBoats, RaceData raceData,
                BoatUpdateEventHandler handler, List<MutablePoint> courseBoundary, WindGenerator windGenerator) throws IOException, JDOMException {
        this.competitors = competitors;
        this.markBoats = markBoats;
        this.handler = handler;
        this.raceData = raceData;
        this.courseBoundary = courseBoundary;
        this.windGenerator = windGenerator;

        polarTable = new PolarTable("/polars/VO70_polar.txt", 12.0);
        utility = new Utility();
        this.buildRoundingLines();
    }


    /**
     * updates the position of all the boats given the boats speed and heading
     *
     * @throws IOException          IOException
     * @throws InterruptedException InterruptedException
     */
    void updatePosition() throws IOException, InterruptedException {
        boolean crewMemberUpdated = false;
        for (Integer sourceId : competitors.keySet()) {
            Competitor boat = competitors.get(sourceId);
            short windDirection = windGenerator.getWindDirection();
            double twa = abs(shortToDegrees(windDirection) - boat.getCurrentHeading());
            if (twa > 180) {
                twa = 180 - (twa - 180); // interpolator only goes up to 180
            }
            double speed = polarTable.getSpeed(twa);
            if (boat.getStatus() != DSQ) {
                if (boat.hasSailsOut()) {
                    boat.getBoatSpeed().increase(0.01);
                    boat.getBoatSpeed().setMagnitude(speed * 4);
                    boat.getBoatSpeed().setDirection(boat.getCurrentHeading());
                } else {
                    boat.getBoatSpeed().reduce(0.99);
                }
            } else {
                boat.getBoatSpeed().reduce(0.99);
            }

            crewMemberUpdated = crewMemberUpdated || pickUpCrew(boat) || crewEaten();
            boat.updatePosition(0.1);


            boolean courseCollision = this.handleCourseCollisions(boat);
            handleBoatCollisions(boat);
            boolean boundaryCollision = this.handleBoundaryCollisions(boat);
            boolean whirlPoolCollision = this.handleWhirlpoolCollisions(boat);

            if (courseCollision || boundaryCollision || whirlPoolCollision) {
                boat.updateHealth(-15);
                handler.boatStateEvent(boat.getSourceID(), boat.getHealthLevel());
            }

//            boat.blownByWind(twa);
            this.handleRounding(boat);

            if (courseBoundary != null && sharks.isEmpty()) {
                createShark();
            }

            if (passedFirstMark()) {
                createWhirlpool();
            }

            if (!whirlpools.isEmpty()) {
                if (updateLeadLeg()) {
                    updateWhirlpool();
                }
                handler.whirlpoolEvent(whirlpools);
            }

            if(!sharks.isEmpty()) {
                updateShark();
                handler.sharkEvent(sharks);
            }
        }
        if (crewMemberUpdated) {
            handler.fallenCrewEvent(crewMembers);
            handler.bloodEvent(bloodList);
        }

    }


    /**
     * function to check if crew member can be picked up
     *
     * @param boat competitor
     * @return boolean if the crew has been picked up
     */
    private boolean pickUpCrew(Competitor boat) {
        boolean updated = false;
        for (CrewLocation crewLocation : new ArrayList<>(crewMembers)) {
            if (boat.getPosition().isWithin(crewLocation.getPosition(), 0.0001)) {
                crewMembers.remove(crewLocation);
                boat.updateHealth(crewLocation.getNumCrew());
                handler.boatStateEvent(boat.getSourceID(), boat.getHealthLevel());
                updated = true;
            }
        }

        return updated;
    }


    /**
     * function to check if a crew member has been eaten by a shark
     * @return boolean if a crew member has been eaten
     */
    private boolean crewEaten() throws IOException{
        boolean updated = false;
        for (CrewLocation crewLocation : new ArrayList<>(crewMembers)) {
            for (Shark shark : new ArrayList<>(sharks)){
                if (shark.getPosition().isWithin(crewLocation.getPosition(), 0.0001)) {
                    Blood blood = new Blood(bloodlocationSourceID++,crewLocation.getPosition());
                    bloodList.add(blood);
                    crewMembers.remove(crewLocation);
                    updated = true;

                }
            }
        }


        return updated;

    }

    /**
     * Checks whether if any of the players have passed the first mark
     * @return boolean if a player has passed the first mark
     */
    private boolean passedFirstMark() {
        boolean passed = false;
        for (Integer sourceId : competitors.keySet()) {
            Competitor boat = competitors.get(sourceId);
            if (boat.getCurrentLegIndex() > 0) {
                passed = true;
            }
        }
        return passed;
    }

    /**
     * checks whether the leader has advanced to the next mark
     * @return boolean if the leader has advanced to the next mark
     */
    private boolean updateLeadLeg() {
        boolean updated = false;
        for (Integer sourceId : competitors.keySet()) {
            Competitor boat = competitors.get(sourceId);
            if (leadLeg < boat.getCurrentLegIndex()) {
                leadLeg = boat.getCurrentLegIndex();
                updated = true;
            }
        }
        return updated;
    }

    /**
     * Randomly generates whirlpool after players have passed the first mark
     */
    private void createWhirlpool() {
        if ((whirlpools.size() < 2)) {
            MutablePoint centroid = utility.centroid(courseBoundary);
            double angle = Math.random()*2*Math.PI;
            double xOff = Math.cos(angle) * 0.05;
            double yOff = Math.sin(angle) * 0.05;
            MutablePoint whirlpoolPos = new MutablePoint(centroid.getXValue() + xOff, centroid.getYValue() + yOff);
            if (isPointInPolygon(whirlpoolPos, courseBoundary)) {
                int currentLeg = leadLeg;
                Whirlpool whirlpool = new Whirlpool(whirlpoolSourceID++, currentLeg, whirlpoolPos);
                whirlpools.add(whirlpool);
            }
        }
    }

    /**
     * Updates whirlpool position whenever leader advances to next mark
     */
    private void updateWhirlpool() {
        for (Whirlpool whirlpool:whirlpools) {
            MutablePoint centroid = utility.centroid(courseBoundary);
            double angle = Math.random()*2*Math.PI;
            double xOff = Math.cos(angle) * 0.01;
            double yOff = Math.sin(angle) * 0.01;
            MutablePoint whirlpoolPos = new MutablePoint(centroid.getXValue() + xOff, centroid.getYValue() + yOff);
            if (isPointInPolygon(whirlpoolPos, courseBoundary)) {
                int currentLeg = leadLeg;
                whirlpool.setCurrentLeg(currentLeg);
                whirlpool.setPosition(whirlpoolPos);
            }
        }
    }

    /**
     * creates Shark at the start
     */
    private void createShark() {
        if(sharks.size() < 2) {
            int velocity = 50;
            double sharkPosX = courseBoundary.get(0).getXValue() + 0.005;
            double sharkPosY = courseBoundary.get(0).getYValue() - 0.02;
            MutablePoint sharkPosition = new MutablePoint(sharkPosX, sharkPosY);
            Shark shark = new Shark(sharkSourceID++, 1, sharkPosition, velocity, 0);
            sharks.add(shark);
        }
        updateSharkRoam();
    }

    /**
     * updates shark's next target for roaming
     */
    private void updateSharkRoam() {
        Random random = new Random();
        double PosX = courseBoundary.get(sharkRoamIndex).getXValue();
        double PosY = courseBoundary.get(sharkRoamIndex).getYValue() - 0.02;
        sharkRoamPos = new MutablePoint(PosX, PosY);

        for (Shark shark : new ArrayList<>(sharks)){
            if (shark.getPosition().isWithin(sharkRoamPos, 0.0001)) {
                sharkRoamIndex = random.nextInt(courseBoundary.size());
            }
        }
    }

    /**
     * update the position and heading of the Obstacles
     */
    private void updateShark() {
        if (!crewMembers.isEmpty()) {
            for (Shark shark : sharks) {
                double crew_x = crewMembers.get(0).getLatitude();
                double crew_y = crewMembers.get(0).getLongitude();
                double angle = atan2(crew_y - shark.getLongitude(), crew_x - shark.getLatitude()) * 180 / PI;
                angle = (angle % 360 + 360) % 360;
                shark.setHeading(angle);
                shark.getSharkSpeed().setDirection(shark.getHeading());
                shark.updatePosition(0.1);
            }
        }
        else {
            updateSharkRoam();
            for (Shark shark : sharks) {
                double angle = atan2(sharkRoamPos.getYValue() - shark.getLongitude(), sharkRoamPos.getXValue()  - shark.getLatitude()) * 180 / PI;
                angle = (angle % 360 + 360) % 360;
                shark.setHeading(angle);
                shark.getSharkSpeed().setDirection(shark.getHeading());
                shark.updatePosition(0.1);
            }
        }
    }





    /**
     * Calculates if the boat rounds the next course feature and adjusts the boats leg index and sends a rounding packet
     *
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
     *
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
                boat.startRounding();
            }
        }
    }


    /**
     * Check for rounding of a gate -does not include finish or start line
     *
     * @param boat    Competitor the boat to check
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
                boat.startRounding();
            }
        }
    }


    /**
     * Check for boat crossing a start or finish line
     *
     * @param boat    Competitor the boat to check
     * @param markIds List the marks of the line
     */
    private void lineRounding(Competitor boat, List<Integer> markIds) {

        int nextLegIndex = boat.getCurrentLegIndex() + 1;
        Competitor mark = markBoats.get(markIds.get(0));

        if (didCrossLine(boat, mark.getRoundingLine1())) {
            boat.setCurrentLegIndex(nextLegIndex);
            handler.markRoundingEvent(boat.getSourceID(), nextLegIndex);

            boat.updateHealth(15);
            handler.boatStateEvent(boat.getSourceID(), boat.getHealthLevel());

        }

        if (boat.getCurrentLegIndex() == 6) {
            if (!finisherList.contains(boat)) {
                boat.switchSails();
                finisherList.add(boat);
            }
        }

    }


    /**
     * Checks if all boats have finished the racing
     *
     * @return true if all boats have finished racing
     */
    boolean checkAllFinished() {
        if (finisherList.size() == competitors.size()) {
            return true;
        }
        return false;
    }


    /**
     * Checks if a boat is within a threshold distance of a line
     *
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
            if (markIds.size() == 2) {
                buildGateLines(markIds);
            } else {
                buildMarkLines(index);
            }
            index += 1;
            markIds = raceData.getLegIndexToMarkSourceIds().get(index + 1);
        }
    }

    /**
     * Build invisible mark lines to determine whether a boat has rounded a mark
     *
     * @param index int index
     */
    private void buildMarkLines(int index) {
        int targetLegIndex = index + 1;
        int nextLegIndex = index + 2;

        Competitor mark = markBoats.get(raceData.getLegIndexToMarkSourceIds().get(targetLegIndex).get(0));

        MutablePoint targetPos = markBoats.get(raceData.getLegIndexToMarkSourceIds().get(targetLegIndex).get(0)).getPosition();
        MutablePoint nextPos = markBoats.get(raceData.getLegIndexToMarkSourceIds().get(nextLegIndex).get(0)).getPosition();
        MutablePoint prevPos = markBoats.get(raceData.getLegIndexToMarkSourceIds().get(index).get(0)).getPosition();

        Vector2D vec = new Vector2D(targetPos.getXValue(), targetPos.getYValue(), nextPos.getXValue(), nextPos.getYValue());
        vec.normalise();
        Line l1 = new Line(targetPos.getXValue(), targetPos.getYValue(), vec.getX() * -200, vec.getY() * -200);

        Vector2D vec2 = new Vector2D(targetPos.getXValue(), targetPos.getYValue(), prevPos.getXValue(), prevPos.getYValue());
        vec.normalise();
        Line l2 = new Line(targetPos.getXValue(), targetPos.getYValue(), vec2.getX() * -200, vec2.getY() * -200);

        mark.setRoundingLine1(l1);
        mark.setRoundingLine2(l2);
    }

    /**
     * Build invisible gate lines to determine whether a boat has passed a gate
     *
     * @param markIds List a list of mark ids
     */
    private void buildGateLines(List<Integer> markIds) {
        Competitor mark1 = markBoats.get(markIds.get(0));
        Competitor mark2 = markBoats.get(markIds.get(1));

        MutablePoint pos1 = mark1.getPosition();
        MutablePoint pos2 = mark2.getPosition();

        Line l1 = new Line(pos1.getXValue(), pos1.getYValue(), pos2.getXValue(), pos2.getYValue());

        Vector2D vec = new Vector2D(pos1.getXValue(), pos1.getYValue(), pos2.getXValue(), pos2.getYValue());
        vec.normalise();
        Line l2 = new Line(pos1.getXValue(), pos1.getYValue(), vec.getX() * -200, vec.getY() * -200);

        Vector2D vec2 = new Vector2D(pos2.getXValue(), pos2.getYValue(), pos1.getXValue(), pos1.getYValue());
        vec2.normalise();
        Line l3 = new Line(pos2.getXValue(), pos2.getYValue(), vec2.getX() * -200, vec2.getY() * -200);

        mark1.setRoundingLine1(l1);
        mark1.setRoundingLine2(l2);
        mark2.setRoundingLine1(l1);
        mark2.setRoundingLine2(l3);
    }


    /**
     * Calculates if the boat collides with any course features and adjusts the boats position
     *
     * @param boat Competitor the boat to check collisions for0
     */
    private boolean handleCourseCollisions(Competitor boat) throws IOException, InterruptedException {

        for (Competitor mark : markBoats.values()) {
            double collisionRadius = mark.getCollisionRadius() + boat.getCollisionRadius();

            double distance = raceCourse.distanceBetweenGPSPoints(mark.getPosition(), boat.getPosition());

            if (distance <= collisionRadius) {
//              send a collision packet
                handler.yachtEvent(boat.getSourceID(), 1);

                collisionHandler(boat.getPosition(), boat.getVelocity(), 10);
                boat.updatePosition(-10);
                return true;
            }
        }
        return false;
    }

    /**
     * Calculates if the boat collides with the course boundary, if so then pushes back the boat.
     *
     * @param boat Competitor the boat to check collisions for
     */

    private boolean handleBoundaryCollisions(Competitor boat) throws IOException, InterruptedException {

        if (!isPointInPolygon(boat.getPosition(), courseBoundary)) {
            handler.yachtEvent(boat.getSourceID(), 1);

            collisionHandler(boat.getPosition(), boat.getVelocity(), 10);
            boat.updatePosition(-10);
            return true;
        }
        return false;
    }

    /**
     * Calculates if the boat collides with the whirlpool
     * @param boat Competitor the boat to check collisions for
     * @return boolean if boat collides
     * @throws IOException IOException
     * @throws InterruptedException InterruptedException
     */
    private boolean handleWhirlpoolCollisions(Competitor boat) throws IOException, InterruptedException {
        for (Whirlpool whirlpool: whirlpools) {
            double collisionRadius = boat.getCollisionRadius() + whirlpool.getCollisionRadius();
            double distance = raceCourse.distanceBetweenGPSPoints(whirlpool.getPosition(), boat.getPosition());
            if (distance <= collisionRadius) {
                handler.yachtEvent(boat.getSourceID(), 2);

                collisionHandler(boat.getPosition(), whirlpool.getCollisionMaginitude(), 10);
                boat.updatePosition(-10);
                return true;
            }

        }
        return false;
    }


    /**
     * function to calculate what happens during collision
     *
     * @param boat1 one of the boat during collision
     * @param boat2 the other boat during collision
     */
    private void calculateCollisions(Competitor boat1, Competitor boat2) throws IOException {

        MutablePoint p1 = mercatorProjection(boat1.getPosition().getXValue(), boat1.getPosition().getYValue());
        MutablePoint p2 = mercatorProjection(boat2.getPosition().getXValue(), boat2.getPosition().getYValue());

        Force v1 = boat1.getBoatSpeed();
        v1.round();
        Force v2 = boat2.getBoatSpeed();
        v2.round();
        Force v1f = calculateFinalVelocity(v1, v2, p1, p2);
        Force v2f = calculateFinalVelocity(v2, v1, p2, p1);

        //collision event
        collisionHandler(boat1.getPosition(), v1f.getMagnitude(), 15);
        collisionHandler(boat2.getPosition(), v2f.getMagnitude(), 15);

        boat1.updatePosition(-10);
        boat2.updatePosition(-10);
        //momentum

        boat1.addForce((Force) multiply(5, v1f));
        boat2.addForce((Force) multiply(5, v2f));


    }

    /**
     * handler for collision, currently throws members off the boat
     * MUST BE CALLED BEFORE BOAT.UPDATEPOSITION !!!!!!!!!!!!
     *
     * @param location  the location where the collision happened
     * @param magnitude the magnitude of the collision
     * @param health    the health reduced, 5 crew members per location
     */
    private void collisionHandler(MutablePoint location, double magnitude, double health) throws IOException {
        Random randomGenerator = new Random();
        int numLocation = (int) health / 5;
        for (int i = 0; i < numLocation; i++) {

            //distance from the boat with mean magnitude and variance magnitude/2
            double distance = magnitude * 10 + randomGenerator.nextGaussian() * magnitude * 5;
            //angle from the boat collision from 0 to 360 degrees
            double angle = randomGenerator.nextDouble() * 360;
            MutablePoint position = movePoint(new Force(distance, angle, false), location, 1);

            CrewLocation crewLocation = new CrewLocation(crewLocationSourceID++, 5, position);
            crewMembers.add(crewLocation);

        }
        handler.fallenCrewEvent(crewMembers);
    }






    /**
     * Calculates if the boat collides with any other boat and adjusts the position of both boats accordingly.
     *
     * @param boat Competitor, the boat to check collisions for
     */
    private boolean handleBoatCollisions(Competitor boat) throws IOException, InterruptedException {

        //Can bump back a fixed amount or try to simulate a real collision.
        for (Competitor comp : this.competitors.values()) {

            if (comp.getSourceID() == boat.getSourceID()) continue; //cant collide with self

            double collisionRadius = comp.getCollisionRadius() + boat.getCollisionRadius();


            double distance = raceCourse.distanceBetweenGPSPoints(comp.getPosition(), boat.getPosition());

            if (distance <= collisionRadius) {
//                send a collision packet
                handler.yachtEvent(comp.getSourceID(), 1);
                handler.yachtEvent(boat.getSourceID(), 1);

                comp.updateHealth(-15);
                handler.boatStateEvent(comp.getSourceID(), comp.getHealthLevel());
                calculateCollisions(comp, boat);
                return true;
            }
        }
        return false;
    }
}