package objects.questions;

import java.io.Serializable;

public class QuestionResponse implements Question, Serializable {

    private final String questionText;
    private final String correctAnswer;
    private final String untrimmedCorrectAnswer;
    private int timer = 0;
    private int questionId = -1;

    public QuestionResponse(String questionText, String correctAnswer) {
        this.questionText = questionText.trim();
        this.untrimmedCorrectAnswer = correctAnswer;
        this.correctAnswer = correctAnswer.trim().toLowerCase();
    }
    @Override
    public String getQuestion() {
        return questionText;
    }

    @Override
    public int evaluate(String answer) {
        return correctAnswer.equals(answer.trim().toLowerCase()) ? 1 : 0;
    }

    @Override
    public void setTimer(int timer) {
        this.timer = timer;
    }

    @Override
    public int getTimer() {
        return timer;
    }

    @Override
    public int getNumFields() {
        return 1;
    }

    @Override
    public String[] getOptions() {
        return null;
    }

    @Override
    public String[] getCorrectAnswers() {
        return new String[] {untrimmedCorrectAnswer};
    }

    @Override
    public boolean isOrdered() {
        return false;
    }

    @Override
    public String getQuestionType() {
        return "QuestionResponse";
    }

    @Override
    public int getQuestionId() {
        return questionId;
    }

    @Override
    public void setQuestionId(int questionId) {
        this.questionId = questionId;
    }
}