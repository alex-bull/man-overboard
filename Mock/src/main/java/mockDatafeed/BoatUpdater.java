package mockDatafeed;

import com.google.common.io.ByteStreams;
import com.google.common.io.CharStreams;
import models.Competitor;
import models.Course;
import models.RaceCourse;
import models.RepelForce;
import org.jdom2.JDOMException;
import parsers.xml.race.RaceData;
import parsers.xml.race.RaceXMLParser;
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

    public BoatUpdater(Map<Integer, Competitor> competitors, Map<Integer, Competitor> markBoats, RaceData raceData, BoatUpdateEventHandler handler) throws IOException, JDOMException {
        this.competitors = competitors;
        this.markBoats = markBoats;
        this.handler = handler;
        this.raceData = raceData;

        polarTable = new PolarTable("/polars/VO70_polar.txt", 12.0);
    }


    /**
     * updates the position of all the boats given the boats speed and heading
     */
    public void updatePosition(WindGenerator windGenerator) throws IOException, InterruptedException {

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

    private boolean markIsCorrectSide(Competitor boat, Competitor mark, boolean isPortSide) {
        // port = 1, starboard = 0
        double xPosBoat = boat.getPosition().getXValue();
        double yPosBoat = boat.getPosition().getYValue();
        double xPosMark = mark.getPosition().getXValue();
        double yPosMark = mark.getPosition().getYValue();

        double heading = boat.getCurrentHeading();
        // TODO find an imaginary point the boat is heading towards
        double xPosPoint = 3;
        double yPosPoint = 3;

        double result = (xPosPoint - xPosBoat) * (yPosMark - yPosBoat) - (yPosPoint - yPosBoat) * (xPosMark - xPosBoat);
        // ((b.X - a.X)*(c.Y - a.Y) - (b.Y - a.Y)*(c.X - a.X))
        // a = boat position, b = point boat is heading towards, c = mark position
        // is port if > 0
        // is starboard if < 0

        if (result == 0) {
            // is heading directly towards mark
            return false;
        }

        if (result > 0) {
            return isPortSide;
        } else {
            return !isPortSide;
        }
    }


    /**
     * Calculates if the boat collides with any course features and adjusts the boats position
     * @param boat Competitor the boat to check collisions for
     */
    private void handleMarkRounding(Competitor boat) throws IOException, InterruptedException {
        final double roundingRadius = 200;
        int nextLegIndex = boat.getCurrentLegIndex() + 1;

        List<Integer> markIds = raceData.getLegIndexToMarkSourceIds().get(nextLegIndex);
        if (markIds == null || markIds.size() < 1) return;

        //System.out.println("Rounding direction: " + raceData.getLegIndexToRoundingDirection().get(nextLegIndex));

        for (Integer markId: markIds) { //COMPARE DISTANCE TO ALL MARKS IN THE COMPOUND MARK (1 for a mark, 2 for a gate)
            Competitor markBoat = markBoats.get(markId);
            Double distance = raceCourse.distanceBetweenGPSPoints(markBoat.getPosition(), boat.getPosition());
            if (distance < roundingRadius && markIsCorrectSide(boat, markBoat, true)) {

                handler.markRoundingEvent(boat.getSourceID(), boat.getCurrentLegIndex());
                boat.setCurrentLegIndex(nextLegIndex);
            }
        }
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
