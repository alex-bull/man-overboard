package Elements;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.transform.Rotate;



/**
 * Created by mattgoodson on 30/08/17.
 * An arrow that circles the next mark to round
 */
public class CurvedGuideArrow extends ImageView {

    private double angle;
    private boolean rotatesClockwise;
    Image image;


    /**
     * Initialize a curved guide arrow
     *
     * @param clockwise boolean, whether the arrow turns clockwise
     */
    public CurvedGuideArrow(Boolean clockwise) {
        this.setPreserveRatio(true);
        if (clockwise) {
            image = new Image("images/clockwiseArrow.png");
        } else {
            image = new Image("images/antiClockwiseArrow.png");
        }
        this.setImage(image);
        rotatesClockwise = clockwise;

    }


    /**
     * Update the arrow position when either zoomed or not zoomed
     *
     * @param markX Double, the x coordinate of the mark
     * @param markY Double, the y coordinate of the mark
     * @param scale Double, size scale factor
     */
    public void updateArrow(Double markX, Double markY, Double scale) {

        this.setFitWidth(image.getWidth() * scale);

        if (rotatesClockwise) {
            angle += 2;
        } else {
            angle -= 2;
        }
        if (rotatesClockwise) {
            applyTransformsToArrow(angle, markX - image.getWidth() * scale, markY - image.getHeight() * scale, image.getWidth() * scale, image.getHeight() * scale);
        }
        else {
            applyTransformsToArrow(angle, markX, markY - image.getHeight() * scale, 0, image.getHeight() * scale);
        }
    }

    /**
     * Apply translation and rotation transforms to the guiding arrow
     *
     * @param angle double the angle by which to rotate the arrow, from north
     * @param x     double the x coordinate for the arrow's origin
     * @param y     double the y coordinate for the arrow's origin
     * @param rotationX double X axis of rotation
     * @param rotationY double Y axis of rotation
     */
    private void applyTransformsToArrow(double angle, double x, double y, double rotationX, double rotationY) {
        this.getTransforms().clear();
        this.setLayoutX(x);
        this.setLayoutY(y);
        this.getTransforms().add(new Rotate(angle, rotationX, rotationY));
    }

    public void hide() {
        this.setVisible(false);
    }

    public void show() {
        this.setVisible(true);
    }
}
