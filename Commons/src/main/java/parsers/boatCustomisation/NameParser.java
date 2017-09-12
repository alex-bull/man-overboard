package parsers.boatCustomisation;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

import static parsers.Converter.hexByteArrayToInt;

/**
 * Created by rjc249 on 12/09/17.
 */
public class NameParser {


    private Integer sourceId;
    private String name;


    /**
     * Process the given connection response data
     * @param body the data received
     */
    public NameParser(byte[] body){
        sourceId = hexByteArrayToInt(Arrays.copyOfRange(body, 0, 4));
        try {
            name = new String(Arrays.copyOfRange(body, 6, 36), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            //
        }
    }

    public Integer getSourceId() {
        return sourceId;
    }

    public String getName() {
        return name;
    }
}
