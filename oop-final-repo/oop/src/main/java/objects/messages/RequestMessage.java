package objects.messages;

import java.sql.Timestamp;

public class RequestMessage extends Message{
    public RequestMessage(int messageID, int senderID, int recieverID, Timestamp sentDate) {
        super(messageID, senderID, recieverID, sentDate);

    }
    public RequestMessage(int senderID, int recieverID, Timestamp sentDate) {
        super(senderID, recieverID, sentDate);

    }
    public RequestMessage(int messageID, int senderID, int recieverID) {
        super(messageID, senderID, recieverID, new Timestamp(new java.util.Date().getTime()));

    }
    public RequestMessage(int senderID, int recieverID) {
        super(senderID, recieverID,new Timestamp(new java.util.Date().getTime()));

    }

    @Override
    public String getType() {
        return "REQUEST";
    }

    @Override
    public String getContent() {
        return null;
    }
}
