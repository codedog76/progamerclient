package singletons;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;

import database.DatabaseHandler;
import models.Achievement;
import models.Level;
import models.Puzzle;
import models.User;
import models.UserAchievement;

public class DatabaseHandlerSingleton {

    private static DatabaseHandlerSingleton sInstance;
    private DatabaseHandler mDatabaseHandler;
    private String mClassName = getClass().toString();
    private String user_student_number;

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

    public long insertUser(User current_user) {
        if (current_user != null) {
            return getDatabaseHandler().insertUser(current_user);
        }
        return -1;
    }

    public long insertLevel(Level current_level) {
        if (current_level != null) {
            return getDatabaseHandler().insertLevel(current_level);
        }
        return -1;
    }

    public long insertPuzzle(Puzzle current_puzzle) {
        if (current_puzzle != null) {
            return getDatabaseHandler().insertPuzzle(current_puzzle);
        }
        return -1;
    }

    public long insertAchievement(Achievement current_achievement) {
        if (current_achievement != null) {
            return getDatabaseHandler().insertAchievement(current_achievement);
        }
        return -1;
    }

    public long insertUserAchievement(UserAchievement current_userachievement) {
        if (current_userachievement != null) {
            return getDatabaseHandler().insertUserAchievement(current_userachievement);
        }
        return -1;
    }

    public boolean checkHasAchievements() {
        return mDatabaseHandler.checkHasAchievements();
    }

    public boolean checkHasLoggedUserAchievements() {
        return mDatabaseHandler.checkHasLoggedUserAchievements();
    }

    public boolean loginUser(String user_student_number) {
        return getDatabaseHandler().loginUser(user_student_number);
    }

    public String getLoggedUserStudentNumber() {
        if (user_student_number == null) {
            user_student_number = getLoggedUser().getUser_student_number_id();
        }
        return user_student_number;
    }

    public boolean logoutUser() {
        if (getDatabaseHandler().logoutUser(getLoggedUser())) {
            sInstance = null;
            return true;
        } else {
            return false;
        }
    }

    public boolean checkUserHasLevels(User current_user) {
        return mDatabaseHandler.checkHasLevels(current_user);
    }

    public User getLoggedUser() {
        return getDatabaseHandler().getLoggedInUser();
    }

    public int deleteLevelPuzzles(Level current_level) {
        if (current_level != null) {
            return getDatabaseHandler().deleteLevelPuzzles(current_level);
        }
        return -1;
    }

    public ArrayList<Level> getLevels() {
        return getDatabaseHandler().getLevels(getLoggedUser());
    }

    public ArrayList<UserAchievement> getLoggedUserAchievements() {
        return getDatabaseHandler().getLoggedUserAchievements(getLoggedUser());
    }

    public Puzzle getNextPuzzle(Level current_level) {
        return getDatabaseHandler().getNextPuzzle(current_level);
    }

    public int updatePuzzleData(Puzzle current_puzzle) {
        if (current_puzzle != null) {
            return getDatabaseHandler().updatePuzzleData(current_puzzle);
        }
        return -1;
    }

    public Level getLevel(int level_id) {
        return getDatabaseHandler().getLevel(level_id);
    }

    public int updateLevelData(Level current_level) {
        if (current_level != null) {
            return getDatabaseHandler().updateLevelData(current_level);
        }
        return -1;
    }

    public int resetLevelsUpdated() {
        return getDatabaseHandler().resetLevelsUpdated(getLoggedUser());
    }

    public int resetUserUpdated() {
        return getDatabaseHandler().resetUserUpdated(getLoggedUser());
    }
}
