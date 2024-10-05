package objects;

import java.sql.Timestamp;

public class Challenge {
    private int id = -1;
    private int challengerID;
    private int challengedID;
    private int quizID;
    private Timestamp dateSent;

    public Challenge(int challengerID, int challengedID, int quizID, Timestamp dateSent) {
        this.challengerID = challengerID;
        this.challengedID = challengedID;
        this.quizID = quizID;
        this.dateSent = dateSent;
    }

    public Challenge(int challengerID, int challengedID, int quizID) {
        this(challengerID, challengedID, quizID, new Timestamp(new java.util.Date().getTime()));
    }

    public void setChallengeId(int id) {
        this.id = id;
    }

    public int getChallengeId() {
        return id;
    }

    public int getChallengerID() {
        return challengerID;
    }

    public int getChallengedID() {
        return challengedID;
    }

    public int getQuizID() {
        return quizID;
    }

    public Timestamp getDateSent() {
        return dateSent;
    }
}
