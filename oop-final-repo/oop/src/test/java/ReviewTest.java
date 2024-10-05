import objects.Review;
import objects.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.sql.Timestamp;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
public class ReviewTest {
    private Review review;

    @BeforeEach
    void setUp() {
        review = new Review(1,2,"this quiz was very easy in this category");
    }
    @Test
    void getIDTest() {
        assertEquals(-1,review.getReview_id());
    }
    @Test
    void setIDTest() {
        review.setId(1);
        assertNotEquals(-1,review.getReview_id());
        assertEquals(1,review.getReview_id());
    }
    @Test
    void getUserIDTest() {
        assertEquals(1,review.getUser_id());
    }

    @Test
    void getQuizIDTest() {
        assertEquals(2,review.getQuiz_id());
    }

    @Test
    void getContentTest() {
        assertEquals("this quiz was very easy in this category",review.getContent());
    }
    @Test
    void setContentTest() {
        review.setContent("i halved my timer on this quiz");
        assertNotEquals("this quiz was very easy in this category",review.getContent());
        assertEquals("i halved my timer on this quiz",review.getContent());
    }
    @Test
    void setAndGetTimeTest(){
        Timestamp newTime = Timestamp.valueOf("2023-08-12 12:42:18.700");
        assertNotEquals(newTime,review.getTime());
        review.setTime(newTime);
        assertEquals(newTime, review.getTime());
    }}
