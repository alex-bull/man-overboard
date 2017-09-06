package models;

/**
 * Created by Izzy on 5/09/17.
 *
 */
public class Shark {

    private int sourceId;
    private int numSharks;
    private float heading;
    private MutablePoint position;
    private MutablePoint position17;
    private MutablePoint positionOriginal;

    public MutablePoint getPositionOriginal() {
        return positionOriginal;
    }

    public int getSourceId() {
        return sourceId;
    }

    public Shark(int sourceId, int numSharks, MutablePoint position) {
        this.sourceId += sourceId;
        this.numSharks = numSharks;
        this.position = position;
    }

    public Shark(int sourceId,int numSharks, MutablePoint position, MutablePoint position17, MutablePoint positionOriginal, float heading) {
        this.sourceId=sourceId;
        this.numSharks = numSharks;
        this.position = position;
        this.position17=position17;
        this.positionOriginal=positionOriginal;
        this.heading = heading;
    }

    public void setPosition17(MutablePoint position17) {
        this.position17 = position17;
    }

    public void setPosition(MutablePoint position){ this.position = position;}

    public void setHeading(float heading){this.heading = heading;}

    public float getHeading(){ return heading; }

    public int getNumSharks() {
        return numSharks;
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
        return "Shark{" +
                "numSharks=" + numSharks +
                ", position=" + position +
                '}';
    }
}
