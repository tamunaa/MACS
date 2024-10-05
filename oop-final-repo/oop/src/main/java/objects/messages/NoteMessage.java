package objects.messages;

import java.sql.Timestamp;

public class NoteMessage extends Message {
    private String messageContent;
    public NoteMessage(int messageID,int senderID, int recieverID, String messageContent, Timestamp sentDate) {
        super(messageID,senderID,recieverID,sentDate);
        this.messageContent = messageContent;
    }
    public NoteMessage(int senderID, int recieverID,String messageContent, Timestamp sentDate) {
        super(senderID,recieverID,sentDate);
        this.messageContent = messageContent;
    }
    public NoteMessage(int senderID, int recieverID,String messageContent) {
        super(senderID,recieverID,new Timestamp(new java.util.Date().getTime()));
        this.messageContent = messageContent;
    }

    public NoteMessage(int messageID,int senderID, int recieverID,String messageContent) {
        super(messageID,senderID,recieverID,new Timestamp(new java.util.Date().getTime()));
        this.messageContent = messageContent;
    }
    @Override
    public String getType() {
        return "NOTE";
    }

    @Override
    public String getContent() {
        return messageContent;
    }
}
