import objects.Quiz;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.*;

class QuizTest {
    private Quiz quiz;
    private Timestamp time;
    @BeforeEach
    void setUp() {
        quiz = new Quiz(3, "Quiz", "Test Quiz", 30, "math");
        time = quiz.getDateCreated();
    }

    @Test
    void setDateCreated() {
        Timestamp time = quiz.getDateCreated();
        quiz.setDateCreated(Timestamp.valueOf("2023-08-12 00:10:15.885"));
        assertNotEquals(time, quiz.getDateCreated());
        assertEquals(Timestamp.valueOf("2023-08-12 00:10:15.885"), quiz.getDateCreated());

    }

    @Test
    void setRandom() {
        assertEquals(false, quiz.isRandom());
        quiz.setRandom(true);
        assertEquals(true, quiz.isRandom());
    }

    @Test
    void setOnOnePage() {
        assertEquals(false, quiz.isOnOnePage());
        quiz.setOnOnePage(true);
        assertEquals(true, quiz.isOnOnePage());
    }

    @Test
    void setCorrectImmediately() {
        assertEquals(false, quiz.correctImmediately());
        quiz.setCorrectImmediately(true);
        assertEquals(true, quiz.correctImmediately());
    }

    @Test
    void setPractice() {
        assertEquals(false, quiz.isPractice());
        quiz.setPractice(true);
        assertEquals(true, quiz.isPractice());
    }

    @Test
    void getAuthor() {
        assertEquals(3, quiz.getAuthor());
    }

    @Test
    void getQuizName() {
        assertEquals("Quiz", quiz.getQuizName());
    }

    @Test
    void getDescription() {
        assertEquals("Test Quiz", quiz.getDescription());
    }

    @Test
    void getTimer() {
        assertEquals(30, quiz.getTimer());
    }

    @Test
    void getDateCreated() {
        assertEquals(time, quiz.getDateCreated());
    }

    @Test
    void isRandom() {
        assertEquals(false, quiz.isRandom());
    }

    @Test
    void isOnOnePage() {
        assertEquals(false, quiz.isOnOnePage());
    }

    @Test
    void correctImmediately() {
        assertEquals(false, quiz.correctImmediately());
    }

    @Test
    void isPractice() {
        assertEquals(false, quiz.isPractice());
    }

    @Test
    void getID() {
        assertEquals(-1, quiz.getID());
    }

    @Test
    void setID() {
        quiz.setID(30);
        assertEquals(30, quiz.getID());
    }

    @Test
    void getCategory() {
        assertEquals("math", quiz.getCategory());
    }

    @Test
    void setCategory() {
        quiz.setCategory("history");
        assertEquals("history", quiz.getCategory());
    }
}