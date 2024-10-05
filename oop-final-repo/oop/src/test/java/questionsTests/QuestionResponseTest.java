package questionsTests;

import objects.questions.QuestionResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class QuestionResponseTest {

    private QuestionResponse questionResponse;

    @BeforeEach
    public void setUp() {
        String questionText = "What is the capital of France?";
        String correctAnswer = "Paris";
        questionResponse = new QuestionResponse(questionText, correctAnswer);
    }

    @Test
    public void testGetQuestion() {
        String expectedQuestionText = "What is the capital of France?";
        String actualQuestionText = questionResponse.getQuestion();
        assertEquals(expectedQuestionText, actualQuestionText);
    }

    @Test
    public void testEvaluateCorrectAnswer() {
        String correctAnswer = "Paris";
        int expectedScore = 1;
        int actualScore = questionResponse.evaluate(correctAnswer);
        assertEquals(expectedScore, actualScore);
    }

    @Test
    public void testEvaluateIncorrectAnswer() {
        String incorrectAnswer = "London";
        int expectedScore = 0;
        int actualScore = questionResponse.evaluate(incorrectAnswer);
        assertEquals(expectedScore, actualScore);
    }

    @Test
    public void testEvaluateCaseInsensitive() {
        String correctAnswer = "  pArIs  ";
        int expectedScore = 1;
        int actualScore = questionResponse.evaluate(correctAnswer);
        assertEquals(expectedScore, actualScore);
    }

    @Test
    public void testSetAndGetTimer() {
        int timer = 30;
        questionResponse.setTimer(timer);
        int actualTimer = questionResponse.getTimer();
        assertEquals(timer, actualTimer);
    }

    @Test
    public void testGetNumFields() {
        int expectedNumFields = 1;
        int actualNumFields = questionResponse.getNumFields();
        assertEquals(expectedNumFields, actualNumFields);
    }

    @Test
    public void testGetQuestionType() {
        assertEquals("QuestionResponse", questionResponse.getQuestionType());
    }
    @Test
    public void testIsOrdered() {
        assertEquals(false, questionResponse.isOrdered());
    }

    @Test
    public void testGetOptions() {
        assertArrayEquals(null, questionResponse.getOptions());
    }

    @Test
    public void testGetAnswers() {
        assertArrayEquals(new String[] {"Paris"}, questionResponse.getCorrectAnswers());
    }


    @Test
    public void testSetAndGetQuestionId() {
        assertEquals(-1, questionResponse.getQuestionId());
        int questionId = 28;
        questionResponse.setQuestionId(questionId);
        assertEquals(questionId, questionResponse.getQuestionId());
    }
}
