package objects;

import java.sql.Timestamp;

public class Announcement {
    private int id;
    private final String title;
    private final int creatorId;
    private final Timestamp creationDate;
    private final String text;


    public Announcement(int creatorId, Timestamp creationDate, String title, String text) {
        this.id = -1;
        this.creatorId = creatorId;
        this.creationDate = creationDate;
        this.title = title;
        this.text = text;
    }
    public void setId(int id){ this.id = id; }

    public int getId() {
        return id;
    }

    public int getCreatorId() {
        return creatorId;
    }

    public Timestamp getCreationDate() {
        return creationDate;
    }
    public String getTitle() {
        return title;
    }

    public String getText() {
        return text;
    }
}
