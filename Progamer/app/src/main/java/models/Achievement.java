package models;

public class Achievement {
    private String achievementTitle, achievementDescription;
    int achievementTotal;
    int achievementProgress;

    public Achievement(String achievementTitle, String achievementDescription, int achievementTotal, int achievementProgress) {
        this.achievementTitle = achievementTitle;
        this.achievementDescription = achievementDescription;
        this.achievementTotal = achievementTotal;
        this.achievementProgress = achievementProgress;
    }

    public int getAchievementProgress() {
        return achievementProgress;
    }

    public void setAchievementProgress(int achievementProgress) {
        this.achievementProgress = achievementProgress;
    }

    public String getAchievementTitle() {
        return achievementTitle;
    }

    public void setAchievementTitle(String achievementTitle) {
        this.achievementTitle = achievementTitle;
    }

    public String getAchievementDescription() {
        return achievementDescription;
    }

    public void setAchievementDescription(String achievementDescription) {
        this.achievementDescription = achievementDescription;
    }

    public int getAchievementTotal() {
        return achievementTotal;
    }

    public void setAchievementTotal(int achievementTotal) {
        this.achievementTotal = achievementTotal;
    }
}
