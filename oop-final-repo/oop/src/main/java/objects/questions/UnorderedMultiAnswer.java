package objects.questions;

import java.io.Serializable;
import java.util.HashSet;

public class UnorderedMultiAnswer implements MultiAnswerType, Serializable {
    private final HashSet<String> correctAnswers;
    public UnorderedMultiAnswer(String[] answers) {
        this.correctAnswers = new HashSet<>();
        for (String answer : answers) {
            this.correctAnswers.add(answer.trim().toLowerCase());
        }
    }
    @Override
    public int evaluate(String answer) {
        return EvaluateScoreForMultipleUnorderedAnswers.evaluate(correctAnswers, answer, false);
    }
}
