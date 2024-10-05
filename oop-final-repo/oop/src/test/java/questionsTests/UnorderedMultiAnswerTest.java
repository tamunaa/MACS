package questionsTests;

import objects.questions.UnorderedMultiAnswer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UnorderedMultiAnswerTest {

    private UnorderedMultiAnswer unorderedMultiAnswer;

    @BeforeEach
    public void setUp() {
        String[] answers = {"Paris", "Berlin", "Rome"};
        unorderedMultiAnswer = new UnorderedMultiAnswer(answers);
    }

    @Test
    public void testEvaluateAllCorrectAnswers() {
        String answer = "Berlin,Paris,Rome";
        int expectedScore = 3;
        int actualScore = unorderedMultiAnswer.evaluate(answer);
        assertEquals(expectedScore, actualScore);
    }

    @Test
    public void testEvaluateSomeCorrectAnswers() {
        String answer = "Berlin,Madrid,Rome";
        int expectedScore = 2;
        int actualScore = unorderedMultiAnswer.evaluate(answer);
        assertEquals(expectedScore, actualScore);
    }

    @Test
    public void testEvaluateSameCorrectAnswers() {
        String answer = "Paris,Paris,Paris";
        int expectedScore = 1;
        int actualScore = unorderedMultiAnswer.evaluate(answer);
        assertEquals(expectedScore, actualScore);
    }

    @Test
    public void testEvaluateAllWrongAnswers() {
        String answer = "Madrid,London,Barcelona";
        int expectedScore = 0;
        int actualScore = unorderedMultiAnswer.evaluate(answer);
        assertEquals(expectedScore, actualScore);
    }

    @Test
    public void testEvaluateCaseInsensitive() {
        String answer = "parIS ,Rome,    beRlin";
        int expectedScore = 3;
        int actualScore = unorderedMultiAnswer.evaluate(answer);
        assertEquals(expectedScore, actualScore);
    }
}
