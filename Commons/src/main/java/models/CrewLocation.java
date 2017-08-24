package models;

/**
 * Created by khe60 on 24/08/17.
 */
public class CrewLocation {
    private int numCrew;
    private MutablePoint position;

    public CrewLocation(int numCrew, MutablePoint position) {
        this.numCrew = numCrew;
        this.position = position;
    }

    public int getNumCrew() {
        return numCrew;
    }

    public MutablePoint getPosition() {
        return position;
    }

    public double getLatitude(){
        return position.getXValue();
    }

    public double getLongitude(){
        return position.getYValue();
    }
}
