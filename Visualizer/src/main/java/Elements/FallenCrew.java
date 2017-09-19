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
    private static final Integer imageWidth = 35;
    private static final Integer imageHeight = 35;


    public FallenCrew(String filePath) {
        Image drowning = new Image(filePath);
        this.setImage(drowning);
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
        if (isZoom) {
            MutablePoint p = crewLocation.getPosition17().shift(-currentPosition17.getXValue() + width / 2, -currentPosition17.getYValue() + height / 2);
            this.relocate(p.getXValue() - imageWidth / 2, p.getYValue() - imageHeight / 2);
        } else {
            MutablePoint p = crewLocation.getPosition();
            this.relocate(p.getXValue() - imageWidth / 2, p.getYValue() - imageHeight / 2);
        }
    }


    public void die(String filePath){
        Image blood = new Image(filePath);
        Timeline animation;
        animation = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(opacityProperty(), 0)),
                new KeyFrame(Duration.seconds(1.0), new KeyValue(opacityProperty(), 1)),
                new KeyFrame(Duration.seconds(3.0), new KeyValue(opacityProperty(), 1)),
                new KeyFrame(Duration.seconds(4.0), new KeyValue(opacityProperty(), 0))
        );
        this.setImage(blood);
        animation.play();
    }

}
