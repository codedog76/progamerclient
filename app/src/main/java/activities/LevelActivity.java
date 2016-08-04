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
    private TextView mTextLevelTitle, mTextLevelDescription, mTextUserMyScoreTitle, mTextUserScoreValue1, mTextUserScoreValue2,
            mTextUserScoreTitle, mTextUserAttemptsTitle, mTextUserAttemptsValue, mTextUserTimeTitle, mTextUserTimeValue, mTextAverageUserScoreTitle,
            mTextAverageScoreValue1, mTextAverageScoreValue2, mTextAverageScoreTitle, mTextAverageAttemptsTitle, mTextAverageAttemptsValue, mTextAverageTimeTitle, mTextAverageTimeValue;
    private ProgressBar mProgressBarUser, mProgressBarAverage;
    private ImageView mImageUserTrophy, mImageAverageTrophy;
    private Button mButtonNext, mButtonTryAgain;
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
        fetchAverageUserData();
        fetchMyScoreData();
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
        loadData();
        AchievementHandlerSingleton achievementHandlerSingleton = AchievementHandlerSingleton.getInstance(this);
        mUserAchievementList = achievementHandlerSingleton.getUserAchievementsNotifications();
        if (mUserAchievementList.size() > 0) {
            mAchievementHandler.postDelayed(mAchievementRunnable, 1000);
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

    private void fetchMyScoreData() {
        mTextUserScoreValue1.setText(getString(R.string.decimal_value, mCurrentLevel.getLevel_score()));
        mTextUserScoreValue2.setText(getString(R.string.decimal_value, mCurrentLevel.getLevel_score()));
        mTextUserAttemptsValue.setText(getString(R.string.decimal_value, mCurrentLevel.getLevel_attempts()));
        mTextUserTimeValue.setText(getString(R.string.decimal_value, mCurrentLevel.getLevel_time()));
        String levelTrophy = mCurrentLevel.getLevel_trophy();
        int id = getResources().getIdentifier(levelTrophy, "drawable", getPackageName());
        Drawable drawable = ContextCompat.getDrawable(this, id);
        mImageUserTrophy.setImageDrawable(drawable);
        if (mCurrentLevel.getLevel_score() >= 1 && mCurrentLevel.getLevel_score() < 50) {
            mProgressBarUser.setMax(50);
        }
        if (mCurrentLevel.getLevel_score() >= 50 && mCurrentLevel.getLevel_score() < 100) {
            mProgressBarUser.setMax(100);
        }
        if (mCurrentLevel.getLevel_score() >= 100 && mCurrentLevel.getLevel_score() <= 150) {
            mProgressBarUser.setMax(150);
        }
        mProgressBarUser.setProgress(mCurrentLevel.getLevel_score());
    }

    private void fetchAverageUserData() {
        mLinearFailedRetry.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.VISIBLE);
        mNetworkManagerSingleton.getAverageLevelDataJsonRequest(mCurrentLevel, new NetworkManagerSingleton.ObjectResponseListener<Level>() {
            @Override
            public void getResult(Level averageLevel, Boolean response, String message) {
                if (response) {
                    mProgressBar.setVisibility(View.GONE);
                    mLinearFailedRetry.setVisibility(View.GONE);
                    mLinearAverageUserContainer.setVisibility(View.VISIBLE);
                    mTextAverageScoreValue1.setText(getString(R.string.decimal_value, averageLevel.getLevel_score()));
                    mTextAverageScoreValue2.setText(getString(R.string.decimal_value, averageLevel.getLevel_score()));
                    mTextAverageAttemptsValue.setText(getString(R.string.decimal_value, averageLevel.getLevel_attempts()));
                    mTextAverageTimeValue.setText(getString(R.string.decimal_value, averageLevel.getLevel_time()));
                    String levelTrophy = mCurrentLevel.getLevel_trophy();
                    int id = getResources().getIdentifier(levelTrophy, "drawable", getPackageName());
                    Drawable drawable = ContextCompat.getDrawable(getApplicationContext(), id);
                    mImageAverageTrophy.setImageDrawable(drawable);
                    if (mCurrentLevel.getLevel_score() >= 1 && averageLevel.getLevel_score() < 50) {
                        mProgressBarUser.setMax(50);
                    }
                    if (mCurrentLevel.getLevel_score() >= 50 && averageLevel.getLevel_score() < 100) {
                        mProgressBarAverage.setMax(100);
                    }
                    if (mCurrentLevel.getLevel_score() >= 100 && averageLevel.getLevel_score() <= 150) {
                        mProgressBarUser.setMax(150);
                    }
                    mProgressBarAverage.setProgress(averageLevel.getLevel_score());
                } else {
                    mLinearFailedRetry.setVisibility(View.VISIBLE);
                    mProgressBar.setVisibility(View.GONE);
                    mLinearAverageUserContainer.setVisibility(View.INVISIBLE);
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
        ;
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
                    mNetworkManagerSingleton.getPuzzlesJsonRequest(mCurrentLevel, new NetworkManagerSingleton.BooleanResponseListener() {
                        @Override
                        public void getResult(Boolean response, String message) {
                            mProgressDialog.dismiss();
                            loadPuzzleActivity();
                        }
                    });
                } else loadPuzzleActivity();
            }
        });
        mButtonTryAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchAverageUserData();
            }
        });
        mImageUserTrophy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showExtraInformationDialog();
            }
        });
        mImageAverageTrophy.setOnClickListener(new View.OnClickListener() {
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
        mTextUserMyScoreTitle = (TextView) findViewById(R.id.text_user_my_score_title);
        mTextUserScoreValue1 = (TextView) findViewById(R.id.text_user_score_value_1);
        mTextUserScoreValue2 = (TextView) findViewById(R.id.text_user_score_value_2);
        mTextUserScoreTitle = (TextView) findViewById(R.id.text_user_score_title);
        mTextUserAttemptsTitle = (TextView) findViewById(R.id.text_user_attempts_title);
        mTextUserAttemptsValue = (TextView) findViewById(R.id.text_user_attempts_value);
        mTextUserTimeTitle = (TextView) findViewById(R.id.text_user_time_title);
        mTextUserTimeValue = (TextView) findViewById(R.id.text_user_time_value);
        mTextAverageUserScoreTitle = (TextView) findViewById(R.id.text_average_user_score_title);
        mTextAverageScoreValue1 = (TextView) findViewById(R.id.text_average_score_value_1);
        mTextAverageScoreValue2 = (TextView) findViewById(R.id.text_average_score_value_2);
        mTextAverageScoreTitle = (TextView) findViewById(R.id.text_average_score_title);
        mTextAverageAttemptsTitle = (TextView) findViewById(R.id.text_average_attempts_title);
        mTextAverageAttemptsValue = (TextView) findViewById(R.id.text_average_attempts_value);
        mTextAverageTimeTitle = (TextView) findViewById(R.id.text_average_time_title);
        mTextAverageTimeValue = (TextView) findViewById(R.id.text_average_time_value);
        mProgressBarUser = (ProgressBar) findViewById(R.id.progress_bar_user);
        mProgressBarAverage = (ProgressBar) findViewById(R.id.progress_bar_average);
        mImageUserTrophy = (ImageView) findViewById(R.id.image_user_trophy);
        mImageAverageTrophy = (ImageView) findViewById(R.id.image_average_trophy);
        mLinearFailedRetry = (LinearLayout) findViewById(R.id.linear_failed_retry);
        mLinearAverageUserContainer = (LinearLayout) findViewById(R.id.linear_average_user_container);
        mButtonNext = (Button) findViewById(R.id.button_next);
        mButtonTryAgain = (Button) findViewById(R.id.button_try_again);
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
        mTextUserMyScoreTitle.setTypeface(Roboto_Regular);
        mTextUserScoreValue1.setTypeface(Roboto_Regular);
        mTextUserScoreValue2.setTypeface(Roboto_Regular);
        mTextUserScoreTitle.setTypeface(Roboto_Regular);
        mTextUserAttemptsTitle.setTypeface(Roboto_Regular);
        mTextUserAttemptsValue.setTypeface(Roboto_Regular);
        mTextUserTimeTitle.setTypeface(Roboto_Regular);
        mTextUserTimeValue.setTypeface(Roboto_Regular);
        mTextAverageUserScoreTitle.setTypeface(Roboto_Regular);
        mTextAverageScoreValue1.setTypeface(Roboto_Regular);
        mTextAverageScoreValue2.setTypeface(Roboto_Regular);
        mTextAverageScoreTitle.setTypeface(Roboto_Regular);
        mTextAverageAttemptsTitle.setTypeface(Roboto_Regular);
        mTextAverageAttemptsValue.setTypeface(Roboto_Regular);
        mTextAverageTimeTitle.setTypeface(Roboto_Regular);
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
