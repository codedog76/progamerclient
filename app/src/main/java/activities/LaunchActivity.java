package activities;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.example.progamer.R;

import models.User;
import singletons.DatabaseHandlerSingleton;
import singletons.SettingsSingleton;

public class LaunchActivity extends AppCompatActivity {

    private TextView mTextLaunchScreenMessage;
    private Handler mLaunchScreenHandler = new Handler();
    private DatabaseHandlerSingleton mDatabaseHandlerSingleton;
    private SettingsSingleton mSettingsSingleton;
    //Adds a delay before calling launchActivity
    private Runnable mLaunchScreenRunnable = new Runnable() {
        @Override
        public void run() {
            launchActivity();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);
        assignSingletons();
        assignViews();
        assignFonts();
        doLaunchScreen();
    }

    // Assigns all required singleton classes
    public void assignSingletons() {
        mSettingsSingleton = SettingsSingleton.getInstance(this);
        mDatabaseHandlerSingleton = DatabaseHandlerSingleton.getInstance(this);
    }

    // Checks settings preferences singleton for how the launch screen should behave
    public void doLaunchScreen() {
        if (mSettingsSingleton.getLaunchScreenActive()) {
            if (mSettingsSingleton.getFirstTimeLaunch()) {
                mTextLaunchScreenMessage.setText(getString(R.string.string_welcome));
                mSettingsSingleton.setFirstTimeLaunch(false);
            } else {
                mTextLaunchScreenMessage.setText(getString(R.string.string_welcome_back));
            }
            mLaunchScreenHandler.postDelayed(mLaunchScreenRunnable, mSettingsSingleton.getLaunchScreenTime());
        } else {
            launchActivity();
        }
    }

    // If the there is a logged in user, go straight to main activity, preventing user from logging
    // in every time
    private void launchActivity() {
        if (mDatabaseHandlerSingleton.getLoggedUser() != null) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

        } else {
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        }
    }

    //assigns all views for this activity
    private void assignViews() {
        mTextLaunchScreenMessage = (TextView) findViewById(R.id.text_launch_screen_message);
    }

    //assigns fonts for the views in this activity
    private void assignFonts() {
        Typeface Roboto_Medium = Typeface.createFromAsset(getAssets(), "Roboto-Medium.ttf");
        mTextLaunchScreenMessage.setTypeface(Roboto_Medium);
    }
}
