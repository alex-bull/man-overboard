package Animations;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.paint.Color;
import javafx.scene.shape.CubicCurve;
import javafx.util.Duration;


/**
 * Created by msl47.
 */
public class WiggleLine extends CubicCurve{
    Timeline animation;

    public WiggleLine(){
        setStroke(Color.WHITE);
    }

    public Timeline animate(double startX){
        animation= new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(controlX1Property(), startX + 10)),
                new KeyFrame(Duration.ZERO, new KeyValue(controlX2Property(), startX - 10)),
                new KeyFrame(Duration.seconds(0.5), new KeyValue(controlX1Property(), startX - 10)),
                new KeyFrame(Duration.seconds(0.5), new KeyValue(controlX2Property(), startX + 10))
        );
        animation.setAutoReverse(true);
        animation.setCycleCount(10);
        return animation;
    }
}
