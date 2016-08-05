package singletons;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import models.Level;
import models.Puzzle;
import models.UserAchievement;

public class AchievementHandlerSingleton {

    private static AchievementHandlerSingleton sAchievementHandlerSingletonInstance;
    private DatabaseHandlerSingleton mDatabaseHandlerSingleton;
    private Context mContext;

    public static AchievementHandlerSingleton getInstance(Context context) {
        if (sAchievementHandlerSingletonInstance == null)
            sAchievementHandlerSingletonInstance = new AchievementHandlerSingleton(context);
        return sAchievementHandlerSingletonInstance;
    }

    private AchievementHandlerSingleton(Context context) {
        mContext = context;
        mDatabaseHandlerSingleton = DatabaseHandlerSingleton.getInstance(mContext);
    }

    public void levelWasComplete(Level current_level) {
        Log.e("score", current_level.getLevel_score()+"");
        Log.e("attempts", current_level.getLevel_attempts()+"");
        Log.e("time", current_level.getLevel_time()+"");
        levelScore(current_level);
        levelAttempts(current_level);
        levelTime(current_level);
    }

    public void puzzleWasComplete(Level current_level) {
        puzzleComplete(current_level);
    }

    public List<UserAchievement> getUserAchievementsNotifications() {
        return mDatabaseHandlerSingleton.getUserAchievementsNotifications();
    }

    private void puzzleComplete(Level current_level) {
        String achievementTarget = "<level_id>" + current_level.getLevel_database_id() + "</level_id><puzzle><complete>";
        mDatabaseHandlerSingleton.updateAchievement(achievementTarget, current_level.getLevel_puzzles_completed());
    }

    private void levelScore(Level current_level) {
        String achievementTarget = "<level_id>" + current_level.getLevel_database_id() + "</level_id><score>";
        mDatabaseHandlerSingleton.updateAchievement(achievementTarget, current_level.getLevel_score());
    }

    private void levelAttempts(Level current_level) {
        String achievementTarget = "<level_id>" + current_level.getLevel_database_id() + "</level_id><attempts>";
        mDatabaseHandlerSingleton.updateAchievement(achievementTarget, current_level.getLevel_attempts());
    }

    private void levelTime(Level current_level) {
        String achievementTarget = "<level_id>" + current_level.getLevel_database_id() + "</level_id><time>";
        mDatabaseHandlerSingleton.updateAchievement(achievementTarget, current_level.getLevel_time());
    }
}
