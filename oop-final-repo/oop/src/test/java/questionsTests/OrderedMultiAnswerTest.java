package questionsTests;

import objects.questions.OrderedMultiAnswer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class OrderedMultiAnswerTest {

    private OrderedMultiAnswer orderedMultiAnswer;

    @BeforeEach
    public void setUp() {
        String[] answers = {"Paris", "Berlin", "Rome"};
        orderedMultiAnswer = new OrderedMultiAnswer(answers);
    }

    @Test
    public void testEvaluateAllCorrectAnswers() {
        String answer = "Paris,Berlin,Rome";
        int expectedScore = 3;
        int actualScore = orderedMultiAnswer.evaluate(answer);
        assertEquals(expectedScore, actualScore);
    }

    @Test
    public void testEvaluateSomeCorrectAnswers() {
        String answer = "Paris,Madrid,Rome";
        int expectedScore = 2;
        int actualScore = orderedMultiAnswer.evaluate(answer);
        assertEquals(expectedScore, actualScore);
    }

    @Test
    public void testEvaluateAllWrongAnswers() {
        String answer = "Madrid,London,Barcelona";
        int expectedScore = 0;
        int actualScore = orderedMultiAnswer.evaluate(answer);
        assertEquals(expectedScore, actualScore);
    }

    @Test
    public void testEvaluateIncorrectOrder1() {
        String answer = "Paris,Rome,Berlin";
        int expectedScore = 1;
        int actualScore = orderedMultiAnswer.evaluate(answer);
        assertEquals(expectedScore, actualScore);
    }

    @Test
    public void testEvaluateSameCorrectAnswers() {
        String answer = "Paris,Paris,Paris";
        int expectedScore = 1;
        int actualScore = orderedMultiAnswer.evaluate(answer);
        assertEquals(expectedScore, actualScore);
    }

    @Test
    public void testEvaluateIncorrectOrder2() {
        String answer = "Rome,Paris,Berlin";
        int expectedScore = 0;
        int actualScore = orderedMultiAnswer.evaluate(answer);
        assertEquals(expectedScore, actualScore);
    }

    @Test
    public void testEvaluateCaseInsensitive() {
        String answer = " PARIS , berlIN  , rome";
        int expectedScore = 3;
        int actualScore = orderedMultiAnswer.evaluate(answer);
        assertEquals(expectedScore, actualScore);
    }
}
