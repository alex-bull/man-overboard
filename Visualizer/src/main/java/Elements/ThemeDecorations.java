package Elements;

import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;

/**
 * Created by psu43 on 20/09/17.
 * Decorations for a theme
 */
public class ThemeDecorations extends Group {
//    private ImageView igloo = new ImageView();


    public ThemeDecorations() {
        Rectangle igloo = new Rectangle(150, 93);
        Image iglooImage = new Image(getClass().getClassLoader().getResource("images/antarctica/igloo.png").toString());
        igloo.setFill(new ImagePattern(iglooImage));
        igloo.setLayoutX(200);
        igloo.setLayoutY(300);

        Rectangle igloo2 = new Rectangle(150, 93);
        Image iglooImage2 = new Image(getClass().getClassLoader().getResource("images/antarctica/igloo-flip.png").toString());
        igloo2.setFill(new ImagePattern(iglooImage2));
        igloo2.setLayoutX(1450);
        igloo2.setLayoutY(150);


//        this.igloo.setImage(image);
        this.getChildren().add(igloo);
        this.getChildren().add(igloo2);

    }
}
