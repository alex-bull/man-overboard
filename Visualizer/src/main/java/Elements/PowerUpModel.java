package Elements;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import models.MutablePoint;
import parsers.powerUp.PowerUp;

import static parsers.powerUp.PowerUpType.BOOST;
import static parsers.powerUp.PowerUpType.POTION;

/**
 * Created by psu43 on 11/09/17.
 * Visual representation of a powerup
 */
public class PowerUpModel extends ImageView {
    private Integer imageWidth = 32;
    private Integer imageHeight = 32;


    /**
     * Create a powerup model
     * @param receivedPowerUp PowerUp the power up
     */
    public PowerUpModel(PowerUp receivedPowerUp) {
        if (receivedPowerUp.getType() == BOOST.getValue() || receivedPowerUp.getType() == POTION.getValue()) {
            Image image;
            if(receivedPowerUp.getType() == BOOST.getValue()) {
                image = new Image("/images/speed3.png");
            }
            else {
                image = new Image("/images/potion.png");
            }
            this.setImage(image);
            this.setFitHeight(imageHeight);
            this.setFitWidth(imageWidth);
        }
    }


    /**
     * Update the position of the powerup model
     * @param isZoom boolean true if zoomed in
     * @param powerUp PowerUp, the powerUp
     * @param currentPosition17 MutablePoint, zoomed position
     * @param width double, the screen width
     * @param height double, the screen height
     */
    public void update(boolean isZoom, PowerUp powerUp, MutablePoint currentPosition17, double width, double height) {
        if (isZoom) {
            MutablePoint p = powerUp.getPosition17().shift(-currentPosition17.getXValue() + width / 2, -currentPosition17.getYValue() + height / 2);
            this.relocate(p.getXValue()-imageWidth/2,p.getYValue()-imageHeight/2);
        } else {
            MutablePoint p=powerUp.getPosition();
            this.relocate(p.getXValue()-imageWidth/2,p.getYValue()-imageHeight/2);
        }
    }
}
