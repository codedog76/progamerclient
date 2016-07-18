package models;

public class Puzzle {
    private int puzzle_id; //pk
    private int puzzle_level_id; //fk
    private String puzzle_instructions;
    private String puzzle_expected_output;
    private int puzzle_completed;
    private int puzzle_attempts;
    private int puzzle_time;

    public int getPuzzle_id() {
        return puzzle_id;
    }

    public void setPuzzle_id(int puzzle_id) {
        this.puzzle_id = puzzle_id;
    }

    public int getPuzzle_level_id() {
        return puzzle_level_id;
    }

    public void setPuzzle_level_id(int puzzle_level_id) {
        this.puzzle_level_id = puzzle_level_id;
    }

    public int getPuzzle_completed() {
        return puzzle_completed;
    }

    public void setPuzzle_completed(int puzzle_completed) {
        this.puzzle_completed = puzzle_completed;
    }

    public int getPuzzle_attempts() {
        return puzzle_attempts;
    }

    public void setPuzzle_attempts(int puzzle_attempts) {
        this.puzzle_attempts = puzzle_attempts;
    }

    public int getPuzzle_time() {
        return puzzle_time;
    }

    public void setPuzzle_time(int puzzle_time) {
        this.puzzle_time = puzzle_time;
    }
}
