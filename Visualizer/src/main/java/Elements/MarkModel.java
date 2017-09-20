package Elements;

import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Shape;
import parsers.xml.race.ThemeEnum;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Random;

import static parsers.xml.race.ThemeEnum.ANTARCTICA;


/**
 * Created by psu43 on 20/09/17.
 * Mark model
 */
public class MarkModel extends Group {
    private Circle mark;

    /**
     * Mark model for a mark or a gate
     * @param x double x coordinate
     * @param y double y coordinate
     * @param theme ThemeEnum course theme
     */
    public MarkModel(ThemeEnum theme, double x, double y) {
        mark = new Circle(x,y,10);
        if (theme.getValue() == ANTARCTICA.getValue()) {
            mark.setFill(new ImagePattern(getBerg()));
        }
        else {
            mark.setFill(new ImagePattern(new Image(getClass().getClassLoader().getResource("images/bermuda/island.png").toString())));
        }
        this.getChildren().add(mark);
    }

    /**
     * Updates the centre position of the mark model
     * @param x double x coordinate
     * @param y double y coordinate
     */
    public void setCentres(double x, double y) {
        mark.setCenterX(x);
        mark.setCenterY(y);
    }

    /**
     * Get a random iceberg image
     * @return Image the iceberg
     */
    private Image getBerg(){
        ArrayList<String> paths = new ArrayList<>();
        paths.add("images/antarctica/berg.png");
        paths.add("images/antarctica/berg2.png");
        paths.add("images/antarctica/berg3.png");
        Random random = new Random();
        int index = random.nextInt(paths.size());
        return new Image(getClass().getClassLoader().getResource(paths.get(index)).toString());

    }

}
