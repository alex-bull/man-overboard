package parsers.connection;

import java.util.Arrays;

import static parsers.Converter.hexByteArrayToInt;

/**
 * Created by mattgoodson on 28/08/17.
 */
public class ConnectionParser {

    private Integer sourceId;
    private Integer status;


    /**
     * Process the given connection response data
     *
     * @param body the data received
     */
    public void update(byte[] body) {
        sourceId = hexByteArrayToInt(Arrays.copyOfRange(body, 0, 4));
        status = hexByteArrayToInt(Arrays.copyOfRange(body, 4, 5));
    }

    public Integer getSourceId() {
        return sourceId;
    }

    public Integer getStatus() {
        return status;
    }
}
