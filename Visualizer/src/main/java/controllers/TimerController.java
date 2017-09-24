package controllers;

import javafx.fxml.FXML;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import models.Clock;
import models.ClockHandler;
import models.RaceClock;
import utilities.DataSource;

/**
 * Created by psu43 on 12/05/17.
 * Controller for race timers
 */
public class TimerController implements ClockHandler {
    @FXML Pane pane;
    @FXML Text timerText;
    private Clock raceClock;
    private long startTime;

    public long getStartTime() {
        return startTime;
    }


    /**
     * Begins the race clock and the world clock
     * @param dataSource DataSource the data to display
     */
    void begin(DataSource dataSource) {

        long expectedStartTime = dataSource.getExpectedStartTime();
        long firstMessageTime = dataSource.getMessageTime();
        this.raceClock = new RaceClock(this, 1, 0);
        long raceTime = firstMessageTime - expectedStartTime;
        raceClock.start(raceTime);

    }

    /**
     * Implementation of ClockHandler interface method
     * @param newTime The currentTime of the clock
     */
    public void clockTicked(String newTime, Clock clock) {
        if (clock == raceClock && System.currentTimeMillis() - startTime <= 31000) {
            timerText.setText(newTime);
            if (newTime.charAt(newTime.length() - 1) % 2 == 0 && System.currentTimeMillis() - startTime > 25000) {
                timerText.setFill(Color.RED);
            } else {
                timerText.setFill(Color.BLACK);
            }
        }
    }

}