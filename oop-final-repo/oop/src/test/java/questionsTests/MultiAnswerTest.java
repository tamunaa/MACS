package questionsTests;

import objects.questions.MultiAnswer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MultiAnswerTest {

    private MultiAnswer orderedMultiAnswer;
    private MultiAnswer unorderedMultiAnswer;

    @BeforeEach
    public void setUp() {
        String questionText = "Whats the capitals of France, Germany and Italy?";
        String[] answers = {"Paris", "Berlin", "Rome"};

        orderedMultiAnswer = new MultiAnswer(questionText, answers, true);
        unorderedMultiAnswer = new MultiAnswer(questionText, answers, false);
    }

    @Test
    public void testGetQuestion() {
        String expectedQuestion = "Whats the capitals of France, Germany and Italy?";
        String actualQuestionOrdered = orderedMultiAnswer.getQuestion();
        String actualQuestionUnordered = unorderedMultiAnswer.getQuestion();

        assertEquals(expectedQuestion, actualQuestionOrdered);
        assertEquals(expectedQuestion, actualQuestionUnordered);
    }

    @Test
    public void testEvaluateOrderedMultiAnswerCorrect() {
        String answer = "Paris,Berlin,Rome";
        int expectedScore = 3;
        int actualScore = orderedMultiAnswer.evaluate(answer);
        assertEquals(expectedScore, actualScore);
    }

    @Test
    public void testEvaluateOrderedMultiAnswerIncorrect() {
        String answer = "Berlin,Paris,Rome";
        int expectedScore = 1;
        int actualScore = orderedMultiAnswer.evaluate(answer);
        assertEquals(expectedScore, actualScore);
    }

    @Test
    public void testEvaluateUnorderedMultiAnswerCorrect() {
        String answer = "Paris,Rome,Berlin";
        int expectedScore = 3;
        int actualScore = unorderedMultiAnswer.evaluate(answer);
        assertEquals(expectedScore, actualScore);
    }

    @Test
    public void testEvaluateUnorderedMultiAnswerSomeCorrect() {
        String answer = "Berlin,Paris,Tbilisi";
        int expectedScore = 2;
        int actualScore = unorderedMultiAnswer.evaluate(answer);
        assertEquals(expectedScore, actualScore);
    }

    @Test
    public void testEvaluateUnorderedMultiAnswerAllWrongAnswers() {
        String answer = "Tbilisi,Batumi,Qutaisi";
        int expectedScore = 0;
        int actualScore = unorderedMultiAnswer.evaluate(answer);
        assertEquals(expectedScore, actualScore);
    }

    @Test
    public void testEvaluateOrderedMultiAnswerAllWrongAnswers() {
        String answer = "Tbilisi,Batumi,Qutaisi";
        int expectedScore = 0;
        int actualScore = orderedMultiAnswer.evaluate(answer);
        assertEquals(expectedScore, actualScore);
    }
    @Test
    public void testEvaluateUnorderedMultiAnswerSameCorrectAnswers() {
        String answer = "Berlin,Berlin,Berlin";
        int expectedScore = 1;
        int actualScore = unorderedMultiAnswer.evaluate(answer);
        assertEquals(expectedScore, actualScore);
    }

    @Test
    public void testEvaluateOrderedMultiAnswerIncorrectOrder() {
        String answer = "Rome,Paris,Berlin";
        int expectedScore = 0;
        int actualScore = orderedMultiAnswer.evaluate(answer);
        assertEquals(expectedScore, actualScore);
    }

    @Test
    public void testEvaluateUnorderedMultiAnswerIncorrectOrder() {
        String answer = "Rome,Paris,Berlin";
        int expectedScore = 3;
        int actualScore = unorderedMultiAnswer.evaluate(answer);
        assertEquals(expectedScore, actualScore);
    }

    @Test
    public void testEvaluateOrderedMultiAnswerSameCorrectAnswers() {
        String answer = "Berlin,Berlin,Berlin";
        int expectedScore = 1;
        int actualScore = orderedMultiAnswer.evaluate(answer);
        assertEquals(expectedScore, actualScore);
    }

    @Test
    public void testSetAndGetTimer() {
        int timer = 20;
        orderedMultiAnswer.setTimer(timer);
        unorderedMultiAnswer.setTimer(timer);

        int actualTimerOrdered = orderedMultiAnswer.getTimer();
        int actualTimerUnordered = unorderedMultiAnswer.getTimer();

        assertEquals(timer, actualTimerOrdered);
        assertEquals(timer, actualTimerUnordered);
    }

    @Test
    public void testGetNumFields() {
        int expectedNumFields = 3;
        int actualNumFieldsOrdered = orderedMultiAnswer.getNumFields();
        int actualNumFieldsUnordered = unorderedMultiAnswer.getNumFields();

        assertEquals(expectedNumFields, actualNumFieldsOrdered);
        assertEquals(expectedNumFields, actualNumFieldsUnordered);
    }

    @Test
    public void testGetQuestionType() {
        assertEquals("MultiAnswer", orderedMultiAnswer.getQuestionType());
        assertEquals("MultiAnswer", unorderedMultiAnswer.getQuestionType());
    }

    @Test
    public void testIsOrdered() {
        assertEquals(true, orderedMultiAnswer.isOrdered());
        assertEquals(false, unorderedMultiAnswer.isOrdered());
    }

    @Test
    public void testGetOptions() {
        assertArrayEquals(null, orderedMultiAnswer.getOptions());
        assertArrayEquals(null, unorderedMultiAnswer.getOptions());
    }

    @Test
    public void testGetAnswersUnordered() {
        String actualAnswer = Arrays.toString(unorderedMultiAnswer.getCorrectAnswers());
        assertTrue(actualAnswer.contains("Paris"));
        assertTrue(actualAnswer.contains("Berlin"));
        assertTrue(actualAnswer.contains("Rome"));
    }

    @Test
    public void testGetAnswersOrdered() {
        assertArrayEquals(new String[] {"Paris", "Berlin", "Rome"}, orderedMultiAnswer.getCorrectAnswers());
    }

    @Test
    public void testSetAndGetQuestionId1() {
        assertEquals(-1, unorderedMultiAnswer.getQuestionId());
        int questionId = 28;
        unorderedMultiAnswer.setQuestionId(questionId);
        assertEquals(questionId, unorderedMultiAnswer.getQuestionId());
    }


    @Test
    public void testSetAndGetQuestionId2() {
        assertEquals(-1, orderedMultiAnswer.getQuestionId());
        int questionId = 28;
        orderedMultiAnswer.setQuestionId(questionId);
        assertEquals(questionId, orderedMultiAnswer.getQuestionId());
    }
}
