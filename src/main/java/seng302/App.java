package seng302;

import java.util.ArrayList;

public class App
{
    public static void main( String[] args )
    {

        Regatta regatta = new RegattaFactory().createRegatta();
        regatta.begin();

    }
}
