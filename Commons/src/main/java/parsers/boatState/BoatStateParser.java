package parsers.boatState;

import java.util.Arrays;

import static parsers.Converter.hexByteArrayToInt;

/**
 * Created by psu43 on 16/08/17.
 * Boat state parser
 */
public class BoatStateParser {
    private Integer sourceId;
    private Integer health;


    /**
     * Process the given boat state data
     *
     * @param body the data received
     */
    public void update(byte[] body) {
        sourceId = hexByteArrayToInt(Arrays.copyOfRange(body, 0, 4));
        health = hexByteArrayToInt(Arrays.copyOfRange(body, 4, 5));
    }

    public Integer getSourceId() {
        return sourceId;
    }

    public Integer getHealth() {
        return health;
    }

}
