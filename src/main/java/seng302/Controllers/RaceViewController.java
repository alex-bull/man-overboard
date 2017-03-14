package seng302.Controllers;

import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import seng302.Model.Competitor;
import seng302.Model.Regatta;

/**
 * Controller for the race view.
 */
public class RaceViewController {


    @FXML
    private Canvas mycanvas;
    private GraphicsContext gc;
    private Regatta regatta;
    @FXML
    void initialize() {
         gc = mycanvas.getGraphicsContext2D();

    }

    private void draw(GraphicsContext gc) {
        for(Competitor b:regatta.getCompetitors()){
            gc.setFill(Color.GREEN);
            gc.fillOval(b.getPosition().getX(),b.getPosition().getY(),30 ,30);
        }
//        System.out.println(regatta);
//        gc.setFill(Color.GREEN);
//        gc.fillOval(50, 100, 200, 200);

    }


    public void begin(){
        draw(gc);
    }
    public void setRegatta(Regatta regatta) {
        this.regatta=regatta;

    }
}
