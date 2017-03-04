package seng302;

import javafx.util.Pair;

import java.util.ArrayList;

/**
 * Created by mgo65 on 4/03/17.
 */
public class RegattaFactory {

    /**
     * Creates an Instance of Regatta and injects all dependencies
     * @return Regatta, a regatta object
     */
    public Regatta createRegatta() {

        //create competitors
        Boat boat1 = new Boat("Oracle Team USA");
        Boat boat2 = new Boat("Emirates Team New Zealand");
        Boat boat3 = new Boat("Ben Ainslie Racing");
        Boat boat4 = new Boat("SoftBank Team Japan");
        Boat boat5 = new Boat("Team France");
        Boat boat6 = new Boat("Artemis Racing");

        ArrayList<Competitor> competitors = new ArrayList<>();
        competitors.add(boat1);
        competitors.add(boat2);
        competitors.add(boat3);
        competitors.add(boat4);
        competitors.add(boat5);
        competitors.add(boat6);

        //create the match races
        MatchRace race1 = new MatchRace();
        MatchRace race2 = new MatchRace();
        MatchRace race3 = new MatchRace();

        ArrayList<Race> races = new ArrayList<>();
        races.add(race1);
        races.add(race2);
        races.add(race3);

        //Create the marks
        ArrayList<CoursePoint> points = new ArrayList<>();
        points.add(new Mark("Start", new Pair<>(0.0, 0.0)));
        points.add(new Mark("Mark", new Pair<>(0.0, 0.0)));
        points.add(new Mark("Leeward", new Pair<>(0.0, 0.0)));
        points.add(new Mark("Windward", new Pair<>(0.0, 0.0)));
        points.add(new Mark("Leeward", new Pair<>(0.0, 0.0)));
        points.add(new Mark("Finish", new Pair<>(0.0, 0.0)));

        //inject the dependencies for the regatta
        return new Regatta(competitors, races, points);
    }
}
