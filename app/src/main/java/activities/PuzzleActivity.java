package activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.progamer.R;

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
    private Button mButtonCheck, mButtonPuzzleCorrectContinue, mButtonPuzzleIncorrectRetry, mButtonLevelCompleteContinue;
    private Handler mTimerHandler = new Handler();
    private Puzzle mCurrentPuzzle;
    private Level mCurrentLevel;
    private String mClassName = getClass().toString();
    private int mTimerSeconds, mTimerResumeTime, mAttemptsCount, mLevelId;
    private long mStartTime = 0;
    private String mCurrentFragment;
    private TextView mTextInstructions, mTextExpectedOutput, mTextTimer, mTextAttempts;
    private View mViewDivider;
    private AlertDialog mDialogPuzzleCorrect, mDialogPuzzleIncorrect, mDialogLevelComplete;
    private View mViewPuzzleCorrect, mViewPuzzleIncorrect, mViewLevelComplete;
    private boolean mCorrectAnswer = false, mLevelComplete = false;
    private List<String> mCompiledCode = new ArrayList<>(), mPuzzleCode = new ArrayList<>();
    private List<Object> mCompiledResult = new ArrayList<>(), mPuzzleCodeResult = new ArrayList<>();
    private TextView mCorrectTextCompiledCodeTitle, mCorrectTextCompiledCode, mCorrectTextResultTitle, mCorrectTextResult, mCorrectTextPuzzleCodeTitle, mCorrectTextPuzzleCode,
            mCorrectTextPuzzleResultTitle, mCorrectTextPuzzleResult;
    private TextView mIncorrectTextCompiledCodeTitle, mIncorrectTextCompiledCode, mIncorrectTextResultTitle, mIncorrectTextResult, mIncorrectTextPuzzleCodeTitle, mIncorrectTextPuzzleCode,
            mIncorrectTextPuzzleResultTitle, mIncorrectTextPuzzleResult;

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

    public PuzzleCodeBuilder getCurrentPuzzleCodeBuilder() {
        return mCurrentPuzzleCodeBuilder;
    }

    public void setCompiledCode(List<String> compiledCode) {
        mCompiledCode = compiledCode;
    }

    public void setPuzzleCode(List<String> puzzleCode) {
        mPuzzleCode = puzzleCode;
    }

    public void setCompiledResult(List<Object> compiledResult) {
        mCompiledResult = compiledResult;
    }

    public void setPuzzleCodeResult(List<Object> codeResult) {
        mPuzzleCodeResult = codeResult;
    }

    private void assignSingletons() {
        mDatabaseHandlerSingleton = DatabaseHandlerSingleton.getInstance(this);
        mAchievementHandlerSingleton = AchievementHandlerSingleton.getInstance(this);
    }

    private void assignViews() {
        mToolbar = (Toolbar) findViewById(R.id.app_actionbar);
        mTextInstructions = (TextView) findViewById(R.id.text_instructions);
        mTextExpectedOutput = (TextView) findViewById(R.id.text_expected_output);
        mTextTimer = (TextView) findViewById(R.id.text_timer);
        mTextAttempts = (TextView) findViewById(R.id.text_attempts);
        mButtonCheck = (Button) findViewById(R.id.button_check);
        mViewDivider = findViewById(R.id.view_divider);

        mDialogPuzzleCorrect = new AlertDialog.Builder(this).create();
        mViewPuzzleCorrect = View.inflate(this, R.layout.dialog_puzzle_correct, null);
        mDialogPuzzleCorrect.setView(mViewPuzzleCorrect);

        mButtonPuzzleCorrectContinue = (Button) mViewPuzzleCorrect.findViewById(R.id.button_puzzle_correct_continue);
        mCorrectTextCompiledCodeTitle = (TextView) mViewPuzzleCorrect.findViewById(R.id.text_compiled_code_title);
        mCorrectTextCompiledCode = (TextView) mViewPuzzleCorrect.findViewById(R.id.text_compiled_code);
        mCorrectTextResultTitle = (TextView) mViewPuzzleCorrect.findViewById(R.id.text_result_title);
        mCorrectTextResult = (TextView) mViewPuzzleCorrect.findViewById(R.id.text_result);
        mCorrectTextPuzzleCodeTitle = (TextView) mViewPuzzleCorrect.findViewById(R.id.text_puzzle_code_title);
        mCorrectTextPuzzleCode = (TextView) mViewPuzzleCorrect.findViewById(R.id.text_puzzle_code);
        mCorrectTextPuzzleResultTitle = (TextView) mViewPuzzleCorrect.findViewById(R.id.text_puzzle_result_title);
        mCorrectTextPuzzleResult = (TextView) mViewPuzzleCorrect.findViewById(R.id.text_puzzle_result);

        mDialogPuzzleIncorrect = new AlertDialog.Builder(this).create();
        mViewPuzzleIncorrect = View.inflate(this, R.layout.dialog_puzzle_incorrect, null);
        mDialogPuzzleIncorrect.setView(mViewPuzzleIncorrect);

        mButtonPuzzleIncorrectRetry = (Button) mViewPuzzleIncorrect.findViewById(R.id.button_puzzle_incorrect_retry);
        mIncorrectTextCompiledCodeTitle = (TextView) mViewPuzzleIncorrect.findViewById(R.id.text_compiled_code_title);
        mIncorrectTextCompiledCode = (TextView) mViewPuzzleIncorrect.findViewById(R.id.text_compiled_code);
        mIncorrectTextResultTitle = (TextView) mViewPuzzleIncorrect.findViewById(R.id.text_result_title);
        mIncorrectTextResult = (TextView) mViewPuzzleIncorrect.findViewById(R.id.text_result);
        mIncorrectTextPuzzleCodeTitle = (TextView) mViewPuzzleIncorrect.findViewById(R.id.text_puzzle_code_title);
        mIncorrectTextPuzzleCode = (TextView) mViewPuzzleIncorrect.findViewById(R.id.text_puzzle_code);
        mIncorrectTextPuzzleResultTitle = (TextView) mViewPuzzleIncorrect.findViewById(R.id.text_puzzle_result_title);
        mIncorrectTextPuzzleResult = (TextView) mViewPuzzleIncorrect.findViewById(R.id.text_puzzle_result);

        mDialogLevelComplete = new AlertDialog.Builder(this).create();
        mViewLevelComplete = View.inflate(this, R.layout.dialog_level_complete, null);
        mDialogLevelComplete.setView(mViewLevelComplete);

        mButtonLevelCompleteContinue = (Button) mViewLevelComplete.findViewById(R.id.button_level_complete_continue);
    }

    private void assignFonts() {
        Typeface Roboto_Medium = Typeface.createFromAsset(getAssets(), "Roboto-Medium.ttf");
        Typeface Roboto_Regular = Typeface.createFromAsset(getAssets(), "Roboto-Regular.ttf");
        Typeface Lucida_Console = Typeface.createFromAsset(getAssets(), "Lucida-Console.ttf");
        mTextInstructions.setTypeface(Roboto_Medium);
        mTextExpectedOutput.setTypeface(Lucida_Console);
        mTextTimer.setTypeface(Roboto_Regular);
        mTextAttempts.setTypeface(Roboto_Regular);
        mButtonCheck.setTypeface(Roboto_Medium);
        mCorrectTextCompiledCodeTitle.setTypeface(Roboto_Regular);
        mCorrectTextCompiledCode.setTypeface(Roboto_Regular);
        mCorrectTextResultTitle.setTypeface(Roboto_Regular);
        mCorrectTextResult.setTypeface(Roboto_Regular);
        mCorrectTextPuzzleCodeTitle.setTypeface(Roboto_Regular);
        mCorrectTextPuzzleCode.setTypeface(Roboto_Regular);
        mCorrectTextPuzzleResultTitle.setTypeface(Roboto_Regular);
        mCorrectTextPuzzleResult.setTypeface(Roboto_Regular);
        mIncorrectTextCompiledCodeTitle.setTypeface(Roboto_Regular);
        mIncorrectTextCompiledCode.setTypeface(Roboto_Regular);
        mIncorrectTextResultTitle.setTypeface(Roboto_Regular);
        mIncorrectTextResult.setTypeface(Roboto_Regular);
        mIncorrectTextPuzzleCodeTitle.setTypeface(Roboto_Regular);
        mIncorrectTextPuzzleCode.setTypeface(Roboto_Regular);
        mIncorrectTextPuzzleResultTitle.setTypeface(Roboto_Regular);
        mIncorrectTextPuzzleResult.setTypeface(Roboto_Regular);
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
                if (checkFragmentAnswers()) { //if puzzle answer is correct
                    Log.e(mClassName, "Correct answer");
                    mCorrectAnswer = true;
                    savePuzzleData();
                    mAchievementHandlerSingleton.puzzleWasComplete(mCurrentLevel); //process puzzle completed achievements
                    loadNextIncompletePuzzle();
                    if (mCurrentPuzzle == null || mCurrentLevel.getLevel_puzzles_completed() == mCurrentLevel.getLevel_puzzles_count()) {
                        //if level is complete
                        mLevelComplete = true;
                        mDatabaseHandlerSingleton.updateLevelData(mCurrentLevel);
                        mAchievementHandlerSingleton.levelWasComplete(mCurrentLevel);
                    }
                    assignPuzzleCorrectViews();

                    mDialogPuzzleCorrect.show();
                } else {
                    Log.e(mClassName, "Incorrect answer");
                    mCorrectAnswer = false;
                    mLevelComplete = false;
                    savePuzzleData();
                    loadNextIncompletePuzzle();
                    assignPuzzleIncorrectViews();
                    mDialogPuzzleIncorrect.show();
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

    private void assignPuzzleCorrectViews() {
        String compiledCode = "";
        for (String compiledCodeLine : mCompiledCode) {
            if (!compiledCodeLine.equals(""))
                if (mCompiledCode.indexOf(compiledCodeLine) == mCompiledCode.size() - 1)
                    compiledCode = compiledCode + compiledCodeLine;
                else
                    compiledCode = compiledCode + compiledCodeLine + "\n";
        }
        mCorrectTextCompiledCode.setText(compiledCode);
        String compiledResult = "";
        for (Object compiledResultLine : mCompiledResult) {
            if (!compiledResultLine.equals(""))
                if (mCompiledResult.indexOf(compiledResultLine) == mCompiledResult.size() - 1)
                    compiledResult = compiledResult + compiledResultLine.toString();
                else
                    compiledResult = compiledResult + compiledResultLine.toString() + "\n";
        }
        mCorrectTextResult.setText(compiledResult);
        String puzzleCode = "";
        for (String puzzleCodeLine : mPuzzleCode) {
            if (!puzzleCodeLine.equals(""))
                if (mCompiledCode.indexOf(puzzleCodeLine) == mCompiledCode.size() - 1)
                    puzzleCode = puzzleCode + puzzleCodeLine;
                else
                    puzzleCode = puzzleCode + puzzleCodeLine + "\n";
        }
        mCorrectTextPuzzleCode.setText(puzzleCode);
        mCorrectTextCompiledCode.setText(compiledCode);
        String puzzleResult = "";
        for (Object puzzleResultLine : mPuzzleCodeResult) {
            if (!puzzleResultLine.toString().equals(""))
                if (mCompiledResult.indexOf(puzzleResultLine) == mCompiledResult.size() - 1)
                    puzzleResult = puzzleResult + puzzleResultLine.toString();
                else
                    puzzleResult = puzzleResult + puzzleResultLine.toString() + "\n";
        }
        mCorrectTextPuzzleResult.setText(puzzleResult);
    }

    private void assignPuzzleIncorrectViews() {
        String compiledCode = "";
        for (String compiledCodeLine : mCompiledCode) {
            if (!compiledCodeLine.equals(""))
                if (mCompiledCode.indexOf(compiledCodeLine) == mCompiledCode.size() - 1)
                    compiledCode = compiledCode + compiledCodeLine;
                else
                    compiledCode = compiledCode + compiledCodeLine + "\n";
        }
        mIncorrectTextCompiledCode.setText(compiledCode);
        String compiledResult = "";
        Log.e(mClassName, "Compiled result size: " + mCompiledResult.size());
        for (Object compiledResultLine : mCompiledResult) {
            if (!compiledResultLine.equals(""))
                if (mCompiledResult.indexOf(compiledResultLine) == mCompiledResult.size() - 1)
                    compiledResult = compiledResult + compiledResultLine.toString();
                else
                    compiledResult = compiledResult + compiledResultLine.toString() + "\n";
        }
        mIncorrectTextResult.setText(compiledResult);
        String puzzleCode = "";
        for (String puzzleCodeLine : mPuzzleCode) {
            if (!puzzleCodeLine.equals(""))
                if (mCompiledCode.indexOf(puzzleCodeLine) == mCompiledCode.size() - 1)
                    puzzleCode = puzzleCode + puzzleCodeLine;
                else
                    puzzleCode = puzzleCode + puzzleCodeLine + "\n";
        }
        mIncorrectTextPuzzleCode.setText(puzzleCode);
        mIncorrectTextCompiledCode.setText(compiledCode);
        String puzzleResult = "";
        for (Object puzzleResultLine : mPuzzleCodeResult) {
            if (!puzzleResultLine.toString().equals(""))
                if (mCompiledResult.indexOf(puzzleResultLine) == mCompiledResult.size() - 1)
                    puzzleResult = puzzleResult + puzzleResultLine.toString();
                else
                    puzzleResult = puzzleResult + puzzleResultLine.toString() + "\n";
        }
        mIncorrectTextPuzzleResult.setText(puzzleResult);
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
        String typeOfPuzzle = "";
        switch (fragmentToLoad) {
            case "<single>":
                typeOfPuzzle = "<font color='#FF4081'>Single selection: </font>";
                break;
            case "<multiple>":
                typeOfPuzzle = "<font color='#FF4081'>Select multiple: </font>";
                break;
            case "<rearrange>":
                typeOfPuzzle = "<font color='#FF4081'>Rearrange: </font>";
                break;
            case "<truefalse>":
                typeOfPuzzle = "<font color='#FF4081'>True or false: </font>";
                break;
        }
        typeOfPuzzle = typeOfPuzzle + mCurrentPuzzle.getPuzzle_instructions();
        mTextInstructions.setText(Html.fromHtml(typeOfPuzzle));
        mTextAttempts.setText(getString(R.string.string_attempts_counter, mAttemptsCount));

        String expectedOutput = mCurrentPuzzleCodeBuilder.getCSharpCodeToDisplayExpectedOutput();
        if (expectedOutput.equals("")) {
            mTextExpectedOutput.setVisibility(View.GONE);
            mViewDivider.setVisibility(View.GONE);
        } else {
            //expectedOutput = expectedOutput.replaceAll("\'(.*)\'", "<font color='#8BC34A'>\'$1\'</font>");
            expectedOutput = expectedOutput.replaceAll("\\d+", "<font color=#FFB300>$0</font>");
            expectedOutput = expectedOutput.replaceAll("\\.", "<font color=#FFB300>.</font>");
            expectedOutput = expectedOutput.replaceAll("int", "<font color=#F06292>int</font>");
            expectedOutput = expectedOutput.replaceAll("double", "<font color=#F06292>double</font>");
            expectedOutput = expectedOutput.replaceAll("String", "<font color=#F06292>String</font>");
            expectedOutput = expectedOutput.replaceAll("char", "<font color=#F06292>char</font>");
            expectedOutput = expectedOutput.replaceAll("Boolean", "<font color=#F06292>Boolean</font>");
            expectedOutput = expectedOutput.replaceAll("\"(.*)\"", "<font color=#8BC34A>\"$1\"</font>");
            expectedOutput = expectedOutput.replaceAll("\'(.*)\'", "<font color=#8BC34A>\'$1\'</font>");
            expectedOutput = expectedOutput.replaceAll("true", "<font color=#0288D1>true</font>");
            expectedOutput = expectedOutput.replaceAll("false", "<font color=#0288D1>false</font>");
            mTextExpectedOutput.setText(Html.fromHtml(expectedOutput));
            mTextExpectedOutput.setVisibility(View.VISIBLE);
            mViewDivider.setVisibility(View.VISIBLE);
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
