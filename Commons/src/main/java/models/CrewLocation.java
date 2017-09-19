package models;

/**
 * Created by khe60 on 24/08/17.
 * class for crewLocation
 */
public class CrewLocation {
    private int sourceId;
    private int numCrew;
    private MutablePoint position;
    private MutablePoint position17;
    private MutablePoint positionOriginal;
    private boolean died = false;

    public CrewLocation(int sourceId, int numCrew, MutablePoint position) {
        this.sourceId += sourceId;

        this.numCrew = numCrew;
        this.position = position;
    }

    public CrewLocation(int sourceId, int numCrew, MutablePoint position, MutablePoint position17, MutablePoint positionOriginal) {
        this.sourceId = sourceId;
        this.numCrew = numCrew;
        this.position = position;
        this.position17 = position17;
        this.positionOriginal = positionOriginal;
    }

    public MutablePoint getPositionOriginal() {
        return positionOriginal;
    }

    public int getSourceId() {
        return sourceId;
    }

    public int getNumCrew() {
        return numCrew;
    }

    public MutablePoint getPosition() {
        return position;
    }

    public MutablePoint getPosition17() {
        return position17;
    }

    public void setPosition17(MutablePoint position17) {
        this.position17 = position17;
    }

    public double getLatitude() {
        return position.getXValue();
    }

    public double getLongitude() {
        return position.getYValue();
    }

    @Override
    public String toString() {
        return "CrewLocation{" +
                "numCrew=" + numCrew +
                ", position=" + position +
                '}';
    }

    public boolean died() {
        return died;
    }

    public void setDied() {
        this.died = true;
    }
}
