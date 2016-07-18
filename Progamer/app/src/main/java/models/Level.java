package models;

import java.io.Serializable;

public class Level implements Serializable {

    private int level_id; //PK
    private String level_student_number; //FK
    private String level_title;
    private int level_progress;
    private int level_score;
    private int level_attempts;
    private int level_time;

    public Level() {
    }

    public int getLevel_id() {
        return level_id;
    }

    public void setLevel_id(int level_id) {
        this.level_id = level_id;
    }

    public String getLevel_student_number() {
        return level_student_number;
    }

    public void setLevel_student_number(String level_student_number) {
        this.level_student_number = level_student_number;
    }

    public String getLevel_title() {
        return level_title;
    }

    public void setLevel_title(String level_title) {
        this.level_title = level_title;
    }

    public int getLevel_progress() {
        return level_progress;
    }

    public void setLevel_progress(int level_progress) {
        this.level_progress = level_progress;
    }

    public int getLevel_score() {
        return level_score;
    }

    public void setLevel_score(int level_score) {
        this.level_score = level_score;
    }

    public int getLevel_attempts() {
        return level_attempts;
    }

    public void setLevel_attempts(int level_attempts) {
        this.level_attempts = level_attempts;
    }

    public int getLevel_time() {
        return level_time;
    }

    public void setLevel_time(int level_time) {
        this.level_time = level_time;
    }
}
