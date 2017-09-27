package Elements;


import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import models.MutablePoint;
import models.Shark;
import parsers.powerUp.PowerUp;

public class SharkModel extends ImageView {

    public SharkModel(Image image) {
        this.setImage(image);
    }


    /**
     * Update the position of the powerup model
     *
     * @param isZoom            boolean true if zoomed in
     * @param shark           Shark, the shark
     * @param currentPosition17 MutablePoint, zoomed position
     * @param width             double, the screen width
     * @param height            double, the screen height
     */
    public void update(boolean isZoom, Shark shark, MutablePoint currentPosition17, double width, double height, double heading) {
        MutablePoint p;
        if (isZoom) {

            p = shark.getPosition17().shift(-currentPosition17.getXValue() + width / 2, -currentPosition17.getYValue() + height / 2);
        } else {
            p = shark.getPosition();
        }
        this.relocate(p.getXValue() - getFitWidth() / 2, p.getYValue() - getFitHeight() / 2);
        this.setRotate(0);
        this.setRotate(heading);
    }

}
