package database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

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
    private static final String USER_STUDENT_NUMBER_ID = "user_student_number_id";
    private static final String USER_NICKNAME = "user_nickname";
    private static final String USER_LOGGED_IN = "user_logged_in";
    private static final String USER_AVATAR = "user_avatar";
    private static final String USER_IS_PRIVATE = "user_is_private";
    private static final String USER_UPDATED = "user_updated";
    private static final String CREATE_TABLE_USER = "create table "
            + USER_TABLE
            + "("
            + USER_STUDENT_NUMBER_ID + " text not null primary key, "
            + USER_NICKNAME + " text not null, "
            + USER_IS_PRIVATE + " integer not null, "
            + USER_AVATAR + " integer not null, "
            + USER_LOGGED_IN + " integer not null, "
            + USER_UPDATED + " integer not null"
            + ");";

    //table level
    private static final String LEVEL_TABLE = "level";
    private static final String LEVEL_ID = "level_id"; //pk
    private static final String LEVEL_USER_STUDENT_NUMBER_ID = "level_user_student_number_id"; //fk
    private static final String LEVEL_DATABASE_ID = "level_database_id";
    private static final String LEVEL_NUMBER = "level_number";
    private static final String LEVEL_TITLE = "level_title";
    private static final String LEVEL_DESCRIPTION = "level_description";
    private static final String LEVEL_COMPLETED = "level_completed";
    private static final String LEVEL_SCORE = "level_score";
    private static final String LEVEL_ATTEMPTS = "level_attempts";
    private static final String LEVEL_TIME = "level_time";
    private static final String LEVEL_UPDATED = "level_updated";
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
            + LEVEL_UPDATED + " integer not null, "
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
    private static final String PUZZLE_ANSWER = "puzzle_answer";
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
            + PUZZLE_ANSWER + " text not null, "
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

    public long insertUser(User new_user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(USER_STUDENT_NUMBER_ID, new_user.getUser_student_number_id());
        values.put(USER_NICKNAME, new_user.getUser_nickname());
        values.put(USER_IS_PRIVATE, new_user.getUser_is_private());
        values.put(USER_AVATAR, new_user.getUser_avatar());
        values.put(USER_LOGGED_IN, new_user.getUser_logged_in());
        values.put(USER_UPDATED, new_user.getUser_updated());
        long user_id = db.insertWithOnConflict(USER_TABLE, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        db.close();
        return user_id;
    }

    public long insertLevel(Level level) {
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
        values.put(LEVEL_UPDATED, level.getLevel_updated());
        long level_id = db.insertWithOnConflict(LEVEL_TABLE, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        db.close();
        return level_id;
    }

    public ArrayList<Level> getLevels(User current_user) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT level_id, level_user_student_number_id, level_database_id, level_number, level_title, level_description, level_completed, level_score, level_attempts, level_time, level_updated" +
                " FROM " + LEVEL_TABLE + " WHERE level_user_student_number_id= ? ", new String[]{current_user.getUser_student_number_id()});
        ArrayList<Level> level_list = new ArrayList<>();
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                Level current_level = new Level();
                current_level.setLevel_id(cursor.getInt(0));
                current_level.setLevel_user_student_number_ID(cursor.getString(1));
                current_level.setLevel_database_id(cursor.getInt(2));
                current_level.setLevel_number(cursor.getInt(3));
                current_level.setLevel_title(cursor.getString(4));
                current_level.setLevel_description(cursor.getString(5));
                current_level.setLevel_completed(cursor.getInt(6));
                current_level.setLevel_score(cursor.getInt(7));
                current_level.setLevel_attempts(cursor.getInt(8));
                current_level.setLevel_time(cursor.getInt(9));
                current_level.setLevel_updated(cursor.getInt(10));
                level_list.add(getPuzzlesCompletionData(current_level));
                cursor.moveToNext();
            }
        }
        cursor.close();
        return level_list;
    }

    public long insertOrUpdatePuzzle(Puzzle puzzle) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(PUZZLE_LEVEL_ID, puzzle.getPuzzle_level_id());
        values.put(PUZZLE_DATABASE_ID, puzzle.getPuzzle_level_id());
        values.put(PUZZLE_TYPE, puzzle.getPuzzle_type());
        values.put(PUZZLE_INSTRUCTIONS, puzzle.getPuzzle_instructions());
        values.put(PUZZLE_EXPECTED_OUTPUT, puzzle.getPuzzle_expected_output());
        values.put(PUZZLE_DATA, puzzle.getPuzzle_data());
        values.put(PUZZLE_ANSWER, puzzle.getPuzzle_answer());
        values.put(PUZZLE_COMPLETED, puzzle.getPuzzle_completed());
        values.put(PUZZLE_ATTEMPTS, puzzle.getPuzzle_attempts());
        values.put(PUZZLE_TIME, puzzle.getPuzzle_time());
        long puzzle_id = db.insertWithOnConflict(PUZZLE_TABLE, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        db.close();
        return puzzle_id;
    }

    public int deleteLevelPuzzles(Level current_level) {
        SQLiteDatabase db = this.getWritableDatabase();
        int id = db.delete(PUZZLE_TABLE, "puzzle_level_id= ? ", new String[]{String.valueOf(current_level.getLevel_id())});
        db.close();
        return id;
    }

    public User getLoggedInUser() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT user_student_number_id, user_nickname, user_is_private, user_avatar, user_updated" +
                " FROM " + USER_TABLE + " WHERE user_logged_in= ? ", new String[]{"1"});
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            User user = new User();
            user.setUser_student_number_id(cursor.getString(0));
            user.setUser_nickname(cursor.getString(1));
            user.setUser_is_private(cursor.getInt(2));
            user.setUser_avatar(cursor.getInt(3));
            user.setUser_updated(cursor.getInt(4));
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

    public boolean checkHasLevels(User current_user) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + LEVEL_TABLE + " WHERE level_user_student_number_id= ? limit 1", new String[]{current_user.getUser_student_number_id()});
        if (cursor != null && cursor.getCount() > 0) {
            cursor.close();
            return true;
        } else {
            return false;
        }
    }//todo:improve

    public Puzzle getNextPuzzle(Level current_level) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + PUZZLE_TABLE +
                " WHERE puzzle_completed=0 AND puzzle_level_id= ? LIMIT 1", new String[]{String.valueOf(current_level.getLevel_id())});
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            Puzzle current_puzzle = new Puzzle();
            current_puzzle.setPuzzle_id(cursor.getInt(cursor.getColumnIndex("puzzle_id")));
            current_puzzle.setPuzzle_database_id(cursor.getInt(cursor.getColumnIndex("puzzle_database_id")));
            current_puzzle.setPuzzle_level_id(cursor.getInt(cursor.getColumnIndex("puzzle_level_id")));
            current_puzzle.setPuzzle_type(cursor.getString(cursor.getColumnIndex("puzzle_type")));
            current_puzzle.setPuzzle_instructions(cursor.getString(cursor.getColumnIndex("puzzle_instructions")));
            current_puzzle.setPuzzle_expected_output(cursor.getString(cursor.getColumnIndex("puzzle_expected_output")));
            current_puzzle.setPuzzle_data(cursor.getString(cursor.getColumnIndex("puzzle_data")));
            current_puzzle.setPuzzle_answer(cursor.getString(cursor.getColumnIndex("puzzle_answer")));
            current_puzzle.setPuzzle_completed(cursor.getInt(cursor.getColumnIndex("puzzle_completed")));
            current_puzzle.setPuzzle_attempts(cursor.getInt(cursor.getColumnIndex("puzzle_attempts")));
            current_puzzle.setPuzzle_time(cursor.getInt(cursor.getColumnIndex("puzzle_time")));
            cursor.close();
            return current_puzzle;
        }
        return null;
    }

    public int updatePuzzleData(Puzzle puzzle) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(PUZZLE_COMPLETED, puzzle.getPuzzle_completed());
        values.put(PUZZLE_ATTEMPTS, puzzle.getPuzzle_attempts());
        values.put(PUZZLE_TIME, puzzle.getPuzzle_time());
        int rowsAffected = db.update(PUZZLE_TABLE, values, PUZZLE_ID + " = ?",
                new String[]{String.valueOf(puzzle.getPuzzle_id())});
        db.close();
        return rowsAffected;
    }

    private Level getPuzzlesCompletionData(Level current_level) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT SUM(puzzle_completed) AS puzzles_completed, COUNT(*) AS puzzles_count FROM " + PUZZLE_TABLE +
                " WHERE puzzle_level_id= ? ", new String[]{String.valueOf(current_level.getLevel_id())});
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            current_level.setLevel_puzzles_completed(cursor.getInt(cursor.getColumnIndex("puzzles_completed")));
            current_level.setLevel_puzzles_count(cursor.getInt(cursor.getColumnIndex("puzzles_count")));
            cursor.close();
        }
        return current_level;
    }

    private Level getPuzzlesData(Level current_level) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT SUM(puzzle_attempts) AS level_attempts, SUM(puzzle_time) AS level_time FROM " + PUZZLE_TABLE +
                " WHERE puzzle_level_id= ? ", new String[]{String.valueOf(current_level.getLevel_id())});
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            current_level.updateLevel_attempts(cursor.getInt(0));
            current_level.updateLevel_time(cursor.getInt(1));
            cursor.close();
        }
        return current_level;
    }

    public Level getLevel(int level_id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT level_id, level_user_student_number_id, level_database_id, level_number, level_title, level_description, level_completed, level_score, level_attempts, level_time, level_updated" +
                " FROM " + LEVEL_TABLE + " WHERE level_id= ? LIMIT 1", new String[]{String.valueOf(level_id)});
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            Level current_level = new Level();
            current_level.setLevel_id(cursor.getInt(0));
            current_level.setLevel_user_student_number_ID(cursor.getString(1));
            current_level.setLevel_database_id(cursor.getInt(2));
            current_level.setLevel_number(cursor.getInt(3));
            current_level.setLevel_title(cursor.getString(4));
            current_level.setLevel_description(cursor.getString(5));
            current_level.setLevel_completed(cursor.getInt(6));
            current_level.setLevel_score(cursor.getInt(7));
            current_level.setLevel_attempts(cursor.getInt(8));
            current_level.setLevel_time(cursor.getInt(9));
            current_level.setLevel_updated(cursor.getInt(10));
            current_level = getPuzzlesCompletionData(current_level);
            cursor.close();
            return current_level;
        } else {
            return null;
        }
    }

    public int updateLevelData(Level current_level) {
        current_level = getPuzzlesCompletionData(current_level);
        current_level = getPuzzlesData(current_level);
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(LEVEL_COMPLETED, current_level.getLevel_completed());
        values.put(LEVEL_SCORE, current_level.updateLevel_score());
        values.put(LEVEL_ATTEMPTS, current_level.getLevel_attempts());
        values.put(LEVEL_TIME, current_level.getLevel_time());
        values.put(LEVEL_UPDATED, 1);
        int rowsAffected = db.update(LEVEL_TABLE, values, LEVEL_ID + " = ?",
                new String[]{String.valueOf(current_level.getLevel_id())});
        db.close();
        return rowsAffected;
    }

    public int resetLevelsUpdated(User current_user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(LEVEL_UPDATED, 0);
        int rowsAffected = db.update(LEVEL_TABLE, values, LEVEL_USER_STUDENT_NUMBER_ID + " = ?",
                new String[]{String.valueOf(current_user.getUser_student_number_id())});
        db.close();
        return rowsAffected;
    }

    public int resetUserUpdated(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(USER_UPDATED, 0);
        int rowsAffected = db.update(USER_TABLE, values, USER_STUDENT_NUMBER_ID + " = ?",
                new String[]{String.valueOf(user.getUser_student_number_id())});
        db.close();
        return rowsAffected;
    }
}
