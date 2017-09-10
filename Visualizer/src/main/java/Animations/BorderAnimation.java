package Animations;

import javafx.animation.FadeTransition;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;

import static javafx.scene.paint.Color.TRANSPARENT;

/**
 * Created by mattgoodson on 30/08/17.
 * An animation for a fading red border around a Pane
 */
public class BorderAnimation {

    private Pane parent;
    private double thickness;


    /**
     * Create the animation
     * @param parent Pane, the pane to display the border on
     * @param thickness double, the width of the border
     */
    public BorderAnimation(Pane parent, double thickness) {
        this.parent = parent;
        this.thickness = thickness;
    }


    /**
     * Show the border
     * Run the fadeOut animation
     */
    public void animate() {


        double height = parent.getHeight();
        double width = parent.getWidth();

        List<Shape> border=new ArrayList<>();
        //offset which the screen shakes by
        double shakeOffset=10;
        LinearGradient linearGradient=new LinearGradient(0,0,0,1,true, CycleMethod.NO_CYCLE,new Stop(0.0f, Color.RED), new Stop(1.0f, TRANSPARENT));
//        LinearGradient linearGradient2=new LinearGradient(0,1,0,0,true,CycleMethod.NO_CYCLE,new Stop(0.0f, Color.RED), new Stop(1.0f, TRANSPARENT));
        LinearGradient linearGradient3=new LinearGradient(0,0,1,0,true,CycleMethod.NO_CYCLE,new Stop(0.0f, Color.RED), new Stop(1.0f, TRANSPARENT));
        LinearGradient linearGradient4=new LinearGradient(1,0,0,0,true,CycleMethod.NO_CYCLE,new Stop(0.0f, Color.RED), new Stop(1.0f, TRANSPARENT));

        Rectangle rectTop =new Rectangle(-shakeOffset,-shakeOffset,width+shakeOffset*2,thickness+shakeOffset);
        rectTop.setFill(linearGradient);

        Rectangle rectLeft =new Rectangle(-shakeOffset,-shakeOffset,thickness+shakeOffset,height+shakeOffset*2);
        rectLeft.setFill(linearGradient3);
        Rectangle rectRight =new Rectangle(width-thickness,-shakeOffset,thickness+shakeOffset,height+shakeOffset*2);
        rectRight.setFill(linearGradient4);

        border.add(rectTop);
//        border.add(rectBot);
        border.add(rectLeft);
        border.add(rectRight);


        for(Shape rect: border ){
            FadeTransition fadeTransition=new FadeTransition(Duration.millis(500),rect);
            fadeTransition.setOnFinished(event -> parent.getChildren().remove(rect));
            fadeTransition.setFromValue(1.0);
            fadeTransition.setToValue(0.0);
            parent.getChildren().add(rect);
            fadeTransition.play();
            rect.toFront();

        }
    }
}
