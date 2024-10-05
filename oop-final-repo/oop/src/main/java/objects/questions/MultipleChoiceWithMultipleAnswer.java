package objects.questions;

import java.io.Serializable;
import java.util.HashSet;

public class MultipleChoiceWithMultipleAnswer implements Question, Serializable {
    private final String questionText;
    private final String[] choices;
    private final String[] answers;
    private final HashSet correctAnswers;
    private int timer = 0;
    private final int numAnswers;
    private int questionId = -1;

    public MultipleChoiceWithMultipleAnswer(String questionText, String[] choices, String[] answers) {
        this.questionText = questionText.trim();
        this.choices = choices.clone();
        this.answers = answers.clone();
        this.numAnswers = choices.length;
        this.correctAnswers = new HashSet();
        for (int i = 0; i < answers.length; i++) {
            this.correctAnswers.add(answers[i].trim().toLowerCase());
        }
    }

    @Override
    public String getQuestion() {
        return questionText;
    }

    @Override
    public int evaluate(String answer) {
        return EvaluateScoreForMultipleUnorderedAnswers.evaluate(correctAnswers, answer, true);
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
        return numAnswers;
    }

    @Override
    public String[] getOptions() {
        return choices.clone();
    }

    @Override
    public String[] getCorrectAnswers() {
        return answers.clone();
    }

    @Override
    public boolean isOrdered() {
        return false;
    }

    @Override
    public String getQuestionType() {
        return "MultipleChoiceWithMultipleAnswer";
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
