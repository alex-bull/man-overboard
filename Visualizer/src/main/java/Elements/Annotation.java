package Elements;

import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.text.Font;
import models.Competitor;
import models.MutablePoint;

import static parsers.BoatStatusEnum.DSQ;

/**
 * Created by mattgoodson on 1/09/17.
 * A group for showing info along side of a boat
 */
public class Annotation extends Group {

    private Label speed;
    private Label name;


    /**
     * Create an annotation for a boat
     * @param boat Competitor
     */
    public Annotation(Competitor boat) {

        name = new Label(boat.getAbbreName());
        speed = new Label(String.valueOf(boat.getVelocity()) + "m/s");
        name.setFont(Font.font("Monospaced"));
        name.setTextFill(boat.getColor());
        speed.setFont(Font.font("Monospaced"));
        speed.setTextFill(boat.getColor());
        this.getChildren().add(speed);
        this.getChildren().add(name);
    }


    /**
     * Update the values and position of the annotation
     * @param boat Competitor
     * @param relativePosition The relative position of the boat on the screen
     * @param zoom Boolean, true if in zoom mode
     */
    public void update(Competitor boat, MutablePoint relativePosition, boolean zoom) {

        int offset = 15;
        if(zoom) offset *= 2;

        String sp = String.format("%.1f", boat.getVelocity());
        speed.setText(sp + "m/s");

        if(boat.getStatus() == DSQ) {
            name.setText("");
            speed.setText("");
        }
        name.setLayoutX(relativePosition.getXValue() + 5);
        name.setLayoutY(relativePosition.getYValue()+ offset);
        offset += 12;
        speed.setLayoutX(relativePosition.getXValue() + 5);
        speed.setLayoutY(relativePosition.getYValue()+ offset);
    }
}
