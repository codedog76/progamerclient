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
    private int level_puzzles_progress;
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
        return level_completed;
    }

    public void setLevel_completed(int level_completed) {
        this.level_completed = level_completed;
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

    public int getLevel_puzzles_progress() {
        return level_puzzles_progress;
    }

    public void setLevel_puzzles_progress(int level_puzzles_progress) {
        this.level_puzzles_progress = level_puzzles_progress;
    }

    public int getLevel_puzzles_count() {
        return level_puzzles_count;
    }

    public void setLevel_puzzles_count(int level_puzzles_count) {
        this.level_puzzles_count = level_puzzles_count;
    }
}
