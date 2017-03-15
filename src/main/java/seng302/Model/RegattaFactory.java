package seng302.Model;


import org.jdom2.JDOMException;
import seng302.Model.*;

import java.io.File;
import java.io.IOException;
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
        competitors.add(new Boat("Oracle Team USA", 20, new Point(100.0, 100.0)));
        competitors.add(new Boat("Emirates Team New Zealand", 19, new Point(200.0, 200.0)));
        competitors.add(new Boat("Ben Ainslie Racing", 18,  new Point(300.0, 300.0)));
        competitors.add(new Boat("SoftBank Team Japan", 17,  new Point(400.0, 400.0)));
        competitors.add(new Boat("Team France", 16,  new Point(500.0, 500.0)));
        competitors.add(new Boat("Artemis Racing", 15,  new Point(600.0, 600.0)));

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
        File inputFile=new File("src/main/resources/course.xml");
        XMLParser parser=new XMLParser(inputFile);
        List<CourseFeature> points = null;
        try {
            points = parser.parseCourse();
        } catch (JDOMException e) {
            System.out.println("XML file format error");
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("file not found or something");
            e.printStackTrace();
        }

        //inject the dependencies for the regatta
        return new Regatta(competitors, races, points);
    }
}
