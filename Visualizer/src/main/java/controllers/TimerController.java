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
    @FXML Pane timer;
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

        long firstMessageTime = dataSource.getMessageTime();
        System.out.println("Timer starting with" + firstMessageTime);
        this.raceClock = new RaceClock(this, 1, 0);
        raceClock.start(firstMessageTime);

    }

    /**
     * Implementation of ClockHandler interface method
     * @param newTime The currentTime of the clock
     */
    public void clockTicked(String newTime, Clock clock, long timeMillis) {
        timerText.setText(newTime);
        int flashInterval = 10000;
        if(timeMillis <= flashInterval) {
            if (timeMillis/500 % 2 == 0) {
                timerText.setFill(Color.RED);
            } else {
                timerText.setFill(Color.BLACK);
            }
        }
        if(timeMillis <= 0) {
            this.raceClock.stop();
        }
    }

}