package model;

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
    }

    @Override
    public void start() {
        super.start();
    }


    @Override
    public void handle(long now) {
        String time = calculateWorldTime();
        this.clockHandler.clockTicked(time, this);
    }

    private String formatHours(Calendar calendar) {
        String hours = Integer.toString(calendar.get(Calendar.HOUR));
        if (hours.equals("0")) {
            hours = "12";
        }
        return hours;
    }

    private String formatMinutes(Calendar calendar) {
        String minutes = Integer.toString(calendar.get(Calendar.MINUTE));
        if (minutes.length() < 2) {
            minutes = "0" + minutes;
        }
        return minutes;
    }

    private String formatSeconds(Calendar calendar) {
        String seconds = Integer.toString(calendar.get(Calendar.SECOND));
        if (seconds.length() < 2) {
            seconds = "0" + seconds;
        }
        return seconds;
    }

    private String getAmOrPm(Calendar calendar) {
        if (calendar.get(Calendar.AM_PM) == Calendar.AM) {
            return "AM";
        } else {
            return "PM";
        }
    }

    private String calculateWorldTime() {
        if (offsetUTC != null) {

            String signUTC = offsetUTC.substring(0,1);
            String absUTC = offsetUTC.substring(1);
            int minUTC = 0;
            int maxUTC = 14;

            boolean isValidUTC = absUTC.matches("[0-9]+")
                    && (signUTC.equals("+") || signUTC.equals("-"))
                    && Integer.valueOf(absUTC) >= minUTC
                    && Integer.valueOf(absUTC) < maxUTC;

            if (isValidUTC) {
                return formatWorldTime() + "  UTC" + offsetUTC;

            } else {
                offsetUTC = "+0";
                return formatWorldTime() + " UTC+0";
            }
        } else {
            offsetUTC = "+0";
            return formatWorldTime() + " UTC+0";
        }
    }

    private String formatWorldTime() {

        TimeZone timeZone = TimeZone.getTimeZone("GMT" + offsetUTC);
        Calendar calendar = Calendar.getInstance(timeZone);

        String hours = formatHours(calendar);
        String minutes = formatMinutes(calendar);
        String seconds = formatSeconds(calendar);
        String ampm = getAmOrPm(calendar);

        String time = hours + ":" + minutes + ":" + seconds + " " + ampm;
        return time;

    }

}
