import objects.Challenge;
import objects.Quiz;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.*;

class ChallengeTest {
    private Challenge challenge;
    private Timestamp time;

    @BeforeEach
    void setUp() {
        challenge = new Challenge(1, 2, 5);
        time = challenge.getDateSent();
    }

    @Test
    void getChallengeId() {
        assertEquals(-1, challenge.getChallengeId());
    }

    @Test
    void setChallengeId() {
        assertEquals(-1, challenge.getChallengeId());
        challenge.setChallengeId(15);
        assertEquals(15, challenge.getChallengeId());
    }

    @Test
    void getChallengerID() {
        assertEquals(1, challenge.getChallengerID());
    }

    @Test
    void getChallengedID() {
        assertEquals(2, challenge.getChallengedID());
    }

    @Test
    void getQuizID() {
        assertEquals(5, challenge.getQuizID());
    }

    @Test
    void getDateSent() {
        assertEquals(time, challenge.getDateSent());
    }
}