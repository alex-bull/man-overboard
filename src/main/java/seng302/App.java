package seng302;

import java.util.ArrayList;

public class App
{
    public static void main( String[] args )
    {
        //create boats
        Boat boat1 = new Boat("Oracle Team USA");
        Boat boat2 = new Boat("Emirates Team New Zealand");
        Boat boat3 = new Boat("Ben Ainslie Racing");
        Boat boat4 = new Boat("SoftBank Team Japan");
        Boat boat5 = new Boat("Team France");
        Boat boat6 = new Boat("Artemis Racing");

        //create the regatta
        Regatta regatta;

        //create the match races
        MatchRace race1 = new MatchRace();
        MatchRace race2 = new MatchRace();
        MatchRace race3 = new MatchRace();

        //Create the dependencies for the regatta using the abstracting interfaces
        ArrayList<Race> races = new ArrayList<>();
        races.add(race1);
        races.add(race2);
        races.add(race3);

        ArrayList<Competitor> competitors = new ArrayList<>();
        competitors.add(boat1);
        competitors.add(boat2);
        competitors.add(boat3);
        competitors.add(boat4);
        competitors.add(boat5);
        competitors.add(boat6);

        //inject the dependencies for the regatta
        regatta = new Regatta(competitors, races);

        regatta.begin();

    }
}
