package objects;

public class QuestionResponsePair {
    private String questionText;
    private Response response;

    public QuestionResponsePair(String questionText, Response response) {
        this.questionText = questionText;
        this.response = response;
    }

    public String getQuestionText() {
        return questionText;
    }

    public Response getResponse() {
        return response;
    }
}
