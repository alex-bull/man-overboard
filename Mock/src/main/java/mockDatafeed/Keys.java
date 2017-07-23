package mockDatafeed;

/**
 * Created by psu43 on 20/07/17.
 * Enum for key presses
 */
public enum Keys {
    UP(5), DOWN(6), VMG(1), SAILS(2), TACK(4), UNKNOWN(-1);

    private final int value;

    Keys(int value) {this.value = value;}

    public int getValue() {
        return value;
    }


    /**
     * Convert integer key value to a enum for the keypress
     * @param value int value of the key
     * @return Keys enum for the key press
     */
    public static Keys getKeys(int value) {
        switch(value) {
            case 5:
                return UP;
            case 6:
                return DOWN;
            case 1:
                return VMG;
            case 2:
                return SAILS;
            case 4:
                return TACK;
            default:
                return UNKNOWN;
        }
    }

}
