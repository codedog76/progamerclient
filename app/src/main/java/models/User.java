package models;

public class User {

    private String user_student_number_id; //pk
    private String user_nickname;
    private String user_type;
    private int user_is_private;
    private int user_avatar;
    private int user_logged_in;
    private int user_updated;
    private int user_levels_completed;

    private String user_password;

    private int user_total_score;
    private int user_total_attempts;
    private int user_total_time;
    private double user_average_score;
    private double user_average_attempts;
    private double user_average_time;

    public User() {
    }

    public int getUser_levels_completed() {
        return user_levels_completed;
    }

    public void setUser_levels_completed(int user_levels_completed) {
        this.user_levels_completed = user_levels_completed;
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

    public String getUser_type() {
        return user_type;
    }

    public void setUser_type(String user_type) {
        this.user_type = user_type;
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
        if (user_is_private) {
            this.user_is_private = 1;
        } else {
            this.user_is_private = 0;
        }
    }

    public int getUser_logged_in() {
        return user_logged_in;
    }

    public void setUser_logged_in(int user_logged_in) {
        this.user_logged_in = user_logged_in;
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

    public int getUser_total_score() {
        return user_total_score;
    }

    public void setUser_total_score(int user_total_score) {
        this.user_total_score = user_total_score;
    }

    public int getUser_total_attempts() {
        return user_total_attempts;
    }

    public void setUser_total_attempts(int user_total_attempts) {
        this.user_total_attempts = user_total_attempts;
    }

    public int getUser_total_time() {
        return user_total_time;
    }

    public void setUser_total_time(int user_total_time) {
        this.user_total_time = user_total_time;
    }

    public double getUser_average_score() {
        return user_average_score;
    }

    public void setUser_average_score(double user_average_score) {
        this.user_average_score = user_average_score;
    }

    public double getUser_average_attempts() {
        return user_average_attempts;
    }

    public void setUser_average_attempts(double user_average_attempts) {
        this.user_average_attempts = user_average_attempts;
    }

    public double getUser_average_time() {
        return user_average_time;
    }

    public void setUser_average_time(double user_average_time) {
        this.user_average_time = user_average_time;
    }

    public int getUser_updated() {
        return user_updated;
    }

    public void setUser_updated(int user_updated) {
        this.user_updated = user_updated;
    }
}
