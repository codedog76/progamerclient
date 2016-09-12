package activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.progamer.R;

import java.util.ArrayList;
import java.util.List;

import models.Level;
import models.UserAchievement;
import singletons.AchievementHandlerSingleton;
import singletons.DatabaseHandlerSingleton;
import singletons.NetworkManagerSingleton;

public class LevelActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private Level mCurrentLevel;
    private DatabaseHandlerSingleton mDatabaseHandlerSingleton;
    private NetworkManagerSingleton mNetworkManagerSingleton;
    private ProgressDialog mProgressDialog;
    private LinearLayout mLinearFailedRetry, mLinearAverageUserContainer, mLinearAchievementPopup;
    private TextView mTextLevelTitle, mTextLevelDescription, mTextPerformanceTitle, mTextUserScoreTitle,
            mTextUser, mTextAverage, mTextScore, mTextAttempts, mTextTime,
            mTextUserScoreValue, mTextUserAttemptsValue, mTextUserTimeValue,
            mTextAverageScoreValue, mTextAverageAttemptsValue, mTextAverageTimeValue;
    private RelativeLayout mRelativeAverageScore, mRelativeAverageAttempts, mRelativeAverageTime;
    private ProgressBar mProgressAverageScore, mProgressAverageAttempts, mProgressAverageTime;
    private ImageView mImageUserTrophy;
    private Button mButtonNext;
    private String mClassName = getClass().toString();
    private int mLevelId;
    private final Handler mAchievementHandler = new Handler();
    private final Handler mAchievementCloseHandler = new Handler();
    private Animation mAnimLeftRight, mAnimRightLeft;
    private int mAchievementDisplayCounter = 0;
    private TextView mTextTitle, mTextDescription, mTextProgressNumeric;
    private ProgressBar mProgressBar;
    private ProgressBar mProgressBarAchievement;
    private List<UserAchievement> mUserAchievementList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level);
        assignProgressDialog();
        assignViews();
        assignFonts();
        assignSingletons();
        assignListeners();
        assignActionBar();
        getBundle();
        fetchAverageData();
        fetchUserData();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mNetworkManagerSingleton.cancelJSONRequest("downloadPuzzlesJSONRequest");
        mNetworkManagerSingleton.cancelJSONRequest("getAverageLevelDataJsonRequest");
    }

    @Override
    protected void onResume() {
        super.onResume();
        mCurrentLevel = mDatabaseHandlerSingleton.getLevel(mLevelId);
        fetchUserData();
        loadData();
        AchievementHandlerSingleton achievementHandlerSingleton = AchievementHandlerSingleton.getInstance(this);
        mUserAchievementList = achievementHandlerSingleton.getUserAchievementsNotifications();
        if (mUserAchievementList.size() > 0) {
            mAchievementHandler.postDelayed(mAchievementRunnable, 500);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mProgressDialog.dismiss();
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private Runnable mAchievementRunnable = new Runnable() {
        @Override
        public void run() {
            if (mAchievementDisplayCounter <= mUserAchievementList.size() - 1) {
                UserAchievement userAchievement = mUserAchievementList.get(mAchievementDisplayCounter);
                mTextTitle.setText(userAchievement.getAchievement_title());
                mTextDescription.setText(userAchievement.getAchievement_description());
                mTextProgressNumeric.setText(getString(R.string.string_out_of, userAchievement.getUserachievement_progress(), userAchievement.getAchievement_total()));
                mProgressBarAchievement.setMax(userAchievement.getAchievement_total());
                mProgressBarAchievement.setProgress(userAchievement.getUserachievement_progress());
                mLinearAchievementPopup.startAnimation(mAnimRightLeft);
                mLinearAchievementPopup.setVisibility(View.VISIBLE);
                mAchievementDisplayCounter++;
                mAchievementCloseHandler.postDelayed(mAchievementCloseRunnable, 5000);
                mAchievementHandler.postDelayed(this, 7000);
                mDatabaseHandlerSingleton.setUserAchievementNotified(userAchievement.getUserachievement_id());
            }
        }
    };

    private Runnable mAchievementCloseRunnable = new Runnable() {
        @Override
        public void run() {
            mLinearAchievementPopup.startAnimation(mAnimLeftRight);
            mLinearAchievementPopup.setVisibility(View.GONE);

        }
    };

    private void fetchUserData() {
        mTextUserScoreTitle.setText(getString(R.string.decimal_value, mCurrentLevel.getLevel_score()));
        mTextUserScoreValue.setText(getString(R.string.decimal_value, mCurrentLevel.getLevel_score()));
        mTextUserAttemptsValue.setText(getString(R.string.decimal_value, mCurrentLevel.getLevel_attempts()));
        mTextUserTimeValue.setText(getString(R.string.decimal_value, mCurrentLevel.getLevel_time()));
        String levelTrophy = mCurrentLevel.getLevel_trophy();
        int id = getResources().getIdentifier(levelTrophy, "drawable", getPackageName());
        Drawable drawable = ContextCompat.getDrawable(this, id);
        mImageUserTrophy.setImageDrawable(drawable);
    }

    private void fetchAverageData() {
        mRelativeAverageScore.setVisibility(View.VISIBLE);
        mRelativeAverageAttempts.setVisibility(View.VISIBLE);
        mRelativeAverageTime.setVisibility(View.VISIBLE);
        mProgressAverageScore.setVisibility(View.VISIBLE);
        mProgressAverageAttempts.setVisibility(View.VISIBLE);
        mProgressAverageTime.setVisibility(View.VISIBLE);
        mNetworkManagerSingleton.getAverageLevelDataJsonRequest(mCurrentLevel, new NetworkManagerSingleton.ObjectResponseListener<Level>() {
            @Override
            public void getResult(Level averageLevel, Boolean response, String message) {
                if (response) {
                    mProgressAverageScore.setVisibility(View.GONE);
                    mProgressAverageAttempts.setVisibility(View.GONE);
                    mProgressAverageTime.setVisibility(View.GONE);
                    mTextAverageScoreValue.setVisibility(View.VISIBLE);
                    mTextAverageAttemptsValue.setVisibility(View.VISIBLE);
                    mTextAverageTimeValue.setVisibility(View.VISIBLE);
                    mTextAverageScoreValue.setText(getString(R.string.decimal_value, averageLevel.getLevel_score()));
                    mTextAverageAttemptsValue.setText(getString(R.string.decimal_value, averageLevel.getLevel_attempts()));
                    mTextAverageTimeValue.setText(getString(R.string.decimal_value, averageLevel.getLevel_time()));
                } else {
                    mProgressAverageScore.setVisibility(View.GONE);
                    mProgressAverageAttempts.setVisibility(View.GONE);
                    mProgressAverageTime.setVisibility(View.GONE);
                    mTextAverageScoreValue.setVisibility(View.VISIBLE);
                    mTextAverageAttemptsValue.setVisibility(View.VISIBLE);
                    mTextAverageTimeValue.setVisibility(View.VISIBLE);
                    mTextAverageScoreValue.setText("N/A");
                    mTextAverageAttemptsValue.setText("N/A");
                    mTextAverageTimeValue.setText("N/A");
                }
            }
        });
    }

    private void showExtraInformationDialog() {
        new AlertDialog.Builder(LevelActivity.this)
                .setTitle("You earn different trophies as follows")
                .setMessage("Bronze: A score between 1 and 49.\nSilver: A score between 50 and 99.\nGold: A score of 100 or higher.")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    }
                }).show();
    }

    private void assignProgressDialog() {
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("Loading..");
        mProgressDialog.setCancelable(false);
        mProgressDialog.setCanceledOnTouchOutside(false);
    }

    private void loadData() {
        setTitle("Level " + mCurrentLevel.getLevel_number());
        mTextLevelTitle.setText(mCurrentLevel.getLevel_title());
        mTextLevelDescription.setText(mCurrentLevel.getLevel_description());
        if (mCurrentLevel.getLevel_puzzles_completed() == 0) {
            if (mCurrentLevel.getPuzzles_completed())
                mButtonNext.setText(getString(R.string.string_try_again));
            else mButtonNext.setText(getString(R.string.string_start));
        } else mButtonNext.setText(getString(R.string.string_continue));
    }

    private void assignListeners() {
        mButtonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCurrentLevel.getPuzzles_completed()) {
                    mProgressDialog.show();
                    downloadPuzzles();
                } else loadPuzzleActivity();
            }
        });
        mImageUserTrophy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showExtraInformationDialog();
            }
        });
        mLinearAchievementPopup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), UserProfileActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("user_student_number", mDatabaseHandlerSingleton.getLoggedUser()
                        .getUser_student_number_id());
                intent.putExtras(bundle);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });
    }

    private void downloadPuzzles() {
        mNetworkManagerSingleton.getPuzzlesJsonRequest(mCurrentLevel, new NetworkManagerSingleton.BooleanResponseListener() {
            @Override
            public void getResult(Boolean response, String message) {
                if (response) {
                    mProgressDialog.dismiss();
                    loadPuzzleActivity();
                } else {
                    new AlertDialog.Builder(LevelActivity.this)
                            .setMessage("Puzzle data failed to download. " + message + " Do you wish to try again?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    downloadPuzzles();
                                }
                            })
                            .setNegativeButton("No", null).show();
                }
            }
        });
    }

    private void loadPuzzleActivity() {
        Intent intent = new Intent(getApplicationContext(), PuzzleActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_HISTORY);
        Bundle bundle = new Bundle();
        bundle.putInt("level_id", mCurrentLevel.getLevel_id());
        intent.putExtras(bundle);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    private void assignSingletons() {
        mDatabaseHandlerSingleton = DatabaseHandlerSingleton.getInstance(this);
        mNetworkManagerSingleton = NetworkManagerSingleton.getInstance(this);
    }

    private void getBundle() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            mLevelId = bundle.getInt("level_id", -1);
            if (mLevelId != -1) mCurrentLevel = mDatabaseHandlerSingleton.getLevel(mLevelId);
            else {
                Log.e(mClassName, "Level data missing");
                finish();
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        }
    }

    private void assignViews() {
        mToolbar = (Toolbar) findViewById(R.id.app_actionbar);
        mTextLevelTitle = (TextView) findViewById(R.id.text_level_title);
        mTextLevelDescription = (TextView) findViewById(R.id.text_level_description);
        mTextLevelTitle = (TextView) findViewById(R.id.text_level_title);
        mTextLevelDescription = (TextView) findViewById(R.id.text_level_description);
        mTextPerformanceTitle = (TextView) findViewById(R.id.text_performance_title);
        mTextUserScoreTitle = (TextView) findViewById(R.id.text_user_score_title);
        mTextUser = (TextView) findViewById(R.id.text_user);
        mTextAverage = (TextView) findViewById(R.id.text_average);
        mTextScore = (TextView) findViewById(R.id.text_score);
        mTextAttempts = (TextView) findViewById(R.id.text_attempts);
        mTextTime = (TextView) findViewById(R.id.text_time);
        mTextUserScoreValue = (TextView) findViewById(R.id.text_user_score_value);
        mTextUserAttemptsValue = (TextView) findViewById(R.id.text_user_attempts_value);
        mTextUserTimeValue = (TextView) findViewById(R.id.text_user_time_value);
        mTextAverageScoreValue = (TextView) findViewById(R.id.text_average_score_value);
        mTextAverageAttemptsValue = (TextView) findViewById(R.id.text_average_attempts_value);
        mTextAverageTimeValue = (TextView) findViewById(R.id.text_average_time_value);
        mRelativeAverageScore = (RelativeLayout) findViewById(R.id.relative_average_score);
        mRelativeAverageAttempts = (RelativeLayout) findViewById(R.id.relative_average_attempts);
        mRelativeAverageTime = (RelativeLayout) findViewById(R.id.relative_average_time);
        mProgressAverageScore = (ProgressBar) findViewById(R.id.progress_average_score);
        mProgressAverageAttempts = (ProgressBar) findViewById(R.id.progress_average_attempts);
        mProgressAverageTime = (ProgressBar) findViewById(R.id.progress_average_time);
        mImageUserTrophy = (ImageView) findViewById(R.id.image_user_trophy);
        mButtonNext = (Button) findViewById(R.id.button_next);
        mLinearAchievementPopup = (LinearLayout) findViewById(R.id.linear_achievement_popup);
        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);
        mAnimLeftRight = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.left_right);
        mAnimRightLeft = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.right_left);
        mTextTitle = (TextView) findViewById(R.id.text_title);
        mTextDescription = (TextView) findViewById(R.id.text_description);
        mProgressBarAchievement = (ProgressBar) findViewById(R.id.progress_bar_achievement);
        mTextProgressNumeric = (TextView) findViewById(R.id.text_progress_numeric);
    }

    private void assignFonts() {
        Typeface Roboto_Medium = Typeface.createFromAsset(getAssets(), "Roboto-Medium.ttf");
        Typeface Roboto_Regular = Typeface.createFromAsset(getAssets(), "Roboto-Regular.ttf");
        mTextLevelTitle.setTypeface(Roboto_Regular);
        mTextLevelDescription.setTypeface(Roboto_Regular);
        mTextLevelTitle.setTypeface(Roboto_Regular);
        mTextLevelDescription.setTypeface(Roboto_Regular);
        mTextPerformanceTitle.setTypeface(Roboto_Regular);
        mTextUserScoreTitle.setTypeface(Roboto_Regular);
        mTextUser.setTypeface(Roboto_Regular, Typeface.BOLD);
        mTextAverage.setTypeface(Roboto_Regular, Typeface.BOLD);
        mTextScore.setTypeface(Roboto_Regular, Typeface.BOLD);
        mTextAttempts.setTypeface(Roboto_Regular, Typeface.BOLD);
        mTextTime.setTypeface(Roboto_Regular, Typeface.BOLD);
        mTextUserScoreValue.setTypeface(Roboto_Regular);
        mTextUserAttemptsValue.setTypeface(Roboto_Regular);
        mTextUserTimeValue.setTypeface(Roboto_Regular);
        mTextAverageScoreValue.setTypeface(Roboto_Regular);
        mTextAverageAttemptsValue.setTypeface(Roboto_Regular);
        mTextAverageTimeValue.setTypeface(Roboto_Regular);
        mButtonNext.setTypeface(Roboto_Medium);
        mTextTitle.setTypeface(Roboto_Regular);
        mTextDescription.setTypeface(Roboto_Regular);
        mTextProgressNumeric.setTypeface(Roboto_Regular);
    }

    private void assignActionBar() {
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        else {
            Log.e(mClassName, "getSupportActionBar null");
            finish();
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        }
    }
}
