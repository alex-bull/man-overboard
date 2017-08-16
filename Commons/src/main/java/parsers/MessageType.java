package parsers;

/**
 * Created by jar156 on 12/05/17.
 * Enum for message types
 */
public enum MessageType {
    UNKNOWN(-1),
    RACE_STATUS(12),
    XML(26),
    BOAT_LOCATION(37),
    MARK_ROUNDING(38),
    BOAT_ACTION(100),
    COURSE_WIND(44),
    SOURCE_ID(56),
    YACHT_ACTION(29),
    BOAT_HEALTH(97),
    REGISTRATION_REQUEST(101),
    REGISTRATION_RESPONSE(102);

    private final int value;

    MessageType(int value) {this.value = value;}

    public int getValue() {
        return value;
    }
}
