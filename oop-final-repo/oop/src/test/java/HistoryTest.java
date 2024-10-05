import objects.History;
import org.junit.Before;
import org.junit.Test;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.Instant;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;


public class HistoryTest {
    private History history;

    @Before
    public void setUp() {
        int quizId = 1;
        int userId = 123;
        int score = 85;
        double timeRelapsed = 45.5;
        Instant dateTaken = Instant.now();

        history = new History(quizId, userId, score, timeRelapsed, Timestamp.from(dateTaken));
    }

    @Test
    public void testGetQuizId() {
        assertEquals(1, history.getQuizId());
    }

    @Test
    public void testGetUserId() {
        assertEquals(123, history.getUserId());
    }

    @Test
    public void testGetScore() {
        assertEquals(85, history.getScore());
    }
    @Test
    public void testSetId() {
        history.setId(5);
        assertEquals(5, history.getId());
    }

    @Test
    public void testGetTimeRelapsed() {
        // Check if the returned timeRelapsed matches the initial value
        assertEquals(45.5, history.getTimeRelapsed(), 0.01); // Using delta for double comparison
    }

    @Test
    public void testSetDateTaken() {
        Instant newDateTaken = Instant.now().plusSeconds(1); // Adding 1 second
        history.setDateTaken(Timestamp.from(newDateTaken));
        assertEquals(newDateTaken, history.getDateTaken().toInstant());
    }

    @Test
    public void testGetDateTaken() {
        assertEquals(history.getDateTaken(), history.getDateTaken());

        assertNotNull(history.getDateTaken());
    }
}
