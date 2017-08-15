package parsers.boatAction;

/**
 * Created by abu59 on 17/07/17.
 * Boat Action
 */
public enum BoatAction {

    VMG(1),
    SAILS_IN(2),
    SAILS_OUT(3),
    TACK_GYBE(4),
    UPWIND(5),
    DOWNWIND(6),
    RIP(0);

    private final int value;

    BoatAction(int value) {this.value = value;}

    public int getValue() {
        return value;
    }

    public static BoatAction getBoatAction(int id) {

        BoatAction action = null;

        switch (id) {
            case 0:
                action = RIP;
                break;
            case 1:
                action = VMG;
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
            default:
                break;
        }
        return action;
    }
}
