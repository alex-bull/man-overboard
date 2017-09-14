package models;

/**
 * Created by Izzy on 8/09/17.
 */
public class Blood {

    private int sourceID;
    private double opacity = 0.0;
    private MutablePoint position;
    private MutablePoint position17;
    private MutablePoint positionOriginal;

    private boolean isIncreasing = true;


    public Blood(int sourceID, MutablePoint position) {
        this.sourceID = sourceID;
        this.position = position;
    }

    public Blood(int sourceID, MutablePoint position, MutablePoint position17, MutablePoint positionOriginal) {
        this.sourceID = sourceID;
        this.position = position;
        this.position17 = position17;
        this.positionOriginal = positionOriginal;
    }

    public int getSourceID() {
        return sourceID;
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

    public MutablePoint getPositionOriginal() {
        return positionOriginal;
    }

    public double getLatitude() {
        return position.getXValue();
    }

    public double getLongitude() {
        return position.getYValue();
    }

    public double getOpacity() {
        return opacity;
    }

    public void updateOpacity() {
        double max = 1;

        if (isIncreasing) {
            opacity += 0.01;
        } else {
            opacity -= 0.001;
        }

        if (opacity >= max) {
            isIncreasing = false;
        }
    }


}
