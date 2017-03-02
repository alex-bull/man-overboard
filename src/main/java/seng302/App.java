package seng302;

public class App 
{
    public static void main( String[] args )
    {
        Boat boat1 = new Boat("Oracle Team USA");
        Boat boat2 = new Boat("Emirates Team New Zealand");

        System.out.println(boat1.getTeamName());
        System.out.println(boat2.getTeamName());
    }
}
