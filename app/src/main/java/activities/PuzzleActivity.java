package activities;

import android.graphics.Typeface;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.progamer.R;

import fragments.LevelsFragment;
import fragments.puzzles.DragListViewFragment;
import models.Level;
import models.Puzzle;
import singletons.DatabaseHandlerSingleton;


public class PuzzleActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TextView puzzleInstructionsTitleText, puzzleInstructionsText, puzzleExpectedOutputTitleText, puzzleExpectedOutputText, puzzleTimerText, puzzleAttemptsText;
    private Button puzzleCheckButton;
    private long startTime = 0;
    private Handler timerHandler = new Handler();
    private DatabaseHandlerSingleton mDatabaseHandlerSingleton;
    private Puzzle selectedPuzzle;
    private String mClassName = getClass().toString();
    private int mTimerSeconds;
    private int mTimerResumeTime;
    private int mAttemptsCount;
    private DragListViewFragment mDragListViewFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_puzzle);
        assignViews();
        assignFonts();
        assignActionBar();
        assignSingletons();
        getBundle();
        assingListeners();
        loadDragListViewFragment();
    }

    private void assingListeners() {
        puzzleCheckButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAttemptsCount++;
                puzzleAttemptsText.setText("Attempts: " + String.valueOf(mAttemptsCount));
            }
        });
    }

    private void assignSingletons() {
        mDatabaseHandlerSingleton = DatabaseHandlerSingleton.getInstance(this);
    }

    private void getBundle() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            int current_level_id = bundle.getInt("current_level_id", -1);
            if (current_level_id != -1) {
                Level tempLevel = new Level();
                tempLevel.setLevel_id(current_level_id);
                selectedPuzzle = mDatabaseHandlerSingleton.getNextPuzzle(tempLevel);
                getSupportActionBar().setTitle(String.valueOf(selectedPuzzle.getPuzzle_id()) +" " + selectedPuzzle.getPuzzle_database_id());
                mTimerResumeTime = selectedPuzzle.getPuzzle_time();
                mAttemptsCount = selectedPuzzle.getPuzzle_attempts();
            } else {
                Log.e(mClassName, "Level data missing");
                finish();
            }
        }
    }

    private void loadDragListViewFragment() {
        if (mDragListViewFragment == null)
            mDragListViewFragment = new DragListViewFragment();
        replaceFragment(mDragListViewFragment);
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
        Toast.makeText(getApplicationContext(), "onResume", Toast.LENGTH_SHORT).show();
        if (mAttemptsCount == 0)
            mAttemptsCount = 1;
        puzzleAttemptsText.setText("Attempts: " + String.valueOf(mAttemptsCount));
        startTime = System.currentTimeMillis();
        timerHandler.postDelayed(timerRunnable, 0);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Toast.makeText(getApplicationContext(), "onPause", Toast.LENGTH_SHORT).show();
        mTimerResumeTime = mTimerSeconds;
        timerHandler.removeCallbacks(timerRunnable);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Toast.makeText(getApplicationContext(), "onDestroy", Toast.LENGTH_SHORT).show();
        selectedPuzzle.setPuzzle_time(mTimerSeconds);
        selectedPuzzle.setPuzzle_attempts(mAttemptsCount);
        mDatabaseHandlerSingleton.setPuzzleData(selectedPuzzle);
    }

    Runnable timerRunnable = new Runnable() {
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

    private void assignViews() {
        toolbar = (Toolbar) findViewById(R.id.app_actionbar);
        puzzleInstructionsTitleText = (TextView) findViewById(R.id.puzzleInstructionsTitleText);
        puzzleInstructionsText = (TextView) findViewById(R.id.puzzleInstructionsText);
        puzzleExpectedOutputTitleText = (TextView) findViewById(R.id.puzzleExpectedOutputTitleText);
        puzzleExpectedOutputText = (TextView) findViewById(R.id.puzzleExpectedOutputText);
        puzzleTimerText = (TextView) findViewById(R.id.puzzleTimerText);
        puzzleAttemptsText = (TextView) findViewById(R.id.puzzleAttemptsText);
        puzzleCheckButton = (Button) findViewById(R.id.puzzleCheckButton);
    }

    private void assignFonts() {
        Typeface Roboto_Medium = Typeface.createFromAsset(getAssets(), "Roboto-Medium.ttf");
        Typeface Roboto_Regular = Typeface.createFromAsset(getAssets(), "Roboto-Regular.ttf");
        puzzleInstructionsTitleText.setTypeface(Roboto_Regular);
        puzzleInstructionsText.setTypeface(Roboto_Regular);
        puzzleExpectedOutputTitleText.setTypeface(Roboto_Regular);
        puzzleExpectedOutputText.setTypeface(Roboto_Regular);
        puzzleTimerText.setTypeface(Roboto_Regular);
        puzzleAttemptsText.setTypeface(Roboto_Regular);
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
