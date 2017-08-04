package utilities;

import javafx.animation.RotateTransition;
import javafx.animation.Timeline;
import javafx.scene.shape.Polygon;
import javafx.util.Duration;

import static utility.Calculator.calculateExpectedTack;

/**
 * Created by ikj11 on 3/08/17.
 */
public class TackAndGybe {

//    private Timeline animation;
    private double expectedHeading;
    private double boatHeading;
    private double windDirection;

    public TackAndGybe(double windDir, double boatHeading, Polygon boatModel){
        this.boatHeading = boatHeading;
        this.windDirection = windDir;
        this.expectedHeading = calculateExpectedTack(windDir, boatHeading);

//        animation = new Timeline(
//        );

    }


    /**
     * Checks if the boat should be tacking clockwise or anticlockwise
     * @return boolean if the tack is clockwise
     */
    private boolean isTackClockwise() {
        return !(this.boatHeading > this.windDirection && this.boatHeading < this.windDirection + 180);
    }


//    public Timeline animate(){
//        this.animation.play();
//        return animation;
//    }

}


