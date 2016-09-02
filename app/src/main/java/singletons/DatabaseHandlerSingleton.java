package singletons;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import database.DatabaseHandler;
import models.Achievement;
import models.Level;
import models.Puzzle;
import models.User;
import models.UserAchievement;

public class DatabaseHandlerSingleton {

    private static DatabaseHandlerSingleton sDatabaseHandlerSingletonInstance;
    private DatabaseHandler mDatabaseHandler;
    private String mClassName = getClass().toString();
    private String mUserStudentNumber;

    public static DatabaseHandlerSingleton getInstance(Context mContext) {
        if (sDatabaseHandlerSingletonInstance == null)
            sDatabaseHandlerSingletonInstance = new DatabaseHandlerSingleton(mContext);
        return sDatabaseHandlerSingletonInstance;
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

    public boolean checkHasLoggedUserAchievements(User user) {
        return mDatabaseHandler.checkHasLoggedUserAchievements(user);
    }

    public boolean loginUser(String user_student_number) {
        return getDatabaseHandler().loginUser(user_student_number);
    }

    public String getLoggedUserStudentNumber() {
        if (mUserStudentNumber == null) {
            mUserStudentNumber = getLoggedUser().getUser_student_number_id();
        }
        return mUserStudentNumber;
    }

    public boolean logoutUser() {
        if (getDatabaseHandler().logoutUser(getLoggedUser())) {
            sDatabaseHandlerSingletonInstance = null;
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

    public Boolean checkHasCompletedALevel() {
        return getDatabaseHandler().checkHasCompletedALevel(getLoggedUser());
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

    //achievements

    public void updateAchievement(String achievementTarget, int amount) {
        getDatabaseHandler().updateUserAchievement(achievementTarget, getLoggedUser(), amount);
    }

    public List<UserAchievement> getUserAchievementsNotifications() {
        return getDatabaseHandler().getUserAchievementsNotifications(getLoggedUser());
    }

    public int setUserAchievementNotified(int achievement_id) {
        return getDatabaseHandler().setUserAchievementNotified(getLoggedUser(), achievement_id);
    }

    public List<UserAchievement> getUserAchievementsUpdated() {
        return getDatabaseHandler().getUserAchievementsUpdated(getLoggedUser());
    }

    public int updateUserAchievementsUpdated(UserAchievement userAchievement) {
        return getDatabaseHandler().updateUserAchievementsUpdated(userAchievement);
    }
}
