package parsers;

/**
 * Created by jar156 on 12/05/17.
 * Enum for message types
 */
public enum MessageType {
    UNKNOWN(-1),
    RACE_STATUS(12),
    XML(26),
    YACHT_ACTION(29),
    BOAT_LOCATION(37),
    MARK_ROUNDING(38),
    COURSE_WIND(44),
    SOURCE_ID(56),
    RESTART_RACE(78),
    DISCONNECT(79),
    BOAT_HEALTH(97),
    BOAT_ACTION(100),
    CONNECTION_REQ(101),
    CONNECTION_RES(102),
    BOAT_STATE(103),
    MODEL_REQUEST(104),
    NAME_REQUEST(106),
    FALLEN_CREW(107),
    PLAYER_READY(110),
    LEAVE_LOBBY(111),
    POWER_UP(112),
    POWER_UP_TAKEN(113),
    WHIRLPOOL(119),
    SHARK(120),
    BLOOD(121);


    private final int value;

    MessageType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
