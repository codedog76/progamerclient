package models;

import java.io.Serializable;
import java.util.ArrayList;

public class Level implements Serializable {

    private int level_id; //PK
    private String level_user_student_number_ID; //fk
    private int level_database_id;
    private int level_number;
    private String level_title;
    private String level_description;
    private int level_completed;
    private int level_score;
    private int level_attempts;
    private int level_time;
    private int level_updated;

    private int level_puzzles_completed;
    private int level_puzzles_count;

    public Level() {
    }

    public int getLevel_id() {
        return level_id;
    }

    public void setLevel_id(int level_id) {
        this.level_id = level_id;
    }

    public String getLevel_user_student_number_ID() {
        return level_user_student_number_ID;
    }

    public void setLevel_user_student_number_ID(String level_user_student_number_ID) {
        this.level_user_student_number_ID = level_user_student_number_ID;
    }

    public int getLevel_database_id() {
        return level_database_id;
    }

    public void setLevel_database_id(int level_database_id) {
        this.level_database_id = level_database_id;
    }

    public int getLevel_number() {
        return level_number;
    }

    public void setLevel_number(int level_number) {
        this.level_number = level_number;
    }

    public String getLevel_title() {
        return level_title;
    }

    public void setLevel_title(String level_title) {
        this.level_title = level_title;
    }

    public String getLevel_description() {
        return level_description;
    }

    public void setLevel_description(String level_description) {
        this.level_description = level_description;
    }

    public int getLevel_completed() {
        if (level_puzzles_completed == level_puzzles_count && level_puzzles_count != 0) {
            level_completed = 1;
        }
        return level_completed;
    }

    public void setLevel_completed(int level_completed) {
        this.level_completed = level_completed;
    }

    public boolean getPuzzles_completed() {
        return level_puzzles_completed == level_puzzles_count && level_puzzles_count != 0;
    }

    public void setLevel_completed(boolean level_completed) {
        if (level_completed)
            this.level_completed = 1;
        else
            this.level_completed = 0;
    }

    public int getLevel_score() {
        return level_score;
    }

    public void setLevel_score(int level_score) {
        this.level_score = level_score;
    }

    public int updateLevel_score() {
        if (level_completed != 1) {
            level_score = 0;
            return level_score;
        }
        int calc_score = 150 - (level_time + (level_attempts * 5));
        if (level_score < 0)
            level_score = 1;
        else
            level_score = calc_score;
        return level_score;
    }

    public int getLevel_updated() {
        return level_updated;
    }

    public void setLevel_updated(int level_updated) {
        this.level_updated = level_updated;
    }

    public int getLevel_attempts() {
        return level_attempts;
    }

    public void setLevel_attempts(int level_attempts) {
        this.level_attempts = level_attempts;
    }

    public int updateLevel_attempts(int new_level_attempts) {
        if (new_level_attempts < level_attempts || level_attempts == 0)
            level_attempts = new_level_attempts;
        return level_attempts;
    }

    public int getLevel_time() {
        return level_time;
    }

    public void setLevel_time(int level_time) {
        this.level_time = level_time;
    }

    public int updateLevel_time(int new_level_time) {
        if (new_level_time < level_time || level_time == 0)
            level_time = new_level_time;
        return level_time;
    }

    public int getLevel_puzzles_completed() {
        return level_puzzles_completed;
    }

    public void setLevel_puzzles_completed(int level_puzzles_completed) {
        this.level_puzzles_completed = level_puzzles_completed;
    }

    public int getLevel_puzzles_count() {
        return level_puzzles_count;
    }

    public void setLevel_puzzles_count(int level_puzzles_count) {
        this.level_puzzles_count = level_puzzles_count;
    }

    public String getLevel_trophy() {
        if (level_score >= 1 && level_score < 100) {
            return "trophy_bronze";
        }
        if (level_score >= 100 && level_score < 200) {
            return "trophy_silver";
        }
        if (level_score >= 100 && level_score <= 300) {
            return "trophy_gold";
        }
        return "trophy_grey";
    }
}
