package questionsTests;

import objects.questions.FillInTheBlank;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class FillInTheBlankTest {

    private FillInTheBlank fillInTheBlank;

    @BeforeEach
    public void setUp() {
        String questionText = "The capital of Georgia is _.";
        String correctAnswer = "Tbilisi";
        fillInTheBlank = new FillInTheBlank(questionText, correctAnswer);
    }

    @Test
    public void testGetQuestion() {
        String expectedQuestionText = "The capital of Georgia is _.";
        String actualQuestionText = fillInTheBlank.getQuestion();
        assertEquals(expectedQuestionText, actualQuestionText);
    }

    @Test
    public void testEvaluateCorrectAnswer() {
        String correctAnswer = "Tbilisi";
        int expectedScore = 1;
        int actualScore = fillInTheBlank.evaluate(correctAnswer);
        assertEquals(expectedScore, actualScore);
    }

    @Test
    public void testEvaluateIncorrectAnswer1() {
        String incorrectAnswer = "Rustavi";
        int expectedScore = 0;
        int actualScore = fillInTheBlank.evaluate(incorrectAnswer);
        assertEquals(expectedScore, actualScore);
    }

    @Test
    public void testEvaluateIncorrectAnswer2() {
        String incorrectAnswer = "Tbilisi Rustavi Tbilisi";
        int expectedScore = 0;
        int actualScore = fillInTheBlank.evaluate(incorrectAnswer);
        assertEquals(expectedScore, actualScore);
    }
    @Test
    public void testEvaluateCaseInsensitive() {
        String correctAnswer = "  TbiLisI  ";
        int expectedScore = 1;
        int actualScore = fillInTheBlank.evaluate(correctAnswer);
        assertEquals(expectedScore, actualScore);
    }

    @Test
    public void testSetAndGetTimer() {
        int timer = 20;
        fillInTheBlank.setTimer(timer);
        int actualTimer = fillInTheBlank.getTimer();
        assertEquals(timer, actualTimer);
    }

    @Test
    public void testSetAndGetQuestionId() {
        assertEquals(-1, fillInTheBlank.getQuestionId());
        int questionId = 28;
        fillInTheBlank.setQuestionId(questionId);
        assertEquals(questionId, fillInTheBlank.getQuestionId());
    }

    @Test
    public void testGetNumFields() {
        int expectedNumFields = 1;
        int actualNumFields = fillInTheBlank.getNumFields();
        assertEquals(expectedNumFields, actualNumFields);
    }

    @Test
    public void testGetQuestionType() {
        assertEquals("FillInTheBlank", fillInTheBlank.getQuestionType());
    }

    @Test
    public void testIsOrdered() {
        assertEquals(false, fillInTheBlank.isOrdered());
    }

    @Test
    public void testGetOptions() {
        assertArrayEquals(null, fillInTheBlank.getOptions());
    }

    @Test
    public void testGetAnswers() {
        assertArrayEquals(new String[] {"Tbilisi"}, fillInTheBlank.getCorrectAnswers());
    }
}
