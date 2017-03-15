package seng302.Controllers;

import com.sun.corba.se.impl.orbutil.graph.Graph;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import seng302.Model.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Controller for the race view.
 */
public class RaceViewController implements RaceDelegate{


    @FXML
    private Canvas mycanvas;

    private GraphicsContext gc;
    private Race race;
    private List<String> finishingOrder = new ArrayList<>();

    /**
     * Initialiser for the raceViewController
     */
    @FXML
    void initialize() {
         gc = mycanvas.getGraphicsContext2D();

    }

    public void boatMoved() {

    }

    /**
     * Draw a circle on the canvas
     */
    private void drawCircle(int x, int y) {

        gc.setFill(Color.GREEN);
        gc.fillOval(x, y, 10, 10);

    }


    /**
     * Begins the race on the canvas
     */
    public void begin(){
        //drawBoats(gc);
        // start the race using the timeline
        try {
            startRace(race);
        }
        catch (Exception e) {
            System.out.println("Thread interrupted");
        }



    }

    /**
     * Start the race using the race timeline in real time
     * @throws InterruptedException if thread sleep interrupted
     * @param race Race a race
     */
    private void startRace(Race race) throws InterruptedException {
//        race.start();
//        int i = 0;
//        Map<Integer, List<RaceEvent>> raceTimeline = race.getRaceTimeline();
//
//        // loop through each boat until all boats have finished
//        while (finishingOrder.size() < race.getCompetitors().size()) {
//            if (raceTimeline.get(i) != null) {
//                // for each event at this time, move the boat
//                for (RaceEvent event: raceTimeline.get(i)) {
//                    System.out.println(event.getEventString());
//                    // move the boat
//                    moveBoat(event.getBoat(), event.getEndPoint());
//                    // check if the boat has passed a finishing mark
//                    if (event.getIsFinish()) {
//                        finishingOrder.add(event.getTeamName());
//                    }
//                }
//            }
//            Thread.sleep(1000);
//            i++;
//        }
//
//        int placing = 1;
//        System.out.println("Finishing order:");
//        for (String team: finishingOrder) {
//            System.out.println("#" + placing + " " + team);
//            placing++;
//        }

    }

    /**
     * Move a boat on the canvas to its next destination
     * @param boat Competitor the competitor
     * @param destination CourseFeature the destination of the boat
     */
    private void moveBoat(Competitor boat, CourseFeature destination) {
        // add a boat to the canvas
        drawCircle(destination.getLocation().getX().intValue(), destination.getLocation().getY().intValue());
    }

    /**
     * Sets the race
     * @param race Race a group of competitors across multiple races on a course
     */
    public void setRace(Race race) {
        this.race=race;

    }
}
