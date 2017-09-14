package parsers.boatAction;

/**
 * Created by abu59 on 17/07/17.
 * Boat Action
 */
public enum BoatAction {


    SAILS_IN(2),
    SAILS_OUT(3),
    SWITCH_SAILS(9),
    TACK_GYBE(4),
    UPWIND(5),
    DOWNWIND(6),
    BOOST(7),
    POTION(8),
    UNKNOWN(-1),
    RIP(0);

    private final int value;

    BoatAction(int value) {
        this.value = value;
    }

    /**
     * Returns the corresponding boat action enum given an id
     *
     * @param id the boat action number
     * @return BoatAction the boat action
     */
    public static BoatAction getBoatAction(int id) {

        BoatAction action = null;

        switch (id) {
            case 0:
                action = RIP;
                break;

            case 2:
                action = SAILS_IN;
                break;
            case 3:
                action = SAILS_OUT;
                break;
            case 4:
                action = TACK_GYBE;
                break;
            case 5:
                action = UPWIND;
                break;
            case 6:
                action = DOWNWIND;
                break;

            case 7:
                action = BOOST;
                break;
            case 8:
                action = POTION;
                break;
            case 9:
                action = SWITCH_SAILS;
                break;
            default:
                action = UNKNOWN;
                break;
        }
        return action;
    }

    public int getValue() {
        return value;
    }
}
