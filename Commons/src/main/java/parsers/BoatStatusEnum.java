package parsers;

/**
 * Created by ikj11 on 10/08/17.
 * Enum for Boat Statuses
 */
public enum BoatStatusEnum {
    UNDEFINED, PRESTART, RACING, FINISHED, DNS, DNF, DSQ, OCS;

    /**
     * Converts the received race status enum to a int.
     *
     * @param status Enum the boat status
     * @return Integer of the boat status
     */
    public static int boatStatusToInt(BoatStatusEnum status) {
        int statusInt = 0;
        switch (status) {
            case UNDEFINED:
                statusInt = 0;
                break;
            case PRESTART:
                statusInt = 1;
                break;
            case RACING:
                statusInt = 2;
                break;
            case FINISHED:
                statusInt = 3;
                break;
            case DNS:
                statusInt = 4;
                break;
            case DNF:
                statusInt = 5;
                break;
            case DSQ:
                statusInt = 6;
                break;
            case OCS:
                statusInt = 7;
                break;
            default:
                break;
        }
        return statusInt;
    }
}

