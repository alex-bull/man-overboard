package Elements;


import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class SharkModel extends ImageView {

    public SharkModel(Image image) {
        this.setImage(image);
    }

    public void update(double PosX, double PosY, double heading) {
        this.setX(PosX);
        this.setY(PosY);

        this.setRotate(0);
        this.setRotate(heading);
    }
}
