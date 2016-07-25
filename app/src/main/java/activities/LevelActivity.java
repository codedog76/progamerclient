package activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.progamer.R;

import java.util.ArrayList;
import java.util.List;

import database.DatabaseHandler;
import models.Level;
import models.Puzzle;
import singletons.DatabaseHandlerSingleton;
import singletons.NetworkManagerSingleton;

public class LevelActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private Level mCurrentLevel;
    private DatabaseHandlerSingleton mDatabaseHandlerSingleton;
    private NetworkManagerSingleton mNetworkManagerSingleton;
    private TextView activityLevelTitleTextView, activityLevelDescription, currentUserScore, currentUserAttempts, currentUserTime, levelActivityPerformance;
    private Button levelContinueButton;
    private String mClassName = getClass().toString();
    private int level_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level);
        assignViews();
        assignFonts();
        assignSingletons();
        assignListeners();
        assignActionBar();
        getBundle();
        loadData();
    }

    private void assignFonts() {
        Typeface Roboto_Medium = Typeface.createFromAsset(getAssets(), "Roboto-Medium.ttf");
        Typeface Roboto_Regular = Typeface.createFromAsset(getAssets(), "Roboto-Regular.ttf");
        activityLevelTitleTextView.setTypeface(Roboto_Regular);
        activityLevelDescription.setTypeface(Roboto_Regular);
        currentUserScore.setTypeface(Roboto_Regular);
        currentUserAttempts.setTypeface(Roboto_Regular);
        currentUserTime.setTypeface(Roboto_Regular);
        levelActivityPerformance.setTypeface(Roboto_Regular);
        levelContinueButton.setTypeface(Roboto_Medium);
    }

    private void loadData() {
        getSupportActionBar().setTitle("Level " + mCurrentLevel.getLevel_number());
        activityLevelTitleTextView.setText(mCurrentLevel.getLevel_title());
        activityLevelDescription.setText(mCurrentLevel.getLevel_description());
        currentUserScore.setText(String.valueOf(mCurrentLevel.getLevel_score()));
        currentUserAttempts.setText(String.valueOf(mCurrentLevel.getLevel_attempts()));
        currentUserTime.setText(String.valueOf(mCurrentLevel.getLevel_time()));
    }

    private void assignListeners() {
        levelContinueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCurrentLevel.getPuzzles_completed()) {
                    showLogoutDialog();
                } else {
                    Intent intent = new Intent(getApplicationContext(), PuzzleActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_HISTORY);
                    Bundle bundle = new Bundle();
                    bundle.putInt("level_id", mCurrentLevel.getLevel_id());
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            }
        });
    }

    private void showLogoutDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Puzzles completed")
                .setMessage("New puzzles will be downloaded. Do you wish to continue?")
                //.setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        mNetworkManagerSingleton.downloadPuzzlesJSONRequest(mCurrentLevel, new NetworkManagerSingleton.BooleanResponseListener() {
                            @Override
                            public void getResult(Boolean response, String message) {
                                if (response) {
                                    Intent intent = new Intent(getApplicationContext(), PuzzleActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_HISTORY);
                                    Bundle bundle = new Bundle();
                                    bundle.putInt("level_id", mCurrentLevel.getLevel_id());
                                    intent.putExtras(bundle);
                                    startActivity(intent);
                                }
                            }
                        });
                    }
                })
                .setNegativeButton("No", null).show();
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
        toolbar = (Toolbar) findViewById(R.id.app_actionbar);
        activityLevelTitleTextView = (TextView) findViewById(R.id.activityLevelTitleTextView);
        activityLevelDescription = (TextView) findViewById(R.id.activityLevelDescription);
        currentUserScore = (TextView) findViewById(R.id.currentUserScore);
        currentUserAttempts = (TextView) findViewById(R.id.currentUserAttempts);
        currentUserTime = (TextView) findViewById(R.id.currentUserTime);
        levelActivityPerformance = (TextView) findViewById(R.id.levelActivityPerformance);
        levelContinueButton = (Button) findViewById(R.id.levelContinueButton);
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
