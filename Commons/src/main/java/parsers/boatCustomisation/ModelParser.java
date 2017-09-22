package parsers.boatCustomisation;

import java.util.Arrays;

import static parsers.Converter.hexByteArrayToInt;

/**
 * Created by rjc249 on 12/09/17.
 */
public class ModelParser {

    private Integer sourceId;
    private Integer model;


    /**
     * Process the given connection response data
     *
     * @param body the data received
     */
    public ModelParser(byte[] body) {
        sourceId = hexByteArrayToInt(Arrays.copyOfRange(body, 0, 4));
        model = hexByteArrayToInt(Arrays.copyOfRange(body, 6, 7));
    }

    public Integer getSourceId() {
        return sourceId;
    }

    public Integer getModel() {
        System.out.println("modelparser" + model);
        return model;
    }
}

