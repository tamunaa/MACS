package objects.questions;

import java.io.Serializable;

public class MultipleChoice implements Question, Serializable {
    private final String questionText;
    private final String[] choices;
    private final String correctAnswer;
    private final String untrimmedCorrectAnswer;
    private final int numFields;
    private int timer = 0;
    private int questionId = -1;

    public MultipleChoice(String questionText, String[] choices, String answer) {
        this.questionText = questionText.trim();
        this.untrimmedCorrectAnswer = answer;
        this.correctAnswer = answer.trim().toLowerCase();
        this.choices = choices.clone();
        this.numFields = choices.length;
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
        return numFields;
    }

    @Override
    public String[] getOptions() {
        return choices.clone();
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
        return "MultipleChoice";
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
