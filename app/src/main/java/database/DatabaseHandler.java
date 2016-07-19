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
import models.UserLevel;

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
    private static final String LEVEL_NUMBER = "level_number";
    private static final String LEVEL_TITLE = "level_title";
    private static final String LEVEL_DESCRIPTION = "level_description";
    private static final String CREATE_TABLE_LEVEL = "create table "
            + LEVEL_TABLE
            + "("
            + LEVEL_ID + " integer not null primary key AUTOINCREMENT, "
            + LEVEL_NUMBER + " integer not null, "
            + LEVEL_TITLE + " text not null, "
            + LEVEL_DESCRIPTION + " text not null"
            + ");";

    //table userlevel
    private static final String USERLEVEL_TABLE = "userlevel";
    private static final String USERLEVEL_ID = "userlevel_id"; //pk
    private static final String USERLEVEL_USER_STUDENT_NUMBER_ID = "userlevel_user_student_number_ID"; //fk
    private static final String USERLEVEL_LEVEL_ID = "userlevel_level_id"; //fk
    private static final String USERLEVEL_COMPLETED = "userlevel_completed";
    private static final String USERLEVEL_SCORE = "userlevel_score";
    private static final String USERLEVEL_ATTEMPTS = "userlevel_attempts";
    private static final String USERLEVEL_TIME = "userlevel_time";
    private static final String CREATE_TABLE_USERLEVEL = "create table "
            + USERLEVEL_TABLE
            + "("
            + USERLEVEL_ID + " integer not null primary key, "
            + USERLEVEL_USER_STUDENT_NUMBER_ID + " text not null, "
            + USERLEVEL_LEVEL_ID + " integer not null, "
            + USERLEVEL_COMPLETED + " integer not null, "
            + USERLEVEL_SCORE + " integer not null, "
            + USERLEVEL_ATTEMPTS + " integer not null, "
            + USERLEVEL_TIME + " integer not null, "
            + "foreign key (" + USERLEVEL_USER_STUDENT_NUMBER_ID + ") references " + USER_TABLE + "(" + USER_STUDENT_NUMBER_ID + "), "
            + "foreign key (" + USERLEVEL_LEVEL_ID + ") references " + LEVEL_TABLE + "(" + LEVEL_ID + ")"
            + ");";

    //table puzzle
    private static final String PUZZLE_TABLE = "puzzle";
    private static final String PUZZLE_ID = "puzzle_id"; //pk
    private static final String PUZZLE_LEVEL_ID = "puzzle_level_id"; //fk
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
            + PUZZLE_ID + " integer not null primary key, "
            + PUZZLE_LEVEL_ID + " integer not null, "
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
        database.execSQL(CREATE_TABLE_USERLEVEL);
        database.execSQL(CREATE_TABLE_PUZZLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + USER_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + LEVEL_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + USERLEVEL_TABLE);
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
        values.put(LEVEL_ID, level.getLevel_id());
        values.put(LEVEL_NUMBER, level.getLevel_number());
        values.put(LEVEL_TITLE, level.getLevel_title());
        values.put(LEVEL_DESCRIPTION, level.getLevel_description());
        db.insertWithOnConflict(LEVEL_TABLE, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        db.close();
    }

    public void insertOrUpdateUserLevel(UserLevel userLevel) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(USERLEVEL_ID, userLevel.getUserlevel_id());
        values.put(USERLEVEL_USER_STUDENT_NUMBER_ID, userLevel.getUserlevel_student_number_id());
        values.put(USERLEVEL_LEVEL_ID, userLevel.getUserlevel_level_id());
        values.put(USERLEVEL_COMPLETED, userLevel.getUserlevel_completed());
        values.put(USERLEVEL_SCORE, userLevel.getUserlevel_score());
        values.put(USERLEVEL_ATTEMPTS, userLevel.getUserlevel_attempts());
        values.put(USERLEVEL_TIME, userLevel.getUserlevel_time());
        db.insertWithOnConflict(PUZZLE_TABLE, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        db.close();
    }

    public void insertOrUpdatePuzzle(Puzzle puzzle) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(PUZZLE_ID, puzzle.getPuzzle_id());
        values.put(PUZZLE_LEVEL_ID, puzzle.getPuzzle_level_id());
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
    }

    public ArrayList<Level> getLevels() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT level_id, level_number, level_title, level_description FROM "+ LEVEL_TABLE, null);
        ArrayList<Level> level_list = new ArrayList<>();
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                Level level = new Level();
                level.setLevel_id(Integer.parseInt(cursor.getString(0)));
                level.setLevel_number(Integer.parseInt(cursor.getString(1)));
                level.setLevel_title(cursor.getString(2));
                level.setLevel_description(cursor.getString(3));
                level_list.add(level);
                cursor.moveToNext();
            }
        }
        cursor.close();
        return level_list;
    }
}
