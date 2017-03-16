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
        competitors.add(new Boat("Oracle Team USA", 20, new MutablePoint(0.0, 0.0)));
        competitors.add(new Boat("Emirates Team New Zealand", 19, new MutablePoint(1.0, 0.0)));
        competitors.add(new Boat("Ben Ainslie Racing", 18,  new MutablePoint(2.0, 0.0)));
        competitors.add(new Boat("SoftBank Team Japan", 17,  new MutablePoint(3.0, 0.0)));
        competitors.add(new Boat("Team France", 16,  new MutablePoint(4.0, 0.0)));
        competitors.add(new Boat("Artemis Racing", 15,  new MutablePoint(5.0, 0.0)));

        //randomly select competitors
        Collections.shuffle(competitors);
        competitors = competitors.subList(0, numBoats);


        return new MatchRace(raceDuration, delegate, raceCourse, competitors);

    }
}
