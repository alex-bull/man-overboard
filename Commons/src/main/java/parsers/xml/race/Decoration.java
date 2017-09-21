package parsers.xml.race;

import models.MutablePoint;

/**
 * Created by psu43 on 21/09/17.
 */
public class Decoration {

    private MutablePoint location;
    private MutablePoint position;
    private MutablePoint position17;
    private MutablePoint positionOriginal;

    public Decoration(MutablePoint location) {
        this.location = location;
    }

    public MutablePoint getLocation() {
        return location;
    }


    public void setPosition(MutablePoint position) {
        this.position = position;
    }

    public void setPosition17(MutablePoint position17) {
        this.position17 = position17;
    }

    public void setPositionOriginal(MutablePoint positionOriginal) {
        this.positionOriginal = positionOriginal;
    }


    public MutablePoint getPosition() {
        return position;
    }



}
