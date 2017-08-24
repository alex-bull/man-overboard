package models;

/**
 * Created by khe60 on 24/08/17.
 */
public class CrewLocation {
    private int numCrew;
    private MutablePoint position;
    private MutablePoint position17;

    public CrewLocation(int numCrew, MutablePoint position) {
        this.numCrew = numCrew;
        this.position = position;
    }

    public CrewLocation(int numCrew, MutablePoint position,MutablePoint position17) {
        this.numCrew = numCrew;
        this.position = position;
        this.position17=position17;
    }

    public int getNumCrew() {
        return numCrew;
    }

    public MutablePoint getPosition() {
        return position;
    }
    public MutablePoint getPosition17(){
        return position17;
    }

    public double getLatitude(){
        return position.getXValue();
    }

    public double getLongitude(){
        return position.getYValue();
    }

    @Override
    public String toString() {
        return "CrewLocation{" +
                "numCrew=" + numCrew +
                ", position=" + position +
                '}';
    }
}
