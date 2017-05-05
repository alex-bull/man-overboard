package seng302.Model;

import javafx.animation.AnimationTimer;

import java.util.Calendar;
import java.util.TimeZone;

/**
 * Created by psu43 on 6/04/17.
 * Live clock
 */
public class WorldClock extends AnimationTimer implements Clock {

    private ClockHandler clockHandler;
    private String offsetUTC;

    public WorldClock(ClockHandler handler, String offsetUTC) {
        this.clockHandler = handler;
        this.offsetUTC = offsetUTC;
    }

    public void start(long startTime) {
        // no op
    }

    @Override
    public void start() {
        super.start();
    }


    @Override
    public void handle(long now) {
        String stringTime = this.formatWorldTime();
        this.clockHandler.clockTicked(stringTime, this);
    }


    private String formatWorldTime() {

        TimeZone timeZone = TimeZone.getTimeZone("GMT" + offsetUTC);
        Calendar calendar = Calendar.getInstance(timeZone);

        String hour = Integer.toString(calendar.get(Calendar.HOUR));
        String minutes = Integer.toString(calendar.get(Calendar.MINUTE));
        String seconds = Integer.toString(calendar.get(Calendar.SECOND));
        String ampm;
        if (calendar.get(Calendar.AM_PM) == Calendar.AM) {
            ampm = "AM";
        } else {
            ampm = "PM";
        }

        if (minutes.length() < 2) {
            minutes = "0" + minutes;
        }
        if (seconds.length() < 2) {
            seconds = "0" + seconds;
        }
        if (hour.equals("0")) {
            hour = "12";
        }

        if (offsetUTC != null) {
            return hour + ":" + minutes + ":" + seconds + " " + ampm + "  UTC" + offsetUTC;
        } else {
            return hour + ":" + minutes + ":" + seconds + " " + ampm + " NZT";
        }
    }

}
