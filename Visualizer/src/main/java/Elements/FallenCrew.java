package Elements;


import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;
import models.CrewLocation;
import models.MutablePoint;

/**
 * Created by khe60 on 19/09/17.
 *
 */
public class FallenCrew extends ImageView {

    private boolean dead = false;
    private Timeline animation;
    public static final int baseSize = 15;


    public FallenCrew(String filePath, Double scale) {
        Image drowning = new Image(filePath);
        this.setImage(drowning);
        this.setPreserveRatio(true);
        this.setFitWidth(scale * baseSize);
    }

    /**
     * Update the position of the fallen crew model
     *
     * @param isZoom            boolean true if zoomed in
     * @param crewLocation      CrewLocation
     * @param currentPosition17 MutablePoint, zoomed position
     * @param width             double, the screen width
     * @param height            double, the screen height
     */
    public void update(boolean isZoom, CrewLocation crewLocation, MutablePoint currentPosition17, double width, double height) {
        MutablePoint p;
        if (isZoom) {
            p = crewLocation.getPosition17().shift(-currentPosition17.getXValue() + width / 2, -currentPosition17.getYValue() + height / 2);
        } else {
            p = crewLocation.getPosition();
        }
        this.relocate(p.getXValue() - getFitWidth() / 2, p.getYValue() - getFitHeight() / 2);
    }

    public boolean getDead() {
        return this.dead;
    }

    public void die(String filePath){
        if (animation != null) return;
        Image blood = new Image(filePath);
        animation = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(opacityProperty(), 0)),
                new KeyFrame(Duration.seconds(2.0), new KeyValue(opacityProperty(), 1)),
                new KeyFrame(Duration.seconds(3.0), new KeyValue(opacityProperty(), 1)),
                new KeyFrame(Duration.seconds(4.0), new KeyValue(opacityProperty(), 0))
        );
        animation.setOnFinished(event -> this.dead = true);
        this.setOpacity(0.0);
        this.setImage(blood);
        animation.play();
    }

}
