package seng302;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by mgo65 on 4/03/17.
 * A factory class for Regatta objects following dependency injection pattern
 */
public class RegattaFactory {

    /**
     * Creates an Instance of Regatta and injects all dependencies
     * @param numberOfBoats int the number of boats in the regatta
     * @return Regatta, a regatta object
     */
    public Regatta createRegatta(int numberOfBoats) {

        //create competitors
        List<Competitor> competitors = new ArrayList<>();
        competitors.add(new Boat("Oracle Team USA", 9));
        competitors.add(new Boat("Emirates Team New Zealand", 8));
        competitors.add(new Boat("Ben Ainslie Racing", 7));
        competitors.add(new Boat("SoftBank Team Japan", 6));
        competitors.add(new Boat("Team France", 5));
        competitors.add(new Boat("Artemis Racing", 4));

        //randomly select competitors
        Collections.shuffle(competitors);
        competitors = competitors.subList(0, numberOfBoats);


        //create the match races, only one is used for now
        MatchRace race1 = new MatchRace();
        MatchRace race2 = new MatchRace();
        MatchRace race3 = new MatchRace();

        List<Race> races = new ArrayList<>();
        races.add(race1);
        races.add(race2);
        races.add(race3);

        //create the marks
        List<CoursePoint> points = new ArrayList<>();
        points.add(new Mark("PreStart", new Point(-20.0, 0.0)));
        points.add(new Mark("Start Gate", new Point(0.0, 0.0)));
        points.add(new Mark("Mark", new Point(100.0, 10.0)));
        points.add(new Mark("Leeward Gate", new Point(120.0, 180.0)));
        points.add(new Mark("Windward Gate", new Point(70.0, -70.0)));
        points.add(new Mark("Leeward Gate", new Point(120.0, 180.0)));
        points.add(new Mark("Finish", new Point(50.0, 230.0)));

        //inject the dependencies for the regatta
        return new Regatta(competitors, races, points);
    }
}
