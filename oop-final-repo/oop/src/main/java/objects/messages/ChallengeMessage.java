package objects.messages;

import java.sql.Timestamp;

public class ChallengeMessage extends Message{
    private String link;
    public ChallengeMessage(int messageID, int senderID, int recieverID,String link, Timestamp sentDate) {
        super(messageID, senderID, recieverID, sentDate);
        this.link = link;
    }
    public ChallengeMessage( int senderID, int recieverID, String link, Timestamp sentDate) {
        super( senderID, recieverID, sentDate);
        this.link = link;
    }
    public ChallengeMessage( int senderID, int recieverID,String link) {
        super( senderID, recieverID, new Timestamp(new java.util.Date().getTime()));
        this.link = link;
    }
    public ChallengeMessage(int messageID, int senderID, int recieverID, String link) {
        super(messageID, senderID, recieverID, new Timestamp(new java.util.Date().getTime()));
        this.link = link;

    }
    @Override
    public String getType() {
        return "CHALLENGE";
    }

    @Override
    public String getContent() {
        return link;
    }
}
