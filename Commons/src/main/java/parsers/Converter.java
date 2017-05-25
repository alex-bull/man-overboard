package parsers;

/**
 * Created by psu43 on 13/04/17.
 * Common parser functions
 */
public class Converter {

    /**
     * Convert a byte array of little endian hex values into an integer
     *
     * @param hexValues byte[] a byte array of hexadecimal bytes in little endian format
     * @return int the value of the hexadecimal bytes
     */
    public static int hexByteArrayToInt(byte[] hexValues) {
        Long value = 0L;
        for (int i = 0; i < hexValues.length; i++) {
            value += ((long) hexValues[i] & 0xffL) << (8 * i);
        }
        return value.intValue();
    }

    /**
     * Convert a byte array of little endian hex values into a decimal heading
     *
     * @param hexValues byte[] a byte array of (2) hexadecimal bytes in little endian format
     * @return Double the value of the heading
     */
    public static Double parseHeading(byte[] hexValues) {
        return (double) hexByteArrayToInt(hexValues) * 360.0 / 65536.0;
    }


    /**
     * Convert a byte array of little endian hex values into a long
     *
     * @param hexValues byte[] a byte array of hexadecimal bytes in little endian format
     * @return long the value of the hexadecimal bytes
     */
    public static long hexByteArrayToLong(byte[] hexValues) {
        Long value = 0L;
        for (int i = 0; i < hexValues.length; i++) {
            value += ((long) hexValues[i] & 0xffL) << (8 * i);
        }
        return value;
    }


    /**
     * Convert the real time to relative time in seconds
     *
     * @param realTime real time from parser
     * @param messageTime time message was received
     * @return converted time in seconds
     */
    public static long convertToRelativeTime(long realTime, long messageTime){
        long relativeTime = 0;
        if (realTime != 0 && messageTime != 0) {
            relativeTime = (realTime - messageTime)/1000;
        }
        return relativeTime;
    }

    /**
     * Converts the received race status integer to a string with meaning.
     * @param status Integer the race status integer
     * @return String the description of the race status
     */
    public static String raceStatusToString(RaceStatusEnum status) {
        String statusString;
        switch(status) {
            case NOT_ACTIVE:
                statusString = "Not Active";
                break;
            case WARNING:
                statusString = "Warning";
                break;
            case PREPARATORY:
                statusString = "Preparatory";
                break;
            case STARTED:
                statusString = "Started";
                break;
            case FINISHED:
                statusString = "Finished";
                break;
            case RETIRED:
                statusString = "Retired";
                break;
            case ABANDONED:
                statusString = "Abandoned";
                break;
            case POSTPONED:
                statusString = "Postponed";
                break;
            case TERMINATED:
                statusString = "Terminated";
                break;
            case NOT_SET:
                statusString = "Race start time not set";
                break;
            case PRESTART:
                statusString = "Prestart";
                break;
            default:
                statusString = "No status found";
                break;
        }
        return statusString;
    }

}
