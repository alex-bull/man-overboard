package controllers;

import javafx.fxml.FXML;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import models.Clock;
import models.ClockHandler;
import models.RaceClock;
import models.WorldClock;
import utilities.DataSource;

/**
 * Created by psu43 on 12/05/17.
 * Controller for race timers
 */
public class TimerController implements ClockHandler {
    @FXML Pane pane;
    @FXML Text worldClockValue;
    @FXML Text timerText;
    private Clock raceClock;
    private Clock worldClock;

    /**
     * Begins the race clock and the world clock
     * @param dataSource DataSource the data to display
     */
    void begin(DataSource dataSource) {

        long expectedStartTime = dataSource.getExpectedStartTime();
        long firstMessageTime = dataSource.getMessageTime();
        if (expectedStartTime != 0 && firstMessageTime != 0) {
            this.raceClock = new RaceClock(this, 1, 0);
            long raceTime = firstMessageTime - expectedStartTime;
            long startTime = System.currentTimeMillis() - raceTime;
            raceClock.start(startTime);
        } else {
            this.raceClock = new RaceClock(this, 1, 27000);
            raceClock.start();
        }

        String timezone = dataSource.getCourseTimezone();
        this.worldClock = new WorldClock(this, timezone);
        worldClock.start();

    }

    /**
     * Implementation of ClockHandler interface method
     * @param newTime The currentTime of the clock
     */
    public void clockTicked(String newTime, Clock clock) {
        if (clock == raceClock) {
            timerText.setText(newTime);
        }
        if (clock == worldClock) {
            worldClockValue.setText(newTime);
        }
    }

}