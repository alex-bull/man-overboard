package parsers.boatAction;

/**
 * Created by abu59 on 17/07/17.
 */
public enum BoatAction {
    VMG(1),
    SAILS_IN(2),
    SAILS_OUT(3),
    TACK_GYBE(4),
    UPWIND(5),
    DOWNWIND(6),
    RIP(0),
    NULL(7);

    private final int value;

    BoatAction(int value) {this.value = value;}

    public int getValue() {
        return value;
    }
}
