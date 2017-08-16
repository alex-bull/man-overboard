package parsers.boatHealth;


import parsers.Converter;

import java.util.Arrays;

import static parsers.Converter.hexByteArrayToInt;

/**
 * Created by psu43 on 14/08/17.
 */
public class HealthEventParser {
    private Integer sourceId;
    private Integer health;


    /**
     * Process the given data and get SourceID and health
     * @param body the data received
     */
    public HealthEventParser(byte[] body){
        sourceId = hexByteArrayToInt(Arrays.copyOfRange(body, 0, 4));
        health = hexByteArrayToInt(Arrays.copyOfRange(body, 4, 8));
    }


    public Integer getSourceId() {
        return sourceId;
    }

    public Integer getHealth() {
        return health;
    }

}
