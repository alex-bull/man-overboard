package parsers.boatState;

import java.util.Arrays;

import static parsers.Converter.hexByteArrayToInt;

/**
 * Created by psu43 on 16/08/17.
 * Boat state parser
 */
public class BoatStateParser {
    private Integer sourceId;
    private Integer sailState;
    private Integer health;


    /**
     * Process the given boat state data
     * @param body the data received
     */
    public BoatStateParser(byte[] body){
        sourceId = hexByteArrayToInt(Arrays.copyOfRange(body, 0, 4));
        sailState = hexByteArrayToInt(Arrays.copyOfRange(body, 4, 5));
        health = hexByteArrayToInt(Arrays.copyOfRange(body, 5, 6));
    }


    public Integer getSailState() {
        return sailState;
    }

    public Integer getSourceId() {
        return sourceId;
    }

    public Integer getHealth() {
        return health;
    }

}
