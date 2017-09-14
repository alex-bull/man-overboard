package Elements;

import javafx.animation.FadeTransition;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Duration;
import models.MutablePoint;

/**
 * Created by mattgoodson on 1/09/17.
 * A Visual representation of a boats path
 */
public class Track extends Group {

    /**
     * Adds a new fading point on the track
     *
     * @param position Mutable point the location of the dot
     * @param color    Color the color of the dot
     */
    public void addDot(MutablePoint position, Color color) {

        Circle circle = new Circle(position.getXValue(), position.getYValue(), 1.5, color);
        //add fadeOut transition
        FadeTransition ft = new FadeTransition(Duration.millis(20000), circle);
        ft.setFromValue(1);
        ft.setToValue(0.15);
        ft.setOnFinished(event -> this.getChildren().remove(circle));
        ft.play();
        this.getChildren().add(circle);
    }
}
