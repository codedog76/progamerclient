package models;

public class Puzzle {
    private int puzzle_id; //pk
    private int puzzle_level_id; //fk
    private int puzzle_database_id;
    private String puzzle_type;
    private String puzzle_instructions;
    private String puzzle_expected_output;
    private String puzzle_data;
    private String puzzle_answer;
    private int puzzle_completed;
    private int puzzle_attempts;
    private int puzzle_time;

    public Puzzle() {

    }

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

    public int getPuzzle_database_id() {
        return puzzle_database_id;
    }

    public void setPuzzle_database_id(int puzzle_database_id) {
        this.puzzle_database_id = puzzle_database_id;
    }

    public String getPuzzle_type() {
        return puzzle_type;
    }

    public void setPuzzle_type(String puzzle_type) {
        this.puzzle_type = puzzle_type;
    }

    public String getPuzzle_instructions() {
        return puzzle_instructions;
    }

    public void setPuzzle_instructions(String puzzle_instructions) {
        this.puzzle_instructions = puzzle_instructions;
    }

    public String getPuzzle_expected_output() {
        return puzzle_expected_output;
    }

    public void setPuzzle_expected_output(String puzzle_expected_output) {
        this.puzzle_expected_output = puzzle_expected_output;
    }

    public String getPuzzle_data() {
        return puzzle_data;
    }

    public void setPuzzle_data(String puzzle_data) {
        this.puzzle_data = puzzle_data;
    }

    public int getPuzzle_completed() {
        return puzzle_completed;
    }

    public void setPuzzle_completed(int puzzle_completed) {
        this.puzzle_completed = puzzle_completed;
    }

    public void setPuzzle_completed(boolean puzzle_completed) {
        if(puzzle_completed)
            this.puzzle_completed = 1;
        else
            this.puzzle_completed = 0;
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

    public String getPuzzle_answer() {
        return puzzle_answer;
    }

    public void setPuzzle_answer(String puzzle_answer) {
        this.puzzle_answer = puzzle_answer;
    }
}
