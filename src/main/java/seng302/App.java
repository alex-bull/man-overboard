package seng302;

public class App 
{
    public static void main( String[] args )
    {
        Boat boat1 = new Boat("Oracle Team USA");
        Boat boat2 = new Boat("Emirates Team New Zealand");

        System.out.println("Entrants:");
        System.out.println("#1: " + boat1.getTeamName());
        System.out.println("#2: " + boat2.getTeamName());

        Race race = new Race(boat1, boat2);
        race.start();
    }
}
