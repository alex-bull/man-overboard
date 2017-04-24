package seng302.TestMockDatafeed;

import javafx.scene.paint.Color;
import seng302.Factories.CourseFactory;
import seng302.Model.Boat;
import seng302.Model.Course;
import seng302.Model.MutablePoint;
import seng302.Model.RaceCourse;

import java.util.*;

/**
 * Created by khe60 on 24/04/17.
 */
public class BoatMocker extends TimerTask{
    private final MutablePoint PRESTART=new MutablePoint(32.29700,-64.861);
    List<Boat> competitors;
    Course raceCourse;

    /**
     * finds the current course of the race
     */
    private void generateCourse(){
        CourseFactory cf=new CourseFactory();
        //screen size is not important
        raceCourse=cf.createCourse(1000.0,1000.0,"src/main/resources/new_format_course.xml");
    }


    /**
     * generates the competitors list given numBoats
     * @param numBoats the number of boats to generate
     */
    private void generateCompetitors(int numBoats){
        competitors=new ArrayList<>();
        //generate all boats
        competitors.add(new Boat("Oracle Team USA", 22, PRESTART, Color.BLACK,"USA"));
        competitors.add(new Boat("Emirates Team New Zealand", 20,PRESTART, Color.RED,"NZL"));
        competitors.add(new Boat("Ben Ainslie Racing", 18, PRESTART, Color.PURPLE, "GBR"));
        competitors.add(new Boat("SoftBank Team Japan", 16,PRESTART, Color.YELLOW,"JPN"));
        competitors.add(new Boat("Team France", 15, PRESTART, Color.BROWN,"FRA"));
        competitors.add(new Boat("Artemis Racing", 19, PRESTART, Color.GREEN,"SWE"));

        //set initial heading
        for(Boat b: competitors){
            b.setCurrentHeading(raceCourse.getPoints().get(0).getExitHeading());
        }

        //randomly select competitors
        Collections.shuffle(competitors);
        competitors = competitors.subList(0, numBoats);
    }

    /**
     * updates the position of all the boats given the boats speed and heading
     * @param dt the time passed
     */
    private void updatePosition(double dt){

        for(Boat boat: competitors){
            boat.updatePosition(dt);
        }
    }

    /**
     * initializes the boats
     * @param args
     */
    public static void main(String [] args){
        BoatMocker me=new BoatMocker();
        //find out the coordinates of the course
        me.generateCourse();

        //generate the boats
        me.generateCompetitors(1);


        //start the race, updates boat position at a rate of 10 hz
        Timer raceTimer=new Timer();
        raceTimer.schedule(me,0,1000);
    }

    /**
     * Sends boat info to port
     * but right now just print it
     */
    public void sendData(){
        for(Boat b: competitors) {
//            System.out.println(b.getCurrentHeading());
        }
    }
    /**
     * updates the boats location
     */
    @Override
    public void run() {
        //check if boats are at the end of the leg
        for(Boat b: competitors){
            //if at the end stop
            if(b.getCurrentLegIndex()==raceCourse.getPoints().size()-1){
                b.setVelocity(0);
                continue;
            }

//            //update direction if they are
//            System.out.println(b.getPosition());
//            System.out.println(raceCourse.getPoints().get(b.getCurrentLegIndex()+1).getGPSPoint());
            if(b.getPosition().isWithin(raceCourse.getPoints().get(b.getCurrentLegIndex()+1).getGPSPoint())){
                System.out.println("123");
                b.setCurrentLegIndex(b.getCurrentLegIndex()+1);
                b.setCurrentHeading(raceCourse.getPoints().get(b.getCurrentLegIndex()).getExitHeading());
                System.out.println(b.getCurrentLegIndex());
            }
        }
        //update the position of the boats given the current position, heading and velocity
        updatePosition(1);
        //send the boat info to receiver
        sendData();
    }
}
