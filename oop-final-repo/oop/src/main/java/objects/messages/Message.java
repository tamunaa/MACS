package objects.messages;

import java.sql.Timestamp;

public abstract class Message implements Comparable {
    private int messageID = -1;
    private int senderID;
    private int recieverID;
    private String content;
    private String messageType;
    private Timestamp sentDate;

    public Message(int messageID, int senderID, int recieverID, Timestamp sentDate) {
        this.messageID = messageID ;
        this.senderID = senderID;
        this.recieverID = recieverID;
        this.sentDate = sentDate;
    }
    public Message(int senderID, int recieverID, Timestamp sentDate) {
        this.senderID = senderID;
        this.recieverID = recieverID;
        this.sentDate = sentDate;
    }


    public int getId() {
        return messageID;
    }

    public int getSenderID() {
        return senderID ;
    }

    public int getRecieverID() {
        return recieverID;
    }

    public Timestamp getDate() {
        return sentDate;
    }
    public void setId(int id) {
        this.messageID = id;
    }
    public void setSentTime(Timestamp sentDate) {
        this.sentDate = sentDate;
    }

    @Override
    public int compareTo(Object o){
        Message other = (Message) o;
        return this.sentDate.compareTo(other.sentDate);
    }

    public abstract String getType();
    public abstract String getContent();
}
