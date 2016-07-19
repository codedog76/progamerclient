package models;

/**
 * Created by Lucien on 7/19/2016.
 */
public class UserLevel {
    private int userlevel_id; //pk
    private String userlevel_student_number_id; //fk
    private int userlevel_level_id; //fk
    private int userlevel_completed;
    private int userlevel_score;
    private int userlevel_attempts;
    private int userlevel_time;

    public UserLevel() {

    }

    public int getUserlevel_id() {
        return userlevel_id;
    }

    public void setUserlevel_id(int userlevel_id) {
        this.userlevel_id = userlevel_id;
    }

    public String getUserlevel_student_number_id() {
        return userlevel_student_number_id;
    }

    public void setUserlevel_student_number_id(String userlevel_student_number_id) {
        this.userlevel_student_number_id = userlevel_student_number_id;
    }

    public int getUserlevel_level_id() {
        return userlevel_level_id;
    }

    public void setUserlevel_level_id(int userlevel_level_id) {
        this.userlevel_level_id = userlevel_level_id;
    }

    public int getUserlevel_completed() {
        return userlevel_completed;
    }

    public void setUserlevel_completed(int userlevel_completed) {
        this.userlevel_completed = userlevel_completed;
    }

    public int getUserlevel_score() {
        return userlevel_score;
    }

    public void setUserlevel_score(int userlevel_score) {
        this.userlevel_score = userlevel_score;
    }

    public int getUserlevel_attempts() {
        return userlevel_attempts;
    }

    public void setUserlevel_attempts(int userlevel_attempts) {
        this.userlevel_attempts = userlevel_attempts;
    }

    public int getUserlevel_time() {
        return userlevel_time;
    }

    public void setUserlevel_time(int userlevel_time) {
        this.userlevel_time = userlevel_time;
    }
}
