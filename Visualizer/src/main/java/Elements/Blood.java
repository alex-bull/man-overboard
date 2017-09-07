package Elements;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.transform.Rotate;

/**
 * Created by Izzy on 7/09/17.
 *
 */
public class Blood extends ImageView {

    private double time = 0.0;

    public Blood(Image image) { this.setImage(image); }

    public void update(double PosX, double PosY, double elapsedTime) {
        time += elapsedTime;

        this.setX(PosX);
        this.setY(PosY);

        this.getTransforms().clear();

        this.setOpacity(time);

    }

}
