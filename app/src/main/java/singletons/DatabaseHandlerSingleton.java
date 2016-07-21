package singletons;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;

import database.DatabaseHandler;
import models.Level;
import models.Puzzle;
import models.User;

public class DatabaseHandlerSingleton {

    private static DatabaseHandlerSingleton sInstance;
    private DatabaseHandler mDatabaseHandler;
    private User current_user;
    private String mClassName = getClass().toString();

    public static DatabaseHandlerSingleton getInstance(Context mContext) {
        if (sInstance == null)
            sInstance = new DatabaseHandlerSingleton(mContext);
        return sInstance;
    }

    private DatabaseHandlerSingleton(Context mContext) {
        mDatabaseHandler = new DatabaseHandler(mContext);
    }

    public DatabaseHandler getDatabaseHandler() {
        return mDatabaseHandler;
    }

    public boolean insertOrUpdateUser(User user) {
        if (user != null) {
            if (getDatabaseHandler().insertOrUpdateUser(user)) {
                current_user = user;
                return true;
            }
        }
        return false;
    }

    public long insertOrUpdateLevel(Level level) {
        if (level != null) {
            long level_id = getDatabaseHandler().insertOrUpdateLevel(level);
            Log.e(mClassName, String.valueOf(level_id));
            return level_id;
        } else {
            return -1;
        }
    }

    public long insertOrUpdatePuzzle(Puzzle puzzle) {
        if (puzzle != null) {
            long puzzle_id = getDatabaseHandler().insertOrUpdatePuzzle(puzzle);
            Log.e(mClassName, String.valueOf(puzzle_id));
            return puzzle_id;
        } else {
            return -1;
        }
    }

    public int getUserOverallScore() {
        return getDatabaseHandler().getLoggedInUserOverallScore(getLoggedUser());
    }

    public int getUserOverallAttempts() {
        return getDatabaseHandler().getLoggedInUserOverallAttempts(getLoggedUser());
    }

    public int getUserOverallTime() {
        return getDatabaseHandler().getLoggedInUserOverallTime(getLoggedUser());
    }

    public boolean loginUser(String user_student_number) {
        return getDatabaseHandler().loginUser(user_student_number);
    }

    public boolean logoutUser() {
        if (getDatabaseHandler().logoutUser(getLoggedUser())) {
            sInstance = null;
            return true;
        } else {
            return false;
        }
    }

    public boolean checkHasLevels() {
        return mDatabaseHandler.checkHasLevels(getLoggedUser());
    }

    public User getLoggedUser() {
        if (current_user == null) {
            current_user = getDatabaseHandler().getLoggedInUser();
            return current_user;
        } else {
            return current_user;
        }
    }

    public ArrayList<Level> getLevels() {
        return getDatabaseHandler().getLevels(getLoggedUser());
    }

    public Puzzle getNextPuzzle(Level level){
        return getDatabaseHandler().getNextPuzzle(getLoggedUser(), level);
    }

    public boolean setPuzzleData(Puzzle puzzle) {
        return getDatabaseHandler().setPuzzleData(puzzle);
    }
}
