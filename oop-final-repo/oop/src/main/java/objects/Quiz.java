package objects;

import java.sql.Date;
import java.sql.Timestamp;

public class Quiz {
    private int Author;
    private String quizName;
    private String description;
    private int timer;
    private String category;
    private Timestamp dateCreated;
    private boolean isRandom;
    private boolean onOnePage;
    private boolean correctImmediately;
    private boolean isPractice;
    private int ID = -1;


    public Quiz(int author, String quizName, String description, int timer, String category, Timestamp created,
                boolean isRandom, boolean onOnePage,boolean correctImmediately, boolean isPractice){
        Author = author;
        this.quizName = quizName;
        this.description = description;
        this.timer = timer;
        this.category = category;
        this.dateCreated = created;
        this.isRandom = isRandom;
        this.onOnePage = onOnePage;
        this.correctImmediately = correctImmediately;
        this.isPractice = isPractice;
    }

    public Quiz(int author, String quizName, String description, int timer, String category){
        this(author, quizName, description, timer, category, new Timestamp(new java.util.Date().getTime()), false, false, false, false);
    }

    public void setDateCreated(Timestamp dateCreated) {
        this.dateCreated = dateCreated;
    }

    public void setRandom(boolean random) {
        isRandom = random;
    }

    public void setOnOnePage(boolean onOnePage) {
        this.onOnePage = onOnePage;
    }

    public void setCorrectImmediately(boolean correctImmediately) {
        this.correctImmediately = correctImmediately;
    }

    public void setPractice(boolean practice) {
        isPractice = practice;
    }

    public int getAuthor() {
        return Author;
    }

    public String getQuizName() {
        return quizName;
    }

    public String getDescription() {
        return description;
    }

    public int getTimer() {
        return timer;
    }

    public Timestamp getDateCreated() {
        return dateCreated;
    }

    public boolean isRandom() {
        return isRandom;
    }

    public boolean isOnOnePage() {
        return onOnePage;
    }

    public boolean correctImmediately() {
        return correctImmediately;
    }

    public boolean isPractice() {
        return isPractice;
    }

    public int getID() {
        return ID;
    }

    public String getCategory() {
        return category;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
