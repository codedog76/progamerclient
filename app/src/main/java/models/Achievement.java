package models;

public class Achievement {

    private int achievement_id; //PK
    private int achievement_database_id;
    private String achievement_title;
    private String achievement_description;
    private int achievement_total;

    public Achievement() {

    }

    public int getAchievement_id() {
        return achievement_id;
    }

    public void setAchievement_id(int achievement_id) {
        this.achievement_id = achievement_id;
    }

    public int getAchievement_database_id() {
        return achievement_database_id;
    }

    public void setAchievement_database_id(int achievement_database_id) {
        this.achievement_database_id = achievement_database_id;
    }

    public String getAchievement_title() {
        return achievement_title;
    }

    public void setAchievement_title(String achievement_title) {
        this.achievement_title = achievement_title;
    }

    public String getAchievement_description() {
        return achievement_description;
    }

    public void setAchievement_description(String achievement_description) {
        this.achievement_description = achievement_description;
    }

    public int getAchievement_total() {
        return achievement_total;
    }

    public void setAchievement_total(int achievement_total) {
        this.achievement_total = achievement_total;
    }
}
