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
     * Converts the received race status integer to a string with meaning.
     *
     * @param status Integer the race status integer
     * @return String the description of the race status
     */
    public static String raceStatusToString(Integer status) {
        String statusString;
        switch (status) {
            case 0:
                statusString = "Not Active";
                break;
            case 1:
                statusString = "Warning";
                break;
            case 2:
                statusString = "Preparatory";
                break;
            case 3:
                statusString = "Started";
                break;
            case 4:
                statusString = "Finished";
                break;
            case 5:
                statusString = "Retired";
                break;
            case 6:
                statusString = "Abandoned";
                break;
            case 7:
                statusString = "Postponed";
                break;
            case 8:
                statusString = "Terminated";
                break;
            case 9:
                statusString = "Race start time not set";
                break;
            case 10:
                statusString = "Prestart";
                break;
            default:
                statusString = "No status found";
                break;
        }
        return statusString;
    }

}
