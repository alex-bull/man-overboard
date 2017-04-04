package seng302.Factories;

import javafx.collections.FXCollections;
import javafx.collections.ObservableArray;
import javafx.collections.ObservableList;
import javafx.scene.paint.Color;
import seng302.Model.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by psu43 on 15/03/17.
 * A factory class for MatchRace objects
 */
public class RaceFactory {

    /**
     * Creates an instance of MatchRace and injects all dependencies
     *
     * @param numBoats     int the number of boats in the race
     * @param raceDuration int the approximate duration of the race
     * @param raceCourse   Course the course for the race
     * @return Race an implementation of Race
     */
    public Race createRace(int numBoats, int raceDuration, Course raceCourse) {

        //create competitors
        List<Competitor> competitors = new ArrayList<>();
        competitors.add(new Boat("Oracle Team USA", 22, new MutablePoint(0.0, 0.0), Color.BLACK,"USA"));
        competitors.add(new Boat("Emirates Team New Zealand", 20, new MutablePoint(30.0, 0.0), Color.RED,"NZL"));
        competitors.add(new Boat("Ben Ainslie Racing", 18, new MutablePoint(60.0, 0.0), Color.PURPLE, "GBR"));
        competitors.add(new Boat("SoftBank Team Japan", 16, new MutablePoint(90.0, 0.0), Color.YELLOW,"JPN"));
        competitors.add(new Boat("Team France", 15, new MutablePoint(120.0, 0.0), Color.BROWN,"FRA"));
        competitors.add(new Boat("Artemis Racing", 19, new MutablePoint(150.0, 0.0), Color.GREEN,"SWE"));

        //randomly select competitors
        Collections.shuffle(competitors);
        competitors = competitors.subList(0, numBoats);


        return new MatchRace(raceDuration, raceCourse, competitors);

    }
}
