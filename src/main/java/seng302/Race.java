package seng302;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by mgo65 on 3/03/17.
 * Race object
 */
public class Race {

    private Boat boat1;
    private Boat boat2;
    private ArrayList<Boat> placings = new ArrayList<>();




    /**
     * Creates a Race with two boats
     * @param boat1 Boat a participating boat
     * @param boat2 Boat a participating boat
     */
    public Race (Boat boat1, Boat boat2) {
        this.boat1 = boat1;
        this.boat2 = boat2;
    }

    public ArrayList<Boat> getPlacings () {
        return this.placings;
    }

    /**
     * Outputs the finishing order
     */
    public void start () {
        generatePlacings();
        System.out.println("Finishing order:");
        System.out.println("#1: " + placings.get(0).getTeamName());
        System.out.println("#2: " + placings.get(1).getTeamName());
    }

    /**
     * Randomly chooses finishing order
     * @return List the placings
     */
    private void generatePlacings() {
        placings.add(boat1);
        placings.add(boat2);
        Collections.shuffle(placings);
    }

}
