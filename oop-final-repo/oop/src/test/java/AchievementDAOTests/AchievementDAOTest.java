package AchievementDAOTests;

import dataBase.AchievementDAO;
import dataBase.ConnectionPool;
import dataBase.DbAchievementDAO;
import objects.Achievement;
import org.apache.commons.dbcp2.BasicDataSource;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class AchievementDAOTest {
    private AchievementDAO achievementDAO;
    private ConnectionPool pool = new ConnectionPool(5,"test_achievement","123456789");
    private Connection conn;

    @BeforeEach
    void setUp() {
        conn = pool.getConnection();
        achievementDAO = new DbAchievementDAO(pool);
    }
    @AfterEach
    public void tearDown() {
        pool.releaseConnection(conn);
    }

    @Test
    void getUserAchievements() {
        List<Achievement> achievements = achievementDAO.getUserAchievements(1);
        assertEquals(2, achievements.size());

        assertEquals(5, achievements.get(1).getAchievementID());
        assertEquals("Best quiztaker", achievements.get(1).getAchievementType());
        assertEquals("https://www.impacttrophies.co.uk/quiz-trophies", achievements.get(1).getPictureUrl());
        assertEquals("Best score!", achievements.get(1).getAchievementDesc());

        assertEquals(1, achievements.get(0).getAchievementID());
        assertEquals("Best writer", achievements.get(0).getAchievementType());
        assertEquals("https://www.vectorstock.com/royalty-free-vector/best-writer-award-icon-simple-style-vector-26751761", achievements.get(0).getPictureUrl());
        assertEquals("Wrote most popular quiz!", achievements.get(0).getAchievementDesc());
    }

    @Test
    @Order(3)
    void addUserAchievement() {
//        assertTrue(achievementDAO.addUserAchievement(1, 1));
//        assertTrue(achievementDAO.addUserAchievement(2, 4));
//        assertTrue(achievementDAO.addUserAchievement(1, 5));
    }

    @Test
    void getAchievementByID() {
        Achievement achievement = achievementDAO.getAchievementByID(5);
        assertEquals(5, achievement.getAchievementID());
        assertEquals("Best quiztaker", achievement.getAchievementType());
        assertEquals("https://www.impacttrophies.co.uk/quiz-trophies", achievement.getPictureUrl());
        assertEquals("Best score!", achievement.getAchievementDesc());
    }

    @Test
    @Order(1)
    void addAchievement() {
        Achievement achievement = new Achievement("Best writer",
                "https://www.vectorstock.com/royalty-free-vector/best-writer-award-icon-simple-style-vector-26751761",
                "Wrote most popular quiz!");
        assertEquals(achievementDAO.addAchievement(achievement), achievement.getAchievementID());

        achievement.setAchievementType("Best quiztaker");
        achievement.setPictureUrl("https://www.impacttrophies.co.uk/quiz-trophies");
        achievement.setAchievementDesc("Best score!");
        assertEquals(achievementDAO.addAchievement(achievement), achievement.getAchievementID());
    }

    @Test
    @Order(2)
    void removeAchievement() {
        assertTrue(achievementDAO.removeAchievement(3));
    }
}
