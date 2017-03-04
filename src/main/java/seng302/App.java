package seng302;

public class App
{
    public static void main( String[] args )
    {

        Regatta regatta = new RegattaFactory().createRegatta();
        regatta.begin();

    }
}
