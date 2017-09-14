package parsers.powerUp;

/**
 * Created by psu43 on 10/09/17.
 * PowerUp Type
 */
public enum PowerUpType {
    BOOST(0),
    POTION(3);

    private final int value;

    PowerUpType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
