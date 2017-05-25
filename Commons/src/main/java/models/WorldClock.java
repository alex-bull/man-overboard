package models;

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

    /**
     * Constructs a World Clock
     * @param handler ClockHandler handler for the clock
     * @param offsetUTC String offset UTC
     */
    public WorldClock(ClockHandler handler, String offsetUTC) {
        this.clockHandler = handler;
        this.offsetUTC = offsetUTC;
    }

    /**
     * Starts the clock at the given time
     * @param startTime long the start time
     */
    public void start(long startTime) {
    }

    /**
     * Starts the clock
     */
    public void start() {
        super.start();
    }


    /**
     * Sets the clock to the world time
     * @param now long the current time
     */
    @Override
    public void handle(long now) {
        String time = calculateWorldTime();
        this.clockHandler.clockTicked(time, this);
    }

    /**
     * Formats calendar time to hours
     * @param calendar Calendar
     * @return String the formatted hour
     */
    private String formatHours(Calendar calendar) {
        String hours = Integer.toString(calendar.get(Calendar.HOUR));
        if (hours.equals("0")) {
            hours = "12";
        }
        return hours;
    }

    /**
     * Formats calendar time to minutes
     * @param calendar Calendar
     * @return String the formatted minutes
     */
    private String formatMinutes(Calendar calendar) {
        String minutes = Integer.toString(calendar.get(Calendar.MINUTE));
        if (minutes.length() < 2) {
            minutes = "0" + minutes;
        }
        return minutes;
    }

    /**
     * Formats calendar time to seconds
     * @param calendar Calendar
     * @return String the formatted seconds
     */
    private String formatSeconds(Calendar calendar) {
        String seconds = Integer.toString(calendar.get(Calendar.SECOND));
        if (seconds.length() < 2) {
            seconds = "0" + seconds;
        }
        return seconds;
    }

    /**
     * Formats calendar time to AM or PM
     * @param calendar Calendar
     * @return String AM or PM
     */
    private String getAmOrPm(Calendar calendar) {
        if (calendar.get(Calendar.AM_PM) == Calendar.AM) {
            return "AM";
        } else {
            return "PM";
        }
    }

    /**
     * Calculates the world time
     * @return String the world time
     */
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

    /**
     * Formats the world time
     * @return String the formatted world time
     */
    private String formatWorldTime() {

        TimeZone timeZone = TimeZone.getTimeZone("GMT" + offsetUTC);
        Calendar calendar = Calendar.getInstance(timeZone);

        String hours = formatHours(calendar);
        String minutes = formatMinutes(calendar);
        String seconds = formatSeconds(calendar);
        String ampm = getAmOrPm(calendar);

        return hours + ":" + minutes + ":" + seconds + " " + ampm;

    }

}
