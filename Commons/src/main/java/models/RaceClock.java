package models;

import javafx.animation.AnimationTimer;

import static java.lang.Math.abs;

/**
 * Created by mgo65 on 30/03/17.
 * Clock for the race
 */
public class RaceClock extends AnimationTimer implements Clock {

    private long startTime;
    private int scaleFactor;
    private int negativeTime;
    private ClockHandler clockHandler;


    public RaceClock(ClockHandler handler, int scaleFactor, int negativeTime) {

        this.clockHandler = handler;
        this.scaleFactor = scaleFactor;
        this.negativeTime = negativeTime;

    }

    public void start(long startTimeMillis) {
        this.startTime = startTimeMillis;
        super.start();
    }

    @Override
    public void start() {
        this.startTime = System.currentTimeMillis();
        super.start();
    }


    @Override
    public void handle(long now) {
        String newTime = this.formatDisplayTime(System.currentTimeMillis() - startTime);
        this.clockHandler.clockTicked(newTime, this);
    }


    /**
     * Creates a formatted display time string in mm:ss and takes into account the scale factor
     *
     * @param display long the current duration of the race
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
