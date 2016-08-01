package models;

/**
 * Created by Lucien on 8/1/2016.
 */
public class UserAchievement {
    private int userachievement_database_id;
    private String user_student_number;
    private int userachievement_progress;
    private int userachievement_updated;
    private int achievement_id;
    private int achievement_database_id;
    private String achievement_title;
    private String achievement_description;
    private int achievement_total;

    public UserAchievement() {

    }

    public int getUserachievement_database_id() {
        return userachievement_database_id;
    }

    public void setUserachievement_database_id(int userachievement_database_id) {
        this.userachievement_database_id = userachievement_database_id;
    }

    public String getUser_student_number() {
        return user_student_number;
    }

    public void setUser_student_number(String user_student_number) {
        this.user_student_number = user_student_number;
    }

    public int getAchievement_id() {
        return achievement_id;
    }

    public void setAchievement_id(int achievement_id) {
        this.achievement_id = achievement_id;
    }

    public int getUserachievement_progress() {
        return userachievement_progress;
    }

    public void setUserachievement_progress(int userachievement_progress) {
        this.userachievement_progress = userachievement_progress;
    }

    public int getUserachievement_updated() {
        return userachievement_updated;
    }

    public void setUserachievement_updated(int userachievement_updated) {
        this.userachievement_updated = userachievement_updated;
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
