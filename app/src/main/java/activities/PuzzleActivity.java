package activities;

import android.app.AlertDialog;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.progamer.R;

import java.util.Locale;
import android.app.AlertDialog;
import de.hdodenhof.circleimageview.CircleImageView;
import fragments.puzzles.DragListViewFragment;
import fragments.puzzles.MultipleChoiceListFragment;
import fragments.puzzles.SingleChoiceListFragment;
import fragments.puzzles.TrueFalseFragment;
import models.Level;
import models.Puzzle;
import puzzle.PuzzleCodeBuilder;
import singletons.DatabaseHandlerSingleton;


public class PuzzleActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private PuzzleCodeBuilder mCurrentPuzzleCodeBuilder;
    private DragListViewFragment mDragListViewFragment;
    private MultipleChoiceListFragment mMultipleChoiceListFragment;
    private SingleChoiceListFragment mSingleChoiceListFragment;
    private TrueFalseFragment mTrueFalseFragment;
    private Button mButtonNext, mButtonResult;
    private Handler mTimerHandler = new Handler();
    private DatabaseHandlerSingleton mDatabaseHandlerSingleton;
    private Puzzle mCurrentPuzzle;
    private Level mCurrentLevel;
    private String mClassName = getClass().toString();
    private int mTimerSeconds, mTimerResumeTime, mAttemptsCount, mLevelId;
    private long mStartTime = 0;
    private Animation mAnimBottomUp, mAnimBottomDown, mAnimLeftRight, mAnimRightLeft;
    private boolean mCorrectAnswer;
    private String mCurrentFragment;
    private TextView mTextInstructions, mTextExpectedOutput, mTextTimer, mTextAttempts,
            mTextResultPopup;
    private View mViewDivider;
    private LinearLayout mLinearResultPopup;
    private AlertDialog mAlertDialogResult;
    private View mViewResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_puzzle);
        assignSingletons();
        assignViews();
        assignFonts();
        assignActionBar();
        getBundle();
        reloadData();
        assignListeners();
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
        if (!mButtonNext.getText().toString().equals("Check")) {
            mStartTime = System.currentTimeMillis();
            mTimerHandler.postDelayed(timerRunnable, 0);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mTimerResumeTime = mTimerSeconds;
        mTimerHandler.removeCallbacks(timerRunnable);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        savePuzzleData();
    }

    public PuzzleCodeBuilder getCurrentPuzzleCodeBuilder() {
        return mCurrentPuzzleCodeBuilder;
    }

    private void assignResultDialog() {
        if(mButtonResult==null) {
            Log.e("button", "null");
        }
        if(mCorrectAnswer) {
            mButtonResult.setText(getString(R.string.string_continue));
        } else {
            mButtonResult.setText(getString(R.string.string_retry));
        }
        mAlertDialogResult.setView(mViewResult);
        mAlertDialogResult.show();
        mButtonResult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAlertDialogResult.cancel();
            }
        });
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

    private void reloadData() {
        if (mCurrentLevel != null) {
            mCurrentPuzzle = mDatabaseHandlerSingleton.getNextPuzzle(mCurrentLevel);
            mTextInstructions.setText(mCurrentPuzzle.getPuzzle_instructions());
            int puzzlesCompleted = mCurrentLevel.getLevel_puzzles_completed() + 1;
            int puzzleCount = mCurrentLevel.getLevel_puzzles_count();
            setTitle(getString(R.string.puzzle_count, puzzlesCompleted, puzzleCount));
            mTimerResumeTime = mCurrentPuzzle.getPuzzle_time();
            mAttemptsCount = mCurrentPuzzle.getPuzzle_attempts();
            mAttemptsCount++;
            mTextAttempts.setText(getString(R.string.string_attempts_counter, mAttemptsCount));
            mCurrentPuzzleCodeBuilder = new PuzzleCodeBuilder();
            mCurrentPuzzleCodeBuilder.processCSharpCode(mCurrentPuzzle.getPuzzle_data());
            String expectedOutput = mCurrentPuzzleCodeBuilder.getCSharpCodeToDisplayExpectedOutput();
            if (expectedOutput.equals("")) {
                mTextExpectedOutput.setVisibility(View.GONE);
                mViewDivider.setVisibility(View.GONE);
            } else {
                mTextExpectedOutput.setText(expectedOutput);
                mTextExpectedOutput.setVisibility(View.VISIBLE);
                mViewDivider.setVisibility(View.VISIBLE);
            }
            loadPuzzle();
            mStartTime = System.currentTimeMillis();
            mTimerHandler.postDelayed(timerRunnable, 0);
        } else {
            Log.e(mClassName, "Level data missing");
            finish();
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        }
    }

    private void loadPuzzle() {
        String fragmentToLoad = mCurrentPuzzleCodeBuilder.getPuzzleFragmentType();
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

    private void assignListeners() {
        mButtonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                assignResultDialog();
                if (mButtonNext.getText().toString().equals("Check")) {
                    if (checkFragmentAnswers()) {
                        mCorrectAnswer = true;
                        mCorrectAnswer = true;
                        savePuzzleData();
                        showCorrectPopup();
                    } else {
                        mCorrectAnswer = false;
                        mAttemptsCount++;
                        savePuzzleData();
                        showIncorrectPopup();
                    }
                } else {
                    mCorrectAnswer = false;
                    hidePopup();
                }
            }
        });
    }

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

    private void hidePopup() {
        //mTextResultPopup.startAnimation(mAnimBottomDown);

        mLinearResultPopup.setVisibility(View.GONE);
        mButtonNext.setText(getString(R.string.string_check));
        //bottomBar.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.green_200));
        reloadData();
    }

    private void showIncorrectPopup() {
        mButtonNext.setText(getString(R.string.string_retry));
        mTextResultPopup.setText(getString(R.string.string_incorrect));
        //resultPopup.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.red_500));
        //bottomBar.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.red_200));
        mLinearResultPopup.startAnimation(mAnimLeftRight);
        mLinearResultPopup.setVisibility(View.VISIBLE);
        mTextAttempts.setText(getString(R.string.string_attempts_counter, mAttemptsCount));
    }

    private void showCorrectPopup() {
        mAlertDialogResult.setView(mViewResult);
        mAlertDialogResult.show();
        if (mDatabaseHandlerSingleton.getLevel(mCurrentLevel.getLevel_id()).getPuzzles_completed()) {
            mDatabaseHandlerSingleton.updateLevelData(mCurrentLevel);
            finish();
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        } else {
            mButtonNext.setText(getString(R.string.string_continue));
            mTextResultPopup.setText(getString(R.string.string_correct));
            //resultPopup.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.green_500));
            mLinearResultPopup.startAnimation(mAnimBottomUp);
            mLinearResultPopup.setVisibility(View.VISIBLE);
        }
    }

    private void assignSingletons() {
        mDatabaseHandlerSingleton = DatabaseHandlerSingleton.getInstance(this);
    }

    private void getBundle() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            mLevelId = bundle.getInt("level_id", -1);
            if (mLevelId != -1) {
                mCurrentLevel = mDatabaseHandlerSingleton.getLevel(mLevelId);
            } else {
                Log.e(mClassName, "Level data missing");
                finish();
            }
        }
    }

    private void savePuzzleData() {
        if (mCurrentPuzzle != null) {
            mTimerResumeTime = mTimerSeconds;
            mTimerHandler.removeCallbacks(timerRunnable);
            mCurrentPuzzle.setPuzzle_time(mTimerSeconds);
            mCurrentPuzzle.setPuzzle_attempts(mAttemptsCount);
            mCurrentPuzzle.setPuzzle_completed(mCorrectAnswer);
            mDatabaseHandlerSingleton.updatePuzzleData(mCurrentPuzzle);
            mCurrentLevel = mDatabaseHandlerSingleton.getLevel(mLevelId);
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

    private void assignViews() {
        mToolbar = (Toolbar) findViewById(R.id.app_actionbar);
        mTextInstructions = (TextView) findViewById(R.id.text_instructions);
        mTextExpectedOutput = (TextView) findViewById(R.id.text_expected_output);
        mTextTimer = (TextView) findViewById(R.id.text_timer);
        mTextAttempts = (TextView) findViewById(R.id.text_attempts);
        mTextResultPopup = (TextView) findViewById(R.id.text_result_popup);
        mButtonNext = (Button) findViewById(R.id.button_next);
        mButtonResult = (Button) findViewById(R.id.button_result);
        mLinearResultPopup = (LinearLayout) findViewById(R.id.linear_result_popup);
        mAnimBottomUp = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.bottom_up);
        mAnimBottomDown = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.bottom_down);
        mAnimLeftRight = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.left_right);
        mAnimRightLeft = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.right_left);
        mViewDivider = findViewById(R.id.view_divider);
        mAlertDialogResult = new AlertDialog.Builder(this).create();
        mViewResult = View.inflate(this, R.layout.dialog_puzzle_result, null);
    }

    private void assignFonts() {
        Typeface Roboto_Medium = Typeface.createFromAsset(getAssets(), "Roboto-Medium.ttf");
        Typeface Roboto_Regular = Typeface.createFromAsset(getAssets(), "Roboto-Regular.ttf");
        mTextInstructions.setTypeface(Roboto_Medium);
        mTextExpectedOutput.setTypeface(Roboto_Regular);
        mTextTimer.setTypeface(Roboto_Regular);
        mTextAttempts.setTypeface(Roboto_Regular);
        mTextResultPopup.setTypeface(Roboto_Regular);
        mButtonNext.setTypeface(Roboto_Medium);
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
