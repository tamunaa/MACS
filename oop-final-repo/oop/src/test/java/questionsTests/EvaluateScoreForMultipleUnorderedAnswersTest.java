package questionsTests;

import objects.questions.EvaluateScoreForMultipleUnorderedAnswers;
import org.junit.jupiter.api.Test;
import java.util.HashSet;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class EvaluateScoreForMultipleUnorderedAnswersTest {

    @Test
    public void testEvaluateAllCorrectAnswers() {
        HashSet<String> correctAnswers = new HashSet<>();
        correctAnswers.add("apple");
        correctAnswers.add("orange");
        correctAnswers.add("banana");

        String answer = "orange,apple,banana";
        int expectedScore = 3;

        int actualScore = EvaluateScoreForMultipleUnorderedAnswers.evaluate(correctAnswers, answer, false);
        assertEquals(expectedScore, actualScore);
    }

    @Test
    public void testEvaluateSomeCorrectAnswers() {
        HashSet<String> correctAnswers = new HashSet<>();
        correctAnswers.add("apple");
        correctAnswers.add("orange");
        correctAnswers.add("banana");

        String answer = "apple,banana";
        int expectedScore = 2;

        int actualScore = EvaluateScoreForMultipleUnorderedAnswers.evaluate(correctAnswers, answer, false);
        assertEquals(expectedScore, actualScore);
    }

    @Test
    public void testEvaluateNoCorrectAnswers() {
        HashSet<String> correctAnswers = new HashSet<>();
        correctAnswers.add("apple");
        correctAnswers.add("orange");
        correctAnswers.add("banana");

        String answer = "grape,pear";
        int expectedScore = 0;

        int actualScore = EvaluateScoreForMultipleUnorderedAnswers.evaluate(correctAnswers, answer, false);
        assertEquals(expectedScore, actualScore);
    }

    @Test
    public void testEvaluateAllWrongAnswers() {
        HashSet<String> correctAnswers = new HashSet<>();
        correctAnswers.add("apple");
        correctAnswers.add("orange");
        correctAnswers.add("banana");

        String answer = "grape,pineapple,kiwi";
        int expectedScore = 0;

        int actualScore = EvaluateScoreForMultipleUnorderedAnswers.evaluate(correctAnswers, answer, false);
        assertEquals(expectedScore, actualScore);
    }

    @Test
    public void testEvaluateMixedCorrectAndWrongAnswers() {
        HashSet<String> correctAnswers = new HashSet<>();
        correctAnswers.add("apple");
        correctAnswers.add("orange");
        correctAnswers.add("banana");

        String answer = "apple,pineapple,orange";
        int expectedScore = 2;

        int actualScore = EvaluateScoreForMultipleUnorderedAnswers.evaluate(correctAnswers, answer, false);
        assertEquals(expectedScore, actualScore);
    }

    @Test
    public void testEvaluateWithEmptyAnswer() {
        HashSet<String> correctAnswers = new HashSet<>();
        correctAnswers.add("apple");
        correctAnswers.add("orange");
        correctAnswers.add("banana");

        String answer = "";
        int expectedScore = 0;

        int actualScore = EvaluateScoreForMultipleUnorderedAnswers.evaluate(correctAnswers, answer, false);
        assertEquals(expectedScore, actualScore);
    }
}
