import objects.Achievement;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AchievementTest {
    private Achievement achievement;

    @BeforeEach
    void setUp(){
        achievement = new Achievement("Best writer",
                "https://www.vectorstock.com/royalty-free-vector/best-writer-award-icon-simple-style-vector-26751761",
                "Wrote most popular quiz!");
    }

    @Test
    void getAchievementID() {
        assertEquals(-1, achievement.getAchievementID());
    }

    @Test
    void setAchievementID() {
        achievement.setAchievementID(1);
        assertEquals(1, achievement.getAchievementID());
    }

    @Test
    void getAchievementType() {
        assertEquals("Best writer", achievement.getAchievementType());
    }

    @Test
    void setAchievementType() {
        achievement.setAchievementType("Best quiztaker");
        assertEquals("Best quiztaker", achievement.getAchievementType());
    }

    @Test
    void getPictureUrl() {
        assertEquals("https://www.vectorstock.com/royalty-free-vector/best-writer-award-icon-simple-style-vector-26751761", achievement.getPictureUrl());
    }

    @Test
    void setPictureUrl() {
        achievement.setPictureUrl("https://www.impacttrophies.co.uk/quiz-trophies");
        assertEquals("https://www.impacttrophies.co.uk/quiz-trophies", achievement.getPictureUrl());
    }

    @Test
    void getAchievementDesc() {
        assertEquals("Wrote most popular quiz!", achievement.getAchievementDesc());
    }

    @Test
    void setAchievementDesc() {
        achievement.setAchievementDesc("Best score!");
        assertEquals("Best score!", achievement.getAchievementDesc());
    }
}