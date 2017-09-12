package Elements;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;
import models.MutablePoint;

/**
 * Created by msl47 on 12/09/17.
 */
public class WhirlpoolModel extends ImageView {

    Timeline animation;
    public WhirlpoolModel(Image image) {
        this.setImage(image);
        this.setFitHeight(40);
        this.setFitWidth(40);
    }

    public void update(MutablePoint position) {
        this.setX(position.getXValue());
        this.setY(position.getYValue());

    }

    public void spin() {
        this.setRotate(this.getRotate() + 8);
    }

    public Timeline animateSpawn() {
        animation=new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(fitHeightProperty(), 10)),
                new KeyFrame(Duration.ZERO, new KeyValue(fitWidthProperty(), 10)),

                new KeyFrame(Duration.seconds(0.5), new KeyValue(fitHeightProperty(), 20)),
                new KeyFrame(Duration.seconds(0.5), new KeyValue(fitWidthProperty(), 20)),

                new KeyFrame(Duration.seconds(1), new KeyValue(fitHeightProperty(), 40)),
                new KeyFrame(Duration.seconds(1), new KeyValue(fitWidthProperty(), 40))
        );
        animation.play();
        return animation;
    }
}
