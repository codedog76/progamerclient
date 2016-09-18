package activities;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.progamer.R;
import com.romainpiel.shimmer.Shimmer;
import com.romainpiel.shimmer.ShimmerTextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import fragments.puzzles.DragListViewFragment;
import fragments.puzzles.MultipleChoiceListFragment;
import fragments.puzzles.SingleChoiceListFragment;
import fragments.puzzles.TrueFalseFragment;
import models.Level;
import models.Puzzle;
import puzzle.PuzzleCodeBuilder;
import singletons.AchievementHandlerSingleton;
import singletons.DatabaseHandlerSingleton;


public class PuzzleActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private PuzzleCodeBuilder mCurrentPuzzleCodeBuilder;
    private DragListViewFragment mDragListViewFragment;
    private MultipleChoiceListFragment mMultipleChoiceListFragment;
    private SingleChoiceListFragment mSingleChoiceListFragment;
    private TrueFalseFragment mTrueFalseFragment;
    private DatabaseHandlerSingleton mDatabaseHandlerSingleton;
    private AchievementHandlerSingleton mAchievementHandlerSingleton;
    private Button mButtonCheck, mButtonPuzzleCorrectContinue, mButtonPuzzleIncorrectRetry,
            mButtonLevelCompleteContinue;
    private Handler mTimerHandler = new Handler();
    private Puzzle mCurrentPuzzle;
    private Level mCurrentLevel;
    private String mClassName = getClass().toString();
    private int mTimerSeconds, mTimerResumeTime, mAttemptsCount, mLevelId;
    private long mStartTime = 0;
    private String mCurrentFragment;
    private TextView mTextTitle, mTextInstructions, mTextExpectedOutput, mTextTimer, mTextAttempts;
    private View mViewDivider;
    private Dialog mDialogPuzzleCorrect, mDialogPuzzleIncorrect, mDialogLevelComplete;
    private boolean mCorrectAnswer = false, mLevelComplete = false;
    private List<String> mCompiledCode = new ArrayList<>();
    private List<Object> mCompiledResult = new ArrayList<>();
    private TextView mTextCorrectAttempts, mTextCorrectAttemptsValue, mTextCorrectTime, mTextCorrectTimeValue;
    private TextView mTextIncorrectCompiledCodeTitle, mTextIncorrectCompiledCode, mTextIncorrectCompiledOutputTitle,
            mTextIncorrectCompiledOutput;
    private ImageView mImageTrophyGold, mImageTrophySilver, mImageTrophyBronze;
    private TextView mTextScore, mTextPrevious, mTextNew, mTextLevelScore, mTextLevelAttempts, mTextLevelTime,
            mTextPreviousLevelScore, mTextPreviousLevelAttempts, mTextPreviousLevelTime,
            mTextNewLevelScore, mTextNewLevelAttempts, mTextNewLevelTime;
    private ShimmerTextView mShimmerTextNewHighScore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_puzzle);
        assignSingletons();
        assignViews();
        assignFonts();
        assignActionBar();
        assignListeners();
        getBundle(); //get current level
        if (loadNextIncompletePuzzle()) {//load next incomplete puzzle data
            //if complete load ui elements and restart puzzle timer at 00:00
            loadPuzzleUi();
            resumePuzzleTimer();
        } else {
            //if false end activity
            finish();
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        }
    }

    @Override
    public void onBackPressed() {
        showBackAlert();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                showBackAlert();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        resumePuzzleTimer();
    }

    @Override
    protected void onPause() {
        super.onPause();
        pausePuzzleTimer();
        mDialogPuzzleCorrect.dismiss();
        mDialogPuzzleIncorrect.dismiss();
        mDialogLevelComplete.dismiss();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        pausePuzzleTimer();
        savePuzzleData();
    }

    public void showBackAlert() {
        new AlertDialog.Builder(PuzzleActivity.this)
                .setMessage("Are you sure you want discard this attempt?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        finish();
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    }
                })
                .setNegativeButton("No", null).show();
    }

    public void showNoSelectionAlert() {
        Toast.makeText(this, "Please select an option", Toast.LENGTH_SHORT).show();
    }

    public PuzzleCodeBuilder getCurrentPuzzleCodeBuilder() {
        return mCurrentPuzzleCodeBuilder;
    }

    public void setCompiledCode(List<String> compiledCode) {
        mCompiledCode = compiledCode;
    }

    public void setCompiledResult(List<Object> compiledResult) {
        mCompiledResult = compiledResult;
    }

    private void assignSingletons() {
        mDatabaseHandlerSingleton = DatabaseHandlerSingleton.getInstance(this);
        mAchievementHandlerSingleton = AchievementHandlerSingleton.getInstance(this);
    }

    private void assignViews() {
        mToolbar = (Toolbar) findViewById(R.id.app_actionbar);
        mTextTitle = (TextView) findViewById(R.id.text_title);
        mTextInstructions = (TextView) findViewById(R.id.text_instructions);
        mTextExpectedOutput = (TextView) findViewById(R.id.text_expected_output);
        mTextTimer = (TextView) findViewById(R.id.text_timer);
        mTextAttempts = (TextView) findViewById(R.id.text_attempts);
        mButtonCheck = (Button) findViewById(R.id.button_check);
        mViewDivider = findViewById(R.id.view_divider);
        mDialogPuzzleCorrect = new Dialog(this);
        mDialogPuzzleCorrect.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialogPuzzleCorrect.setContentView(R.layout.dialog_puzzle_correct);
        mButtonPuzzleCorrectContinue = (Button) mDialogPuzzleCorrect.findViewById(R.id.button_puzzle_correct_continue);
        mTextCorrectAttempts = (TextView) mDialogPuzzleCorrect.findViewById(R.id.text_correct_attempts);
        mTextCorrectAttemptsValue = (TextView) mDialogPuzzleCorrect.findViewById(R.id.text_correct_attempts_value);
        mTextCorrectTime = (TextView) mDialogPuzzleCorrect.findViewById(R.id.text_correct_time);
        mTextCorrectTimeValue = (TextView) mDialogPuzzleCorrect.findViewById(R.id.text_correct_time_value);

        mDialogPuzzleIncorrect = new Dialog(this);
        mDialogPuzzleIncorrect.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialogPuzzleIncorrect.setContentView(R.layout.dialog_puzzle_incorrect);
        mButtonPuzzleIncorrectRetry = (Button) mDialogPuzzleIncorrect.findViewById(R.id.button_puzzle_incorrect_retry);
        mTextIncorrectCompiledCodeTitle = (TextView) mDialogPuzzleIncorrect.findViewById(R.id.text_incorrect_compiled_code_title);
        mTextIncorrectCompiledCode = (TextView) mDialogPuzzleIncorrect.findViewById(R.id.text_incorrect_compiled_code);
        mTextIncorrectCompiledOutputTitle = (TextView) mDialogPuzzleIncorrect.findViewById(R.id.text_incorrect_compiled_output_title);
        mTextIncorrectCompiledOutput = (TextView) mDialogPuzzleIncorrect.findViewById(R.id.text_incorrect_compiled_output);


        mDialogLevelComplete = new Dialog(this);
        mDialogLevelComplete.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialogLevelComplete.setContentView(R.layout.dialog_level_complete);
        mShimmerTextNewHighScore = (ShimmerTextView) mDialogLevelComplete.findViewById(R.id.shimmer_text_new_high_score);
        Shimmer shimmer = new Shimmer();
        shimmer.start(mShimmerTextNewHighScore);
        mImageTrophyGold = (ImageView) mDialogLevelComplete.findViewById(R.id.image_trophy_gold);
        mImageTrophySilver = (ImageView) mDialogLevelComplete.findViewById(R.id.image_trophy_silver);
        mImageTrophyBronze = (ImageView) mDialogLevelComplete.findViewById(R.id.image_trophy_bronze);
        mTextScore = (TextView) mDialogLevelComplete.findViewById(R.id.text_score);
        mTextPrevious = (TextView) mDialogLevelComplete.findViewById(R.id.text_previous);
        mTextNew = (TextView) mDialogLevelComplete.findViewById(R.id.text_new);
        mTextLevelScore = (TextView) mDialogLevelComplete.findViewById(R.id.text_level_score);
        mTextLevelAttempts = (TextView) mDialogLevelComplete.findViewById(R.id.text_level_attempts);
        mTextLevelTime = (TextView) mDialogLevelComplete.findViewById(R.id.text_level_time);
        mTextPreviousLevelScore = (TextView) mDialogLevelComplete.findViewById(R.id.text_previous_level_score);
        mTextPreviousLevelAttempts = (TextView) mDialogLevelComplete.findViewById(R.id.text_previous_level_attempts);
        mTextPreviousLevelTime = (TextView) mDialogLevelComplete.findViewById(R.id.text_previous_level_time);
        mTextNewLevelScore = (TextView) mDialogLevelComplete.findViewById(R.id.text_new_level_score);
        mTextNewLevelAttempts = (TextView) mDialogLevelComplete.findViewById(R.id.text_new_level_attempts);
        mTextNewLevelTime = (TextView) mDialogLevelComplete.findViewById(R.id.text_new_level_time);

        mButtonLevelCompleteContinue = (Button) mDialogLevelComplete.findViewById(R.id.button_level_complete_continue);
    }

    private void assignFonts() {
        Typeface Roboto_Medium = Typeface.createFromAsset(getAssets(), "Roboto-Medium.ttf");
        Typeface Roboto_Regular = Typeface.createFromAsset(getAssets(), "Roboto-Regular.ttf");
        mTextInstructions.setTypeface(Roboto_Regular);
        mTextExpectedOutput.setTypeface(Roboto_Regular);
        mTextTimer.setTypeface(Roboto_Regular);
        mTextAttempts.setTypeface(Roboto_Regular);
        mTextTitle.setTypeface(Roboto_Regular);
        mButtonCheck.setTypeface(Roboto_Medium);
        mTextCorrectAttempts.setTypeface(Roboto_Regular);
        mTextCorrectAttemptsValue.setTypeface(Roboto_Regular);
        mTextCorrectTime.setTypeface(Roboto_Regular);
        mTextCorrectTimeValue.setTypeface(Roboto_Regular);
        mTextIncorrectCompiledCodeTitle.setTypeface(Roboto_Regular);
        mTextIncorrectCompiledCode.setTypeface(Roboto_Regular);
        mTextIncorrectCompiledOutputTitle.setTypeface(Roboto_Regular);
        mTextIncorrectCompiledOutput.setTypeface(Roboto_Regular);
        mShimmerTextNewHighScore.setTypeface(Roboto_Medium, Typeface.BOLD);
        mTextScore.setTypeface(Roboto_Regular, Typeface.BOLD);
        mTextPrevious.setTypeface(Roboto_Regular, Typeface.BOLD);
        mTextNew.setTypeface(Roboto_Regular, Typeface.BOLD);
        mTextLevelScore.setTypeface(Roboto_Regular, Typeface.BOLD);
        mTextLevelAttempts.setTypeface(Roboto_Regular, Typeface.BOLD);
        mTextLevelTime.setTypeface(Roboto_Regular, Typeface.BOLD);
        mTextPreviousLevelScore.setTypeface(Roboto_Regular);
        mTextPreviousLevelAttempts.setTypeface(Roboto_Regular);
        mTextPreviousLevelTime.setTypeface(Roboto_Regular);
        mTextNewLevelScore.setTypeface(Roboto_Regular);
        mTextNewLevelAttempts.setTypeface(Roboto_Regular);
        mTextNewLevelTime.setTypeface(Roboto_Regular);
        mButtonPuzzleCorrectContinue.setTypeface(Roboto_Medium);
        mButtonPuzzleIncorrectRetry.setTypeface(Roboto_Medium);
        mButtonLevelCompleteContinue.setTypeface(Roboto_Medium);
    }

    private void assignActionBar() {
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        else {
            Log.e(mClassName, "getSupportActionBar null");
            finish();
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        }
    }

    private void assignListeners() {
        mButtonCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pausePuzzleTimer();
                Boolean checkedAnswer = checkFragmentAnswers();
                if (checkedAnswer != null) {
                    if (checkedAnswer) { //if puzzle answer is correct
                        mCorrectAnswer = true;
                        savePuzzleData();
                        mAchievementHandlerSingleton.puzzleWasComplete(mCurrentLevel); //process puzzle completed achievements
                        loadNextIncompletePuzzle();
                        if (mCurrentPuzzle == null || mCurrentLevel.getLevel_puzzles_completed() == mCurrentLevel.getLevel_puzzles_count()) {
                            //if level is complete
                            mLevelComplete = true;
                        }
                        assignPuzzleCorrectViews();

                        mDialogPuzzleCorrect.show();
                    } else {
                        mCorrectAnswer = false;
                        mLevelComplete = false;
                        savePuzzleData();
                        loadNextIncompletePuzzle();
                        assignPuzzleIncorrectViews();
                        mDialogPuzzleIncorrect.show();
                    }
                } else {
                    resumePuzzleTimer();
                }
            }
        });
        mButtonPuzzleCorrectContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialogPuzzleCorrect.dismiss();
            }
        });
        mDialogPuzzleCorrect.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (!mLevelComplete) {
                    loadPuzzleUi();
                    resumePuzzleTimer();
                } else {
                    assignLevelCompleteViews();
                    mDialogLevelComplete.show();
                }
            }
        });
        mButtonPuzzleIncorrectRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialogPuzzleIncorrect.dismiss();
            }
        });
        mDialogPuzzleIncorrect.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                loadPuzzleUi();
                resumePuzzleTimer();
            }
        });
        mButtonLevelCompleteContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialogLevelComplete.dismiss();
            }
        });
        mDialogLevelComplete.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                finish();
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });
    }

    private boolean assignLevelCompleteViews() {
        try {
            int prevScore = mCurrentLevel.getLevel_score();
            mTextPreviousLevelScore.setText(String.valueOf(prevScore));
            mTextPreviousLevelAttempts.setText(String.valueOf(mCurrentLevel.getLevel_attempts()));
            mTextPreviousLevelTime.setText(String.valueOf(mCurrentLevel.getLevel_time()));
            Level updatedLevel = mDatabaseHandlerSingleton.updateLevelData(mCurrentLevel);
            mAchievementHandlerSingleton.levelWasComplete(mCurrentLevel);
            if (loadLevelData()) {
                int newScore = updatedLevel.getActual_level_score();
                mTextNewLevelScore.setText(String.valueOf(newScore));
                mTextNewLevelAttempts.setText(String.valueOf(updatedLevel.getActual_level_attempts()));
                mTextNewLevelTime.setText(String.valueOf(updatedLevel.getActual_level_time()));
                mTextScore.setText(String.valueOf(newScore));
                if (newScore >= 1) {
                    mTextScore.setTextColor(ContextCompat.getColor(this, R.color.bronze));
                    mImageTrophyBronze.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.trophy_bronze));
                }
                if (newScore >= 50 ) {
                    mTextScore.setTextColor(ContextCompat.getColor(this, R.color.silver));
                    mImageTrophySilver.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.trophy_silver));
                }
                if (newScore >= 100) {
                    mTextScore.setTextColor(ContextCompat.getColor(this, R.color.gold));
                    mImageTrophyGold.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.trophy_gold));
                }

                if (newScore > prevScore) {
                    mTextNew.setText("New");
                    mTextPrevious.setText("Previous");
                    mShimmerTextNewHighScore.setVisibility(View.VISIBLE);
                } else {
                    mTextNew.setText("Attempted");
                    mTextPrevious.setText("Current");
                    mShimmerTextNewHighScore.setVisibility(View.GONE);
                }
            }
            return true;
        } catch (Exception e) {
            Log.e(mClassName, e.getMessage());
            return false;
        }
    }

    private void assignPuzzleCorrectViews() {
        mTextCorrectAttemptsValue.setText(mTextAttempts.getText().toString().replace("Attempts: ", ""));
        int displayMinutes = mTimerSeconds / 60;
        int displaySeconds = mTimerSeconds % 60;
        mTextCorrectTimeValue.setText(String.format(Locale.UK, "%d:%02d", displayMinutes, displaySeconds));
    }

    private void assignPuzzleIncorrectViews() {
        StringBuilder compiledCode = new StringBuilder();
        for (String compiledCodeLine : mCompiledCode) {
            if (!compiledCodeLine.equals("")) {
                if (compiledCodeLine.toLowerCase().contains(mCompiledResult.get(0).toString().toLowerCase().trim())) {
                    compiledCodeLine = "<b><u><font color=#B71C1C>" + compiledCodeLine + "</font></u></b>";
                } else {
                    compiledCodeLine = compiledCodeLine.replaceAll("int", "<font color=#2962FF>int</font>");
                    compiledCodeLine = compiledCodeLine.replaceAll("float", "<font color=#2962FF>float</font>");
                    compiledCodeLine = compiledCodeLine.replaceAll("String", "<font color=#2962FF>String</font>");
                    compiledCodeLine = compiledCodeLine.replaceAll("char", "<font color=#2962FF>char</font>");
                    compiledCodeLine = compiledCodeLine.replaceAll("Boolean", "<font color=#2962FF>Boolean</font>");
                    compiledCodeLine = compiledCodeLine.replaceAll("\"(.*)\"", "<font color=#EF5350>\"$1\"</font>");
                    compiledCodeLine = compiledCodeLine.replaceAll("\'(.*)\'", "<font color=#EF5350>\'$1\'</font>");
                    compiledCodeLine = compiledCodeLine.replaceAll("true", "<font color=#2962FF>true</font>");
                    compiledCodeLine = compiledCodeLine.replaceAll("false", "<font color=#2962FF>false</font>");
                    compiledCodeLine = compiledCodeLine.replaceAll("Console", "<font color=#40C4FF>Console</font>");
                }
                if (mCompiledCode.indexOf(compiledCodeLine) == mCompiledCode.size() - 1)
                    compiledCode.append(compiledCodeLine);
                else
                    compiledCode.append(compiledCodeLine + "<br />");
            }
        }
        mTextIncorrectCompiledCode.setText(Html.fromHtml(compiledCode.toString()));
        if (mCompiledResult.size() == 1) {
            mTextIncorrectCompiledOutput.setText(mCompiledResult.get(0).toString());
        } else {
            mTextIncorrectCompiledOutput.setText(mCompiledResult.get(1).toString());
        }

    }

    //loads from database the next incomplete puzzle
    //if the puzzle is not null, return true else false
    private boolean loadNextIncompletePuzzle() {
        mCurrentPuzzle = mDatabaseHandlerSingleton.getNextPuzzle(mCurrentLevel);
        if (mCurrentPuzzle != null) {
            mTimerResumeTime = mCurrentPuzzle.getPuzzle_time();
            mAttemptsCount = mCurrentPuzzle.getPuzzle_attempts();
            mCorrectAnswer = mCurrentPuzzle.getPuzzle_completed() == 1;
            mCurrentPuzzleCodeBuilder = new PuzzleCodeBuilder();
            mCurrentPuzzleCodeBuilder.processCSharpCode(mCurrentPuzzle.getPuzzle_data());
            return true;
        } else {
            return false;
        }
    }

    //load puzzle ui elements and then load puzzle fragment
    private void loadPuzzleUi() {
        mAttemptsCount++;
        String fragmentToLoad = mCurrentPuzzleCodeBuilder.getPuzzleFragmentType();
        switch (fragmentToLoad) {
            case "<single>":
                mTextTitle.setText("SELECT ONE OF THE FOLLOWING");
                break;
            case "<multiple>":
                mTextTitle.setText("MULTIPLE CHOICE");
                break;
            case "<rearrange>":
                mTextTitle.setText("REARRANGE");
                break;
            case "<truefalse>":
                mTextTitle.setText("TRUE OR FALSE");
                break;
        }
        mTextInstructions.setText(mCurrentPuzzle.getPuzzle_instructions());
        mTextAttempts.setText(getString(R.string.string_attempts_counter, mAttemptsCount));

        String expectedOutput = mCurrentPuzzleCodeBuilder.getCSharpCodeToDisplayExpectedOutput();
        if (expectedOutput.equals("")) {
            mTextExpectedOutput.setVisibility(View.GONE);
        } else {
            expectedOutput = expectedOutput.replaceAll("int", "<font color=#2962FF>int</font>");
            expectedOutput = expectedOutput.replaceAll("float", "<font color=#2962FF>float</font>");
            expectedOutput = expectedOutput.replaceAll("String", "<font color=#2962FF>String</font>");
            expectedOutput = expectedOutput.replaceAll("char", "<font color=#2962FF>char</font>");
            expectedOutput = expectedOutput.replaceAll("Boolean", "<font color=#2962FF>Boolean</font>");
            expectedOutput = expectedOutput.replaceAll("\"(.*)\"", "<font color=#B71C1C>\"$1\"</font>");
            expectedOutput = expectedOutput.replaceAll("\'(.*)\'", "<font color=#B71C1C>\'$1\'</font>");
            expectedOutput = expectedOutput.replaceAll("true", "<font color=#2962FF>true</font>");
            expectedOutput = expectedOutput.replaceAll("false", "<font color=#2962FF>false</font>");
            expectedOutput = expectedOutput.replaceAll("Console", "<font color=#40C4FF>Console</font>");
            mTextExpectedOutput.setText(Html.fromHtml(expectedOutput));
            mTextExpectedOutput.setVisibility(View.VISIBLE);
        }
        setTitle(getString(R.string.puzzle_count, (mCurrentLevel.getLevel_puzzles_completed() + 1),
                mCurrentLevel.getLevel_puzzles_count()));
        loadPuzzleFragment();
    }

    private Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            long millis = (System.currentTimeMillis() - mStartTime);
            mTimerSeconds = mTimerResumeTime + (int) (millis / 1000);
            int displayMinutes = mTimerSeconds / 60;
            int displaySeconds = mTimerSeconds % 60;
            mTextTimer.setText(getString(R.string.string_timer,
                    String.format(Locale.UK, "%d:%02d", displayMinutes, displaySeconds)));
            mTimerHandler.postDelayed(this, 500);
        }
    };

    private void resumePuzzleTimer() {
        mStartTime = System.currentTimeMillis();
        mTimerHandler.postDelayed(timerRunnable, 0);
    }

    private void pausePuzzleTimer() {
        mTimerResumeTime = mTimerSeconds;
        mTimerHandler.removeCallbacks(timerRunnable);
    }

    //check which fragment to load, if none are found finish activity
    private void loadPuzzleFragment() {
        String fragmentToLoad = mCurrentPuzzleCodeBuilder.getPuzzleFragmentType();
        Log.e(mClassName, fragmentToLoad);
        switch (fragmentToLoad) {
            case "<single>":
                loadSingleChoiceListFragment();
                break;
            case "<multiple>":
                loadMultipleChoiceListFragment();
                break;
            case "<rearrange>":
                loadDragListViewFragment();
                break;
            case "<truefalse>":
                loadTrueFalseFragment();
                break;
            default:
                finish();
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                break;
        }
    }

    private boolean loadLevelData() {
        mCurrentLevel = mDatabaseHandlerSingleton.getLevel(mLevelId);
        return mCurrentLevel != null;
    }

    //calls the loaded fragment and tells it to check if answer is correct
    private Boolean checkFragmentAnswers() {
        if (mDragListViewFragment != null &&
                mCurrentFragment.equals(mDragListViewFragment.getClass().toString())) {
            mDragListViewFragment = (DragListViewFragment) getSupportFragmentManager()
                    .findFragmentByTag(mCurrentFragment);
            return mDragListViewFragment.checkIfCorrect();
        }
        if (mMultipleChoiceListFragment != null &&
                mCurrentFragment.equals(mMultipleChoiceListFragment.getClass().toString())) {
            mMultipleChoiceListFragment = (MultipleChoiceListFragment) getSupportFragmentManager()
                    .findFragmentByTag(mCurrentFragment);
            return mMultipleChoiceListFragment.checkIfCorrect();
        }
        if (mSingleChoiceListFragment != null &&
                mCurrentFragment.equals(mSingleChoiceListFragment.getClass().toString())) {
            mSingleChoiceListFragment = (SingleChoiceListFragment) getSupportFragmentManager()
                    .findFragmentByTag(mCurrentFragment);
            return mSingleChoiceListFragment.checkIfCorrect();
        }
        if (mTrueFalseFragment != null &&
                mCurrentFragment.equals(mTrueFalseFragment.getClass().toString())) {
            mTrueFalseFragment = (TrueFalseFragment) getSupportFragmentManager()
                    .findFragmentByTag(mCurrentFragment);
            return mTrueFalseFragment.checkIfCorrect();
        }
        return false;
    }

    //fetches current level with bundled level_id
    //if level_id is incorrect or current level is null finish activity
    private void getBundle() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            mLevelId = bundle.getInt("level_id", -1);
            if (mLevelId != -1 && !loadLevelData()) {
                finish();
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        }
    }

    //save puzzle current data in local database
    private void savePuzzleData() {
        if (mCurrentPuzzle != null) {
            mCurrentPuzzle.setPuzzle_time(mTimerSeconds);
            mCurrentPuzzle.setPuzzle_attempts(mAttemptsCount);
            mCurrentPuzzle.setPuzzle_completed(mCorrectAnswer);
            mDatabaseHandlerSingleton.updatePuzzleData(mCurrentPuzzle);
            if (!loadLevelData()) {
                finish();
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        }
    }

    private void loadDragListViewFragment() {
        mDragListViewFragment = new DragListViewFragment();
        replaceFragment(mDragListViewFragment);
        mCurrentFragment = mDragListViewFragment.getClass().toString();
    }

    private void loadMultipleChoiceListFragment() {
        mMultipleChoiceListFragment = new MultipleChoiceListFragment();
        replaceFragment(mMultipleChoiceListFragment);
        mCurrentFragment = mMultipleChoiceListFragment.getClass().toString();
    }

    private void loadSingleChoiceListFragment() {
        mSingleChoiceListFragment = new SingleChoiceListFragment();
        replaceFragment(mSingleChoiceListFragment);
        mCurrentFragment = mSingleChoiceListFragment.getClass().toString();
    }

    private void loadTrueFalseFragment() {
        mTrueFalseFragment = new TrueFalseFragment();
        replaceFragment(mTrueFalseFragment);
        mCurrentFragment = mTrueFalseFragment.getClass().toString();
    }

    private void replaceFragment(Fragment fragment) {
        String tag = fragment.getClass().toString();
        try {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, fragment, tag);
            fragmentTransaction.addToBackStack(tag);
            fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            fragmentTransaction.commit();
            getSupportFragmentManager().executePendingTransactions();
        } catch (Exception ex) {
            Log.e("FragmentException", ex.getMessage());
        }
    }
}
