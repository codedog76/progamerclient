package singletons;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;

import database.DatabaseHandler;
import models.Level;
import models.Puzzle;
import models.User;
import models.UserLevel;

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

    public void insertOrUpdateLevel(Level level) {
        if (level != null) {
            getDatabaseHandler().insertOrUpdateLevel(level);
        }
    }

    public void insertOrUpdateUserLevel(UserLevel userLevel) {
        if (userLevel != null) {
            getDatabaseHandler().insertOrUpdateUserLevel(userLevel);
        }
    }

    public void insertOrUpdatePuzzle(Puzzle puzzle) {
        if (puzzle != null)
            getDatabaseHandler().insertOrUpdatePuzzle(puzzle);
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
        return mDatabaseHandler.checkHasLevels();
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
        return getDatabaseHandler().getLevels();
    }
}
