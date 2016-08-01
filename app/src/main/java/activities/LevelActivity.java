package activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.progamer.R;

import models.Level;
import singletons.DatabaseHandlerSingleton;
import singletons.NetworkManagerSingleton;

public class LevelActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private Level mCurrentLevel;
    private DatabaseHandlerSingleton mDatabaseHandlerSingleton;
    private NetworkManagerSingleton mNetworkManagerSingleton;
    private ProgressDialog mProgressDialog;
    private TextView mLevelTitle, mLevelDescription, currentUserScore, currentUserAttempts, currentUserTime, levelActivityPerformance;

    private Button levelContinueButton;
    private String mClassName = getClass().toString();
    private int level_id;


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
        loadData();
    }

    private void assignProgressDialog() {
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("Loading..");
        mProgressDialog.setCancelable(false);
        mProgressDialog.setCanceledOnTouchOutside(false);
    }

    private void assignFonts() {
        Typeface Roboto_Medium = Typeface.createFromAsset(getAssets(), "Roboto-Medium.ttf");
        Typeface Roboto_Regular = Typeface.createFromAsset(getAssets(), "Roboto-Regular.ttf");
        mLevelTitle.setTypeface(Roboto_Regular);
        mLevelDescription.setTypeface(Roboto_Regular);
        /*currentUserScore.setTypeface(Roboto_Regular);
        currentUserAttempts.setTypeface(Roboto_Regular);
        currentUserTime.setTypeface(Roboto_Regular);
        levelActivityPerformance.setTypeface(Roboto_Regular);*/
        levelContinueButton.setTypeface(Roboto_Medium);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mProgressDialog.dismiss();
    }

    private void loadData() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Level " + mCurrentLevel.getLevel_number());
        }
        mLevelTitle.setText(mCurrentLevel.getLevel_title());
        mLevelDescription.setText(mCurrentLevel.getLevel_description());
        /*currentUserScore.setText(String.valueOf(mCurrentLevel.getLevel_score()));
        currentUserAttempts.setText(String.valueOf(mCurrentLevel.getLevel_attempts()));
        currentUserTime.setText(String.valueOf(mCurrentLevel.getLevel_time()));*/
        if (mCurrentLevel.getLevel_puzzles_completed() == 0) {
            if (mCurrentLevel.getPuzzles_completed()) levelContinueButton.setText(getString(R.string.string_try_again));
            else levelContinueButton.setText(getString(R.string.string_start));
        } else levelContinueButton.setText(getString(R.string.string_continue));
    }

    private void assignListeners() {
        levelContinueButton.setOnClickListener(new View.OnClickListener() {
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
                } else {
                    loadPuzzleActivity();
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

    @Override
    protected void onStop() {
        super.onStop();
        mNetworkManagerSingleton.cancelJSONRequest("downloadPuzzlesJSONRequest");
    }

    @Override
    protected void onResume() {
        super.onResume();
        mCurrentLevel = mDatabaseHandlerSingleton.getLevel(level_id);
        loadData();
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

    private void assignViews() {
        mToolbar = (Toolbar) findViewById(R.id.app_actionbar);
        mLevelTitle = (TextView) findViewById(R.id.text_level_title);
        mLevelDescription = (TextView) findViewById(R.id.text_level_description);
        levelActivityPerformance = (TextView) findViewById(R.id.levelActivityScore);
        levelContinueButton = (Button) findViewById(R.id.levelContinueButton);
    }

    private void assignActionBar() {
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
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
