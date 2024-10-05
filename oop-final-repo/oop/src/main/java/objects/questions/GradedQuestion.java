package objects.questions;

import java.io.Serializable;

public class GradedQuestion implements Question, Serializable {
    private final String questionText;
    private int timer = 0;
    private int questionId = -1;
    private String answer;

    public GradedQuestion(String questionText) {
        this.questionText = questionText;
    }

    @Override
    public String getQuestion() {
        return this.questionText;
    }

    @Override
    public int evaluate(String answer) {
        return 0;
    }

    @Override
    public void setTimer(int timer) {
        this.timer = timer;
    }

    @Override
    public int getTimer() {
        return this.timer;
    }

    @Override
    public int getNumFields() {
        return 0;
    }

    @Override
    public String[] getOptions() {
        return null;
    }

    @Override
    public String[] getCorrectAnswers() {
        return null;
    }

    @Override
    public boolean isOrdered() {
        return false;
    }

    @Override
    public String getQuestionType() {
        return "Graded";
    }

    @Override
    public int getQuestionId() {
        return this.questionId;
    }

    @Override
    public void setQuestionId(int questionId) {
        this.questionId = questionId;
    }
}
