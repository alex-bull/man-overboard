package seng302.Model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by psu43 on 15/03/17.
 * A factory class for MatchRace objects
 */
public class RaceFactory {

    /**
     * Creates an Instance of Race and injects all dependencies
     * @return Race a match race
     */
    public Race createRace(int numBoats, int raceDuration, RaceDelegate delegate, Course raceCourse){

        //create competitors
        List<Competitor> competitors = new ArrayList<>();
        competitors.add(new Boat("Oracle Team USA", 20, new MutablePoint(100.0, 100.0)));
        competitors.add(new Boat("Emirates Team New Zealand", 19, new MutablePoint(200.0, 200.0)));
        competitors.add(new Boat("Ben Ainslie Racing", 18,  new MutablePoint(300.0, 300.0)));
        competitors.add(new Boat("SoftBank Team Japan", 17,  new MutablePoint(400.0, 400.0)));
        competitors.add(new Boat("Team France", 16,  new MutablePoint(500.0, 500.0)));
        competitors.add(new Boat("Artemis Racing", 15,  new MutablePoint(600.0, 600.0)));

        //randomly select competitors
        Collections.shuffle(competitors);
        competitors = competitors.subList(0, numBoats);


        return new MatchRace(raceDuration, delegate, raceCourse, competitors);

    }
}
