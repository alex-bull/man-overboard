package Elements;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import models.MutablePoint;
import parsers.powerUp.PowerUp;

/**
 * Created by psu43 on 11/09/17.
 * Visual representation of a powerup
 */
public class PowerUpModel extends ImageView {

    /**
     * Create a powerup model
     *
     * @param receivedPowerUp PowerUp the power up
     * @param isZoom          if the visualizer is zoomed in or not
     * @param scale           the scaling used
     */
    public PowerUpModel(PowerUp receivedPowerUp, boolean isZoom, Double scale) {
        Image image;
        double originalScale = 0.4;
        switch (receivedPowerUp.getType()) {
            case BOOST:
                image = new Image("/images/tinyspeed.png");
                break;
            case POTION:
                image = new Image("/images/tinyhealth.png");
                break;
            default:
                image = null;
                break;
        }
//        double imageWidth = image.getWidth();
//        double imageHeight = image.getHeight();
//        this.setFitHeight(imageHeight * originalScale);
//        this.setFitWidth(imageWidth * originalScale);
        this.setImage(image);
        this.setPreserveRatio(true);
        if (isZoom) {
            this.setFitWidth(scale * image.getWidth());
        }
    }


    /**
     * Update the position of the powerup model
     *
     * @param isZoom            boolean true if zoomed in
     * @param powerUp           PowerUp, the powerUp
     * @param currentPosition17 MutablePoint, zoomed position
     * @param width             double, the screen width
     * @param height            double, the screen height
     */
    public void update(boolean isZoom, PowerUp powerUp, MutablePoint currentPosition17, double width, double height) {
        MutablePoint p;
        if (isZoom) {
            p = powerUp.getPosition17().shift(-currentPosition17.getXValue() + width / 2, -currentPosition17.getYValue() + height / 2);
        } else {
            p = powerUp.getPosition();
        }
        this.relocate(p.getXValue() - getFitWidth() / 2, p.getYValue() - getFitHeight() / 2);


    }
}
