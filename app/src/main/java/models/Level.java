package models;

import java.io.Serializable;
import java.util.ArrayList;

public class Level implements Serializable {

    private int level_id; //PK
    private int level_number;
    private String level_title;
    private String level_description;
    private ArrayList<Puzzle> puzzle_list;

    public Level() {
    }

    public int getLevel_id() {
        return level_id;
    }

    public void setLevel_id(int level_id) {
        this.level_id = level_id;
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

    public ArrayList<Puzzle> getPuzzle_list() {
        return puzzle_list;
    }

    public void addToPuzzle_list(Puzzle puzzle) {
        this.puzzle_list.add(puzzle);
    }
}
