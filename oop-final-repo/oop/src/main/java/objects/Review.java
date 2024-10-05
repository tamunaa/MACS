package objects;
import java.sql.Timestamp;

public class Review {
    private int review_id = -1;
    private int user_id;
    private int quiz_id;
    private String content;
    private Timestamp time;

    public Review(int user_id, int quiz_id, String content, Timestamp time) {
        this.user_id = user_id;
        this.quiz_id = quiz_id;
        this.content = content;
        this.time = time;
    }

    public Review(int user_id, int quiz_id, String content) {
        this(user_id,quiz_id,content,new Timestamp(new java.util.Date().getTime()));
    }
    public int getReview_id(){
        return review_id;
    }
    public int getUser_id() {
        return user_id;
    }

    public int getQuiz_id() {
        return quiz_id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String newContent){
        this.content = newContent;
    }

    public Timestamp getTime() {
        return time;
    }

    public void setTime(Timestamp newTime){
        this.time = newTime;
    }

    public void setId(int newId){
        this.review_id = newId;
    }
}
