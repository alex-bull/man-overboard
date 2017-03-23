package seng302.Controllers;

import javafx.animation.AnimationTimer;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.transform.Rotate;
import seng302.Model.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Controller for the race view.
 */
public class RaceViewController implements RaceDelegate{


    @FXML
    private Canvas mycanvas;
    private Race race;

    @FXML
    private TableController tableController;



    /**
     * Initialiser for the raceViewController
     */
    @FXML
    void initialize() {

    }

    /**
     * Sets the race
     * @param race Race a group of competitors across multiple races on a course
     */
    public void begin(Race race, double width, double height) {
        this.race=race;
        animate(width, height);

    }

    /**
     * Draws an arrow on the screen at top left corner
     * @param gc graphics context
     * @param angle the angle of rotation
     */
    void drawArrow(GraphicsContext gc, double angle) {
        gc.save();
        gc.setFill(Color.BLACK);
        Rotate r = new Rotate(angle, 35, 40);

        gc.setTransform(r.getMxx(), r.getMyx(), r.getMxy(), r.getMyy(), r.getTx(), r.getTy());

        gc.fillPolygon(new double[]{20,30,30,40,40,50,35}, new double[]{30,30,70,70,30,30,10},
                7);
        gc.restore();
    }


    /**
     * Draws the course features on the canvas
     * @param gc GraphicContext the context to draw on
     */
    private void drawCourse(GraphicsContext gc) {

        for (CourseFeature courseFeature : this.race.getCourseFeatures()) {
            gc.setFill(Color.ORANGERED);
            gc.setStroke(Color.BLUE);

            List<MutablePoint> marks = courseFeature.getLocations();
            Double x1 = marks.get(0).getXValue();
            Double y1 = marks.get(0).getYValue();

            // if it is a gate
            if (marks.size() == 2) {
                gc.setLineWidth(3);
                int d = 15;
                double r = d/2;
                Double x2 = marks.get(1).getXValue();
                double y2 = marks.get(1).getYValue();

                // check if gate needs line
                if(courseFeature.isLine()){
                    gc.strokeLine(x1, y1, x2, y2);
                }

                gc.fillOval(x1 - r, y1 - r, d, d);
                gc.fillOval(x2 - r, y2 - r, d, d);
            } else {
                gc.fillOval(x1, y1, 20, 20);
            }

        }

        drawArrow(gc, race.getWindDirection());
    }


    /**
     * Starts the animation timer to animate the race
     * @param width the width of the canvas
     * @param height the height of the canvas
     */
    public void animate(double width, double height){

        //the offset for each overlapping label
        int offsetY=40;
        //arraylists to store coordinates
        ArrayList<Double> xCoords=new ArrayList<>();
        ArrayList<Double> yCoords=new ArrayList<>();
        // start the race using the timeline
        Timeline t = race.generateTimeline(tableController);
        List<Competitor> competitors = race.getCompetitors();

        AnimationTimer timer = new AnimationTimer() {

            long startTimeNano = System.nanoTime();
            long currentTimeNano = System.nanoTime();
            int counter=0;
            int fps;

            @Override
            public void handle(long now) {
                counter++;

                // clear the canvas
                GraphicsContext gc = mycanvas.getGraphicsContext2D();
                gc.clearRect(0,0,width,height);

                //calculate
                currentTimeNano=System.nanoTime();
                if (currentTimeNano > startTimeNano + 1000000000){
                    startTimeNano = System.nanoTime();

                    fps=counter;
                    counter=0;
                }

                // draw course
                gc.setFill(Color.LIGHTBLUE);
                gc.fillRect(0,0,width,height);
                drawCourse(gc);

                //draw fps counter
                gc.setFill(Color.BLACK);
                gc.setFont(Font.font("Monospaced",20));
                gc.fillText(String.format("FPS: %d",fps),0,height-10);

                // draw competitors
                for(int i =0; i< competitors.size(); i++)  {
                    Competitor boat=competitors.get(i);
                    double xValue=boat.getPosition().getXValue();
                    double yValue=boat.getPosition().getYValue();
                    gc.setFill(boat.getColor());
                    gc.fillOval(
                            xValue,
                            yValue,
                            10,
                            10
                    );

                    //set font to monospaced for easier layout formatting
                    gc.setFont(Font.font("Monospaced"));

//                    //check if labels are overlapping, if so offset the y value
//                    for (int j=0;j<xCoords.size();j++){
//                        double x=xCoords.get(j);
//                        double y=yCoords.get(j);
//                        if (xValue>(x-25) && xValue<(x+25) && yValue<(y+10) && yValue>(y-10)){
//                            yValue+=offsetY;
//                        }
//                    }
                    //draw label

                    gc.fillText(boat.getAbbreName(),xValue-10,yValue);
                    gc.fillText(boat.getVelocity().toString()+" m/s",xValue-20,yValue+20);
                    yCoords.add(yValue);
                    xCoords.add(xValue);
//                    System.out.println(xCoords);
                }
            }
        };

        timer.start();
        t.play();

    }




}
