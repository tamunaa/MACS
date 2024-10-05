package objects;

public class Response {
    private int id;
    private int questionId;
    private int historyId;
    private int grade;
    private boolean isGraded;
    private String responseText;

    public Response(int id, int questionId, int historyId, int grade, boolean isGraded, String responseText) {
        this.id = id;
        this.questionId = questionId;
        this.historyId = historyId;
        this.grade = grade;
        this.isGraded = isGraded;
        this.responseText = responseText;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setQuestionId(int questionId) {
        this.questionId = questionId;
    }

    public void setHistoryId(int historyId) {
        this.historyId = historyId;
    }

    public void setGrade(int grade) {
        this.grade = grade;
    }

    public void setGraded(boolean graded) {
        isGraded = graded;
    }

    public void setResponseText(String responseText) {
        this.responseText = responseText;
    }

    public int getId() {
        return id;
    }

    public int getQuestionId() {
        return questionId;
    }

    public int getHistoryId() {
        return historyId;
    }

    public int getGrade() {
        return grade;
    }

    public boolean isGraded() {
        return isGraded;
    }

    public String getResponseText() {
        return responseText;
    }

    @Override
    public String toString() {
        return "Response{" +
                "id=" + id +
                ", questionId=" + questionId +
                ", historyId=" + historyId +
                ", grade=" + grade +
                ", isGraded=" + isGraded +
                ", responseText='" + responseText + '\'' +
                '}';
    }
}
