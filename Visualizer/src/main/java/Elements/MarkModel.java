package Elements;

import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import parsers.xml.race.ThemeEnum;

import java.util.ArrayList;
import java.util.Random;


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
        mark = new Circle(x,y,25);
        switch (theme){
            case ANTARCTICA:
                mark.setFill(new ImagePattern(getBerg()));
                break;
            case AMAZON:
                mark.setFill(new ImagePattern(new Image(getClass().getClassLoader().getResource("images/amazon/mangrove.png").toString())));
                break;
            case BERMUDA:
                mark.setFill(new ImagePattern(new Image(getClass().getClassLoader().getResource("images/bermuda/island.png").toString())));
                break;
            case NILE:
                mark.setFill(new ImagePattern(new Image(getClass().getClassLoader().getResource("images/nile/hippo.png").toString())));
                break;
            default:
               // System.out.println("invalid theme id: "+theme);
                break;
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
        paths.add("images/antarctica/tinyberg.png");
        paths.add("images/antarctica/tinyberg2.png");
        paths.add("images/antarctica/tinyberg3.png");
        Random random = new Random();
        int index = random.nextInt(paths.size());
        return new Image(getClass().getClassLoader().getResource(paths.get(index)).toString());
    }

}
