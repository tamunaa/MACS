package objects.questions;

public interface Question {
    String getQuestion();
    int evaluate(String answer);
    void setTimer(int timer);
    int getTimer();
    int getNumFields();
    String[] getOptions();
    String[] getCorrectAnswers();
    boolean isOrdered();
    String getQuestionType();
    int getQuestionId();
    void setQuestionId(int questionId);
}