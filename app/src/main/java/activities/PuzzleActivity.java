package activities;

import android.app.ProgressDialog;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.progamer.R;

import java.util.List;
import java.util.Random;

import fragments.puzzles.DragListViewFragment;
import fragments.puzzles.MultipleChoiceListFragment;
import fragments.puzzles.SingleChoiceListFragment;
import fragments.puzzles.TrueFalseFragment;
import models.Level;
import models.Puzzle;
import puzzle.PuzzleCodeBuilder;
import singletons.DatabaseHandlerSingleton;


public class PuzzleActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TextView puzzleInstructionsText, puzzleExpectedOutputText, puzzleTimerText, puzzleAttemptsText,
            resultPopupTextView;
    private CardView resultPopup;
    private LinearLayout bottomBar;
    private Button puzzleButton;
    private long startTime = 0;
    private Handler timerHandler = new Handler();
    private DatabaseHandlerSingleton mDatabaseHandlerSingleton;
    private Puzzle mCurrentPuzzle;
    private Level mCurrentLevel;
    private String mClassName = getClass().toString();
    private int mTimerSeconds, mTimerResumeTime, mAttemptsCount, level_id;
    private Animation bottomUp, bottomDown;
    private boolean mCorrectAnswer;
    private String mCurrentFragment;
    private PuzzleCodeBuilder mCurrentPuzzleCodeBuilder;
    private DragListViewFragment mDragListViewFragment;
    private MultipleChoiceListFragment mMultipleChoiceListFragment;
    private SingleChoiceListFragment mSingleChoiceListFragment;
    private TrueFalseFragment mTrueFalseFragment;

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

    private Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            long millis = (System.currentTimeMillis() - startTime);
            mTimerSeconds = mTimerResumeTime + (int) (millis / 1000);
            int displayMinutes = mTimerSeconds / 60;
            int displaySeconds = mTimerSeconds % 60;
            puzzleTimerText.setText("Timer: " + String.format("%d:%02d", displayMinutes, displaySeconds));
            timerHandler.postDelayed(this, 500);
        }
    };

    private void reloadData() {
        if (mCurrentLevel != null) {
            mCurrentPuzzle = mDatabaseHandlerSingleton.getNextPuzzle(mCurrentLevel);
            puzzleInstructionsText.setText(mCurrentPuzzle.getPuzzle_instructions());
            getSupportActionBar().setTitle("Puzzle " + (mCurrentLevel.getLevel_puzzles_completed() + 1) + "/" + mCurrentLevel.getLevel_puzzles_count());
            mTimerResumeTime = mCurrentPuzzle.getPuzzle_time();
            mAttemptsCount = mCurrentPuzzle.getPuzzle_attempts();
            mAttemptsCount++;
            puzzleAttemptsText.setText("Attempts: " + String.valueOf(mAttemptsCount));
            mCurrentPuzzleCodeBuilder = new PuzzleCodeBuilder();
            mCurrentPuzzleCodeBuilder.processCSharpCode(mCurrentPuzzle.getPuzzle_data());
            String expectedOutput = mCurrentPuzzleCodeBuilder.getCSharpCodeToDisplayExpectedOutput();
            if (!expectedOutput.equals("")) {
                puzzleExpectedOutputText.setText(expectedOutput);
                puzzleExpectedOutputText.setVisibility(View.VISIBLE);
            } else {
                puzzleExpectedOutputText.setVisibility(View.GONE);
            }
            loadPuzzle();
            startTime = System.currentTimeMillis();
            timerHandler.postDelayed(timerRunnable, 0);
        } else {
            Log.e(mClassName, "Level data missing");
            finish();
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
                break;
        }
    }

    public PuzzleCodeBuilder getCurrentPuzzleCodeBuilder() {
        return mCurrentPuzzleCodeBuilder;
    }

    private void assignListeners() {
        puzzleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (puzzleButton.getText().toString().equals("Check")) {
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
        if (mDragListViewFragment != null && mCurrentFragment.equals(mDragListViewFragment.getClass().toString())) {
            mDragListViewFragment = (DragListViewFragment) getSupportFragmentManager().findFragmentByTag(mCurrentFragment);
            return mDragListViewFragment.checkIfCorrect();
        }
        if (mMultipleChoiceListFragment != null && mCurrentFragment.equals(mMultipleChoiceListFragment.getClass().toString())) {
            mMultipleChoiceListFragment = (MultipleChoiceListFragment) getSupportFragmentManager().findFragmentByTag(mCurrentFragment);
            return mMultipleChoiceListFragment.checkIfCorrect();
        }
        if (mSingleChoiceListFragment != null && mCurrentFragment.equals(mSingleChoiceListFragment.getClass().toString())) {
            mSingleChoiceListFragment = (SingleChoiceListFragment) getSupportFragmentManager().findFragmentByTag(mCurrentFragment);
            return mSingleChoiceListFragment.checkIfCorrect();
        }
        if (mTrueFalseFragment != null && mCurrentFragment.equals(mTrueFalseFragment.getClass().toString())) {
            mTrueFalseFragment = (TrueFalseFragment) getSupportFragmentManager().findFragmentByTag(mCurrentFragment);
            return mTrueFalseFragment.checkIfCorrect();
        }
        return false;
    }

    private void hidePopup() {
        resultPopup.startAnimation(bottomDown);
        resultPopup.setVisibility(View.GONE);
        puzzleButton.setText("Check");
        //bottomBar.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.green_200));
        reloadData();
    }

    private void showIncorrectPopup() {
        puzzleButton.setText("Retry?");
        resultPopupTextView.setText("INCORRECT!");
        //resultPopup.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.red_500));
        //bottomBar.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.red_200));
        resultPopup.startAnimation(bottomUp);
        resultPopup.setVisibility(View.VISIBLE);
        puzzleAttemptsText.setText("Attempts: " + String.valueOf(mAttemptsCount));
    }

    private void showCorrectPopup() {
        if (mDatabaseHandlerSingleton.getLevel(mCurrentLevel.getLevel_id()).getPuzzles_completed()) {
            mDatabaseHandlerSingleton.updateLevelData(mCurrentLevel);
            finish();
        } else {
            puzzleButton.setText("Continue");
            resultPopupTextView.setText("CORRECT!");
            //resultPopup.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.green_500));
            resultPopup.startAnimation(bottomUp);
            resultPopup.setVisibility(View.VISIBLE);
        }
    }

    private void assignSingletons() {
        mDatabaseHandlerSingleton = DatabaseHandlerSingleton.getInstance(this);
    }

    private void getBundle() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            level_id = bundle.getInt("level_id", -1);
            if (level_id != -1) {
                mCurrentLevel = mDatabaseHandlerSingleton.getLevel(level_id);
            } else {
                Log.e(mClassName, "Level data missing");
                finish();
            }
        }
    }

    private void savePuzzleData() {
        if (mCurrentPuzzle != null) {
            mTimerResumeTime = mTimerSeconds;
            timerHandler.removeCallbacks(timerRunnable);
            mCurrentPuzzle.setPuzzle_time(mTimerSeconds);
            mCurrentPuzzle.setPuzzle_attempts(mAttemptsCount);
            mCurrentPuzzle.setPuzzle_completed(mCorrectAnswer);
            mDatabaseHandlerSingleton.updatePuzzleData(mCurrentPuzzle);
            mCurrentLevel = mDatabaseHandlerSingleton.getLevel(level_id);
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

    @Override
    protected void onResume() {
        super.onResume();
        if (!puzzleButton.getText().toString().equals("Check")) {
            startTime = System.currentTimeMillis();
            timerHandler.postDelayed(timerRunnable, 0);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mTimerResumeTime = mTimerSeconds;
        timerHandler.removeCallbacks(timerRunnable);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        savePuzzleData();
    }

    private void assignViews() {
        toolbar = (Toolbar) findViewById(R.id.app_actionbar);
        puzzleInstructionsText = (TextView) findViewById(R.id.puzzleInstructionsText);
        puzzleExpectedOutputText = (TextView) findViewById(R.id.puzzleExpectedOutputText);
        puzzleTimerText = (TextView) findViewById(R.id.puzzleTimerText);
        puzzleAttemptsText = (TextView) findViewById(R.id.puzzleAttemptsText);
        resultPopupTextView = (TextView) findViewById(R.id.resultPopupTextView);
        puzzleButton = (Button) findViewById(R.id.puzzleButton);
        resultPopup = (CardView) findViewById(R.id.resultPopup);
        bottomBar = (LinearLayout) findViewById(R.id.bottomBar);
        bottomUp = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.bottom_up);
        bottomDown = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.bottom_down);
    }

    private void assignFonts() {
        Typeface Roboto_Medium = Typeface.createFromAsset(getAssets(), "Roboto-Medium.ttf");
        Typeface Roboto_Regular = Typeface.createFromAsset(getAssets(), "Roboto-Regular.ttf");
        puzzleInstructionsText.setTypeface(Roboto_Medium);
        puzzleExpectedOutputText.setTypeface(Roboto_Regular);
        puzzleTimerText.setTypeface(Roboto_Regular);
        puzzleAttemptsText.setTypeface(Roboto_Regular);
        resultPopupTextView.setTypeface(Roboto_Regular);
        puzzleButton.setTypeface(Roboto_Medium);
    }

    private void assignActionBar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
