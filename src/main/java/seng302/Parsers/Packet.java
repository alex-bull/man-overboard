package seng302.Parsers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by psu43 on 10/04/17.
 */
public class Packet {



    public boolean validBoatLocation(ArrayList<String> data) {
        if(data.get(2).equals("25") && data.get(13).equals("38")) {
//            System.out.println(data);
            return true;
        }
        return false;
    }

    public void processMsgBody(ArrayList<String> body) {
        // get source id, latitude, long, heading, speed
        List sourceID = body.subList(7, 11);
        System.out.println("sourceID " + sourceID);

        List latitude = body.subList(16, 20);
        List longitude = body.subList(20, 24);
        List heading = body.subList(28, 30);
        System.out.println("lat " + latitude);
        System.out.println("long " + longitude);
        System.out.println("head " + heading);

        List speed = body.subList(34, 36);
        System.out.println("Speed " + speed);
    }

}

