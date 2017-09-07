package Elements;


import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.transform.Rotate;

public class SharkModel extends ImageView {

    public SharkModel(Image image) {
        this.setImage(image);
    }

    public void update(double PosX, double PosY, double heading) {
        this.setX(PosX);
        this.setY(PosY);

        this.getTransforms().clear();

        this.getTransforms().add(new Rotate(heading, PosX, PosY));
    }
}
