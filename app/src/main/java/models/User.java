package models;

import java.io.Serializable;

public class User {

    private String user_student_number_id; //pk
    private String user_nickname;
    private int user_is_private;
    private int user_avatar;
    private int user_logged_in;
    private int user_updated;

    private String user_password;
    private int user_current_level;
    private int user_overall_score;
    private int user_overall_attempts;
    private int user_overall_time;
    private int user_overall_score_rank;
    private int user_overall_time_rank;
    private int user_overall_attempts_rank;

    public User() {
    }

    public String getUser_student_number_id() {
        return user_student_number_id;
    }

    public void setUser_student_number_id(String user_student_number_id) {
        this.user_student_number_id = user_student_number_id;
    }

    public String getUser_nickname() {
        return user_nickname;
    }

    public void setUser_nickname(String user_nickname) {
        this.user_nickname = user_nickname;
    }

    public int getUser_avatar() {
        return user_avatar;
    }

    public void setUser_avatar(int user_avatar) {
        this.user_avatar = user_avatar;
    }

    public int getUser_is_private() {
        return user_is_private;
    }

    public void setUser_is_private(boolean user_is_private) {
        if(user_is_private) {
            this.user_is_private = 1;
        } else {
            this.user_is_private = 0;
        }
    }

    public void setUser_is_private(int user_is_private) {
        this.user_is_private = user_is_private;
    }

    public String getUser_password() {
        return user_password;
    }

    public void setUser_password(String user_password) {
        this.user_password = user_password;
    }

    public int getUser_current_level() {
        return user_current_level;
    }

    public void setUser_current_level(int user_current_level) {
        this.user_current_level = user_current_level;
    }

    public int getUser_overall_score() {
        return user_overall_score;
    }

    public void setUser_overall_score(int user_overall_score) {
        this.user_overall_score = user_overall_score;
    }

    public int getUser_overall_attempts() {
        return user_overall_attempts;
    }

    public void setUser_overall_attempts(int user_overall_attempts) {
        this.user_overall_attempts = user_overall_attempts;
    }

    public int getUser_overall_time() {
        return user_overall_time;
    }

    public void setUser_overall_time(int user_overall_time) {
        this.user_overall_time = user_overall_time;
    }

    public int getUser_logged_in() {
        return user_logged_in;
    }

    public void setUser_logged_in(int user_logged_in) {
        this.user_logged_in = user_logged_in;
    }

    public int getUser_overall_score_rank() {
        return user_overall_score_rank;
    }

    public void setUser_overall_score_rank(int user_overall_score_rank) {
        this.user_overall_score_rank = user_overall_score_rank;
    }

    public int getUser_overall_time_rank() {
        return user_overall_time_rank;
    }

    public void setUser_overall_time_rank(int user_overall_time_rank) {
        this.user_overall_time_rank = user_overall_time_rank;
    }

    public int getUser_overall_attempts_rank() {
        return user_overall_attempts_rank;
    }

    public void setUser_overall_attempts_rank(int user_overall_attempts_rank) {
        this.user_overall_attempts_rank = user_overall_attempts_rank;
    }

    public int getUser_updated() {
        return user_updated;
    }

    public void setUser_updated(int user_updated) {
        this.user_updated = user_updated;
    }
}
