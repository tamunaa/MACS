package objects.questions;

import java.util.HashSet;
import java.io.Serializable;
public class OrderedMultiAnswer implements MultiAnswerType, Serializable {
    private final String[] correctAnswers;
    private final HashSet<String> correctAnswersSet;
    public OrderedMultiAnswer(String[] answers) {
        this.correctAnswers = new String[answers.length];
        this.correctAnswersSet = new HashSet<>();
        for (int i = 0; i < answers.length; i++) {
            String curr = answers[i].trim().toLowerCase();
            this.correctAnswers[i] = curr;
            this.correctAnswersSet.add(curr);
        }
    }

    @Override
    public int evaluate(String answer) {
        String[] answers = Parser.StringToStringArray(answer);
        int score = 0;

        for (int i = 0; i < answers.length; i++) {
            if (correctAnswers[i].equals(answers[i].trim().toLowerCase())) {
                score++;
            }
        }

        return Math.max(score, 0);
    }
}
