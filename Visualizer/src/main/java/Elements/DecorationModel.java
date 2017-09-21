package Elements;

import com.rits.cloning.Cloner;
import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import models.MutablePoint;
import parsers.powerUp.PowerUp;
import parsers.xml.race.Decoration;
import utilities.DataSource;
import utility.Projection;

import javax.xml.crypto.Data;

import static java.lang.Math.pow;

/**
 * Created by psu43 on 20/09/17.
 * Decorations for a theme
 */
public class DecorationModel extends Group {
    private double imageWidth = 150;
    private double imageHeight = 93;
    private Decoration decoration;

    /**
     * Constructs a Decoration item
     * @param decorationModel Decoration a decoration model
     */
    public DecorationModel(Decoration decorationModel) {
        this.decoration = decorationModel;
        Rectangle igloo = new Rectangle(imageWidth, imageHeight);
        Image iglooImage = new Image(getClass().getClassLoader().getResource("images/antarctica/igloo.png").toString());
        igloo.setFill(new ImagePattern(iglooImage));
        igloo.setLayoutX(this.decoration.getPosition().getXValue());
        igloo.setLayoutY(this.decoration.getPosition().getYValue());
        this.getChildren().add(igloo);

    }

    /**
     * Update the position of the decoration model
     * @param isZoom boolean true if zoomed in
     * @param currentPosition17 MutablePoint, zoomed position
     * @param width double, the screen width
     * @param height double, the screen height
     */
    public void update(boolean isZoom, MutablePoint currentPosition17, double width, double height) {
        if (isZoom) {
            MutablePoint p = this.decoration.getPosition17().shift(-currentPosition17.getXValue() + width / 2, -currentPosition17.getYValue() + height / 2);
            this.relocate(p.getXValue()-imageWidth/2,p.getYValue()-imageHeight/2);
        } else {
            MutablePoint p=this.decoration.getPosition();
            this.relocate(p.getXValue()-imageWidth/2,p.getYValue()-imageHeight/2);
        }
    }

}
