package objects.questions;

import java.io.Serializable;

public class MultiAnswer implements Question, Serializable {
    private final String questionText;
    private final String answers[];
    private final int numFields;
    private final MultiAnswerType questionType;
    private final boolean isOrdered;
    private int timer = 0;
    private int questionId = -1;

    public 	MultiAnswer(String questionText, String answers[], boolean isOrdered) {
        this.questionText = questionText;
        this.numFields = answers.length;
        this.isOrdered = isOrdered;
        this.answers = answers.clone();
        this.questionType = isOrdered ? new OrderedMultiAnswer(answers) : new UnorderedMultiAnswer(answers);
    }

    @Override
    public String getQuestion() {
        return questionText;
    }

    @Override
    public int evaluate(String answer) {
        return questionType.evaluate(answer);
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
        return null;
    }

    @Override
    public String[] getCorrectAnswers() {
        return answers.clone();
    }

    @Override
    public boolean isOrdered() {
        return isOrdered;
    }

    @Override
    public String getQuestionType() {
        return "MultiAnswer";
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
