package seng302.TestMockDatafeed;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import seng302.Factories.CourseFactory;
import seng302.Model.Boat;
import seng302.Model.Course;
import seng302.Model.MutablePoint;

import java.io.File;
import java.io.IOException;
import java.net.SocketException;
import java.time.ZonedDateTime;
import java.util.*;

/**
 * Created by khe60 on 24/04/17.
 */
public class BoatMocker extends TimerTask{
    private  MutablePoint prestart;
    List<Boat> competitors;
    List<Boat> markBoats;
    Course raceCourse;
    int raceStatus;
    ZonedDateTime expectedStartTime;

    BinaryPackager binaryPackager;
    DataSender dataSender;

    public BoatMocker() throws IOException {
        binaryPackager=new BinaryPackager();
        dataSender=new DataSender(4941);
        prestart=new MutablePoint(32.29700,-64.861);
        raceStatus=3;
        expectedStartTime=ZonedDateTime.now();
    }

    /**
     * finds the current course of the race
     */
    public void generateCourse(){
        CourseFactory cf=new CourseFactory();
        //screen size is not important
        raceCourse=cf.createCourse(1000.0,1000.0,"src/main/resources/mockXML/new_format_course.xml");
    }


    /**
     * generates the competitors list given numBoats
     * @param numBoats the number of boats to generate
     */
    public void generateCompetitors(int numBoats){
        competitors=new ArrayList<>();
        //generate all boats
        competitors.add(new Boat("Oracle Team USA", 22, prestart,"USA",101,1));
        competitors.add(new Boat("Emirates Team New Zealand", 20,prestart,"NZL",103,1));
        competitors.add(new Boat("Ben Ainslie Racing", 18, prestart,  "GBR",106,1));
        competitors.add(new Boat("SoftBank Team Japan", 16,prestart, "JPN",104,1));
        competitors.add(new Boat("Team France", 15, prestart, "FRA",105,1));
        competitors.add(new Boat("Artemis Racing", 19, prestart,"SWE",102,1));

        //generate mark boats
        markBoats=new ArrayList<>();
        markBoats.add(new Boat("Start Line 1",0,new MutablePoint(32.296577,-64.854304),"SL1",122,0));
        markBoats.add(new Boat("Start Line 2",0,new MutablePoint(32.293771,-64.855242),"SL2",123,0));
        markBoats.add(new Boat("Mark1",0,new MutablePoint(32.293039,-64.843983),"M1",131,0));
        markBoats.add(new Boat("Lee Gate 1",0,new MutablePoint(32.309693,-64.835249),"LG1",124,0));
        markBoats.add(new Boat("Lee Gate 2",0,new MutablePoint(32.308046,-64.831785),"LG2",125,0));

        markBoats.add(new Boat("Wind Gate 1",0,new MutablePoint(32.284680,-64.850045),"WG1",126,0));
        markBoats.add(new Boat("Wind Gate 2",0,new MutablePoint(32.280164,-64.847591),"WG2",127,0));

        markBoats.add(new Boat("Finish Line 1",0,new MutablePoint(32.317379,-64.839291),"FL1",128,0));
        markBoats.add(new Boat("Finish Line 2",0,new MutablePoint(32.317257,-64.836260),"FL2",129,0));

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

            //send all xml data first
            me.sendAllXML();
            //start the race, updates boat position at a rate of 10 hz
            Timer raceTimer=new Timer();
            raceTimer.schedule(me,0,1000);
        }
        catch (SocketException e){

        }
        catch (IOException e) {
            e.printStackTrace();

        }


    }

    /**
     * Sends boat info to port
     */
    public void sendBoatLocation() throws IOException {
        //send competitor boats
        for(Boat boat: competitors){
            byte[] boatinfo=binaryPackager.packageBoatLocation(boat.getSourceID(),boat.getPosition().getXValue(),boat.getPosition().getYValue(),
                    boat.getCurrentHeading(),boat.getVelocity());
            dataSender.sendData(boatinfo);
            System.out.println("sent race data");
        }
        //send mark boats
        for(Boat markBoat:markBoats){
            byte[] boatinfo=binaryPackager.packageBoatLocation(markBoat.getSourceID(),markBoat.getPosition().getXValue(),markBoat.getPosition().getYValue(),
                    markBoat.getCurrentHeading(),markBoat.getVelocity());
            dataSender.sendData(boatinfo);
        }
    }

    /**
     * Sends Race Status to outputport
     * @throws IOException
     */
    public void sendRaceStatus() throws IOException{
        //TODO: make race status message
        byte[] raceStatusPacket=binaryPackager.packageRaceStatus(123546789,raceStatus,expectedStartTime );
        byte[] eachBoatPacket=binaryPackager.packageEachBoat(competitors);
        dataSender.sendData(binaryPackager.packetRaceStatus(raceStatusPacket,eachBoatPacket));
        System.out.println("sent race status");
    }


    /**
     * Send a xml file
     */
    public void sendXml(String xmlPath, int messageType) throws IOException {
        String mockBoatsString= Files.toString(new File(xmlPath), Charsets.UTF_8);
        dataSender.sendData(binaryPackager.packageXML(mockBoatsString.length(),mockBoatsString,messageType));

    }

    /**
     * Sends all xml files
     */
    public void sendAllXML() {

        try {
            sendXml("src/main/resources/mockXML/mock_boats.xml",7);
            sendXml("src/main/resources/mockXML/mock_regatta.xml",5);
            sendXml("src/main/resources/mockXML/new_format_course.xml",6);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("asfd");
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
                b.setStatus(3);
                continue;
            }
            //set status to started
            if(b.getCurrentLegIndex()==1){
                b.setStatus(1);
                continue;
            }


//            //update direction if they are
//            System.out.println(b.getPosition());
//            System.out.println(raceCourse.getPoints().get(b.getCurrentLegIndex()+1).getGPSPoint());
            if(b.getPosition().isWithin(raceCourse.getPoints().get(b.getCurrentLegIndex()+1).getGPSPoint())){
                b.setCurrentLegIndex(b.getCurrentLegIndex()+1);
                b.setCurrentHeading(raceCourse.getPoints().get(b.getCurrentLegIndex()).getExitHeading());
                System.out.println(b.getCurrentLegIndex());
            }
        }
        //update the position of the boats given the current position, heading and velocity
        updatePosition(1);
        //send the boat info to receiver

        try {
            sendBoatLocation();
            sendRaceStatus();
        } catch (IOException e) {
            e.printStackTrace();

        }

    }
}
