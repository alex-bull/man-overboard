package utilities;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Shape;
import javafx.util.Duration;

/**
 * Created by abu59 on 25/07/17.
 */
public class CollisionRipple extends Circle{

    private double radius;

    public CollisionRipple(double centerX, double centerY, double radius){
        super(centerX, centerY, 0, null);
        setStrokeWidth(3);
        setStroke(Color.rgb(255, 0, 0));
        radius = radius;
    }

    Timeline animation = new Timeline(
            new KeyFrame(Duration.ZERO, new KeyValue(radiusProperty(), 0)),
            new KeyFrame(Duration.ZERO, new KeyValue(opacityProperty(), 1)),
            new KeyFrame(Duration.seconds(0.5), new KeyValue(radiusProperty(), 20)),
            new KeyFrame(Duration.seconds(0.5), new KeyValue(opacityProperty(), 0))
    );

    public void animate(){
        this.animation.play();
    }
}
