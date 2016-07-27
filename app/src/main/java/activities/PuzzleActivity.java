package activities;

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
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.progamer.R;

import java.util.List;

import fragments.puzzles.DragListViewFragment;
import fragments.puzzles.MultipleChoiceListFragment;
import models.Level;
import models.Puzzle;
import singletons.DatabaseHandlerSingleton;


public class PuzzleActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TextView puzzleInstructionsText, puzzleExpectedOutputText, puzzleTimerText, puzzleAttemptsText,
            resultPopupTextView;
    private LinearLayout resultPopup, bottomBar;
    private Button puzzleButton;
    private long startTime = 0;
    private Handler timerHandler = new Handler();
    private DatabaseHandlerSingleton mDatabaseHandlerSingleton;
    private Puzzle mCurrentPuzzle;
    private Level mCurrentLevel;
    private int level_id;
    private String mClassName = getClass().toString();
    private int mTimerSeconds;
    private int mTimerResumeTime;
    private int mAttemptsCount;
    private DragListViewFragment mDragListViewFragment;
    private MultipleChoiceListFragment mMultipleChoiceListFragment;
    private Animation bottomUp;
    private Animation bottomDown;
    private boolean mCorrectAnswer;
    private String mCurrentFragment;

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
            getSupportActionBar().setTitle("Puzzle " + (mCurrentLevel.getLevel_puzzles_completed() + 1) + "/" + mCurrentLevel.getLevel_puzzles_count());
            puzzleInstructionsText.setText(mCurrentPuzzle.getPuzzle_instructions());
            mTimerResumeTime = mCurrentPuzzle.getPuzzle_time();
            mAttemptsCount = mCurrentPuzzle.getPuzzle_attempts();
            if (mAttemptsCount == 0)
                mAttemptsCount++;
            puzzleAttemptsText.setText("Attempts: " + String.valueOf(mAttemptsCount));
            if (mCurrentPuzzle.getPuzzle_type().equals("drag_list")) {
                loadDragListViewFragment();
            }
            if (mCurrentPuzzle.getPuzzle_type().equals("multiple_list")) {
                loadMultipleChoiceListFragment();
            }
            startTime = System.currentTimeMillis();
            timerHandler.postDelayed(timerRunnable, 0);
        } else {
            Log.e(mClassName, "Level data missing");
            finish();
        }
    }

    public void setExpectedOutput(List<String> expectedOutput) {
        String expected_output = "";
        for(String output:expectedOutput) {
            expected_output = expected_output + output;
        }
        puzzleExpectedOutputText.setText(expected_output);
    }

    private void assignListeners() {
        puzzleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleFragmentTouch();
                if (puzzleButton.getText().toString().equals("Check")) {
                    if (checkFragmentAnswers()) {
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

    private boolean checkFragmentAnswers() {
        if (mDragListViewFragment != null && mCurrentFragment.equals(mDragListViewFragment.getClass().toString())) {
            mDragListViewFragment = (DragListViewFragment) getSupportFragmentManager().findFragmentByTag(mCurrentFragment);
            return mDragListViewFragment.checkIfCorrect();
        }
        if (mMultipleChoiceListFragment != null && mCurrentFragment.equals(mMultipleChoiceListFragment.getClass().toString())) {
            mMultipleChoiceListFragment = (MultipleChoiceListFragment) getSupportFragmentManager().findFragmentByTag(mCurrentFragment);
            return mMultipleChoiceListFragment.checkIfCorrect();
        }
        return false;
    }

    private void toggleFragmentTouch() {
        if (mDragListViewFragment != null && mCurrentFragment.equals(mDragListViewFragment.getClass().toString())) {
            mDragListViewFragment = (DragListViewFragment) getSupportFragmentManager().findFragmentByTag(mCurrentFragment);
            mDragListViewFragment.toggleTouch();
        }
        if (mMultipleChoiceListFragment != null && mCurrentFragment.equals(mMultipleChoiceListFragment.getClass().toString())) {
            mMultipleChoiceListFragment = (MultipleChoiceListFragment) getSupportFragmentManager().findFragmentByTag(mCurrentFragment);
            mMultipleChoiceListFragment.toggleTouch();
        }
    }

    private void hidePopup() {
        resultPopup.startAnimation(bottomDown);
        resultPopup.setVisibility(View.GONE);
        puzzleButton.setText("Check");
        bottomBar.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.green_200));
        reloadData();
    }

    private void showIncorrectPopup() {
        puzzleButton.setText("Retry?");
        resultPopupTextView.setText("INCORRECT!");
        resultPopup.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.red_500));
        bottomBar.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.red_200));
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
            resultPopup.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.green_500));
            resultPopup.startAnimation(bottomUp);
            resultPopup.setVisibility(View.VISIBLE);
        }
    }

    private void assignSingletons() {
        mDatabaseHandlerSingleton = DatabaseHandlerSingleton.getInstance(this);
    }

    public Puzzle getCurrentPuzzle() {
        return mCurrentPuzzle;
    }

    public Level getCurrentLevel() {
        return mCurrentLevel;
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
        resultPopup = (LinearLayout) findViewById(R.id.resultPopup);
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
