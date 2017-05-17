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
    MARK_ROUNDING(38);

    private final int value;

    MessageType(int value) {this.value = value;}

    public int getValue() {
        return value;
    }
}
