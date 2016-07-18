package activities;

import android.graphics.Typeface;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import com.example.progamer.R;


public class PuzzleActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TextView puzzleInstructionsTitleText, puzzleInstructionsText, puzzleExpectedOutputTitleText, puzzleExpectedOutputText, puzzleTimerText, puzzleAttemptsText;
    private Button puzzleCheckButton;
    long startTime = 0;
    Handler timerHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_puzzle);
        assignViews();
        assignFonts();
        assignActionBar();
        startTime = System.currentTimeMillis();
        timerHandler.postDelayed(timerRunnable, 0);
    }

    Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            long millis = System.currentTimeMillis() - startTime;
            int seconds = (int) (millis / 1000);
            int minutes = seconds / 60;
            seconds = seconds % 60;

            puzzleTimerText.setText("Timer: " + String.format("%d:%02d", minutes, seconds));

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
