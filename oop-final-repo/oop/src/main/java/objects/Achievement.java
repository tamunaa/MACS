package objects;

public class Achievement {
    private int achievementID = -1;
    private String achievementType;
    private String pictureUrl;
    private String achievementDesc;

    public Achievement(String achievementType, String pictureUrl, String achievementDesc) {
        this.achievementType = achievementType;
        this.pictureUrl = pictureUrl;
        this.achievementDesc = achievementDesc;
    }

    public int getAchievementID() {
        return achievementID;
    }

    public void setAchievementID(int achievementID) {
        this.achievementID = achievementID;
    }

    public String getAchievementType() {
        return achievementType;
    }

    public void setAchievementType(String achievementType) {
        this.achievementType = achievementType;
    }

    public String getPictureUrl() {
        return pictureUrl;
    }

    public void setPictureUrl(String pictureUrl) {
        this.pictureUrl = pictureUrl;
    }

    public String getAchievementDesc() {
        return achievementDesc;
    }

    public void setAchievementDesc(String achievementDesc) {
        this.achievementDesc = achievementDesc;
    }
}
