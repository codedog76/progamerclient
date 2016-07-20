package database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

import models.Level;
import models.Puzzle;
import models.User;

public class DatabaseHandler extends SQLiteOpenHelper {

    // Logcat tag
    private static final String LOG = DatabaseHandler.class.getName();
    // Database Version
    private static final int DATABASE_VERSION = 1;
    // Database Name
    private static final String DATABASE_NAME = "progamer";

    // Table user
    private static final String USER_TABLE = "user";
    private static final String USER_STUDENT_NUMBER_ID = "user_student_number";
    private static final String USER_NICKNAME = "user_nickname";
    private static final String USER_LOGGED_IN = "user_logged_in";
    private static final String USER_AVATAR = "user_avatar";
    private static final String USER_IS_PRIVATE = "user_is_private";
    private static final String CREATE_TABLE_USER = "create table "
            + USER_TABLE
            + "("
            + USER_STUDENT_NUMBER_ID + " text not null primary key, "
            + USER_NICKNAME + " text not null, "
            + USER_IS_PRIVATE + " integer not null, "
            + USER_AVATAR + " integer not null, "
            + USER_LOGGED_IN + " integer not null"
            + ");";

    //table level
    private static final String LEVEL_TABLE = "level";
    private static final String LEVEL_ID = "level_id"; //pk
    private static final String LEVEL_USER_STUDENT_NUMBER_ID = "level_user_student_number_ID"; //fk
    private static final String LEVEL_DATABASE_ID = "level_database_id";
    private static final String LEVEL_NUMBER = "level_number";
    private static final String LEVEL_TITLE = "level_title";
    private static final String LEVEL_DESCRIPTION = "level_description";
    private static final String LEVEL_COMPLETED = "level_completed";
    private static final String LEVEL_SCORE = "level_score";
    private static final String LEVEL_ATTEMPTS = "level_attempts";
    private static final String LEVEL_TIME = "level_time";
    private static final String CREATE_TABLE_LEVEL = "create table "
            + LEVEL_TABLE
            + "("
            + LEVEL_ID + " integer not null primary key AUTOINCREMENT, "
            + LEVEL_USER_STUDENT_NUMBER_ID + " text not null, "
            + LEVEL_DATABASE_ID + " integer not null, "
            + LEVEL_NUMBER + " integer not null, "
            + LEVEL_TITLE + " text not null, "
            + LEVEL_DESCRIPTION + " text not null, "
            + LEVEL_COMPLETED + " integer not null, "
            + LEVEL_SCORE + " integer not null, "
            + LEVEL_ATTEMPTS + " integer not null, "
            + LEVEL_TIME + " integer not null, "
            + "foreign key (" + LEVEL_USER_STUDENT_NUMBER_ID + ") references " + USER_TABLE + "(" + USER_STUDENT_NUMBER_ID + ")"
            + ");";

    //table puzzle
    private static final String PUZZLE_TABLE = "puzzle";
    private static final String PUZZLE_ID = "puzzle_id"; //pk
    private static final String PUZZLE_LEVEL_ID = "puzzle_level_id"; //fk
    private static final String PUZZLE_DATABASE_ID = "puzzle_database_id";
    private static final String PUZZLE_TYPE = "puzzle_type";
    private static final String PUZZLE_INSTRUCTIONS = "puzzle_instructions";
    private static final String PUZZLE_EXPECTED_OUTPUT = "puzzle_expected_output";
    private static final String PUZZLE_DATA = "puzzle_data";
    private static final String PUZZLE_COMPLETED = "puzzle_completed";
    private static final String PUZZLE_ATTEMPTS = "puzzle_attempts";
    private static final String PUZZLE_TIME = "puzzle_time";
    private static final String CREATE_TABLE_PUZZLE = "create table "
            + PUZZLE_TABLE
            + "("
            + PUZZLE_ID + " integer not null primary key AUTOINCREMENT, "
            + PUZZLE_LEVEL_ID + " integer not null, "
            + PUZZLE_DATABASE_ID + " integer not null, "
            + PUZZLE_TYPE + " text not null, "
            + PUZZLE_INSTRUCTIONS + " text not null, "
            + PUZZLE_EXPECTED_OUTPUT + " text not null, "
            + PUZZLE_DATA + " text not null, "
            + PUZZLE_COMPLETED + " integer not null, "
            + PUZZLE_ATTEMPTS + " integer not null, "
            + PUZZLE_TIME + " integer not null, "
            + "foreign key (" + PUZZLE_LEVEL_ID + ") references " + LEVEL_TABLE + "(" + LEVEL_ID + ")"
            + ");";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(CREATE_TABLE_USER);
        database.execSQL(CREATE_TABLE_LEVEL);
        database.execSQL(CREATE_TABLE_PUZZLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + USER_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + LEVEL_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + PUZZLE_TABLE);
        onCreate(db);
    }

    public boolean insertOrUpdateUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(USER_STUDENT_NUMBER_ID, user.getUser_student_number_id());
        values.put(USER_NICKNAME, user.getUser_nickname());
        values.put(USER_IS_PRIVATE, user.getUser_is_private());
        values.put(USER_AVATAR, user.getUser_avatar());
        values.put(USER_LOGGED_IN, user.getUser_logged_in());
        long user_id = db.insertWithOnConflict(USER_TABLE, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        db.close();
        return user_id != -1;
    }

    public void insertOrUpdateLevel(Level level) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(LEVEL_USER_STUDENT_NUMBER_ID, level.getLevel_user_student_number_ID());
        values.put(LEVEL_DATABASE_ID, level.getLevel_database_id());
        values.put(LEVEL_NUMBER, level.getLevel_number());
        values.put(LEVEL_TITLE, level.getLevel_title());
        values.put(LEVEL_DESCRIPTION, level.getLevel_description());
        values.put(LEVEL_COMPLETED, level.getLevel_completed());
        values.put(LEVEL_SCORE, level.getLevel_score());
        values.put(LEVEL_ATTEMPTS, level.getLevel_attempts());
        values.put(LEVEL_TIME, level.getLevel_time());
        db.insertWithOnConflict(LEVEL_TABLE, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        db.close();
    }

    public void insertOrUpdatePuzzle(Puzzle puzzle) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(PUZZLE_LEVEL_ID, puzzle.getPuzzle_level_id());
        values.put(PUZZLE_DATABASE_ID, puzzle.getPuzzle_level_id());
        values.put(PUZZLE_TYPE, puzzle.getPuzzle_type());
        values.put(PUZZLE_INSTRUCTIONS, puzzle.getPuzzle_instructions());
        values.put(PUZZLE_EXPECTED_OUTPUT, puzzle.getPuzzle_expected_output());
        values.put(PUZZLE_DATA, puzzle.getPuzzle_data());
        values.put(PUZZLE_COMPLETED, puzzle.getPuzzle_completed());
        values.put(PUZZLE_ATTEMPTS, puzzle.getPuzzle_attempts());
        values.put(PUZZLE_TIME, puzzle.getPuzzle_time());
        db.insertWithOnConflict(PUZZLE_TABLE, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        db.close();
    }

    public User getLoggedInUser() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(USER_TABLE, new String[]{USER_STUDENT_NUMBER_ID, USER_NICKNAME, USER_IS_PRIVATE, USER_AVATAR}, USER_LOGGED_IN + "=?",
                new String[]{"1"}, null, null, null, null);
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            User user = new User();
            user.setUser_student_number_id(cursor.getString(0));
            user.setUser_nickname(cursor.getString(1));
            user.setUser_is_private(cursor.getInt(2));
            user.setUser_avatar(cursor.getInt(3));
            cursor.close();
            return user;
        } else {
            return null;
        }
    }

    public int getLoggedInUserOverallScore(User current_user) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT SUM(puzzle_score) AS overall_score FROM " + PUZZLE_TABLE + ", " + LEVEL_TABLE +
                " WHERE level_student_number= ? AND puzzle_level_id=level_id", new String[]{current_user.getUser_student_number_id()});
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            int overall_score = cursor.getInt(cursor.getColumnIndex("overall_score"));
            cursor.close();
            return overall_score;
        } else {
            return -1;
        }
    }

    public int getLoggedInUserOverallAttempts(User current_user) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT SUM(puzzle_attempts) AS overall_attempts FROM " + PUZZLE_TABLE + ", " + LEVEL_TABLE +
                " WHERE level_student_number= ? AND puzzle_level_id=level_id", new String[]{current_user.getUser_student_number_id()});
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            int overall_attempts = cursor.getInt(cursor.getColumnIndex("overall_attempts"));
            cursor.close();
            return overall_attempts;
        } else {
            return -1;
        }
    }

    public int getLoggedInUserOverallTime(User current_user) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT SUM(puzzle_time) AS overall_time FROM " + PUZZLE_TABLE + ", " + LEVEL_TABLE +
                " WHERE level_student_number= ? AND puzzle_level_id=level_id", new String[]{current_user.getUser_student_number_id()});
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            int overall_time = cursor.getInt(cursor.getColumnIndex("overall_time"));
            cursor.close();
            return overall_time;
        } else {
            return -1;
        }
    }

    public boolean loginUser(String user_student_number) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(USER_LOGGED_IN, 1);
        int rowsAffected = db.update(USER_TABLE, values, USER_STUDENT_NUMBER_ID + " = ?",
                new String[]{user_student_number});
        return rowsAffected == 1;
    }

    public boolean logoutUser(User current_user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(USER_LOGGED_IN, 0);
        int rowsAffected = db.update(USER_TABLE, values, USER_STUDENT_NUMBER_ID + " = ?",
                new String[]{current_user.getUser_student_number_id()});
        return rowsAffected == 1;
    }

    public boolean checkHasLevels() { //Todo: finish
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + LEVEL_TABLE + " limit 1", null);
        if (cursor != null && cursor.getCount() > 0) {
            cursor.close();
            return true;
        } else {
            return false;
        }
    }//todo:improve

    public ArrayList<Level> getLevels(User current_user) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT level_id, level_database_id, level_number, level_title, level_description, level_completed, level_score, level_attempts, level_time FROM " + LEVEL_TABLE +
                " WHERE level_user_student_number_ID= ? ", new String[]{current_user.getUser_student_number_id()});
        ArrayList<Level> level_list = new ArrayList<>();
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                Level level = new Level();
                level.setLevel_id(Integer.parseInt(cursor.getString(0)));
                level.setLevel_database_id(Integer.parseInt(cursor.getString(1)));
                level.setLevel_number(Integer.parseInt(cursor.getString(2)));
                level.setLevel_title(cursor.getString(3));
                level.setLevel_description(cursor.getString(4));
                level.setLevel_completed(Integer.parseInt(cursor.getString(5)));
                level.setLevel_score(Integer.parseInt(cursor.getString(6)));
                level.setLevel_attempts(Integer.parseInt(cursor.getString(7)));
                level.setLevel_time(Integer.parseInt(cursor.getString(8)));
                level_list.add(getPuzzlesProgress(level));
                cursor.moveToNext();
            }
        }
        cursor.close();
        return level_list;
    }

    private Level getPuzzlesProgress(Level level) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT SUM(puzzle_completed) AS level_puzzles_progress, COUNT(*) AS level_puzzles_count FROM " + PUZZLE_TABLE +
                " WHERE puzzle_level_id= ? ", new String[]{String.valueOf(level.getLevel_id())});
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            level.setLevel_puzzles_progress(cursor.getInt(cursor.getColumnIndex("level_puzzles_progress")));
            level.setLevel_puzzles_count(cursor.getInt(cursor.getColumnIndex("level_puzzles_count")));
            cursor.close();
        }
        return level;
    }

    public Puzzle getNextPuzzle(User user, Level level) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT puzzle_id, puzzle_level_id, puzzle_type, puzzle_instructions, puzzle_expected_output, puzzle_data, puzzle_completed, puzzle_attempts, puzzle_time FROM " + PUZZLE_TABLE + " INNER JOIN " + LEVEL_TABLE +
                " WHERE puzzle_level_id=level_id AND puzzle_completed=0 AND level_user_student_number_ID= ? AND level_id= ? LIMIT 1", new String[]{user.getUser_student_number_id(), String.valueOf(level.getLevel_id())});
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            Puzzle puzzle = new Puzzle();
            puzzle.setPuzzle_id(cursor.getInt(cursor.getColumnIndex("puzzle_id")));
            puzzle.setPuzzle_level_id(cursor.getInt(cursor.getColumnIndex("puzzle_level_id")));
            puzzle.setPuzzle_type(cursor.getString(cursor.getColumnIndex("puzzle_type")));
            puzzle.setPuzzle_instructions(cursor.getString(cursor.getColumnIndex("puzzle_instructions")));
            puzzle.setPuzzle_expected_output(cursor.getString(cursor.getColumnIndex("puzzle_expected_output")));
            puzzle.setPuzzle_data(cursor.getString(cursor.getColumnIndex("puzzle_data")));
            puzzle.setPuzzle_completed(cursor.getInt(cursor.getColumnIndex("puzzle_completed")));
            puzzle.setPuzzle_attempts(cursor.getInt(cursor.getColumnIndex("puzzle_attempts")));
            puzzle.setPuzzle_time(cursor.getInt(cursor.getColumnIndex("puzzle_time")));
            cursor.close();
            return puzzle;
        }
        return null;
    }

    public boolean setPuzzleCompleted(Puzzle puzzle) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(PUZZLE_COMPLETED, 1);
        int rowsAffected = db.update(PUZZLE_TABLE, values, PUZZLE_ID + " = ?",
                new String[]{String.valueOf(puzzle.getPuzzle_id())});
        return rowsAffected == 1;
    }
}
