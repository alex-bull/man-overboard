package parsers;

/**
 * Created by jar156 on 12/05/17.
 * Enum for message types
 */
public enum MessageType {
    // TODO put these in order
    UNKNOWN(-1),
    RACE_STATUS(12),
    XML(26),
    BOAT_LOCATION(37),
    MARK_ROUNDING(38),
    BOAT_ACTION(100),
    COURSE_WIND(44),
    SOURCE_ID(56),
    YACHT_ACTION(29),
    RESTART_RACE(78),
    BOAT_HEALTH(97),
    BOAT_STATE(103),
    CONNECTION_REQ(101),
    CONNECTION_RES(102),
    PLAYER_READY(110),
    LEAVE_LOBBY(111),
    WHIRLPOOL(119),
    SHARK(120),
    BLOOD(-100),
    FALLEN_CREW(107),
    NAME_REQUEST(106),
    MODEL_REQUEST(104),
    POWER_UP(112),
    POWER_UP_TAKEN(113);


    private final int value;

    MessageType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
