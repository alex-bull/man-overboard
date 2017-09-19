package Elements;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;
import models.CrewLocation;
import models.MutablePoint;
import models.Whirlpool;

/**
 * Created by msl47 on 12/09/17.
 */
public class WhirlpoolModel extends ImageView {

    Timeline animation;

    public WhirlpoolModel(Image image, Double scale) {

        this.setImage(image);
        this.setPreserveRatio(true);
        this.setFitWidth(scale * image.getWidth());
    }

    public void update(MutablePoint position) {
        this.setX(position.getXValue());
        this.setY(position.getYValue());
    }


    /**
     * Update the position of the whirlpool model
     *
     * @param isZoom            boolean true if zoomed in
     * @param whirlPool         WhirlPool
     * @param currentPosition17 MutablePoint, zoomed position
     * @param width             double, the screen width
     * @param height            double, the screen height
     */
    public void update(boolean isZoom, Whirlpool whirlPool, MutablePoint currentPosition17, double width, double height) {
        MutablePoint p;
        if (isZoom) {
            p = whirlPool.getPosition17().shift(-currentPosition17.getXValue() + width / 2, -currentPosition17.getYValue() + height / 2);
        } else {
            p = whirlPool.getPosition();
        }
        this.relocate(p.getXValue() - getFitWidth() / 2, p.getYValue() - getFitHeight() / 2);
    }


    public void spin() {
        this.setRotate(this.getRotate() + 5);
    }

    public Timeline animateSpawn() {
        animation = new Timeline(
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
