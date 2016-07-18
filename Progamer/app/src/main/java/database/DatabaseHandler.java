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
    private static final String LEVEL_TITLE = "level_title";
    private static final String LEVEL_STUDENT_NUMBER = "level_student_number"; //fk
    private static final String CREATE_TABLE_LEVEL = "create table "
            + LEVEL_TABLE
            + "("
            + LEVEL_ID + " integer not null primary key, "
            + LEVEL_TITLE + " text not null, "
            + LEVEL_STUDENT_NUMBER + " text not null, "
            + "foreign key (" + LEVEL_STUDENT_NUMBER + ") references " + USER_TABLE + "(" + USER_STUDENT_NUMBER_ID + ")"
            + ");";

    //table puzzle
    private static final String PUZZLE_TABLE = "puzzle";
    private static final String PUZZLE_ID = "puzzle_id"; //pk
    private static final String PUZZLE_COMPLETED = "puzzle_completed";
    private static final String PUZZLE_ATTEMPTS = "puzzle_attempts";
    private static final String PUZZLE_TIME = "puzzle_time";
    private static final String PUZZLE_LEVEL_ID = "puzzle_level_id"; //fk
    private static final String CREATE_TABLE_PUZZLE = "create table "
            + PUZZLE_TABLE
            + "("
            + PUZZLE_ID + " integer not null primary key, "
            + PUZZLE_COMPLETED + " integer not null, "
            + PUZZLE_ATTEMPTS + " integer not null, "
            + PUZZLE_TIME + " integer not null, "
            + PUZZLE_LEVEL_ID + " integer not null, "
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
        values.put(USER_STUDENT_NUMBER_ID, user.getUser_student_number());
        values.put(USER_NICKNAME, user.getUser_nickname());
        values.put(USER_IS_PRIVATE, user.getUser_is_private());
        values.put(USER_AVATAR, user.getUser_avatar());
        values.put(USER_LOGGED_IN, user.getUser_logged_in());
        long user_id = db.insertWithOnConflict(USER_TABLE, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        db.close();
        return user_id != -1;
    }

    public void insertOrUpdateLevel(User current_user, Level level) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(LEVEL_ID, level.getLevel_id());
        values.put(LEVEL_TITLE, level.getLevel_title());
        values.put(LEVEL_STUDENT_NUMBER, current_user.getUser_student_number());
        db.insertWithOnConflict(LEVEL_TABLE, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        db.close();
    }

    public void insertOrUpdatePuzzle(Puzzle puzzle) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(PUZZLE_ID, puzzle.getPuzzle_id());
        values.put(PUZZLE_COMPLETED, puzzle.getPuzzle_completed());
        values.put(PUZZLE_ATTEMPTS, puzzle.getPuzzle_attempts());
        values.put(PUZZLE_TIME, puzzle.getPuzzle_time());
        values.put(PUZZLE_LEVEL_ID, puzzle.getPuzzle_level_id());
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
            user.setUser_student_number(cursor.getString(0));
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
                " WHERE level_student_number= ? AND puzzle_level_id=level_id", new String[]{current_user.getUser_student_number()});
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
                " WHERE level_student_number= ? AND puzzle_level_id=level_id", new String[]{current_user.getUser_student_number()});
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
                " WHERE level_student_number= ? AND puzzle_level_id=level_id", new String[]{current_user.getUser_student_number()});
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
                new String[]{current_user.getUser_student_number()});
        return rowsAffected == 1;
    }

    public ArrayList<Level> getUserLevels(User current_user) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(LEVEL_TABLE, new String[]{LEVEL_ID, LEVEL_TITLE}, LEVEL_STUDENT_NUMBER + "=?",
                new String[]{current_user.getUser_student_number()}, null, null, null, null);
        ArrayList<Level> userLevels = new ArrayList<>();
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                Level level = new Level();
                level.setLevel_id(Integer.parseInt(cursor.getString(0)));
                level.setLevel_title(cursor.getString(1));
                userLevels.add(level);
                cursor.moveToNext();
            }
        }
        cursor.close();
        return userLevels;
    }
}
