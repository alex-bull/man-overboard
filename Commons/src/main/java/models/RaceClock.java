package models;

import javafx.animation.AnimationTimer;
import parsers.Converter;

import java.util.Timer;
import java.util.TimerTask;

import static java.lang.Math.abs;

/**
 * Created by mgo65 on 30/03/17.
 * Clock for the race
 */
public class RaceClock extends TimerTask implements Clock {

    private long startTime;
    private int scaleFactor;
    private int negativeTime;
    private ClockHandler clockHandler;
    private Timer t;


    public RaceClock(ClockHandler handler, int scaleFactor, int negativeTime) {

        this.clockHandler = handler;
        this.scaleFactor = scaleFactor;
        this.negativeTime = negativeTime;

    }


    public void start(long startTimeMillis) {
        this.startTime = startTimeMillis;
         this.t = new Timer();
        t.schedule(this, 0, 500);
    }


    public void run() {
        int gameDuration = 300000; // 5min
        long time = gameDuration - (System.currentTimeMillis() - startTime);
        String newTime = this.formatDisplayTime(time);
        this.clockHandler.clockTicked(newTime, this, time);
    }

    public void stop() {
        this.t.cancel();
    }


    /**
     * Creates a formatted display time string in mm:ss and takes into account the scale factor
     *
     * @param display long the current duration of the race in milliseconds
     * @return String the time string
     */
    private String formatDisplayTime(long display) {

        // calculate the actual race time using the scale factor
        display = (display - (negativeTime / scaleFactor)) * scaleFactor;

        // format the time shown
        int displayTime = abs((int) display / 1000);
        int minutes = displayTime / 60;
        int seconds = displayTime - (60 * minutes);
        String formattedTime = "";

        if (display < 0 && displayTime != 0) {
            formattedTime += "-";
        }

        if (minutes > 9) {
            formattedTime += minutes + ":";
        } else {
            formattedTime += "0" + minutes + ":";
        }
        if (seconds > 9) {
            formattedTime += seconds;
        } else {
            formattedTime += "0" + seconds;
        }

        return formattedTime;
    }


}
