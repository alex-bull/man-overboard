package parsers.xml.race;

/**
 * Created by psu43 on 20/09/17.
 */
public enum ThemeEnum {

    ANTARCTICA(0),
    BERMUDA(1);

    private final int value;

    ThemeEnum(int value) {this.value = value;}

    public int getValue() {
        return value;
    }

}
