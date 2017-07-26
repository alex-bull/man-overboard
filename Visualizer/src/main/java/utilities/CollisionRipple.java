package utilities;

import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Shape;
import javafx.util.Duration;

/**
 * Created by abu59 on 25/07/17.
 */
public class CollisionRipple extends Circle{

    Timeline animation;
    FadeTransition ft;
    public CollisionRipple(double centerX, double centerY,int radius){
        super(centerX, centerY, 0, null);
        setStrokeWidth(3);
        setStroke(Color.rgb(255, 0, 0));
        animation=new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(radiusProperty(), 0)),
                new KeyFrame(Duration.ZERO, new KeyValue(opacityProperty(), 1)),
                new KeyFrame(Duration.seconds(0.5), new KeyValue(radiusProperty(), radius)),
                new KeyFrame(Duration.seconds(0.5), new KeyValue(opacityProperty(), 0))
        );
        ft=new FadeTransition(Duration.millis(1000),this);
        ft.setFromValue(1);
        ft.setToValue(0);
    }



    public Timeline animate(){
        this.animation.play();
        ft.play();
        return animation;
    }
}
