package dataBase;

import objects.Achievement;

import java.util.List;

public interface AchievementDAO {
    public List<Achievement> getUserAchievements(int userid);
    public boolean addUserAchievement(int userid, int achievementID);
    public Achievement getAchievementByID(int id);
    public int addAchievement(Achievement achievement);
    public boolean removeAchievement(int achievementID);
}
