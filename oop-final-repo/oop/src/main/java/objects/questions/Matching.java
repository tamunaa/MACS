package objects.questions;

import java.io.Serializable;
import java.util.*;

public class Matching implements Question, Serializable {
    private final String questionText;
    private final HashMap<String, String> correctAnswer;
    private final int numFields;
    private final String[] source;
    private final String[] target;
    private int timer = 0;
    private int questionId = -1;

    public Matching(String questionText, String[] source, String[] target) {
        this.questionText = questionText;
        this.numFields = source.length;
        this.correctAnswer = new HashMap<>();
        int n = Math.min(source.length, target.length);
        this.source = new String[n];
        this.target = new String[n];
        for (int i = 0; i < n; i++) {
            correctAnswer.put(source[i].trim().toLowerCase(), target[i].trim().toLowerCase());
            this.source[i] = source[i];
            this.target[i] = target[i];
        }
    }
    @Override
    public String getQuestion() {
        return questionText;
    }

    @Override
    public int evaluate(String answer) {
        int score = 0;
        HashMap<String, String> ans = Parser.GetQuestionsAndAnswersOfMatching(answer);
        for (Map.Entry<String, String> entry : ans.entrySet()) {
            if (correctAnswer.get(entry.getKey().trim().toLowerCase()).equals(entry.getValue().trim().toLowerCase())) {
                score++;
            }
        }
        return score;
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
        return source.clone();
    }

    /**
     * NEEDS TO BE SHUFFLED AFTER
     */
    @Override
    public String[] getCorrectAnswers() {
        return target.clone();
    }

    @Override
    public boolean isOrdered() {
        return true;
    }

    @Override
    public String getQuestionType() {
        return "Matching";
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
