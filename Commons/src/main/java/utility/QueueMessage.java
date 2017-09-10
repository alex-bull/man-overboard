package utility;

/**
 * Created by mattgoodson on 30/08/17.
 * Used to queue mesages between server and boat mocker
 */
public class QueueMessage {

    private Integer clientId;
    private byte[] header;
    private byte[] body;
    private byte[] message;

    public QueueMessage(Integer clientId, byte[] header, byte[] body) {
        this.header = header;
        this.body = body;
        this.clientId = clientId;
    }


    public QueueMessage(Integer clientId, byte[] message) {
        this.message = message;
        this.clientId = clientId;
    }


    public Integer getClientId() {
        return clientId;
    }

    public byte[] getHeader() {
        return header;
    }

    public byte[] getBody() {
        return body;
    }

    public byte[] getMessage() {
        return message;
    }

}
