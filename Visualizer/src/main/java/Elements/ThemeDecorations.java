package Elements;

import com.rits.cloning.Cloner;
import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import models.MutablePoint;
import parsers.powerUp.PowerUp;
import utilities.DataSource;
import utility.Projection;

import javax.xml.crypto.Data;

import static java.lang.Math.pow;

/**
 * Created by psu43 on 20/09/17.
 * Decorations for a theme
 */
public class ThemeDecorations extends Group {
//    private ImageView igloo = new ImageView();
    private double imageWidth = 150;
    private double imageHeight = 93;

    private MutablePoint position17;
    private MutablePoint position;
    private MutablePoint positionOriginal;
    private Cloner cloner=new Cloner();
    private MutablePoint iglooLocation;
    private MutablePoint iglooLocation2;

    private DataSource dataSource;


    public ThemeDecorations(DataSource dataSource) {
        this.iglooLocation = new MutablePoint(-64.68501, -63.3863); // was 200 and 300
        this.position = dataSource.evaluatePosition(iglooLocation);
        this.positionOriginal = dataSource.evaluateOriginalPosition(iglooLocation);
        this.position17 = dataSource.evaluatePosition17(this.position);

        Rectangle igloo = new Rectangle(imageWidth, imageHeight);
        Image iglooImage = new Image(getClass().getClassLoader().getResource("images/antarctica/igloo.png").toString());
        igloo.setFill(new ImagePattern(iglooImage));
        igloo.setLayoutX(this.position.getXValue());
        igloo.setLayoutY(this.position.getYValue());

        Rectangle igloo2 = new Rectangle(imageWidth, imageHeight);
        this.iglooLocation2 = new MutablePoint(-64.64946, -62.50877);

        Image iglooImage2 = new Image(getClass().getClassLoader().getResource("images/antarctica/igloo-flip.png").toString());
        igloo2.setFill(new ImagePattern(iglooImage2));
        igloo2.setLayoutX(1450);
        igloo2.setLayoutY(150);


//        this.igloo.setImage(image);
        this.getChildren().add(igloo);
        this.getChildren().add(igloo2);




    }

    public void update(boolean isZoom, MutablePoint currentPosition17, double width, double height) {

//        this.positionOriginal = cloner.deepClone(Projection.mercatorProjection(iglooLocation));
//
//        position = cloner.deepClone(Projection.mercatorProjection(iglooLocation));
//        this.position.factor(scaleFactor, scaleFactor, minXMercatorCoord, minYMercatorCoord, paddingX, paddingY);
//
//        this.position17=cloner.deepClone(Projection.mercatorProjection(position));
//        this.position17.factor(pow(2,zoomLevel), pow(2,zoomLevel), minXMercatorCoord, minYMercatorCoord, paddingX, paddingY);
        System.out.println("Zooming " + isZoom);
        System.out.println(iglooLocation.getXValue());
        System.out.println(iglooLocation.getXValue()-imageWidth/2);
        if (isZoom) {
            MutablePoint p = this.position17.shift(-currentPosition17.getXValue() + width / 2, -currentPosition17.getYValue() + height / 2);
            this.relocate(iglooLocation.getXValue()-imageWidth/2,iglooLocation.getYValue()-imageHeight/2);
        } else {
//            MutablePoint p= this.position;
            this.relocate(iglooLocation.getXValue()-imageWidth/2,iglooLocation.getYValue()-imageHeight/2);
        }

//        MutablePoint point=cloner.deepClone(powerUp.getPositionOriginal());
//        point.factor(pow(2,zoomLevel), pow(2,zoomLevel), minXMercatorCoord, minYMercatorCoord, paddingX, paddingY);
//        powerUp.setPosition17(point);
    }
}
