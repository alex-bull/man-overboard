package parsers;

/**
 * Created by jar156 on 14/04/17.
 * Enum for race status
 */
public enum RaceStatusEnum {
    NOT_ACTIVE(0),
    WARNING(1),
    PREPARATORY(2),
    STARTED(3),
    FINISHED(4),
    RETIRED(5),
    ABANDONED(6),
    POSTPONED(7),
    TERMINATED(8),
    NOT_SET(9),
    PRESTART(10),
    NOT_VALID(11);

    private final int value;

    RaceStatusEnum(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
