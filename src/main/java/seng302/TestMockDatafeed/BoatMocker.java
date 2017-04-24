package seng302.TestMockDatafeed;

import javafx.scene.paint.Color;
import seng302.Factories.CourseFactory;
import seng302.Model.Boat;
import seng302.Model.Course;
import seng302.Model.MutablePoint;
import seng302.Model.RaceCourse;

import java.io.IOException;
import java.util.*;

/**
 * Created by khe60 on 24/04/17.
 */
public class BoatMocker extends TimerTask{
    private  MutablePoint prestart;
    List<Boat> competitors;
    Course raceCourse;
    BinaryPackager binaryPackager;
    DataSender dataSender;

    public BoatMocker() throws IOException {
        binaryPackager=new BinaryPackager();
        dataSender=new DataSender(4941);
        prestart=new MutablePoint(32.29700,-64.861);
    }

    /**
     * finds the current course of the race
     */
    public void generateCourse(){
        CourseFactory cf=new CourseFactory();
        //screen size is not important
        raceCourse=cf.createCourse(1000.0,1000.0,"src/main/resources/new_format_course.xml");
    }


    /**
     * generates the competitors list given numBoats
     * @param numBoats the number of boats to generate
     */
    public void generateCompetitors(int numBoats){
        competitors=new ArrayList<>();
        //generate all boats
        competitors.add(new Boat("Oracle Team USA", 22, prestart, Color.BLACK,"USA"));
        competitors.add(new Boat("Emirates Team New Zealand", 20,prestart, Color.RED,"NZL"));
        competitors.add(new Boat("Ben Ainslie Racing", 18, prestart, Color.PURPLE, "GBR"));
        competitors.add(new Boat("SoftBank Team Japan", 16,prestart, Color.YELLOW,"JPN"));
        competitors.add(new Boat("Team France", 15, prestart, Color.BROWN,"FRA"));
        competitors.add(new Boat("Artemis Racing", 19, prestart, Color.GREEN,"SWE"));

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
        BoatMocker me= null;
        try {
            me = new BoatMocker();
            //find out the coordinates of the course
            me.generateCourse();

            //generate the boats
            me.generateCompetitors(1);


            //start the race, updates boat position at a rate of 10 hz
            Timer raceTimer=new Timer();
            raceTimer.schedule(me,0,1000);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    /**
     * Sends boat info to port
     * but right now just print it
     */
    public void sendData() throws IOException {
        for(int i =0;i<competitors.size();i++){
            Boat boat=competitors.get(i);
            byte[] boatinfo=binaryPackager.packageBoatLocation(i,boat.getPosition().getXValue(),boat.getPosition().getYValue(),
                    boat.getCurrentHeading(),boat.getVelocity());
            dataSender.sendData(boatinfo);
            System.out.println("sent");
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
        try {
            sendData();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
