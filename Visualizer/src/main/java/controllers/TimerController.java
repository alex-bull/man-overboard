package controllers;

import javafx.fxml.FXML;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import models.Clock;
import models.ClockHandler;
import models.RaceClock;
import models.WorldClock;
import parsers.Converter;
import parsers.RaceStatusEnum;
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
    private DataSource dataSource;

    /**
     * Begins the race clock and the world clock
     * @param dataSource DataSource the data to display
     */
    void begin(DataSource dataSource) {
        this.dataSource = dataSource;
        long expectedStartTime = dataSource.getExpectedStartTime();
        long firstMessageTime = dataSource.getMessageTime();
        long raceTime = Converter.convertToRelativeTime(expectedStartTime, firstMessageTime); // time in seconds since start of race

        this.raceClock = new RaceClock(this, 1, 0);
        long startTime = System.currentTimeMillis() - (raceTime * 1000); // absolute time that the race started
        raceClock.start(startTime);

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
            RaceStatusEnum status = dataSource.getRaceStatus();
            switch (status) {
                case PRESTART:
                case WARNING:
                    // reset clock (if the stream has looped back to the start)
                    begin(dataSource);
                case PREPARATORY:
                case STARTED:
                    timerText.setText(newTime);
                    break;
                case TERMINATED:
                case FINISHED:
                    // keep current timer text
                    break;
                case ABANDONED:
                case NOT_SET:
                case NOT_ACTIVE:
                case NOT_VALID:
                case RETIRED:
                case POSTPONED:
                    timerText.setText("Unknown");

            }
        }
        if (clock == worldClock) {
            worldClockValue.setText(newTime);
        }
    }

}