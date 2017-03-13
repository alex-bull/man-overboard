package seng302.Model;


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
     * @param raceDuration int the race duration in minutes
     * @return Regatta, a regatta object
     */
    public Regatta createRegatta(int numberOfBoats, int raceDuration) {

        //create competitors
        List<Competitor> competitors = new ArrayList<>();
        competitors.add(new Boat("Oracle Team USA", 20, new Point(10.0, 20.0)));
        competitors.add(new Boat("Emirates Team New Zealand", 19, new Point(9.0, 20.0)));
        competitors.add(new Boat("Ben Ainslie Racing", 18, new Point(11.0, 20.0)));
        competitors.add(new Boat("SoftBank Team Japan", 17, new Point(13.0, 21.0)));
        competitors.add(new Boat("Team France", 16, new Point(12.0, 23.0)));
        competitors.add(new Boat("Artemis Racing", 15, new Point(10.0, 30.0)));

        //randomly select competitors
        Collections.shuffle(competitors);
        competitors = competitors.subList(0, numberOfBoats);


        //create the match races, only one is used for now
        MatchRace race1 = new MatchRace(raceDuration);
        MatchRace race2 = new MatchRace(raceDuration);
        MatchRace race3 = new MatchRace(raceDuration);

        List<Race> races = new ArrayList<>();
        races.add(race1);
        races.add(race2);
        races.add(race3);

        //create the marks
        List<CourseFeature> points = new ArrayList<>();
        points.add(new Mark("PreStart", new Point(-100.0, 0.0)));
        points.add(new Mark("Start Gate", new Point(0.0, 0.0)));
        points.add(new Mark("Mark", new Point(500.0, 50.0)));
        points.add(new Mark("Leeward Gate", new Point(600.0, 900.0)));
        points.add(new Mark("Windward Gate", new Point(350.0, -350.0)));
        points.add(new Mark("Leeward Gate", new Point(600.0, 900.0)));
        points.add(new Mark("Finish", new Point(250.0, 1150.0), true));

        //inject the dependencies for the regatta
        return new Regatta(competitors, races, points);
    }
}
