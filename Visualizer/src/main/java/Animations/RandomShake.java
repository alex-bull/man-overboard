package Animations;

import javafx.animation.KeyFrame;
import javafx.animation.RotateTransition;
import javafx.animation.Timeline;
import javafx.scene.Node;
import javafx.util.Duration;

import java.util.Random;

/**
 * Created by mgo65 on 15/08/17.
 */
public class RandomShake {

    private Node node;
    private boolean x;
    private boolean y;


    public RandomShake(Node node) {
        this.node = node;
    }

    /**
     * Spins the node multiple times.
     */
    public void spin() {
        RotateTransition rt = new RotateTransition(Duration.seconds(3), node);
        rt.setByAngle(1080);
        rt.setOnFinished(event -> node.setRotate(0));
        rt.play();
    }

    /**
     * Animates the node to shake for a short time
     */
    public void animate() {

        Timeline timelineX = new Timeline(new KeyFrame(Duration.seconds(0.05), t -> {
            int val = new Random().nextInt(20) - 10;
            if (x) {
                node.setLayoutX(val);
            } else {
                node.setLayoutX(val);
            }
            x = !x;
        }));

        timelineX.setCycleCount(8);
        timelineX.setAutoReverse(false);
        timelineX.setOnFinished(event -> node.setLayoutX(0.0));


        Timeline timelineY = new Timeline(new KeyFrame(Duration.seconds(0.05), t -> {
            int val = new Random().nextInt(20) - 10;
            if (y) {
                node.setLayoutY(val);
            } else {
                node.setLayoutY(val);
            }
            y = !y;
        }));

        timelineY.setCycleCount(8);
        timelineY.setAutoReverse(false);
        timelineY.setOnFinished(event -> node.setLayoutY(0.0));

        timelineX.play();
        timelineY.play();
    }


}
