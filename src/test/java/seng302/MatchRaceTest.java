package seng302;

import org.junit.Test;


import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertTrue;

/**
 * Created by mgo65 on 3/03/17.
 */
public class MatchRaceTest {


    @Test
    public void testGetCompetitors () {

        //create competitors
        List<Competitor> competitors = new ArrayList<>();
        competitors.add(new Boat("Oracle Team USA", 9));
        competitors.add(new Boat("Emirates Team New Zealand", 8));
        competitors.add(new Boat("Ben Ainslie Racing", 7));
        competitors.add(new Boat("SoftBank Team Japan", 6));
        competitors.add(new Boat("Team France", 5));
        competitors.add(new Boat("Artemis Racing", 4));

        MatchRace matchRace = new MatchRace();

        matchRace.setCompetitors(competitors);

        assertTrue(matchRace.getCompetitors().equals(competitors));

    }
}
