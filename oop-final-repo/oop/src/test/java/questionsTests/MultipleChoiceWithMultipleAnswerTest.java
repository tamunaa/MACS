package questionsTests;

import objects.questions.MultipleChoiceWithMultipleAnswer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MultipleChoiceWithMultipleAnswerTest {

    private MultipleChoiceWithMultipleAnswer multipleChoiceWithMultipleAnswer;

    @BeforeEach
    public void setUp() {
        String questionText = "Select all the countries in Europe.";
        String[] choices = {"France", "Germany", "Italy", "Nigeria"};
        String[] correctAnswers = {"France", "Germany", "Italy"};
        multipleChoiceWithMultipleAnswer = new MultipleChoiceWithMultipleAnswer(questionText, choices, correctAnswers);
    }

    @Test
    public void testGetQuestion() {
        String expectedQuestion = "Select all the countries in Europe.";
        String actualQuestion = multipleChoiceWithMultipleAnswer.getQuestion();
        assertEquals(expectedQuestion, actualQuestion);
    }

    @Test
    public void testEvaluateAllCorrectAnswers() {
        String answer = "France,Germany,Italy";
        int expectedScore = 3;
        int actualScore = multipleChoiceWithMultipleAnswer.evaluate(answer);
        assertEquals(expectedScore, actualScore);
    }

    @Test
    public void testEvaluateSomeCorrectAnswers() {
        String answer = "Germany,Italy,Nigeria";
        int expectedScore = 1;
        int actualScore = multipleChoiceWithMultipleAnswer.evaluate(answer);
        assertEquals(expectedScore, actualScore);
    }

    @Test
    public void testEvaluateOnlyOneCorrectAnswer() {
        String answer = "Italy";
        int expectedScore = 1;
        int actualScore = multipleChoiceWithMultipleAnswer.evaluate(answer);
        assertEquals(expectedScore, actualScore);
    }

    @Test
    public void testEvaluateAllWrongAnswers() {
        String answer = "UK,USA,Canada";
        int expectedScore = 0;
        int actualScore = multipleChoiceWithMultipleAnswer.evaluate(answer);
        assertEquals(expectedScore, actualScore);
    }

    @Test
    public void testEvaluateCaseInsensitive() {
        String answer = "   fRance,  geRMany ,  iTAly ";
        int expectedScore = 3;
        int actualScore = multipleChoiceWithMultipleAnswer.evaluate(answer);
        assertEquals(expectedScore, actualScore);
    }

    @Test
    public void testSetAndGetTimer() {
        int timer = 30;
        multipleChoiceWithMultipleAnswer.setTimer(timer);
        int actualTimer = multipleChoiceWithMultipleAnswer.getTimer();
        assertEquals(timer, actualTimer);
    }

    @Test
    public void testGetNumFields() {
        int expectedNumFields = 4;
        int actualNumFields = multipleChoiceWithMultipleAnswer.getNumFields();
        assertEquals(expectedNumFields, actualNumFields);
    }

    @Test
    public void testGetQuestionType() {
        assertEquals("MultipleChoiceWithMultipleAnswer", multipleChoiceWithMultipleAnswer.getQuestionType());
    }

    @Test
    public void testIsOrdered() {
        assertEquals(false, multipleChoiceWithMultipleAnswer.isOrdered());
    }

    @Test
    public void testGetOptions() {
        String[] actualOptions = {"France", "Germany", "Italy", "Nigeria"};
        assertArrayEquals(actualOptions, multipleChoiceWithMultipleAnswer.getOptions());
    }

    @Test
    public void testGetAnswers() {
        String[] correctAnswers = {"France", "Germany", "Italy"};
        assertArrayEquals(correctAnswers, multipleChoiceWithMultipleAnswer.getCorrectAnswers());
    }


    @Test
    public void testSetAndGetQuestionId() {
        assertEquals(-1, multipleChoiceWithMultipleAnswer.getQuestionId());
        int questionId = 28;
        multipleChoiceWithMultipleAnswer.setQuestionId(questionId);
        assertEquals(questionId, multipleChoiceWithMultipleAnswer.getQuestionId());
    }
}
