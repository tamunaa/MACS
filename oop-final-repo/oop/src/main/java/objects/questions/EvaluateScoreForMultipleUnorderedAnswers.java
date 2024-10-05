package objects.questions;

import java.util.HashSet;

public class EvaluateScoreForMultipleUnorderedAnswers {
    // if isPenalty is true, for each wrong answers score will be decreased by 1
    public static int evaluate(HashSet<String> correctAnswers, String answer, boolean isPenalty) {
        int score = 0;
        HashSet<String> alreadyAnswered = new HashSet<>();
        String[] ans = answer.split(",");

        for (String str : ans) {
            String trimmedAnswer = str.trim().toLowerCase();
            if (isPenalty && !correctAnswers.contains(trimmedAnswer)) {
                score--;
            } else if (correctAnswers.contains(trimmedAnswer) && !alreadyAnswered.contains(trimmedAnswer)) {
                alreadyAnswered.add(trimmedAnswer);
                score++;
            }
        }
        return Math.max(score, 0);
    }
}
