package questionsTests;

import objects.questions.Matching;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;

public class MatchingTest {

    private Matching matching;

    @BeforeEach
    public void setUp() {
        String questionText = "Match the capitals with their respective countries.";
        String[] questions = {"France", "Germany", "Italy"};
        String[] answers = {"Paris", "Berlin", "Rome"};
        matching = new Matching(questionText, questions, answers);
    }

    @Test
    public void testGetQuestion() {
        String expectedQuestion = "Match the capitals with their respective countries.";
        String actualQuestion = matching.getQuestion();
        assertEquals(expectedQuestion, actualQuestion);
    }

    @Test
    public void testEvaluateAllCorrectAnswers() {
        String answer = "France:Paris,Germany:Berlin,Italy:Rome";
        int expectedScore = 3;
        int actualScore = matching.evaluate(answer);
        assertEquals(expectedScore, actualScore);
    }

    @Test
    public void testEvaluateSomeCorrectAnswers() {
        String answer = "France:Berlin,Germany:Berlin,Italy:Rome";
        int expectedScore = 2;
        int actualScore = matching.evaluate(answer);
        assertEquals(expectedScore, actualScore);
    }

    @Test
    public void testEvaluateAllWrongAnswers() {
        String answer = "France:Berlin,Germany:Rome,Italy:Paris";
        int expectedScore = 0;
        int actualScore = matching.evaluate(answer);
        assertEquals(expectedScore, actualScore);
    }

    @Test
    public void testEvaluateCaseInsensitive() {
        String answer = "france  :   paRis  ,  GERMANY: BERLIN,Italy:roMe";
        int expectedScore = 3;
        int actualScore = matching.evaluate(answer);
        assertEquals(expectedScore, actualScore);
    }

    @Test
    public void testSetAndGetTimer() {
        int timer = 30;
        matching.setTimer(timer);
        int actualTimer = matching.getTimer();
        assertEquals(timer, actualTimer);
    }

    @Test
    public void testGetNumFields() {
        int expectedNumFields = 3;
        int actualNumFields = matching.getNumFields();
        assertEquals(expectedNumFields, actualNumFields);
    }

    @Test
    public void testGetQuestionType() {
        assertEquals("Matching", matching.getQuestionType());
    }

    @Test
    public void testIsOrdered() {
        assertEquals(true, matching.isOrdered());
    }

    @Test
    public void testGetOptions() {
        assertArrayEquals(new String[] {"France", "Germany", "Italy"}, matching.getOptions());
    }

    @Test
    public void testGetAnswers() {
        String actualAnswer = Arrays.toString(matching.getCorrectAnswers());
        assertTrue(actualAnswer.contains("Paris"));
        assertTrue(actualAnswer.contains("Berlin"));
        assertTrue(actualAnswer.contains("Rome"));
    }

    @Test
    public void testSetAndGetQuestionId() {
        assertEquals(-1, matching.getQuestionId());
        int questionId = 28;
        matching.setQuestionId(questionId);
        assertEquals(questionId, matching.getQuestionId());
    }
}
