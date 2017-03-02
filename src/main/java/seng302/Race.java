package seng302;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by mgo65 on 3/03/17.
 */
public class Race {

    private Boat boat1;
    private Boat boat2;

    /**
     * Creates a Race with two boats
     * @param boat1 Boat a participating boat
     * @param boat2 Boat a participating boat
     */
    public Race (Boat boat1, Boat boat2) {
        this.boat1 = boat1;
        this.boat2 = boat2;
    }

    /**
     * Begins the race event
     */
    public void start () {
        List<Boat> placings = generatePlacings();
        System.out.println("Finishing order:");
        System.out.println("#1: " + placings.get(0).getTeamName());
        System.out.println("#2: " + placings.get(1).getTeamName());
    }

    /**
     * Randomly chooses finishing order
     * @return List the placings
     */
    private List<Boat> generatePlacings() {
        ArrayList placings = new ArrayList();
        placings.add(boat1);
        placings.add(boat2);
        Collections.shuffle(placings);
        return placings;
    }

}
